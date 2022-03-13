package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.TableClickPosition;
import net.sourceforge.squirrel_sql.fw.gui.ClipboardUtil;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class TableCopyColumnHeaderCommand implements ICommand
{
   private DataSetViewerTablePanel _dataSetViewerTablePanel;
   private TableClickPosition _currentTableClickPosition;

   public TableCopyColumnHeaderCommand(DataSetViewerTablePanel dataSetViewerTablePanel, TableClickPosition currentTableClickPosition)
   {
      _dataSetViewerTablePanel = dataSetViewerTablePanel;
      _currentTableClickPosition = currentTableClickPosition;
   }

   @Override
   public void execute()
   {
      if(_currentTableClickPosition.isClickedOnTableHeader())
      {
         TableColumnModel cm = _dataSetViewerTablePanel.getTable().getColumnModel();
         int columnIndexAtX = cm.getColumnIndexAtX(_currentTableClickPosition.getX());
         final Object headerValue = cm.getColumn(columnIndexAtX).getHeaderValue();

         if(null != headerValue && false == StringUtilities.isEmpty("" + headerValue, true))
         {
            ClipboardUtil.copyToClip("" + headerValue);
         }
      }
      else
      {
         int nbrSelCols = _dataSetViewerTablePanel.getTable().getSelectedColumnCount();
         int[] selCols = _dataSetViewerTablePanel.getTable().getSelectedColumns();
         if (0 == selCols.length)
         {
            return;
         }

         StringBuilder buf = new StringBuilder();
         for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
         {
            TableColumn col = _dataSetViewerTablePanel.getTable().getColumnModel().getColumn(selCols[colIdx]);

            if (0 == buf.length())
            {
               buf.append(col.getHeaderValue());
            }
            else
            {
               buf.append("," + col.getHeaderValue());
            }
         }

         ClipboardUtil.copyToClip(buf);
      }
   }
}
