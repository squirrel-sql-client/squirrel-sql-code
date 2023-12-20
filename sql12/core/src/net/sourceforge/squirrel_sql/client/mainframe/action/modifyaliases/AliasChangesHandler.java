package net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaDetailProperties;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaProperties;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class AliasChangesHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasChangesHandler.class);
   public static final String INDENT = "   ";

   private StringBuilder _changeReport = new StringBuilder();

   private String _indent = "";

   private List<AliasChange> _aliasChanges = new ArrayList<>();


   public AliasChangesHandler()
   {
   }

   public boolean isEmpty()
   {
      return 0 == _changeReport.length();
   }

   public String getReport()
   {
      return _changeReport.toString();
   }

   public void indentInnerBean()
   {
      // As SQLAlias does not have inner-inner beans that's all we need.
      _indent = INDENT;
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
         writeSchemaPropertyCollectionChangeReport(sqlAliasPropType, previousAliasPropValue, editedAliasPropValue);
      }
      else if(sqlAliasPropType == SQLAliasPropType.driverProp_useDriverProperties)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.driverProp.prefix")).append(" ");

         String changeMsg = s_stringMgr.getString("AliasChangesHandler.changeFromTo",
               sqlAliasPropType.getI18nString(),
               previousAliasPropValue, editedAliasPropValue);

         _changeReport.append(changeMsg).append('\n');
      }
      else if(sqlAliasPropType == SQLAliasPropType.driverProp_driverPropertyCollection)
      {
         writeDriverPropertyCollectionChangeReport((SQLDriverPropertyCollection) previousAliasPropValue, (SQLDriverPropertyCollection) editedAliasPropValue);
      }
      else if(sqlAliasPropType.isColorProp())
      {
         writeColorPropertyChangeReport(sqlAliasPropType, previousAliasPropValue, editedAliasPropValue);
      }
      else
      {
         String changeMsg = s_stringMgr.getString("AliasChangesHandler.changeFromTo",
               sqlAliasPropType.getI18nString(),
               previousAliasPropValue, editedAliasPropValue);

         _changeReport.append(changeMsg).append('\n');
      }

   }

   private void writeSchemaPropertyCollectionChangeReport(SQLAliasPropType sqlAliasPropType, Object previousAliasPropValue, Object editedAliasPropValue)
   {
      if (sqlAliasPropType == SQLAliasPropType.schemaProp_globalState)
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.schemaProp.prefix")).append(" ");

         String changeMsg = s_stringMgr.getString("AliasChangesHandler.globalState.changeFromTo",
                                                  getGlobalSchemaLoadingState((Integer) previousAliasPropValue),
                                                  getGlobalSchemaLoadingState((Integer) editedAliasPropValue));

         _changeReport.append(changeMsg).append('\n');
      }
      else if (sqlAliasPropType == SQLAliasPropType.schemaProp_schemaDetails)
      {
         SQLAliasSchemaDetailProperties[] previousSchemaDetails = (SQLAliasSchemaDetailProperties[]) previousAliasPropValue;
         SQLAliasSchemaDetailProperties[] editedSchemaDetails = (SQLAliasSchemaDetailProperties[]) editedAliasPropValue;

         for (SQLAliasSchemaDetailProperties editedSchemaDetail : editedSchemaDetails)
         {
            Optional<SQLAliasSchemaDetailProperties> prevMatch =
                  Stream.of(previousSchemaDetails).filter(sd -> StringUtils.equalsIgnoreCase(sd.getSchemaName(), editedSchemaDetail.getSchemaName())).findFirst();

            writeSchemaDetailChangeReportWhenChanged(prevMatch.orElse(null), editedSchemaDetail);
         }
      }
      else
      {
         _changeReport.append(s_stringMgr.getString("AliasChangesHandler.schemaProp.prefix")).append(" ");

         String changeMsg = s_stringMgr.getString("AliasChangesHandler.changeFromTo",
                                                  sqlAliasPropType.getI18nString(),
                                                  previousAliasPropValue, editedAliasPropValue);

         _changeReport.append(changeMsg).append('\n');
      }
   }

   private void writeSchemaDetailChangeReportWhenChanged(SQLAliasSchemaDetailProperties previousSchemaDetail, SQLAliasSchemaDetailProperties editedSchemaDetail)
   {
      if(null == previousSchemaDetail || previousSchemaDetail.getTable() != editedSchemaDetail.getTable())
      {
         _changeReport.append(INDENT).append(s_stringMgr.getString("AliasChangesHandler.schemaProp.prefix")).append(" ");
         String msg = s_stringMgr.getString(
               "AliasChangesHandler.schema.detail.table.changedFromTo",
               editedSchemaDetail.getSchemaName(),
               null == previousSchemaDetail ? "<null>" : getSchemDetailString(previousSchemaDetail.getTable()),
               getSchemDetailString(editedSchemaDetail.getTable()));

         _changeReport.append(msg).append('\n');
      }
      if(null == previousSchemaDetail || previousSchemaDetail.getView() != editedSchemaDetail.getView())
      {
         _changeReport.append(INDENT).append(s_stringMgr.getString("AliasChangesHandler.schemaProp.prefix")).append(" ");
         String msg = s_stringMgr.getString(
               "AliasChangesHandler.schema.detail.view.changedFromTo",
               editedSchemaDetail.getSchemaName(),
               null == previousSchemaDetail ? "<null>" : getSchemDetailString(previousSchemaDetail.getTable()),
               getSchemDetailString(editedSchemaDetail.getTable()));

         _changeReport.append(msg).append('\n');
      }
      if(null == previousSchemaDetail || previousSchemaDetail.getProcedure() != editedSchemaDetail.getProcedure())
      {
         _changeReport.append(INDENT).append(s_stringMgr.getString("AliasChangesHandler.schemaProp.prefix")).append(" ");
         String msg = s_stringMgr.getString(
               "AliasChangesHandler.schema.detail.procedure.changedFromTo",
               editedSchemaDetail.getSchemaName(),
               null == previousSchemaDetail ? "<null>" : getSchemDetailString(previousSchemaDetail.getTable()),
               getSchemDetailString(editedSchemaDetail.getTable()));

         _changeReport.append(msg).append('\n');
      }
      if(null == previousSchemaDetail || previousSchemaDetail.getUDT() != editedSchemaDetail.getUDT())
      {
         _changeReport.append(INDENT).append(s_stringMgr.getString("AliasChangesHandler.schemaProp.prefix")).append(" ");
         String msg = s_stringMgr.getString(
               "AliasChangesHandler.schema.detail.udt.changedFromTo",
               editedSchemaDetail.getSchemaName(),
               null == previousSchemaDetail ? "<null>" : getSchemDetailString(previousSchemaDetail.getTable()),
               getSchemDetailString(editedSchemaDetail.getTable()));

         _changeReport.append(msg).append('\n');
      }
   }

   private String getSchemDetailString(int schemaLoadingId)
   {
      String schemaLoadingDetailState;
      switch (schemaLoadingId)
      {
         case SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_DONT_CACHE:
            schemaLoadingDetailState = s_stringMgr.getString("AliasChangesHandler.SCHEMA_LOADING_ID_LOAD_DONT_CACHE");
            break;
         case SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE:
            schemaLoadingDetailState = s_stringMgr.getString("AliasChangesHandler.SCHEMA_LOADING_ID_LOAD_AND_CACHE");
            break;
         case SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD:
            schemaLoadingDetailState = s_stringMgr.getString("AliasChangesHandler.SCHEMA_LOADING_ID_DONT_LOAD");
            break;
         default:
            throw new IllegalArgumentException("Unknown Global schema detail loading and caching state");
      }
      return schemaLoadingDetailState;
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

   private void writeDriverPropertyCollectionChangeReport(SQLDriverPropertyCollection previousDriverProps, SQLDriverPropertyCollection editedDriverProps)
   {
      boolean propertyChangeReportInitDone = false;

      for (SQLDriverProperty editedProp : editedDriverProps.getDriverProperties())
      {
         SQLDriverProperty previousProp = editedDriverProps.getDriverPropertyByName(editedProp.getName());

         if(null == previousProp)
         {
            continue;
         }

         if(editedProp.isSpecified() != previousProp.isSpecified())
         {
            if (false == propertyChangeReportInitDone)
            {
               _changeReport.append(s_stringMgr.getString("AliasChangesHandler.driverProp.prefix")).append(" ");
               _changeReport.append('\n');
               propertyChangeReportInitDone = true;
            }
            _changeReport.append(s_stringMgr.getString("AliasChangesHandler.driverProp.specified.changeFromTo", previousProp.getName(), previousProp.isSpecified(), editedProp.isSpecified()));
            _changeReport.append('\n');
         }
         if(false == StringUtils.equals(editedProp.getValue(), previousProp.getValue()))
         {
            if (false == propertyChangeReportInitDone)
            {
               _changeReport.append(s_stringMgr.getString("AliasChangesHandler.driverProp.prefix")).append(" ");
               _changeReport.append('\n');
               propertyChangeReportInitDone = true;
            }
            _changeReport.append(s_stringMgr.getString("AliasChangesHandler.driverProp.value.changeFromTo", previousProp.getName(), previousProp.getValue(), editedProp.getValue()));
            _changeReport.append('\n');
         }
      }
   }
}
