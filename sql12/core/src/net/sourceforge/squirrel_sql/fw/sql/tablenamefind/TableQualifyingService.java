package net.sourceforge.squirrel_sql.fw.sql.tablenamefind;

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SqlScriptPluginAccessor;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class TableQualifyingService
{
   public static QualifyResult qualifyIfNeeded(String tableName, ISession session)
   {
      try
      {
         if(StringUtilities.isEmpty(tableName, true))
         {
            return new QualifyResult(tableName, QualifyResultState.EMPTY);
         }

         if(tableName.contains("."))
         {
            return new QualifyResult(tableName, QualifyResultState.OK);
         }

         ITableInfo tableInfo = new TableInfo(null, null, tableName, null, null, session.getMetaData());

         if( isQualifyingRequired(session) )
         {
            ITableInfo buf = heuristicallyFindMatchingTableInfo(tableName, session);

            if(null != buf)
            {
               tableInfo = buf;
            }

            return new QualifyResult(SqlScriptPluginAccessor.formatTableName(tableInfo), QualifyResultState.HEURISTIC_USED);
         }
         else
         {
            return new QualifyResult(SqlScriptPluginAccessor.formatTableName(tableInfo), QualifyResultState.OK);
         }

      }
      catch (SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   private static ITableInfo heuristicallyFindMatchingTableInfo(String tableName, ISession session)
   {
      ITableInfo ret = null;

      String currentSchemaOrNull = getCurrentSchemaOrNull(session);

      ITableInfo[] tableInfos = session.getSchemaInfo().getITableInfos(null, currentSchemaOrNull, tableName);

      if(0 == tableInfos.length && currentSchemaOrNull != null)
      {
         tableInfos = session.getSchemaInfo().getITableInfos(null, null, tableName);
      }

      if(0 < tableInfos.length )
      {
         ret = tableInfos[0];
      }
      return ret;
   }

   private static String getCurrentSchemaOrNull(ISession session)
   {
      String currentSchema = null;
      if( false == session.getCurrentSchemaModel().isInitialized())
      {
         session.getCurrentSchemaModel().refreshSchema(true);
      }
      if( session.getCurrentSchemaModel().isJDBCConnectionProvidesCurrentSchema())
      {
         currentSchema = session.getCurrentSchemaModel().getCurrentSchemaString();
      }

      return currentSchema;
   }

   private static boolean isQualifyingRequired(ISession session) throws SQLException
   {
      return SqlScriptPluginAccessor.isQualifyTableRequired()
             && (session.getMetaData().supportsSchemas() || session.getMetaData().supportsCatalogs());
   }
}
