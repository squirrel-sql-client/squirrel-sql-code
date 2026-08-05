package net.sourceforge.squirrel_sql.client.session.mcp.server;

import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultMetaData;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultRow;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultSet;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpSimpleString;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.SimpleDataSet;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.lang3.StringUtils;

public class McpApprovalCallPreviewBuilder
{
   public static DataSetViewerTablePanel createSingleMcpStringSetViewerTablePanel(McpCallExecutor callExecutor, McpCall mcpCall)
   {
      try
      {
         String stringContent = ((McpSimpleString) callExecutor.executeCall()).stringContent();
         ColumnDisplayDefinition[] columnDisplayDefinitions = {new ColumnDisplayDefinition(200, mcpCall.name())};
         ArrayList<Object[]> list = new ArrayList<>();
         list.add(new Object[]{stringContent});

         SimpleDataSet simpleDataSet = new SimpleDataSet(list, columnDisplayDefinitions);
         DataSetViewerTablePanel table = new DataSetViewerTablePanel();
         table.init(null, null);
         table.show(simpleDataSet);

         return table;
      }
      catch(DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public static DataSetViewerTablePanel createMcpResultSetDataSetViewerTablePanel(McpCallExecutor callExecutor, McpCall mcpCall)
   {
      try
      {
         McpResultSet mcpRes = callExecutor.executeCall();
         if( StringUtils.isBlank(mcpRes.errorMessage()) )
         {
            String errMsg = mcpRes.errorMessage();
            ColumnDisplayDefinition[] columnDisplayDefinitions = {new ColumnDisplayDefinition(200, mcpCall.name())};
            ArrayList<Object[]> list = new ArrayList<>();
            list.add(new Object[]{errMsg});

            SimpleDataSet simpleDataSet = new SimpleDataSet(list, columnDisplayDefinitions);
            DataSetViewerTablePanel table = new DataSetViewerTablePanel();
            table.init(null, null);
            table.show(simpleDataSet);
         }

         ArrayList<ColumnDisplayDefinition> columnDisplayDefinitions = new ArrayList<>();

         for( McpResultMetaData resMeta : mcpRes.resultMetaData() )
         {
            ColumnDisplayDefinition buf = new ColumnDisplayDefinition(200, resMeta.columnName());
            buf.setSqlType(resMeta.sqlType());
            buf.setSqlTypeName(resMeta.sqlTypeName());
            columnDisplayDefinitions.add(buf);
         }

         ArrayList<Object[]> tableViewerRows = new ArrayList<>();

         for(McpResultRow mcpRow : mcpRes.rows())
         {
            Object[] row = new Object[mcpRes.resultMetaData().size()];
            tableViewerRows.add(row);
            for( int i = 0; i < row.length; i++ )
            {
               row[i] = mcpRow.cells().get(i).value();
            }
         }

         SimpleDataSet simpleDataSet = new SimpleDataSet(tableViewerRows, columnDisplayDefinitions.toArray(new ColumnDisplayDefinition[0]));
         DataSetViewerTablePanel table = new DataSetViewerTablePanel();
         table.init(null, null);
         table.show(simpleDataSet);

         return table;
      }
      catch(DataSetException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
