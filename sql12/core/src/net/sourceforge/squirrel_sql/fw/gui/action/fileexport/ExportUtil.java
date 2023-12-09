package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.ClobDescriptor;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.time.StopWatch;

import javax.swing.*;
import java.io.File;
import java.sql.Types;
import java.text.NumberFormat;

public class ExportUtil
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExportUtil.class);


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

   public static String createDefaultExportName(int index)
   {
      return DataExportExcelWriter.DEFAULT_EXCEL_EXPORT_SHEET_NAME + " " + index;
   }

   public static void writeExportMessage(StopWatch stopWatch, long writtenRows, File targetFile)
   {
      NumberFormat nf = NumberFormat.getIntegerInstance();
      String rows = nf.format(writtenRows);
      String seconds = nf.format(stopWatch.getTime() / 1000);
      writeExportMessage(rows, targetFile, seconds);
   }

   public static void writeExportMessage(String rows, File targetFile, String seconds)
   {
      String msg = s_stringMgr.getString("CreateFileOfCurrentSQLCommand.progress.successMessage",
            rows,
            targetFile,
            seconds);
      Main.getApplication().getMessageHandler().showMessage(msg);
   }
}
