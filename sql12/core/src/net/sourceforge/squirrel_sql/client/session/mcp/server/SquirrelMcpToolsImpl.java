package net.sourceforge.squirrel_sql.client.session.mcp.server;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetExportedKeysArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetImportedKeysArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetIndexInfoArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetTablesArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpNoArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultCell;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultMetaData;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultRow;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultSet;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpSimpleString;
import net.sourceforge.squirrel_sql.client.session.mcp.ui.McpServerContext;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

/**
 * Default implementation of {@link SquirrelMcpTools}. Stateless and thread-safe.
 * <p>
 * NOTE: {@link #getTables} is currently a <b>stub</b> that returns a single
 * synthetic row echoing the requested filter, shaped like the standard JDBC
 * {@code getTables} columns. Wire it to a real
 * {@link java.sql.DatabaseMetaData#getTables(String, String, String, String[])}
 * call once a database connection is available.
 */
public final class SquirrelMcpToolsImpl implements SquirrelMcpTools
{
   private final McpServerContext _mcpServerContext;

   public SquirrelMcpToolsImpl(McpServerContext mcpServerContext)
   {
      _mcpServerContext = mcpServerContext;
   }

   @Override
   public McpSimpleString getSessionName(McpNoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.session().getTitle()), true);
   }

   @Override
   public McpSimpleString getDriverClassName(McpNoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.session().getJdbcData().getDriverClassName()), true);
   }

   @Override
   public McpSimpleString getDriverName(McpNoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getDriverName()), true);
   }

   @Override
   public McpSimpleString getDriverVersion(McpNoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getDriverVersion()), true);
   }

   @Override
   public McpSimpleString getDatabaseProductName(McpNoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getDatabaseProductName()), true);
   }

   @Override
   public McpSimpleString getDatabaseProductVersion(McpNoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.getDatabaseProductVersion()), true);
   }

   @Override
   public McpResultSet executeQuery(McpSimpleString sql)
   {
      return McpQueryExecuter.executeQuery(sql, _mcpServerContext);
   }


   @Override
   public McpResultSet getTables(McpGetTablesArgs args)
   {
      try
      {
         // Citation from java.sql.DatabaseMetaData.getTables: "as it is stored in the database"
         String catalogName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveCatalogName(args.catalog());
         String schemaName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveSchemaName(args.schemaPattern());
         String tableName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveTableName(args.tableNamePattern());

         ITableInfo[] tables = _mcpServerContext.session().getMetaData().getTables(catalogName, schemaName, tableName, args.types(), null);

         List<McpResultMetaData> metaData = List.of(
               new McpResultMetaData(1, "TABLE_CAT", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(2, "TABLE_SCHEM", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(3, "TABLE_NAME", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(4, "TABLE_TYPE", Types.VARCHAR, "VARCHAR"),
               new McpResultMetaData(5, "REMARKS", Types.VARCHAR, "VARCHAR"));


         List<McpResultRow> tablesRes = new ArrayList<>();
         for(ITableInfo table : tables)
         {
            McpResultRow tableRow = new McpResultRow(List.of(
                  McpResultCell.ofString(table.getCatalogName()),
                  McpResultCell.ofString(table.getSchemaName()),
                  McpResultCell.ofString(table.getSimpleName()),
                  McpResultCell.ofString(table.getType()),
                  McpResultCell.ofString(table.getRemarks())));

            tablesRes.add(tableRow);
         }

         return McpResultSet.ofResult(metaData, tablesRes);
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   @Override
   public McpResultSet getImportedKeys(McpGetImportedKeysArgs args)
   {
      return null;
   }

   @Override
   public McpResultSet getExportedKeys(McpGetExportedKeysArgs args)
   {
      return null;
   }

   @Override
   public McpResultSet getIndexInfo(McpGetIndexInfoArgs args)
   {
      return null;
   }
}
