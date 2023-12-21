package net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasSchemaDetailProperties;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

public class AliasChangesUtil
{
   public static final String INDENT = "   ";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasChangesUtil.class);
   public static final String I18N_ALIAS_CHANGES_UTIL_SCHEMA_PROP_PREFIX = "AliasChangesUtil.schemaProp.prefix";
   public static final String I18N_ALIAS_CHANGES_UTIL_DRIVER_PROP_PREFIX = "AliasChangesUtil.driverProp.prefix";


   public static void compareSchemaDetailProperties(SQLAliasSchemaDetailProperties[] editedSchemaDetails, SQLAliasSchemaDetailProperties[] previousSchemaDetails, ChangeReport changeReport)
   {
      for (SQLAliasSchemaDetailProperties editedSchemaDetail : editedSchemaDetails)
      {
         Optional<SQLAliasSchemaDetailProperties> prevMatch =
               Stream.of(previousSchemaDetails).filter(sd -> StringUtils.equalsIgnoreCase(sd.getSchemaName(), editedSchemaDetail.getSchemaName())).findFirst();

         writeSchemaDetailChangeReportWhenChanged(prevMatch.orElse(null), editedSchemaDetail, changeReport);
      }
   }

   private static void writeSchemaDetailChangeReportWhenChanged(SQLAliasSchemaDetailProperties previousSchemaDetail, SQLAliasSchemaDetailProperties editedSchemaDetail, ChangeReport changeReport)
   {
      if (null == previousSchemaDetail || previousSchemaDetail.getTable() != editedSchemaDetail.getTable())
      {
         changeReport.append(INDENT).append(s_stringMgr.getString(I18N_ALIAS_CHANGES_UTIL_SCHEMA_PROP_PREFIX)).append(" ");
         String msg = s_stringMgr.getString(
               "AliasChangesUtil.schema.detail.table.changedFromTo",
               editedSchemaDetail.getSchemaName(),
               null == previousSchemaDetail ? "<null>" : getSchemDetailString(previousSchemaDetail.getTable()),
               getSchemDetailString(editedSchemaDetail.getTable()));

         changeReport.append(msg).append('\n');
      }
      if (null == previousSchemaDetail || previousSchemaDetail.getView() != editedSchemaDetail.getView())
      {
         changeReport.append(INDENT).append(s_stringMgr.getString(I18N_ALIAS_CHANGES_UTIL_SCHEMA_PROP_PREFIX)).append(" ");
         String msg = s_stringMgr.getString(
               "AliasChangesUtil.schema.detail.view.changedFromTo",
               editedSchemaDetail.getSchemaName(),
               null == previousSchemaDetail ? "<null>" : getSchemDetailString(previousSchemaDetail.getTable()),
               getSchemDetailString(editedSchemaDetail.getTable()));

         changeReport.append(msg).append('\n');
      }
      if (null == previousSchemaDetail || previousSchemaDetail.getProcedure() != editedSchemaDetail.getProcedure())
      {
         changeReport.append(INDENT).append(s_stringMgr.getString(I18N_ALIAS_CHANGES_UTIL_SCHEMA_PROP_PREFIX)).append(" ");
         String msg = s_stringMgr.getString(
               "AliasChangesUtil.schema.detail.procedure.changedFromTo",
               editedSchemaDetail.getSchemaName(),
               null == previousSchemaDetail ? "<null>" : getSchemDetailString(previousSchemaDetail.getTable()),
               getSchemDetailString(editedSchemaDetail.getTable()));

         changeReport.append(msg).append('\n');
      }
      if (null == previousSchemaDetail || previousSchemaDetail.getUDT() != editedSchemaDetail.getUDT())
      {
         changeReport.append(INDENT).append(s_stringMgr.getString(I18N_ALIAS_CHANGES_UTIL_SCHEMA_PROP_PREFIX)).append(" ");
         String msg = s_stringMgr.getString(
               "AliasChangesUtil.schema.detail.udt.changedFromTo",
               editedSchemaDetail.getSchemaName(),
               null == previousSchemaDetail ? "<null>" : getSchemDetailString(previousSchemaDetail.getTable()),
               getSchemDetailString(editedSchemaDetail.getTable()));

         changeReport.append(msg).append('\n');
      }
   }

   private static String getSchemDetailString(int schemaLoadingId)
   {
      String schemaLoadingDetailState;
      switch (schemaLoadingId)
      {
         case SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_DONT_CACHE:
            schemaLoadingDetailState = s_stringMgr.getString("AliasChangesUtil.SCHEMA_LOADING_ID_LOAD_DONT_CACHE");
            break;
         case SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_LOAD_AND_CACHE:
            schemaLoadingDetailState = s_stringMgr.getString("AliasChangesUtil.SCHEMA_LOADING_ID_LOAD_AND_CACHE");
            break;
         case SQLAliasSchemaDetailProperties.SCHEMA_LOADING_ID_DONT_LOAD:
            schemaLoadingDetailState = s_stringMgr.getString("AliasChangesUtil.SCHEMA_LOADING_ID_DONT_LOAD");
            break;
         default:
            throw new IllegalArgumentException("Unknown Global schema detail loading and caching state");
      }
      return schemaLoadingDetailState;
   }

   public static void compareSQLDriverPropertyCollection(SQLDriverPropertyCollection previousDriverProps, SQLDriverPropertyCollection editedDriverProps, ChangeReport changeReport)
   {
      //boolean propertyChangeReportInitDone = false;

      for (SQLDriverProperty editedProp : editedDriverProps.getDriverProperties())
      {
         SQLDriverProperty previousProp = previousDriverProps.getDriverPropertyByName(editedProp.getName());

         if(null == previousProp)
         {
            continue;
         }

         if(editedProp.isSpecified() != previousProp.isSpecified())
         {
            //if (false == propertyChangeReportInitDone)
            //{
            //   changeReport.append(s_stringMgr.getString(I18N_ALIAS_CHANGES_UTIL_DRIVER_PROP_PREFIX)).append(" ");
            //   changeReport.append('\n');
            //   propertyChangeReportInitDone = true;
            //}
            changeReport.append(s_stringMgr.getString("AliasChangesUtil.driverProp.specified.changeFromTo", previousProp.getName(), previousProp.isSpecified(), editedProp.isSpecified()));
            changeReport.append('\n');
         }
         if(false == StringUtils.equals(editedProp.getValue(), previousProp.getValue()))
         {
            //if (false == propertyChangeReportInitDone)
            //{
            //   changeReport.append(s_stringMgr.getString(I18N_ALIAS_CHANGES_UTIL_DRIVER_PROP_PREFIX)).append(" ");
            //   changeReport.append('\n');
            //   propertyChangeReportInitDone = true;
            //}
            changeReport.append(s_stringMgr.getString("AliasChangesUtil.driverProp.value.changeFromTo", previousProp.getName(), previousProp.getValue(), editedProp.getValue()));
            changeReport.append('\n');
         }
      }
   }
}
