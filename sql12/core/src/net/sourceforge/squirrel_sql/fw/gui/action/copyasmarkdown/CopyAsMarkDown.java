package net.sourceforge.squirrel_sql.fw.gui.action.copyasmarkdown;

import java.sql.Types;
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowNumberTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.steppschuh.markdowngenerator.table.Table;

public class CopyAsMarkDown
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CopyAsMarkDown.class);

   public static String createMarkdownForSelectedCells(JTable table)
   {
      return _createMarkDownForSelectedCells(table, null, true);
   }

   public static String createMarkdownForSelectedCells(JTable table, boolean appendTopAndBottomLines)
   {
      return _createMarkDownForSelectedCells(table, null, appendTopAndBottomLines);
   }

   public static CopyAsMarkDownResult createMarkdownForSelectedCellsIncludingRawData(JTable table)
   {
      RawDataTable rawDataTable = new RawDataTable();
      String markDownString = _createMarkDownForSelectedCells(table, rawDataTable, true);

      if(null == markDownString)
      {
         return CopyAsMarkDownResult.EMPTY;
      }

      return new CopyAsMarkDownResult(markDownString, rawDataTable);
   }

   private static String _createMarkDownForSelectedCells(JTable table, RawDataTable rawDataTable, boolean appendTopAndBottomLines)
   {
      int nbrSelRows = table.getSelectedRowCount();
      int nbrSelCols = table.getSelectedColumnCount();
      int[] selRows = table.getSelectedRows();
      int[] selCols = table.getSelectedColumns();

      Integer rowNumberColIx =null;

      ArrayList<ColumnDisplayDefinition> columnDisplayDefinitions = new ArrayList<>();
      for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
      {
         TableColumn col = table.getColumnModel().getColumn(selCols[colIdx]);

         if(col instanceof RowNumberTableColumn)
         {
            rowNumberColIx = colIdx;
            continue;
         }


         if (col instanceof ExtTableColumn)
         {
            columnDisplayDefinitions.add(((ExtTableColumn) col).getColumnDisplayDefinition());
         }
         else
         {
            columnDisplayDefinitions.add(createDefaultStringColumnDisplayDefinition(col));
            //Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("TableCopyAsMarkdownCommand.failed.to.copy"));
            //return null;
         }
      }


      ColumnDisplayDefinition[] colDefs = columnDisplayDefinitions.toArray(new ColumnDisplayDefinition[columnDisplayDefinitions.size()]);

      String[] colNames = new String[colDefs.length];

      for (int i = 0; i < colDefs.length; i++)
      {
         colNames[i] = colDefs[i].getColumnName();
      }


      Table.Builder tableBuilder = new Table.Builder();
      tableBuilder.addRow((Object[]) colNames);

      if(null != rawDataTable)
      {
         rawDataTable.setColumnNames(colNames);
      }

      for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
      {
         Object[] row = new Object[colDefs.length];

         int curIx = 0;
         for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
         {
            if(null != rowNumberColIx && colIdx == rowNumberColIx)
            {
               continue;
            }


            Object cellObj = table.getValueAt(selRows[rowIdx], selCols[colIdx]);

            if(cellObj instanceof String && -1 < ((String)cellObj).indexOf('\n'))
            {
               int lineBreakPos = ((String)cellObj).indexOf('\n');
               row[curIx] = ((String)cellObj).substring(0, lineBreakPos);
            }
            else if(null == cellObj)
            {
               row[curIx] = BaseDataTypeComponent.NULL_VALUE_PATTERN;
            }
            else
            {
               row[curIx] = cellObj;
            }
            ++curIx;

            if(null != rawDataTable)
            {
               rawDataTable.setCell(rowIdx, colIdx, cellObj);
            }
         }

         tableBuilder.addRow(row);
      }

      Table markDownTable = tableBuilder.build();
      String markdownString = markDownTable.toString();

      int width = markdownString.indexOf('\n');

      if(appendTopAndBottomLines)
      {
         String line = StringUtilities.pad(width, '-')  + "\n";

         return line + markdownString + "\n" + line;
      }
      else
      {
         return markdownString;
      }
   }

   private static ColumnDisplayDefinition createDefaultStringColumnDisplayDefinition(TableColumn col)
   {
      String colName = "" + col.getHeaderValue();
      ColumnDisplayDefinition stringDisplayDefinition = new ColumnDisplayDefinition(100, colName);
      stringDisplayDefinition.setColumnName(colName);
      stringDisplayDefinition.setSqlType(Types.VARCHAR);
      stringDisplayDefinition.setSqlTypeName("VARCHAR");
      return stringDisplayDefinition;
   }
}
