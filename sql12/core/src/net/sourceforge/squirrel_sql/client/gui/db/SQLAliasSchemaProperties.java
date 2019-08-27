package net.sourceforge.squirrel_sql.client.gui.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class SQLAliasSchemaProperties implements Serializable
{
   SQLAliasSchemaDetailProperties[] _schemaDetails = new SQLAliasSchemaDetailProperties[0];

   public static final int GLOBAL_STATE_LOAD_ALL_CACHE_NONE = 0;
   public static final int GLOBAL_STATE_LOAD_AND_CACHE_ALL = 1;
   public static final int GLOBAL_STATE_SPECIFY_SCHEMAS_BY_LIKE_STRING = 3;
   public static final int GLOBAL_STATE_SPECIFY_SCHEMAS = 2;

   private int _globalState = GLOBAL_STATE_LOAD_ALL_CACHE_NONE;
   private boolean _cacheSchemaIndependentMetaData;
   private String _byLikeStringInclude;
   private String _byLikeStringExclude;

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


   public boolean isCacheSchemaIndependentMetaData()
   {
      return _cacheSchemaIndependentMetaData;
   }

   public void setCacheSchemaIndependentMetaData(boolean b)
   {
      _cacheSchemaIndependentMetaData = b;
   }

   public boolean loadSchemaIndependentMetaData(SQLAliasSchemaProperties schemaPropsCacheIsBasedOn)
   {
      if(null == schemaPropsCacheIsBasedOn)
      {
         return true;
      }

      return !(schemaPropsCacheIsBasedOn._cacheSchemaIndependentMetaData && _cacheSchemaIndependentMetaData);

   }


   /**
    * @param schemaPropsCacheIsBasedOn null means that cache is not considered
    */
   public SchemaLoadInfo[] fetchSchemaLoadInfos(SQLAliasSchemaProperties schemaPropsCacheIsBasedOn,
                                              String[] tableTypes,
                                              String[] viewTypes)
   {
      if(null == schemaPropsCacheIsBasedOn)
      {
         return fetchSchemasToLoadDefault(tableTypes, viewTypes);
      }

      if(GLOBAL_STATE_LOAD_AND_CACHE_ALL == _globalState &&
         GLOBAL_STATE_LOAD_AND_CACHE_ALL == schemaPropsCacheIsBasedOn._globalState)
      {
         // See also loadSchemaNames()
         return new SchemaLoadInfo[0];
      }

      if(GLOBAL_STATE_SPECIFY_SCHEMAS == _globalState &&
         GLOBAL_STATE_SPECIFY_SCHEMAS == schemaPropsCacheIsBasedOn._globalState)
      {
         ArrayList<SchemaLoadInfo> ret = new ArrayList<SchemaLoadInfo>();

         for (int i = 0; i < _schemaDetails.length; i++)
         {
            SQLAliasSchemaDetailProperties cachedDetailProp =
               fetchMatchingDetail(_schemaDetails[i].getSchemaName(), schemaPropsCacheIsBasedOn._schemaDetails);

            SchemaLoadInfo buf = new SchemaLoadInfo(addStringArrays(tableTypes, viewTypes));
            buf.setSchemaName(_schemaDetails[i].getSchemaName());

            ArrayList<String> tableTypesToLoad = new ArrayList<String>();

            if(needsLoading(_schemaDetails[i].getTable(), null == cachedDetailProp ? null : cachedDetailProp.getTable()))
            {
               tableTypesToLoad.addAll(Arrays.asList(tableTypes));
            }

            if(needsLoading(_schemaDetails[i].getView(), null == cachedDetailProp ? null : cachedDetailProp.getView()))
            {
               tableTypesToLoad.addAll(Arrays.asList(viewTypes));
            }


            buf.setLoadProcedures(needsLoading(_schemaDetails[i].getProcedure(), null == cachedDetailProp ? null : cachedDetailProp.getProcedure()));

            if(0 < tableTypesToLoad.size() || buf.isLoadProcedures())
            {
               buf.setTableTypes(tableTypesToLoad.toArray(new String[tableTypesToLoad.size()]));
               ret.add(buf);
            }


            buf.setLoadUDTs(needsLoading(_schemaDetails[i].getUDT(), null == cachedDetailProp ? null : cachedDetailProp.getUDT()));

            if(0 < tableTypesToLoad.size() || buf.isLoadUDTs())
            {
               buf.setTableTypes(tableTypesToLoad.toArray(new String[tableTypesToLoad.size()]));
               ret.add(buf);
            }

         }

         return ret.toArray(new SchemaLoadInfo[ret.size()]);
      }

      return fetchSchemasToLoadDefault(tableTypes, viewTypes);
   }

   /**
    * Returns SchemaLoadInfos as if there was no cache.
    */
   private SchemaLoadInfo[] fetchSchemasToLoadDefault(String[] tableTypes, String[] viewTypes)
   {
      if(GLOBAL_STATE_LOAD_ALL_CACHE_NONE == _globalState ||
         GLOBAL_STATE_LOAD_AND_CACHE_ALL== _globalState)
      {
         // Means load all Schemas from database.
         return new SchemaLoadInfo[]{new SchemaLoadInfo(addStringArrays(tableTypes, viewTypes))};

      }
      else if(SQLAliasSchemaProperties.GLOBAL_STATE_SPECIFY_SCHEMAS == _globalState)
      {
         ArrayList<SchemaLoadInfo> schemaLoadInfos = new ArrayList<SchemaLoadInfo>();

         for (int i = 0; i < _schemaDetails.length; i++)
         {
            if(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD ==_schemaDetails[i].getTable() &&
               SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD ==_schemaDetails[i].getView() &&
               SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD ==_schemaDetails[i].getProcedure() &&
               SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD ==_schemaDetails[i].getUDT())
            {
               continue;
            }

            SchemaLoadInfo schemaLoadInfo = new SchemaLoadInfo(addStringArrays(tableTypes, viewTypes));
            schemaLoadInfo.setSchemaName(_schemaDetails[i].getSchemaName());
            schemaLoadInfo.setTableTypes(new String[0]);

            if(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD !=_schemaDetails[i].getTable())
            {
               schemaLoadInfo.setTableTypes(addStringArrays(schemaLoadInfo.getTableTypes(), tableTypes));
            }

            if(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD !=_schemaDetails[i].getView())
            {
               schemaLoadInfo.setTableTypes(addStringArrays(schemaLoadInfo.getTableTypes(), viewTypes));
            }

            if(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD !=_schemaDetails[i].getProcedure())
            {
               schemaLoadInfo.setLoadProcedures(true);
            }
            else
            {
               schemaLoadInfo.setLoadProcedures(false);
            }

            if(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD !=_schemaDetails[i].getUDT())
            {
               schemaLoadInfo.setLoadUDTs(true);
            }
            else
            {
               schemaLoadInfo.setLoadUDTs(false);
            }

            schemaLoadInfos.add(schemaLoadInfo);
         }

         return schemaLoadInfos.toArray(new SchemaLoadInfo[schemaLoadInfos.size()]);
      }
      else
      {
         throw new IllegalStateException("Undefined global state " + _globalState);
      }
   }


   private String[] addStringArrays(String[] tableTypes, String[] viewTypes)
   {
      ArrayList<String> ret = new ArrayList<String>();
      ret.addAll(Arrays.asList(tableTypes));
      ret.addAll(Arrays.asList(viewTypes));

      return ret.toArray(new String[ret.size()]);
   }

   private boolean needsLoading(int loadingID, Integer cachedLoadingID)
   {
      if(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD == loadingID)
      {
         // current Schema says don't load
         return false;
      }
      else if(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE == loadingID &&
              null != cachedLoadingID &&
              SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE == cachedLoadingID.intValue())
      {
         return false;
      }

      return true;
   }

   private SQLAliasSchemaDetailProperties fetchMatchingDetail(String schemaName, SQLAliasSchemaDetailProperties[] schemaDetails)
   {
      for (int i = 0; i < schemaDetails.length; i++)
      {
         if(schemaDetails[i].getSchemaName().equals(schemaName))
         {
            return schemaDetails[i];
         }
      }

      return null;

   }

   public SchemaTableTypeCombination[] fetchAllSchemaTableTypeCombinationsNotToBeCached(String[] tableTypes, String[] viewTypes)
   {
      ArrayList<SchemaTableTypeCombination> ret = 
          new ArrayList<SchemaTableTypeCombination>();

      for (int i = 0; i < _schemaDetails.length; i++)
      {
         if(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE != _schemaDetails[i].getTable())
         {
            SchemaTableTypeCombination buf = new SchemaTableTypeCombination();
            buf.schemaName = _schemaDetails[i].getSchemaName();
            buf.types = tableTypes;
            ret.add(buf);
         }

         if(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE != _schemaDetails[i].getView())
         {
            SchemaTableTypeCombination buf = new SchemaTableTypeCombination();
            buf.schemaName = _schemaDetails[i].getSchemaName();
            buf.types = viewTypes;
            ret.add(buf);
         }
      }

      return ret.toArray(new SchemaTableTypeCombination[ret.size()]);
   }

   public String[] fetchAllSchemaProceduresNotToBeCached()
   {
      ArrayList<String> ret = new ArrayList<String>();

      for (int i = 0; i < _schemaDetails.length; i++)
      {
         if(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE != _schemaDetails[i].getProcedure())
         {
            ret.add(_schemaDetails[i].getSchemaName());
         }
      }

      return ret.toArray(new String[ret.size()]);
   }

   public String[] fetchAllSchemaUDTsNotToBeCached()
   {
      ArrayList<String> ret = new ArrayList<String>();

      for (int i = 0; i < _schemaDetails.length; i++)
      {
         if(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE != _schemaDetails[i].getUDT())
         {
            ret.add(_schemaDetails[i].getSchemaName());
         }
      }

      return ret.toArray(new String[ret.size()]);
   }

   public boolean fetchExpectsSomeCachedData()
   {
      if(_cacheSchemaIndependentMetaData || GLOBAL_STATE_LOAD_AND_CACHE_ALL == _globalState)
      {
         return true;
      }

      if(GLOBAL_STATE_SPECIFY_SCHEMAS == _globalState)
      {
         // Note: If we are here _cacheSchemaIndependentMetaData must be false

         for (int i = 0; i < _schemaDetails.length; i++)
         {
            if(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE ==_schemaDetails[i].getTable() ||
               SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE ==_schemaDetails[i].getView() ||
               SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE ==_schemaDetails[i].getProcedure() ||
               SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE ==_schemaDetails[i].getUDT())
            {
               return true;
            }
         }
      }

      return false;
   }

   public boolean loadSchemaNames(SQLAliasSchemaProperties schemaPropsCacheIsBasedOn)
   {
      if(GLOBAL_STATE_LOAD_AND_CACHE_ALL == _globalState &&
         GLOBAL_STATE_LOAD_AND_CACHE_ALL == schemaPropsCacheIsBasedOn._globalState)
      {
         return true;
      }
      else
      {
         return false;
      }
   }


   public SchemaNameLoadInfo fetchSchemaNameLoadInfo(SQLAliasSchemaProperties schemaPropsCacheIsBasedOn)
   {
      SchemaNameLoadInfo ret = new SchemaNameLoadInfo();

      if(GLOBAL_STATE_LOAD_AND_CACHE_ALL == _globalState &&
         null != schemaPropsCacheIsBasedOn &&
         GLOBAL_STATE_LOAD_AND_CACHE_ALL == schemaPropsCacheIsBasedOn._globalState)
      {
         ret.state = SchemaNameLoadInfo.STATE_DONT_REFERESH_SCHEMA_NAMES;
      }
      else if(GLOBAL_STATE_SPECIFY_SCHEMAS == _globalState)
      {
         ArrayList<String> schemaNames = new ArrayList<String>();

         ret.state = SchemaNameLoadInfo.STATE_USES_PROVIDED_SCHEMA_NAMES;

         for (int i = 0; i < _schemaDetails.length; i++)
         {
            if(SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD ==_schemaDetails[i].getTable() &&
               SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD ==_schemaDetails[i].getView() &&
               SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD ==_schemaDetails[i].getProcedure())
            {
               continue;
            }

            schemaNames.add(_schemaDetails[i].getSchemaName());
         }

         ret.schemaNames = schemaNames.toArray(new String[schemaNames.size()]);
      }
      else
      {
         ret.state = SchemaNameLoadInfo.STATE_REFERESH_SCHEMA_NAMES_FROM_DB;
      }

      return ret;
   }

   public String getByLikeStringInclude()
   {
      return _byLikeStringInclude;
   }

   public void setByLikeStringInclude(String byLikeStringInclude)
   {
      _byLikeStringInclude = byLikeStringInclude;
   }

   public String getByLikeStringExclude()
   {
      return _byLikeStringExclude;
   }

   public void setByLikeStringExclude(String byLikeStringExclude)
   {
      _byLikeStringExclude = byLikeStringExclude;
   }
}
