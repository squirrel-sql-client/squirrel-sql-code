package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.ClobDescriptor;

import javax.swing.JTable;
import java.sql.Types;

public class ExportUtil
{
   public static boolean isUITableMissingBlobData(JTable table, String sepChar)
   {
      // TODO: if the use checks "export entire table" and doesn't select all,
      // then the selected indices are not set, and this check doesn't properly
      // detect missing data.  If export entire table is selected, we need to
      // set the selected indexes below correctly.
      int firstSelectedColIdx = table.getSelectedColumn();
      int lastSelectedColIdx = firstSelectedColIdx + table.getSelectedColumnCount();
      int firstSelectedRowIdx = table.getSelectedRow();
      int lastSelectedRowIdx = firstSelectedRowIdx + table.getSelectedRowCount();
      for (int colIdx = table.getSelectedColumn(); colIdx < lastSelectedColIdx; colIdx++)
      {
         if(false == table.getColumnModel().getColumn(colIdx) instanceof ExtTableColumn)
         {
            continue;
         }

         ExtTableColumn col = (ExtTableColumn) table.getColumnModel().getColumn(colIdx);
         int sqlType = col.getColumnDisplayDefinition().getSqlType();
         if(sqlType == Types.CLOB)
         {
            for (int rowIdx = firstSelectedRowIdx; rowIdx < lastSelectedRowIdx; rowIdx++)
            {
               Object cellObj = table.getValueAt(rowIdx, colIdx);
               // TODO stefan why did we need the csv data?
               String data = DataExportCSVWriter.getDataCSV(sepChar, "" + cellObj);
               if(ClobDescriptor.i18n.CLOB_LABEL.equals(data))
               {
                  return true;
               }
            }
         }
      }
      return false;
   }
}
