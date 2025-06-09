package net.sourceforge.squirrel_sql.fw.gui.action.copyseparatedby;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;

public class TableCopySeparatedByCommand
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableCopySeparatedByCommand.class);

   private DataSetViewerTable _table;

   public TableCopySeparatedByCommand(DataSetViewerTable table)
   {
      _table = table;
   }

   public void execute()
   {
      int nbrSelCols = _table.getSelectedColumnCount();

      CopySeparatedByCtrl copySeparatedByCtrl = new CopySeparatedByCtrl(_table, nbrSelCols > 1);

      if(false == copySeparatedByCtrl.isOk())
      {
         return;
      }


      String cellSeparator = copySeparatedByCtrl.getCellSeparator();

      String rowSeparator = "\n";
      if(false == copySeparatedByCtrl.isIncludeHeaders())
      {
         rowSeparator = copySeparatedByCtrl.getRowSeparator();
      }

      if(1 == nbrSelCols)
      {
         rowSeparator = "";
      }

      int preferredLineLength = copySeparatedByCtrl.getPreferredLineLength();




      int nbrSelRows = _table.getSelectedRowCount();
      int[] selRows = _table.getSelectedRows();
      int[] selCols = _table.getSelectedColumns();

      StringBuilder sb = new StringBuilder();

      if(copySeparatedByCtrl.isIncludeHeaders())
      {
         for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
         {
            if(0 < colIdx)
            {
               sb.append(cellSeparator);
            }

            sb.append(wrapCellDelimiter(_table.getColumnName(selCols[colIdx]), copySeparatedByCtrl.getCellDelimiter()));

         }
         sb.append("\n");
      }

      for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
      {
         if(1 < nbrSelCols && rowIdx > 0)
         {
            sb.append(rowSeparator);

            if(preferredLineLength < getDistToLastNewLine(sb))
            {
               sb.append("\n");
            }
         }


         for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
         {
            Object cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);

            if(nbrSelCols == 1)
            {
               if (rowIdx > 0)
               {
                  sb.append(cellSeparator);

                  if(false == copySeparatedByCtrl.isIncludeHeaders() && preferredLineLength < getDistToLastNewLine(sb))
                  {
                     sb.append("\n");
                  }
               }
            }
            else
            {
               if(colIdx > 0)
               {
                  sb.append(cellSeparator);
               }
            }

            if(cellObj instanceof String && -1 < ((String)cellObj).indexOf('\n'))
            {
               int lineBreakPos = ((String)cellObj).indexOf('\n');
               sb.append(wrapCellDelimiter(cellObj, copySeparatedByCtrl.getCellDelimiter()), 0, lineBreakPos);
            }
            else if(null == cellObj)
            {
               sb.append(wrapCellDelimiter(BaseDataTypeComponent.NULL_VALUE_PATTERN, copySeparatedByCtrl.getCellDelimiter()));
            }
            else
            {
               sb.append(wrapCellDelimiter(cellObj, copySeparatedByCtrl.getCellDelimiter()));
            }
         }
      }

      ClipboardUtil.copyToClip(sb);
   }

   private String wrapCellDelimiter(Object cellContent, String cellDelimiter)
   {
      if(StringUtilities.isEmpty(cellDelimiter, true))
      {
         return "" + cellContent;
      }

      return cellDelimiter + StringUtils.replace("" + cellContent, cellDelimiter, cellDelimiter+cellDelimiter) + cellDelimiter;
   }

   private int getDistToLastNewLine(StringBuilder sb)
   {
      return sb.length() - sb.lastIndexOf("\n");
   }
}
