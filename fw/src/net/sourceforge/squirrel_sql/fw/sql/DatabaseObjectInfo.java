package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.io.Serializable;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class DatabaseObjectInfo implements IDatabaseObjectInfo, Serializable
{
   /** Property names for this bean. */
   public interface IPropertyNames
   {
      /** Catalog name. */
      String CATALOG_NAME = "catalogName";

      /** Schema name. */
      String SCHEMA_NAME = "schemaName";

      /** Simple name. */
      String SIMPLE_NAME = "simpleName";

      /** Qualified name. */
      String QUALIFIED_NAME = "qualifiedName";
   }

   static final long serialVersionUID = -7138016566181091160L;

/** Catalog name. Can be <CODE>null</CODE> */
   private final String _catalog;

   /** Schema name. Can be <CODE>null</CODE> */
   private final String _schema;

   /** Simple object name. */
   private final String _simpleName;

   /** Fully qualified name for this object. */
   private final String _qualifiedName;

   /** Object type. @see DatabaseObjectType.*/
   private DatabaseObjectType _dboType = DatabaseObjectType.OTHER;

   public DatabaseObjectInfo(String catalog, String schema, String simpleName,
                             DatabaseObjectType dboType, ISQLDatabaseMetaData md)
   {
      super();
      if (dboType == null)
      {
         throw new IllegalArgumentException("DatabaseObjectType == null");
      }
      if (md == null)
      {
         throw new IllegalArgumentException("SQLDatabaseMetaData == null");
      }

      _catalog = catalog;
      _schema = schema;
      _simpleName = simpleName;
      _qualifiedName = generateQualifiedName(md);
      _dboType = dboType;
   }

   /**
    * Default constructor for using instances of this class to contain 
    * information about new objects that will be created soon.
    */
   public DatabaseObjectInfo(String catalog, String schema, String simpleName) {
       _catalog = catalog;
       _schema = schema;
       _simpleName = simpleName;
       _qualifiedName = simpleName;
   }
   
   public String toString()
   {
      return getSimpleName();
   }

   public String getCatalogName()
   {
      return _catalog;
   }


   public String getSchemaName()
   {
      return _schema;
   }

   public String getSimpleName()
   {
      return _simpleName;
   }

   public String getQualifiedName()
   {
      return _qualifiedName;
   }

   public DatabaseObjectType getDatabaseObjectType()
   {
      return _dboType;
   }

   protected String generateQualifiedName(ISQLConnection conn)
   {
      return generateQualifiedName(conn.getSQLMetaData());
   }
   
   /**
    * Informix represents database objects in catalogs *and* schemas.  So a table
    * called <table> might be found in the <databasename> catalog, which lives in 
    * the <schemaname> schema and is addressed as:
    * 
    * <databasename>:"<schemaname>".<table>
    * 
    * It may also be referred to as simply <table>
    * 
    * This method returns a qualifed name that meets this criteria. 
    * 
    * @return a valid Informix qualified name - if catalog *and* schema are not
    *         null/empty, this returns the database object name such as
    *         catalog:schema.simpleName.  However, if either catalog or schema
    *         (or both) are null, this simply returns the simpleName
    */
   private String getInformixQualifiedName() {
       StringBuffer result = new StringBuffer();
       if (_catalog != null && _schema != null) {
           result.append(_catalog);
           result.append(":");
           result.append("\"");
           result.append(_schema);
           result.append("\"");
           result.append(".");
       }
       result.append(_simpleName);
       return result.toString();
   }
   
   protected String generateQualifiedName(ISQLDatabaseMetaData md)
   {
      String catSep = null;
      String identifierQuoteString = null;
      boolean supportsSchemasInDataManipulation = false;
      boolean supportsCatalogsInDataManipulation = false;

      // check for Informix - it has very "special" qualified names
      if (DialectFactory.isInformix(md)) {
          return getInformixQualifiedName();
      }
      
      try
      {
         supportsSchemasInDataManipulation = md.supportsSchemasInDataManipulation();
      }
      catch (SQLException ignore)
      {
         // Ignore.
      }
      try
      {
          supportsCatalogsInDataManipulation = md.supportsCatalogsInDataManipulation();
      }
      catch (SQLException ignore)
      {
         // Ignore.
      }
      
      try
      {
//         if (supportsCatalogsInDataManipulation)
//         {
            catSep = md.getCatalogSeparator();
//         }
      }
      catch (SQLException ignore)
      {
         // Ignore.
      }
      
      
      if (StringUtilities.isEmpty(catSep))
      {
          catSep = ".";
      }
    		  
      try
      {
         identifierQuoteString = md.getIdentifierQuoteString();
         if (identifierQuoteString != null
            && identifierQuoteString.equals(" "))
         {
            identifierQuoteString = null;
         }
      }
      catch (SQLException ignore)
      {
         // Ignore.
      }

      StringBuffer buf = new StringBuffer();
      //if (catSep != null
      //   && catSep.length() > 0
      //&& _catalog != null
      //&& _catalog.length() > 0)
      if (supportsCatalogsInDataManipulation
    	&& !StringUtilities.isEmpty(_catalog))	  
      {
         if (identifierQuoteString != null)
         {
            buf.append(identifierQuoteString);
         }
         buf.append(_catalog);
         if (identifierQuoteString != null)
         {
            buf.append(identifierQuoteString);
         }
         buf.append(catSep);
      }

      if (supportsSchemasInDataManipulation && _schema != null
         && _schema.length() > 0)
      {
         if (identifierQuoteString != null)
         {
            buf.append(identifierQuoteString);
         }
         buf.append(_schema);
         if (identifierQuoteString != null)
         {
            buf.append(identifierQuoteString);
         }
         
         //buf.append(".");
         buf.append(catSep);
      }

      if (identifierQuoteString != null)
      {
         buf.append(identifierQuoteString);
      }
      buf.append(_simpleName);
      if (identifierQuoteString != null)
      {
         buf.append(identifierQuoteString);
      }
      return buf.toString();
   }

   public boolean equals(Object obj)
   {
      if (obj == null) {
          return false;
      }
      if (obj.getClass() == this.getClass())
      {
         DatabaseObjectInfo info = (DatabaseObjectInfo) obj;
         if ((info._catalog == null && _catalog == null)
            || ((info._catalog != null && _catalog != null)
               && info._catalog.equals(_catalog)))
         {
            if ((info._qualifiedName == null && _qualifiedName == null)
               || ((info._qualifiedName != null && _qualifiedName != null)
                  && info._qualifiedName.equals(_qualifiedName)))
            {
               if ((info._schema == null && _schema == null)
                  || ((info._schema != null && _schema != null)
                     && info._schema.equals(_schema)))
               {
                  return (
                     (info._simpleName == null && _simpleName == null)
                        || ((info._simpleName != null
                           && _simpleName != null)
                           && info._simpleName.equals(_simpleName)));
               }

            }
         }
      }
      return false;
   }

   public int hashCode()
   {
      return _qualifiedName.hashCode();
   }

   public int compareTo(Object o)
   {
      DatabaseObjectInfo other = (DatabaseObjectInfo) o;
      return _qualifiedName.compareTo(other._qualifiedName);
   }
}
