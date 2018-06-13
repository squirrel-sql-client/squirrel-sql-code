package net.sourceforge.squirrel_sql.fw.gui.copyseparatedby;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

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

      String columnSeparator = copySeparatedByCtrl.getRowSeparator();

      if(1 == nbrSelCols)
      {
         columnSeparator = "";
      }

      int preferedLineLength = copySeparatedByCtrl.getPreferedLineLength();




      int nbrSelRows = _table.getSelectedRowCount();
      int[] selRows = _table.getSelectedRows();
      int[] selCols = _table.getSelectedColumns();

      StringBuilder sb = new StringBuilder();

      for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
      {
         if(1 < nbrSelCols && rowIdx > 0)
         {
            sb.append(columnSeparator);

            if(preferedLineLength < getDistToLastNewLine(sb))
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

                  if(preferedLineLength < getDistToLastNewLine(sb))
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
               sb.append((String)cellObj, 0, lineBreakPos);
            }
            else if(null == cellObj)
            {
               sb.append(BaseDataTypeComponent.NULL_VALUE_PATTERN);
            }
            else
            {
               sb.append(cellObj.toString());
            }
         }
      }

      StringSelection ss = new StringSelection(sb.toString());
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);

   }

   private int getDistToLastNewLine(StringBuilder sb)
   {
      return sb.length() - sb.lastIndexOf("\n");
   }
}
