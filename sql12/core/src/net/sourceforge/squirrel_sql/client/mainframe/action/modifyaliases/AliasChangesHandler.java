package net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases;

import net.sourceforge.squirrel_sql.client.Main;
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

   private String _indent = "";

   private List<AliasChange> _aliasChanges = new ArrayList<>();


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

   public void indentInnerBean()
   {
      // As SQLAlias does not have inner-inner beans that's all we need.
      _indent = AliasChangesUtil.INDENT;
   }

   public void unindentInnerBean()
   {
      // As SQLAlias does not have inner-inner beans that's all we need.
      _indent = "";
   }


   public void addChange(SQLAliasPropType sqlAliasPropType, PropertyDescriptor pd, Object previousAliasPropValue, Object editedAliasPropValue)
   {
      _aliasChanges.add(new AliasChange(sqlAliasPropType, pd, editedAliasPropValue));

      _changeReport.append(_indent);

      if (sqlAliasPropType.isSchemaProp())
      {
         writeSchemaPropertyChangeReport(sqlAliasPropType, previousAliasPropValue, editedAliasPropValue);
      }
      else if(sqlAliasPropType.isDriverProp())
      {
         writeDriverPropertyChangeReport(sqlAliasPropType, previousAliasPropValue, editedAliasPropValue);
      }
      else if(sqlAliasPropType.isColorProp())
      {
         writeColorPropertyChangeReport(sqlAliasPropType, previousAliasPropValue, editedAliasPropValue);
      }
      else if(sqlAliasPropType.isConnectionProp())
      {
         writeConnectionPropertyChangeReport(sqlAliasPropType, previousAliasPropValue, editedAliasPropValue);
      }
      else
      {
         writeAliasPropertyChangeReport(sqlAliasPropType, previousAliasPropValue, editedAliasPropValue);
      }
   }

   private void writeAliasPropertyChangeReport(SQLAliasPropType sqlAliasPropType, Object previousAliasPropValue, Object editedAliasPropValue)
   {
      _changeReport.append(s_stringMgr.getString("AliasChangesHandler.alias.prefix")).append(" ");

      String changeMsg;
      if (sqlAliasPropType == SQLAliasPropType.driverIdentifier)
      {
         changeMsg = s_stringMgr.getString(I18N_ALIAS_CHANGES_HANDLER_CHANGE_FROM_TO,
                                           sqlAliasPropType.getI18nString(),
                                           getDriverNameByIdentifier((IIdentifier)previousAliasPropValue), getDriverNameByIdentifier((IIdentifier)editedAliasPropValue));
      }
      else
      {
         changeMsg = s_stringMgr.getString(I18N_ALIAS_CHANGES_HANDLER_CHANGE_FROM_TO,
                                           sqlAliasPropType.getI18nString(),
                                           previousAliasPropValue, editedAliasPropValue);
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

   private void writeConnectionPropertyChangeReport(SQLAliasPropType sqlAliasPropType, Object previousAliasPropValue, Object editedAliasPropValue)
   {

      String changeMsg;
      if (sqlAliasPropType == SQLAliasPropType.connectionProp_keepAliveSqlStatement)
      {
         _changeReport.append(s_stringMgr.getString(AliasChangesUtil.I18N_ALIAS_CHANGES_UTIL_DRIVER_PROP_PREFIX)).append(" ");
         changeMsg = s_stringMgr.getString(I18N_ALIAS_CHANGES_HANDLER_CHANGE_FROM_TO,
                                           s_stringMgr.getString("AliasChangesHandler.connectionProp.keepAliveSql"),
                                           previousAliasPropValue, editedAliasPropValue);
      }
      else
      {
         _changeReport.append(s_stringMgr.getString(AliasChangesUtil.I18N_ALIAS_CHANGES_UTIL_DRIVER_PROP_PREFIX)).append(" ");
         changeMsg = s_stringMgr.getString(I18N_ALIAS_CHANGES_HANDLER_CHANGE_FROM_TO,
                                           sqlAliasPropType.getI18nString(),
                                           previousAliasPropValue, editedAliasPropValue);
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

   private void writeSchemaPropertyChangeReport(SQLAliasPropType sqlAliasPropType, Object previousAliasPropValue, Object editedAliasPropValue)
   {
      if (sqlAliasPropType == SQLAliasPropType.schemaProp_globalState)
      {
         _changeReport.append(s_stringMgr.getString(AliasChangesUtil.I18N_ALIAS_CHANGES_UTIL_SCHEMA_PROP_PREFIX)).append(" ");

         String changeMsg = s_stringMgr.getString("AliasChangesHandler.globalState.changeFromTo",
                                                  getGlobalSchemaLoadingState((Integer) previousAliasPropValue),
                                                  getGlobalSchemaLoadingState((Integer) editedAliasPropValue));

         _changeReport.append(changeMsg).append('\n');
      }
      else if (sqlAliasPropType == SQLAliasPropType.schemaProp_schemaDetails)
      {
         SQLAliasSchemaDetailProperties[] previousSchemaDetails = (SQLAliasSchemaDetailProperties[]) previousAliasPropValue;
         SQLAliasSchemaDetailProperties[] editedSchemaDetails = (SQLAliasSchemaDetailProperties[]) editedAliasPropValue;

         AliasChangesUtil.compareSchemaDetailProperties(editedSchemaDetails, previousSchemaDetails, _changeReport);
      }
      else
      {
         _changeReport.append(s_stringMgr.getString(AliasChangesUtil.I18N_ALIAS_CHANGES_UTIL_SCHEMA_PROP_PREFIX)).append(" ");

         String changeMsg = s_stringMgr.getString(I18N_ALIAS_CHANGES_HANDLER_CHANGE_FROM_TO,
                                                  sqlAliasPropType.getI18nString(),
                                                  previousAliasPropValue, editedAliasPropValue);

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

   private void writeColorPropertyChangeReport(SQLAliasPropType sqlAliasPropType, Object previousAliasPropValue, Object editedAliasPropValue)
   {
      _changeReport.append(s_stringMgr.getString("AliasChangesHandler.color.prop.prefix")).append(" ");

      // Toolbar
      if(sqlAliasPropType == SQLAliasPropType.colorProp_overrideToolbarBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.toolbarBackground.color.prop.override.changeFromTo", previousAliasPropValue, editedAliasPropValue));
      }
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_toolbarBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.toolbarBackground.color.prop.changeFromTo", new Color((Integer) previousAliasPropValue), new Color((Integer) editedAliasPropValue)));
      }
      // ObjectTree
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_overrideObjectTreeBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.objectTreeBackground.color.prop.override.changeFromTo", previousAliasPropValue, editedAliasPropValue));
      }
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_objectTreeBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.objectTreeBackground.color.prop.changeFromTo", new Color((Integer) previousAliasPropValue), new Color((Integer) editedAliasPropValue)));
      }
      // StatusBar
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_overrideStatusBarBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.statusBarBackground.color.prop.override.changeFromTo", previousAliasPropValue, editedAliasPropValue));
      }
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_statusBarBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.statusBarBackground.color.prop.changeFromTo", new Color((Integer) previousAliasPropValue), new Color((Integer) editedAliasPropValue)));
      }
      // Alias
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_overrideAliasBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.aliasBackground.color.prop.override.changeFromTo", previousAliasPropValue, editedAliasPropValue));
      }
      else if(sqlAliasPropType == SQLAliasPropType.colorProp_aliasBackgroundColor)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.aliasBackground.color.prop.changeFromTo", new Color((Integer) previousAliasPropValue), new Color((Integer) editedAliasPropValue)));
      }
      _changeReport.append('\n');
   }
}
