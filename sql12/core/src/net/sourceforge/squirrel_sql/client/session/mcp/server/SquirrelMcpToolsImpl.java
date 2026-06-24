package net.sourceforge.squirrel_sql.client.session.mcp.server;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.GetTablesArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.NoArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.ResultCell;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.ResultMetaData;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.ResultRow;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.ResultSet;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.SimpleString;
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
   public SimpleString getSessionName(NoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new SimpleString(_mcpServerContext.session().getTitle()));
   }

   @Override
   public SimpleString getDriverClassName(NoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new SimpleString(_mcpServerContext.session().getJdbcData().getDriverClassName()));
   }

   @Override
   public SimpleString getJdbcUrl(NoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new SimpleString(_mcpServerContext.session().getJdbcData().getUrl()));
   }

   @Override
   public ResultSet getTables(GetTablesArgs args)
   {
      return GUIUtils.callOnSwingEventThread(() -> _getTables(args));
   }

   @Override
   public ResultSet executeQuery(SimpleString sql)
   {
      return GUIUtils.callOnSwingEventThread(() -> _executeQuery(sql));
   }

   private ResultSet _executeQuery(SimpleString sql)
   {
      throw new UnsupportedOperationException("NYI");
   }


   private ResultSet _getTables(GetTablesArgs args)
   {
      try
      {
         // Citation from java.sql.DatabaseMetaData.getTables: "as it is stored in the database"
         String catalogName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveCatalogName(args.catalog());
         String schemaName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveSchemaName(args.schemaPattern());
         String tableName = _mcpServerContext.session().getSchemaInfo().getCaseSensitiveTableName(args.tableNamePattern());

         ITableInfo[] tables = _mcpServerContext.session().getMetaData().getTables(catalogName, schemaName, tableName, args.types(), null);

         List<ResultMetaData> metaData = List.of(
               new ResultMetaData(1, "TABLE_CAT", Types.VARCHAR, "VARCHAR"),
               new ResultMetaData(2, "TABLE_SCHEM", Types.VARCHAR, "VARCHAR"),
               new ResultMetaData(3, "TABLE_NAME", Types.VARCHAR, "VARCHAR"),
               new ResultMetaData(4, "TABLE_TYPE", Types.VARCHAR, "VARCHAR"),
               new ResultMetaData(5, "REMARKS", Types.VARCHAR, "VARCHAR"));


         List<ResultRow> tablesRes= new ArrayList<>();
         for(ITableInfo table : tables)
         {
            ResultRow tableRow = new ResultRow(List.of(
                  ResultCell.ofString(table.getCatalogName()),
                  ResultCell.ofString(table.getSchemaName()),
                  ResultCell.ofString(table.getSimpleName()),
                  ResultCell.ofString(table.getType()),
                  ResultCell.ofString(table.getRemarks())));

            tablesRes.add(tableRow);
         }

         return new ResultSet(metaData, tablesRes);
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }


   //@Override
   //public ResultSet getTables(GetTablesArgs args)
   //{
   //   List<ResultMetaData> metaData = List.of(
   //         new ResultMetaData(1, "TABLE_CAT", Types.VARCHAR, "VARCHAR"),
   //         new ResultMetaData(2, "TABLE_SCHEM", Types.VARCHAR, "VARCHAR"),
   //         new ResultMetaData(3, "TABLE_NAME", Types.VARCHAR, "VARCHAR"),
   //         new ResultMetaData(4, "TABLE_TYPE", Types.VARCHAR, "VARCHAR"),
   //         new ResultMetaData(5, "REMARKS", Types.VARCHAR, "VARCHAR"));
   //
   //   String tableName = args.tableNamePattern() == null ? "SAMPLE_TABLE" : args.tableNamePattern() + "_GERD_TABLE";
   //   ResultRow sampleRow = new ResultRow(List.of(
   //         ResultCell.ofString(args.catalog()),
   //         ResultCell.ofString(args.schemaPattern()),
   //         ResultCell.ofString(tableName),
   //         ResultCell.ofString("TABLE"),
   //         ResultCell.ofString("stub row — wire java.sql.DatabaseMetaData.getTables for real data")));
   //
   //   return new ResultSet(metaData, List.of(sampleRow));
   //}
}
