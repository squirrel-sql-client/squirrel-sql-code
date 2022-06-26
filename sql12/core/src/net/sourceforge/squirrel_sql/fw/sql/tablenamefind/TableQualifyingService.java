package net.sourceforge.squirrel_sql.fw.sql.tablenamefind;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SqlScriptPluginAccessor;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.sql.SQLException;

public class TableQualifyingService
{
   public static String qualifyIfNeeded(String tableName, ISession session)
   {
      try
      {
         if(StringUtilities.isEmpty(tableName, true))
         {
            return tableName;
         }

         if(tableName.contains("."))
         {
            return tableName;
         }

         ITableInfo tableInfo = new TableInfo(null, null, tableName, null, null, session.getMetaData());

         if(     SqlScriptPluginAccessor.isQualifyTableRequired()
             && (session.getMetaData().supportsSchemas() || session.getMetaData().supportsCatalogs())
           )
         {
            ITableInfo[] tableInfos = session.getSchemaInfo().getITableInfos(null, null, tableName);

            if(0 < tableInfos.length )
            {
               tableInfo = tableInfos[0];
            }
         }

         return SqlScriptPluginAccessor.formatTableName(tableInfo);
      }
      catch (SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
