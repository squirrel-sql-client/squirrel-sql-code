package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TableExportCsvCommand
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(TableExportCsvCommand.class);

   private static ILogger s_log = LoggerController.createLogger(TableExportCsvCommand.class);

   private JTable _table;


   public TableExportCsvCommand(JTable table)
   {
      _table = table;
   }

   public void execute()
   {
      TableExportCsvController ctrl = new TableExportCsvController();

      if(false == ctrl.isOK())
      {
         return;
      }


      if(writeCsvFile(ctrl))
      {
         String command = ctrl.getCommand();

         if(null != command)
         {
            executeCommand(command);
         }
      }
   }

   private void executeCommand(String command)
   {
      try
      {
         Runtime.getRuntime().exec(command);
      }
      catch (IOException e)
      {
         Object[] params = new Object[]{command, e.getMessage()};
         // i18n[TableExportCsvCommand.failedToExecuteCommand=Failed to execute\n{0}\nError message\n{1}\nSee last log entry for details.]
         String msg = s_stringMgr.getString("TableExportCsvCommand.failedToExecuteCommand", params);
         s_log.error(msg, e);
         JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), msg);
      }
   }

   private boolean writeCsvFile(TableExportCsvController ctrl)
   {
      File file = null;
      try
      {


         file = ctrl.getFile();
         if(null != file.getParentFile())
         {
            file.getParentFile().mkdirs();
         }

         FileWriter fw = new FileWriter(file);
         BufferedWriter bw = new BufferedWriter(fw);

         String separator = ctrl.getSeparatorChar();
         boolean includeHeaders = ctrl.includeHeaders();
         boolean complete = ctrl.exportComplete();

         int nbrSelRows = _table.getSelectedRowCount();
         if(0 == nbrSelRows || complete)
         {
            nbrSelRows = _table.getRowCount();
         }

         int nbrSelCols = _table.getSelectedColumnCount();
         if(0 == nbrSelCols || complete)
         {
            nbrSelCols = _table.getColumnCount();
         }

         int[] selRows = _table.getSelectedRows();
         if(0 == selRows.length || complete)
         {
            selRows = new int[nbrSelRows];
            for (int i = 0; i < selRows.length; i++)
            {
               selRows[i] = i;
            }
         }

         int[] selCols = _table.getSelectedColumns();
         if(0 == selCols.length || complete)
         {
            selCols = new int[nbrSelCols];
            for (int i = 0; i < selCols.length; i++)
            {
               selCols[i] = i;
            }
         }


         if (includeHeaders)
         {
            for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
            {
               String columnName = _table.getColumnName(selCols[colIdx]);
               bw.write(getData(separator, columnName));
               if(nbrSelCols -1 > colIdx)
               {
                  bw.write(separator);
               }
            }
            bw.write('\n');
         }


         for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
         {
            for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
            {
               Object cellObj;
               if(ctrl.useGloablPrefsFormatting() && _table.getColumnModel().getColumn(colIdx) instanceof ExtTableColumn)
               {
                  ExtTableColumn col = (ExtTableColumn) _table.getColumnModel().getColumn(colIdx);
                  cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);

                  if(null != cellObj)
                  {
                     cellObj = CellComponentFactory.renderObject(cellObj, col.getColumnDisplayDefinition());
                  }
               }
               else
               {
                  cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);
               }
               bw.write(getData(separator, cellObj));

               if(nbrSelCols -1 > colIdx)
               {
                  bw.write(separator);
               }
            }
            bw.write('\n');
         }

         bw.flush();
         fw.flush();
         bw.close();
         fw.close();

         return true;

      }
      catch (IOException e)
      {

         Object[] params = new Object[]{file, e.getMessage()};
         // i18n[TableExportCsvCommand.failedToWriteCsvFile=Failed to write file\n{0}\nError message\n{1}\nSee last log entry for details.]
         String msg = s_stringMgr.getString("TableExportCsvCommand.failedToWriteCsvFile", params);
         s_log.error(msg, e);
         JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), msg);
         return false;
      }
   }

   private String getData(String sepChar, Object cellObj)
   {
      if (cellObj == null)
      {
         return "";
      }
      else
      {
         String ret = cellObj.toString().trim();

         if(0 <= ret.indexOf(sepChar) || 0 <= ret.indexOf('\n'))
         {
            ret = "\"" + ret.replaceAll("\"", "\"\"") + "\"";
         }

         return ret;
      }
   }
}
