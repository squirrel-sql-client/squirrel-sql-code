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
            ITableInfo[] tableInfos = session.getSchemaInfo().getITableInfos(null, null, tableName);

            if(0 < tableInfos.length )
            {
               tableInfo = tableInfos[0];
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

   private static boolean isQualifyingRequired(ISession session) throws SQLException
   {
      return SqlScriptPluginAccessor.isQualifyTableRequired()
             && (session.getMetaData().supportsSchemas() || session.getMetaData().supportsCatalogs());
   }
}
