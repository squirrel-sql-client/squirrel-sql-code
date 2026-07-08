package net.sourceforge.squirrel_sql.client.session.mcp.server;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.SqlPanelExecutionFuture;
import net.sourceforge.squirrel_sql.client.session.SqlPanelExecutionResult;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltypecheck.SQLTypeCheck;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultCell;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultMetaData;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultRow;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultSet;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpSimpleString;
import net.sourceforge.squirrel_sql.client.session.mcp.ui.McpServerContext;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

public class McpQueryExecuter
{
   static McpResultSet executeQuery(McpSimpleString sql, McpServerContext mcpServerContext)
   {
      if(mcpServerContext.getSession().getAlias().isReadOnly())
      {
         return McpResultSet.ofError("The Session's Alias allows to execute SELECT-Statements only.");
      }
      else if(mcpServerContext.getMcpUiProps().isApplyAliasesReadOnlyRules() && false == SQLTypeCheck.isSelectStatement(sql.stringContent()))
      {
         return McpResultSet.ofError("AIs are allowed to execute SELECT-Statements only.");
      }


      final SqlPanelExecutionFuture sqlPanelExecutionFuture = new SqlPanelExecutionFuture();

      GUIUtils.processOnSwingEventThread(() -> mcpServerContext.getMcpSqlTab().getSQLPanelAPI().executeSQL(sql.stringContent(), sqlPanelExecutionFuture), false);
      SqlPanelExecutionResult executionResult = sqlPanelExecutionFuture.waitForSqlResult();

      if(executionResult.hasError())
      {
         return McpResultSet.ofError(executionResult.composeErrorMessage());
      }

      if(executionResult.hasUpdateMessage())
      {
         return McpResultSet.ofUpdateMessage(executionResult.updateMessage());
      }

      //executionResult.getSqlsResultTab().setBorder(BorderFactory.createLineBorder(Color.red));

      ResultSetDataSet resultSetData = executionResult.sqlResultTab().getResultSetDataSetByReference();

      List<McpResultMetaData> metaData = new ArrayList<>();

      ColumnDisplayDefinition[] columnDefinitions = resultSetData.getDataSetDefinition().getColumnDefinitions();
      for(int i = 0; i < columnDefinitions.length; i++)
      {
         metaData.add(new McpResultMetaData(i + 1, columnDefinitions[i].getColumnName(), columnDefinitions[i].getSqlType(), columnDefinitions[i].getSqlTypeName()));
      }

      List<McpResultRow> sqlRes = new ArrayList<>();

      for(Object[] row : resultSetData.getAllDataForReadOnly())
      {
         List<McpResultCell> cellsOfRow = new ArrayList<>();

         for(int i = 0; i < row.length; i++)
         {
            switch(metaData.get(i).sqlType())
            {
               case Types.INTEGER -> cellsOfRow.add(McpResultCell.ofInt(getIntValue(row[i])));
               case Types.BIGINT -> cellsOfRow.add(McpResultCell.ofInt(getIntValue(row[i])));
               case Types.SMALLINT -> cellsOfRow.add(McpResultCell.ofInt(getIntValue(row[i])));
               case Types.TINYINT -> cellsOfRow.add(McpResultCell.ofInt(getIntValue(row[i])));
               case Types.DOUBLE -> cellsOfRow.add(McpResultCell.ofDouble(getDoubleValue(row[i])));
               case Types.NUMERIC -> cellsOfRow.add(McpResultCell.ofDouble(getDoubleValue(row[i])));
               case Types.REAL -> cellsOfRow.add(McpResultCell.ofDouble(getDoubleValue(row[i])));
               case Types.DECIMAL -> cellsOfRow.add(McpResultCell.ofDouble(getDoubleValue(row[i])));
               case Types.BIT -> cellsOfRow.add(McpResultCell.ofBool(row[i]));
               case Types.DATE -> cellsOfRow.add(McpResultCell.ofDate((Date) row[i]));
               case Types.TIMESTAMP -> cellsOfRow.add(McpResultCell.ofDate((Date) row[i]));
               case Types.TIME -> cellsOfRow.add(McpResultCell.ofDate((Date) row[i]));
               default -> cellsOfRow.add(McpResultCell.ofString("" + row[i]));
            }
         }
         sqlRes.add(new McpResultRow(cellsOfRow));
      }

      return McpResultSet.ofResult(metaData, sqlRes, resultSetData.isResultLimitedByMaxRowsCount() ? resultSetData.currentRowCount() : null);
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
}
