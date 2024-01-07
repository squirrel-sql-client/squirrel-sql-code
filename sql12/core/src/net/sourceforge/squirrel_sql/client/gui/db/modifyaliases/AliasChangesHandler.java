package net.sourceforge.squirrel_sql.client.gui.db.modifyaliases;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaDetailProperties;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaProperties;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.*;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public class AliasChangesHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasChangesHandler.class);
   public static final String I18N_ALIAS_CHANGES_HANDLER_CHANGE_FROM_TO = "AliasChangesHandler.changeFromTo";

   private ChangeReport _changeReport = new ChangeReport();

   private List<AliasChange> _aliasChanges = new ArrayList<>();
   private boolean _schemaTableWasCleared;


   public AliasChangesHandler()
   {
   }

   public boolean isEmpty()
   {
      return 0 == _changeReport.length();
   }

   public ChangeReport getChangeReport()
   {
      return _changeReport;
   }


   public void addChange(SQLAliasPropType sqlAliasPropType, PropertyDescriptor pd, Object uneditedAliasPropValue, Object editedAliasPropValue)
   {
      _aliasChanges.add(new AliasChange(pd, sqlAliasPropType, editedAliasPropValue));

      _changeReport.append(sqlAliasPropType.isNested() ? AliasChangesUtil.INDENT : "");

      if (sqlAliasPropType.isSchemaProp())
      {
         writeSchemaPropertyChangeReport(sqlAliasPropType, uneditedAliasPropValue, editedAliasPropValue);
      }
      else if(sqlAliasPropType.isDriverProp())
      {
         writeDriverPropertyChangeReport(sqlAliasPropType, uneditedAliasPropValue, editedAliasPropValue);
      }
      else if(sqlAliasPropType.isColorProp())
      {
         writeColorPropertyChangeReport(sqlAliasPropType, uneditedAliasPropValue, editedAliasPropValue);
      }
      else if(sqlAliasPropType.isConnectionProp())
      {
         writeConnectionPropertyChangeReport(sqlAliasPropType, uneditedAliasPropValue, editedAliasPropValue);
      }
      else
      {
         writeAliasPropertyChangeReport(sqlAliasPropType, uneditedAliasPropValue, editedAliasPropValue);
      }
   }

   private void writeAliasPropertyChangeReport(SQLAliasPropType sqlAliasPropType, Object uneditedAliasPropValue, Object editedAliasPropValue)
   {
      _changeReport.append(s_stringMgr.getString("AliasChangesHandler.alias.prefix")).append(" ");

      String changeMsg;
      if (sqlAliasPropType == SQLAliasPropType.driverIdentifier)
      {
         changeMsg = s_stringMgr.getString(I18N_ALIAS_CHANGES_HANDLER_CHANGE_FROM_TO,
                                           sqlAliasPropType.getI18nString(),
                                           getDriverNameByIdentifier((IIdentifier)uneditedAliasPropValue), getDriverNameByIdentifier((IIdentifier)editedAliasPropValue));
      }
      else
      {
         changeMsg = s_stringMgr.getString(I18N_ALIAS_CHANGES_HANDLER_CHANGE_FROM_TO,
                                           sqlAliasPropType.getI18nString(),
                                           uneditedAliasPropValue, editedAliasPropValue);
      }

      _changeReport.append(changeMsg).append('\n');
   }

   private Object getDriverNameByIdentifier(IIdentifier driverIdentifier)
   {
      if(null == driverIdentifier)
      {
         return "<null>";
      }

      return Main.getApplication().getAliasesAndDriversManager().getDriver(driverIdentifier).getName();
   }

   private void writeConnectionPropertyChangeReport(SQLAliasPropType sqlAliasPropType, Object uneditedAliasPropValue, Object editedAliasPropValue)
   {

      String changeMsg;
      if (sqlAliasPropType == SQLAliasPropType.connectionProp_keepAliveSqlStatement)
      {
         _changeReport.append(s_stringMgr.getString(AliasChangesUtil.I18N_ALIAS_CHANGES_UTIL_DRIVER_PROP_PREFIX)).append(" ");
         changeMsg = s_stringMgr.getString(I18N_ALIAS_CHANGES_HANDLER_CHANGE_FROM_TO,
                                           s_stringMgr.getString("AliasChangesHandler.connectionProp.keepAliveSql"),
                                           uneditedAliasPropValue, editedAliasPropValue);
      }
      else
      {
         _changeReport.append(s_stringMgr.getString(AliasChangesUtil.I18N_ALIAS_CHANGES_UTIL_DRIVER_PROP_PREFIX)).append(" ");
         changeMsg = s_stringMgr.getString(I18N_ALIAS_CHANGES_HANDLER_CHANGE_FROM_TO,
                                           sqlAliasPropType.getI18nString(),
                                           uneditedAliasPropValue, editedAliasPropValue);
      }

      _changeReport.append(changeMsg).append('\n');
   }

   private void writeDriverPropertyChangeReport(SQLAliasPropType sqlAliasPropType, Object previousAliasPropValue, Object editedAliasPropValue)
   {
      if(sqlAliasPropType == SQLAliasPropType.driverProp_useDriverProperties)
      {
         _changeReport.append(s_stringMgr.getString(AliasChangesUtil.I18N_ALIAS_CHANGES_UTIL_DRIVER_PROP_PREFIX)).append(" ");

         String changeMsg = s_stringMgr.getString(I18N_ALIAS_CHANGES_HANDLER_CHANGE_FROM_TO,
                                                  sqlAliasPropType.getI18nString(),
                                                  previousAliasPropValue, editedAliasPropValue);

         _changeReport.append(changeMsg).append('\n');
      }
      else if(sqlAliasPropType == SQLAliasPropType.driverProp_driverPropertyCollection)
      {
         AliasChangesUtil.compareSQLDriverPropertyCollection((SQLDriverPropertyCollection) previousAliasPropValue, (SQLDriverPropertyCollection) editedAliasPropValue, _changeReport);
      }
   }

   private void writeSchemaPropertyChangeReport(SQLAliasPropType sqlAliasPropType, Object uneditedAliasPropValue, Object editedAliasPropValue)
   {
      if (sqlAliasPropType == SQLAliasPropType.schemaProp_globalState)
      {
         _changeReport.append(s_stringMgr.getString(AliasChangesUtil.I18N_ALIAS_CHANGES_UTIL_SCHEMA_PROP_PREFIX)).append(" ");

         String changeMsg = s_stringMgr.getString("AliasChangesHandler.globalState.changeFromTo",
                                                  getGlobalSchemaLoadingState((Integer) uneditedAliasPropValue),
                                                  getGlobalSchemaLoadingState((Integer) editedAliasPropValue));

         _changeReport.append(changeMsg).append('\n');
      }
      else if (sqlAliasPropType == SQLAliasPropType.schemaProp_schemaDetails)
      {
         SQLAliasSchemaDetailProperties[] uneditedSchemaDetails = (SQLAliasSchemaDetailProperties[]) uneditedAliasPropValue;
         SQLAliasSchemaDetailProperties[] editedSchemaDetails = (SQLAliasSchemaDetailProperties[]) editedAliasPropValue;

         AliasChangesUtil.compareSchemaDetailProperties(editedSchemaDetails, uneditedSchemaDetails, _changeReport);
      }
      else if (sqlAliasPropType == SQLAliasPropType.schemaProp_schemaTableWasCleared_transientForMultiAliasModificationOnly)
      {
         if( (Boolean) editedAliasPropValue )
         {
            _schemaTableWasCleared = true;
            _changeReport.append(s_stringMgr.getString(AliasChangesUtil.I18N_ALIAS_CHANGES_UTIL_SCHEMA_PROP_PREFIX)).append(" ");
            _changeReport.append(s_stringMgr.getString("AliasChangesHandler.aliasBackground.color.prop.schemaTableWasCleared")).append('\n');
         }
      }
      else
      {
         _changeReport.append(s_stringMgr.getString(AliasChangesUtil.I18N_ALIAS_CHANGES_UTIL_SCHEMA_PROP_PREFIX)).append(" ");

         String changeMsg = s_stringMgr.getString(I18N_ALIAS_CHANGES_HANDLER_CHANGE_FROM_TO,
                                                  sqlAliasPropType.getI18nString(),
                                                  uneditedAliasPropValue, editedAliasPropValue);

         _changeReport.append(changeMsg).append('\n');
      }
   }

   private static String getGlobalSchemaLoadingState(Integer globalSchemaLoadingState)
   {
      String previousGlobalState;
      switch (globalSchemaLoadingState)
      {
         case SQLAliasSchemaProperties.GLOBAL_STATE_LOAD_ALL_CACHE_NONE:
            previousGlobalState = s_stringMgr.getString("AliasChangesHandler.GLOBAL_STATE_LOAD_ALL_CACHE_NONE");
            break;
         case SQLAliasSchemaProperties.GLOBAL_STATE_LOAD_AND_CACHE_ALL:
            previousGlobalState = s_stringMgr.getString("AliasChangesHandler.GLOBAL_STATE_LOAD_AND_CACHE_ALL");
            break;
         case SQLAliasSchemaProperties.GLOBAL_STATE_SPECIFY_SCHEMAS_BY_LIKE_STRING:
            previousGlobalState = s_stringMgr.getString("AliasChangesHandler.GLOBAL_STATE_SPECIFY_SCHEMAS_BY_LIKE_STRING");
            break;
         case SQLAliasSchemaProperties.GLOBAL_STATE_SPECIFY_SCHEMAS:
            previousGlobalState = s_stringMgr.getString("AliasChangesHandler.GLOBAL_STATE_SPECIFY_SCHEMAS");
            break;
         default:
            throw new IllegalArgumentException("Unknown Global schema loading and caching state");
      }
      return previousGlobalState;
   }

   private void writeColorPropertyChangeReport(SQLAliasPropType sqlAliasPropType, Object uneditedAliasPropValue, Object editedAliasPropValue)
   {
      _changeReport.append(s_stringMgr.getString("AliasChangesHandler.color.prop.prefix")).append(" ");

      // Toolbar
      if(sqlAliasPropType == SQLAliasPropType.colorProp_overrideToolbarBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.toolbarBackground.color.prop.override.changeFromTo", uneditedAliasPropValue, editedAliasPropValue));
      }
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_toolbarBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.toolbarBackground.color.prop.changeFromTo", new Color((Integer) uneditedAliasPropValue), new Color((Integer) editedAliasPropValue)));
      }
      // ObjectTree
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_overrideObjectTreeBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.objectTreeBackground.color.prop.override.changeFromTo", uneditedAliasPropValue, editedAliasPropValue));
      }
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_objectTreeBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.objectTreeBackground.color.prop.changeFromTo", new Color((Integer) uneditedAliasPropValue), new Color((Integer) editedAliasPropValue)));
      }
      // StatusBar
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_overrideStatusBarBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.statusBarBackground.color.prop.override.changeFromTo", uneditedAliasPropValue, editedAliasPropValue));
      }
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_statusBarBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.statusBarBackground.color.prop.changeFromTo", new Color((Integer) uneditedAliasPropValue), new Color((Integer) editedAliasPropValue)));
      }
      // Alias
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_overrideAliasBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.aliasBackground.color.prop.override.changeFromTo", uneditedAliasPropValue, editedAliasPropValue));
      }
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_aliasBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.aliasBackground.color.prop.changeFromTo", new Color((Integer) uneditedAliasPropValue), new Color((Integer) editedAliasPropValue)));
      }
      _changeReport.append('\n');
   }

   public void applyChanges(SQLAlias newSelectedAlias)
   {
      _aliasChanges.forEach(aliasChange -> aliasChange.applyChange(newSelectedAlias));

      if(_schemaTableWasCleared)
      {
         newSelectedAlias.getSchemaProperties().setSchemaDetails(new SQLAliasSchemaDetailProperties[0]);
      }
   }
}
