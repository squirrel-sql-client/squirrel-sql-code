package net.sourceforge.squirrel_sql.client.session.mcp.server;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.sourceforge.squirrel_sql.client.session.SqlPanelExecutionFuture;
import net.sourceforge.squirrel_sql.client.session.SqlPanelExecutionResult;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.GetTablesArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpNoArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpSimpleString;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.ResultCell;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.ResultMetaData;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.ResultRow;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.ResultSet;
import net.sourceforge.squirrel_sql.client.session.mcp.ui.McpServerContext;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
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
   public McpSimpleString getJdbcUrl(McpNoArgs none)
   {
      return GUIUtils.callOnSwingEventThread(() -> new McpSimpleString(_mcpServerContext.session().getJdbcData().getUrl()), true);
   }

   @Override
   public ResultSet getTables(GetTablesArgs args)
   {
      return _getTables(args);
   }

   @Override
   public ResultSet executeQuery(McpSimpleString sql)
   {
      return _executeQuery(sql);
   }

   private ResultSet _executeQuery(McpSimpleString sql)
   {
      final SqlPanelExecutionFuture sqlPanelExecutionFuture = new SqlPanelExecutionFuture();

      GUIUtils.processOnSwingEventThread(() -> _mcpServerContext.mcpSqlTab().getSQLPanelAPI().executeSQL(sql.stringContent(), sqlPanelExecutionFuture), false);
      SqlPanelExecutionResult executionResult =  sqlPanelExecutionFuture.waitForSqlResult();

      //executionResult.getSqlsResultTab().setBorder(BorderFactory.createLineBorder(Color.red));

      ResultSetDataSet resultSetData = executionResult.getSqlResultTab().getResultSetDataSetByReference();

      List<ResultMetaData> metaData = new ArrayList<>();

      ColumnDisplayDefinition[] columnDefinitions = resultSetData.getDataSetDefinition().getColumnDefinitions();
      for(int i = 0; i < columnDefinitions.length; i++)
      {
         metaData.add(new ResultMetaData(i+1, columnDefinitions[i].getColumnName(), columnDefinitions[i].getSqlType(), columnDefinitions[i].getSqlTypeName()));
      }

      List<ResultRow> sqlRes= new ArrayList<>();

      for(Object[] row : resultSetData.getAllDataForReadOnly())
      {
         List<ResultCell> cellsOfRow = new ArrayList<>();

         for(int i = 0; i < row.length; i++)
         {
            switch(metaData.get(i).sqlType())
            {
               case Types.INTEGER -> cellsOfRow.add(ResultCell.ofInt(getIntValue(row[i])));
               case Types.BIGINT -> cellsOfRow.add(ResultCell.ofInt(getIntValue(row[i])));
               case Types.SMALLINT -> cellsOfRow.add(ResultCell.ofInt(getIntValue(row[i])));
               case Types.TINYINT -> cellsOfRow.add(ResultCell.ofInt(getIntValue(row[i])));
               case Types.DOUBLE -> cellsOfRow.add(ResultCell.ofDouble(getDoubleValue(row[i])));
               case Types.NUMERIC -> cellsOfRow.add(ResultCell.ofDouble(getDoubleValue(row[i])));
               case Types.REAL -> cellsOfRow.add(ResultCell.ofDouble(getDoubleValue(row[i])));
               case Types.DECIMAL -> cellsOfRow.add(ResultCell.ofDouble(getDoubleValue(row[i])));
               case Types.BIT -> cellsOfRow.add(ResultCell.ofBool(row[i]));
               case Types.DATE -> cellsOfRow.add(ResultCell.ofDate((Date) row[i]));
               case Types.TIMESTAMP -> cellsOfRow.add(ResultCell.ofDate((Date) row[i]));
               case Types.TIME -> cellsOfRow.add(ResultCell.ofDate((Date) row[i]));
               default -> cellsOfRow.add(ResultCell.ofString("" + row[i]));
            }
         }
         sqlRes.add(new ResultRow(cellsOfRow));
      }

      return new ResultSet(metaData, sqlRes);
   }

   private static Double getDoubleValue(Object cell)
   {
      if(null == cell)
      {
         return null;
      }

      return ((Number) cell).doubleValue();
   }

   private static Integer getIntValue(Object cell)
   {
      if(null == cell)
      {
         return null;
      }

      return ((Number) cell).intValue();
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


         List<ResultRow> tablesRes = new ArrayList<>();
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
