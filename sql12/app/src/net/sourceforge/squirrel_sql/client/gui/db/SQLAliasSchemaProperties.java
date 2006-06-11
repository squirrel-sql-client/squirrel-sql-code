package net.sourceforge.squirrel_sql.client.gui.db;

public class SQLAliasSchemaProperties
{
   SQLAliasSchemaDetailProperties[] _schemaDetails = new SQLAliasSchemaDetailProperties[0];

   public static final int GLOBAL_STATE_LOAD_ALL_CACHE_NONE = 0;
   public static final int GLOBAL_STATE_LOAD_AND_CACHE_ALL = 1;
   public static final int GLOBAL_STATE_SPECIFY_SCHEMAS = 2;

   private int _globalState = GLOBAL_STATE_LOAD_ALL_CACHE_NONE;

   public SQLAliasSchemaDetailProperties[] getSchemaDetails()
   {
      return _schemaDetails;
   }

   public void setSchemaDetails(SQLAliasSchemaDetailProperties[] schemaDetails)
   {
      _schemaDetails = schemaDetails;
   }


   public int getGlobalState()
   {
      return _globalState;
   }

   public void setGlobalState(int globalState)
   {
      this._globalState = globalState;
   }


}
