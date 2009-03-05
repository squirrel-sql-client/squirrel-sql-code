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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.io.Serializable;

/**
 *
 * Defines the different types of database objects.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DatabaseObjectType implements IHasIdentifier, Serializable
{
   static final long serialVersionUID = 2325635336825122256L;
   
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
   public final static DatabaseObjectType CATALOG = createNewDatabaseObjectTypeI18n("DatabaseObjectType.catalog");

   /** Column. */
   public final static DatabaseObjectType COLUMN = createNewDatabaseObjectTypeI18n("DatabaseObjectType.column");

   /** Database. */
   public final static DatabaseObjectType SESSION = createNewDatabaseObjectTypeI18n("DatabaseObjectType.database");


   /**
    * Datbase object type for a "Database" node in the object tree.  There is onle one
    * node of this type in the object tree and it indicates the alias of the database.
    */
   public final static DatabaseObjectType DATABASE_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.DATABASE_TYPE_DBO"); //DatabaseObjectType.DATABASE_TYPE_DBO=Database Type


   /** Standard datatype. */
   public final static DatabaseObjectType DATATYPE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.datatype");

    /** Unique Key for a table. */
    public final static DatabaseObjectType PRIMARY_KEY = createNewDatabaseObjectTypeI18n("DatabaseObjectType.primarykey");

   /** Foreign Key relationship. */
   public final static DatabaseObjectType FOREIGN_KEY = createNewDatabaseObjectTypeI18n("DatabaseObjectType.foreignkey");

   /** Function. */
   public final static DatabaseObjectType FUNCTION = createNewDatabaseObjectTypeI18n("DatabaseObjectType.function");

   /**
    * Database object type for a "Index Type" node in the object tree. There is
    * one node of this type in the object tree for each table and it is labeled "INDEX".
    */   
   public static final DatabaseObjectType INDEX_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.INDEX_TYPE_DBO"); //DatabaseObjectType.INDEX_TYPE_DBO=Index Type
   
   /** Index. */
   public final static DatabaseObjectType INDEX = createNewDatabaseObjectTypeI18n("DatabaseObjectType.index");

   /** Stored procedure. */
   public final static DatabaseObjectType PROCEDURE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.storproc");

   /**
    * Database object type for a "Procedure Type" node in the object tree. There is
    * only one node of this type in the object tree and it is labeled "PROCEDURE".
    */
   public final static DatabaseObjectType PROC_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.PROC_TYPE_DBO"); //DatabaseObjectType.PROC_TYPE_DBO=Stored Procedure Type



   /** Schema. */
   public final static DatabaseObjectType SCHEMA = createNewDatabaseObjectTypeI18n("DatabaseObjectType.schema");

   /**
    * Database object type for a "Sequence Type" node in the object tree. There is
    * one node of this type in the object tree for each table and it is labeled "SEQUENCE".
    */   
   public static final DatabaseObjectType SEQUENCE_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.SEQUENCE_TYPE_DBO"); //DatabaseObjectType.SEQUENCE_TYPE_DBO=Sequence Type
   
   
   /**
    * An object that generates uniques IDs for primary keys. E.G. an Oracle
    * sequence.
    */
   public final static DatabaseObjectType SEQUENCE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.sequence");

   /** TABLE. */
   public final static DatabaseObjectType TABLE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.table");

   /**
    * Database object type for a "Table Type" node in the object tree. Some examples
    * are "TABLE", "SYSTEM TABLE", "VIEW" etc.
    */
   public final static DatabaseObjectType TABLE_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.TABLE_TYPE_DBO"); //DatabaseObjectType.TABLE_TYPE_DBO=Table Type

   public static final DatabaseObjectType VIEW = createNewDatabaseObjectTypeI18n("DatabaseObjectType.view");

   /**
    * Database object type for a "Trigger Type" node in the object tree. There is
    * one node of this type in the object tree for each table and it is labeled "TRIGGER".
    */   
   public static final DatabaseObjectType TRIGGER_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.TRIGGER_TYPE_DBO"); //DatabaseObjectType.TRIGGER_TYPE_DBO=Trigger Type
   
   /** Trigger. */
   public final static DatabaseObjectType TRIGGER = createNewDatabaseObjectTypeI18n("DatabaseObjectType.catalog");

   /** User defined type. */
   public final static DatabaseObjectType UDT = createNewDatabaseObjectTypeI18n("DatabaseObjectType.udt");

   /**
    * Database object type for a "UDT Type" node in the object tree. There is only one
    * node of this type in the object tree and it says "UDT".
    */
   public final static DatabaseObjectType UDT_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.UDT_TYPE_DBO"); //DatabaseObjectType.UDT_TYPE_DBO=UDT Type

   /** User defined function. */
   public final static DatabaseObjectType UDF = createNewDatabaseObjectTypeI18n("DatabaseObjectType.udf");

   /**
    * Database object type for a "UDF Type" node in the object tree. There is only one
    * node of this type in the object tree and it says "UDF".
    */
   public final static DatabaseObjectType UDF_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.UDF_TYPE_DBO"); //DatabaseObjectType.UDF_TYPE_DBO=UDF Type

   /** A database user. */
   public final static DatabaseObjectType USER = createNewDatabaseObjectTypeI18n("DatabaseObjectType.user");

   /** Uniquely identifies this Object. */
   private final IIdentifier _id;

   /** Describes this object type. */
   private final String _name;
   private String _keyForSerializationReplace;

   /**
    * Default ctor.
    */
   private DatabaseObjectType(String name, String keyForSerializationReplace)
   {
      _keyForSerializationReplace = keyForSerializationReplace;
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

   public String toString()
   {
      return getName();
   }

   public static DatabaseObjectType createNewDatabaseObjectTypeI18n(String key)
   {
      return new DatabaseObjectType(key, s_stringMgr.getString(key));
   }

   public static DatabaseObjectType createNewDatabaseObjectType(String key)
   {
      return new DatabaseObjectType(key, key);
   }

}
