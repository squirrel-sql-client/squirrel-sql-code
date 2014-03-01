package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2002-2004 Colin Bell
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
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;

import static net.sourceforge.squirrel_sql.fw.resources.LibraryResources.IImageNames.*;

import java.io.Serializable;

/**
 *
 * Defines the different types of database objects.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DatabaseObjectType implements IHasIdentifier, Serializable
{
   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DatabaseObjectType.class);

   /** Factory to generate unique IDs for these objects. */
   private final static IntegerIdentifierFactory s_idFactory = new IntegerIdentifierFactory();

   /** Other - general purpose. */
   public final static DatabaseObjectType OTHER = createNewDatabaseObjectTypeI18n("DatabaseObjectType.other");

   /** Catalog. */
   public final static DatabaseObjectType BEST_ROW_ID = createNewDatabaseObjectTypeI18n("DatabaseObjectType.bestRowID");

   /** Catalog. */
   public final static DatabaseObjectType CATALOG = createNewDatabaseObjectTypeI18n("DatabaseObjectType.catalog", DOT_CATALOG);

   /** Column. */
   public final static DatabaseObjectType COLUMN = createNewDatabaseObjectTypeI18n("DatabaseObjectType.column");

   /** Database. */
   public final static DatabaseObjectType SESSION = createNewDatabaseObjectTypeI18n("DatabaseObjectType.database", DOT_DATABASE);


   /**
    * Datbase object type for a "Database" node in the object tree.  There is onle one
    * node of this type in the object tree and it indicates the alias of the database.
    */
   public final static DatabaseObjectType DATABASE_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.DATABASE_TYPE_DBO", DOT_DATABASE); //DatabaseObjectType.DATABASE_TYPE_DBO=Database Type


   /** Standard datatype. */
   public final static DatabaseObjectType DATATYPE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.datatype", DOT_DATATYPE);

    /** Unique Key for a table. */
    public final static DatabaseObjectType PRIMARY_KEY = createNewDatabaseObjectTypeI18n("DatabaseObjectType.primarykey");

   /** Foreign Key relationship. */
   public final static DatabaseObjectType FOREIGN_KEY = createNewDatabaseObjectTypeI18n("DatabaseObjectType.foreignkey");

   /** Function. */
   public final static DatabaseObjectType FUNCTION = createNewDatabaseObjectTypeI18n("DatabaseObjectType.function", DOT_FUNCTION);

   /**
    * Database object type for a "Index Type" node in the object tree. There is
    * one node of this type in the object tree for each table and it is labeled "INDEX".
    */   
   public static final DatabaseObjectType INDEX_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.INDEX_TYPE_DBO", DOT_INDEXES); //DatabaseObjectType.INDEX_TYPE_DBO=Index Type
   
   /** Index. */
   public final static DatabaseObjectType INDEX = createNewDatabaseObjectTypeI18n("DatabaseObjectType.index", DOT_INDEX);

   /** Stored procedure. */
   public final static DatabaseObjectType PROCEDURE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.storproc", DOT_PROCEDURE);

   /**
    * Database object type for a "Procedure Type" node in the object tree. There is
    * only one node of this type in the object tree and it is labeled "PROCEDURE".
    */
   public final static DatabaseObjectType PROC_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.PROC_TYPE_DBO", DOT_PROCEDURES); //DatabaseObjectType.PROC_TYPE_DBO=Stored Procedure Type



   /** Schema. */
   public final static DatabaseObjectType SCHEMA = createNewDatabaseObjectTypeI18n("DatabaseObjectType.schema", DOT_SCHEMA);

   /**
    * Database object type for a "Sequence Type" node in the object tree. There is
    * one node of this type in the object tree for each table and it is labeled "SEQUENCE".
    */   
   public static final DatabaseObjectType SEQUENCE_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.SEQUENCE_TYPE_DBO", DOT_SEQUENCE); //DatabaseObjectType.SEQUENCE_TYPE_DBO=Sequence Type
   
   
   /**
    * An object that generates uniques IDs for primary keys. E.G. an Oracle
    * sequence.
    */
   public final static DatabaseObjectType SEQUENCE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.sequence", DOT_SEQUENCES);

   /**
    * Database object type for a "Synonym Type" node in the object tree. There is
    * one node of this type in the object tree for each table and it is labeled "SYNONYM".
    */
   public static final DatabaseObjectType SYNONYM_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.SYNONYM_TYPE_DBO"); //DatabaseObjectType.SEQUENCE_TYPE_DBO=Sequence Type

   /**
    * An object that is an alias for another object.  While support for this isn't standardized or universal
    * this type of object is found in a few different databases (e.g. Oracle, Netezza)
    */
   public final static DatabaseObjectType SYNONYM = createNewDatabaseObjectTypeI18n("DatabaseObjectType.synonym");

   /** TABLE. */
   public final static DatabaseObjectType TABLE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.table", DOT_TABLE);

   /**
    * Database object type for a "Table Type" node in the object tree. Some examples
    * are "TABLE", "SYSTEM TABLE", "VIEW" etc.
    */
   public final static DatabaseObjectType TABLE_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.TABLE_TYPE_DBO", DOT_TABLES); //DatabaseObjectType.TABLE_TYPE_DBO=Table Type

   public static final DatabaseObjectType VIEW = createNewDatabaseObjectTypeI18n("DatabaseObjectType.view", DOT_VIEW);

   /**
    * Database object type for a "Trigger Type" node in the object tree. There is
    * one node of this type in the object tree for each table and it is labeled "TRIGGER".
    */   
   public static final DatabaseObjectType TRIGGER_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.TRIGGER_TYPE_DBO", DOT_TRIGGERS); //DatabaseObjectType.TRIGGER_TYPE_DBO=Trigger Type
   
   /** Trigger. */
   public final static DatabaseObjectType TRIGGER = createNewDatabaseObjectTypeI18n("DatabaseObjectType.catalog", DOT_TRIGGER);

   /** User defined type. */
   public final static DatabaseObjectType UDT = createNewDatabaseObjectTypeI18n("DatabaseObjectType.udt", DOT_DATATYPE);

   /**
    * Database object type for a "UDT Type" node in the object tree. There is only one
    * node of this type in the object tree and it says "UDT".
    */
   public final static DatabaseObjectType UDT_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.UDT_TYPE_DBO", DOT_DATATYPES); //DatabaseObjectType.UDT_TYPE_DBO=UDT Type

   /** User defined function. */
   public final static DatabaseObjectType UDF = createNewDatabaseObjectTypeI18n("DatabaseObjectType.udf");

   /**
    * Database object type for a "UDF Type" node in the object tree. There is only one
    * node of this type in the object tree and it says "UDF".
    */
   public final static DatabaseObjectType UDF_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.UDF_TYPE_DBO"); //DatabaseObjectType.UDF_TYPE_DBO=UDF Type

   /** A database user. */
   public final static DatabaseObjectType USER = createNewDatabaseObjectTypeI18n("DatabaseObjectType.user", DOT_USER);

   /** Uniquely identifies this Object. */
   private final IIdentifier _id;

   /** Describes this object type. */
   private final String _name;
   private String _keyForSerializationReplace;
   private Icon _icon;

   /**
    * Default ctor.
    */
   private DatabaseObjectType(String name, String keyForSerializationReplace, Icon icon)
   {
      _keyForSerializationReplace = keyForSerializationReplace;
      _icon = icon;
      _id = s_idFactory.createIdentifier();
      _name = name != null ? name : _id.toString();
   }

   /**
    * Return the object that uniquely identifies this object.
    *
    * @return	Unique ID.
    */
   public IIdentifier getIdentifier()
   {
      return _id;
   }

   /**
    * Retrieve the descriptive name of this object.
    *
    * @return	The descriptive name of this object.
    */
   public String getName()
   {
      return _name;
   }


   public String getKeyForSerializationReplace()
   {
      return _keyForSerializationReplace;
   }

   public Icon getIcon()
   {
      return _icon;
   }

   public String toString()
   {
      return getName();
   }

   private static DatabaseObjectType createNewDatabaseObjectTypeI18n(String key, String imageName)
   {
      ImageIcon icon = null;

      if (null != imageName)
      {
         icon = new LibraryResources().getIcon(imageName);
      }

      return createNewDatabaseObjectType(s_stringMgr.getString(key), icon);
   }

   private static DatabaseObjectType createNewDatabaseObjectTypeI18n(String key)
   {
      return createNewDatabaseObjectTypeI18n(key, null);
   }

   public static DatabaseObjectType createNewDatabaseObjectType(String key)
   {
      return createNewDatabaseObjectType(key, null);
   }

   public static DatabaseObjectType createNewDatabaseObjectType(String key, Icon icon)
   {
      return new DatabaseObjectType(key, key, icon);
   }

}
