package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaDetailProperties;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

class SchemaTableCboItem
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SchemaTableCboItem.class);


   static final SchemaTableCboItem[] items = new SchemaTableCboItem[]
   {
      new SchemaTableCboItem(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_DONT_CACHE),
      new SchemaTableCboItem(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE),
      new SchemaTableCboItem(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD),
   };

   static SchemaTableCboItem getItemForID(int schemaID)
   {
      switch(schemaID)
      {
         case SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_DONT_CACHE:
            return SchemaTableCboItem.items[0];
         case SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE:
            return SchemaTableCboItem.items[1];
         case SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD:
            return SchemaTableCboItem.items[2];
         default:
            throw new IllegalArgumentException("Unknown schemaID " + schemaID);
      }
   }



   private String _toString;

   private int _id;

   private SchemaTableCboItem(int _schemaID)
   {
      this._id = _schemaID;
      switch(_schemaID)
      {
         case SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_DONT_CACHE:
            // i18n[SchemaTableCboItem.schemaID.loadButDontCache=Load but don't cache]
            _toString = s_stringMgr.getString("SchemaTableCboItem.schemaID.loadButDontCache");
            break;
         case SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE:
            // i18n[SchemaTableCboItem.schemaID.loadAndCache=Load and cache]
            _toString = s_stringMgr.getString("SchemaTableCboItem.schemaID.loadAndCache");
            break;
         case SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD:
            // i18n[SchemaTableCboItem.schemaID.dontLoad=Don't load]
            _toString = s_stringMgr.getString("SchemaTableCboItem.schemaID.dontLoad");
            break;
      }
   }

   public String toString()
   {
      return _toString;
   }

   public int getID()
   {
      return _id;
   }



}
