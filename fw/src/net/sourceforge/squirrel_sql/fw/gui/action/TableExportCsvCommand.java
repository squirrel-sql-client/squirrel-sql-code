package net.sourceforge.squirrel_sql.fw.gui.action;

import jxl.Workbook;
import jxl.write.WritableCell;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.Calendar;

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


      if(writeFile(ctrl))
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

   private boolean writeFile(TableExportCsvController ctrl)
   {
      File file = null;
      try
      {

         file = ctrl.getFile();
         if(null != file.getParentFile())
         {
            file.getParentFile().mkdirs();
         }


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

         if(TableExportCsvController.EXPORT_FORMAT_CSV == ctrl.getExportFormat())
         {
            return writeCSV(file, ctrl, includeHeaders, nbrSelCols, selCols, nbrSelRows, selRows);
         }
         else if(TableExportCsvController.EXPORT_FORMAT_XLS == ctrl.getExportFormat())
         {
            return writeXLS(file, includeHeaders, nbrSelCols, selCols, nbrSelRows, ctrl, selRows);
         }
         else
         {
            throw new IllegalStateException("Unknown export format " + ctrl.getExportFormat());
         }


      }
      catch (IOException e)
      {

         Object[] params = new Object[]{file, e.getMessage()};
         // i18n[TableExportCsvCommand.failedToWriteFile=Failed to write file\n{0}\nError message\n{1}\nSee last log entry for details.]
         String msg = s_stringMgr.getString("TableExportCsvCommand.failedToWriteFile", params);
         s_log.error(msg, e);
         JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), msg);
         return false;
      }
      catch (jxl.write.WriteException e)
      {

         Object[] params = new Object[]{file, e.getMessage()};
         // i18n[TableExportCsvCommand.failedToWriteFile=Failed to write file\n{0}\nError message\n{1}\nSee last log entry for details.]
         String msg = s_stringMgr.getString("TableExportCsvCommand.failedToWriteFile", params);
         s_log.error(msg, e);
         JOptionPane.showMessageDialog(GUIUtils.getMainFrame(), msg);
         return false;
      }

   }


   private boolean writeXLS(File file, boolean includeHeaders, int nbrSelCols, int[] selCols, int nbrSelRows, TableExportCsvController ctrl, int[] selRows)
      throws IOException, WriteException
   {
      WritableWorkbook workbook = Workbook.createWorkbook(file);
      WritableSheet sheet = workbook.createSheet("Squirrel SQL Export", 0);


      int curRow= 0;
      if (includeHeaders)
      {
         for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
         {
            String columnName = _table.getColumnName(selCols[colIdx]);
            jxl.write.Label label = new jxl.write.Label(colIdx, curRow, columnName);
            sheet.addCell(label);
         }
         curRow++;
      }


      for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
      {
         for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
         {
            WritableCell xlsCell;
            if(ctrl.useGloablPrefsFormatting() && _table.getColumnModel().getColumn(colIdx) instanceof ExtTableColumn)
            {
               ExtTableColumn col = (ExtTableColumn) _table.getColumnModel().getColumn(colIdx);
               xlsCell = getXlsCell(col, colIdx, curRow, _table.getValueAt(selRows[rowIdx], selCols[colIdx]));
            }
            else
            {
               xlsCell = getXlsCell(null, colIdx, curRow, _table.getValueAt(selRows[rowIdx], selCols[colIdx]));
            }
            sheet.addCell(xlsCell);

         }
         curRow++;
      }

      // All sheets and cells added. Now write out the workbook
      workbook.write();
      workbook.close();

      return true;
   }

   private WritableCell getXlsCell(ExtTableColumn col, int colIdx, int curRow, Object cellObj)
   {
      if(null == cellObj)
      {
         return new jxl.write.Label(colIdx, curRow, getDataXLSAsString(cellObj));         
      }

      if(null == col)
      {
         return new jxl.write.Label(colIdx, curRow, getDataXLSAsString(cellObj));
      }


      WritableCell ret;
      ColumnDisplayDefinition colDef = col.getColumnDisplayDefinition();
      int colType = colDef.getSqlType();
      switch (colType)
      {
         case Types.BIT:
         case Types.BOOLEAN:
            ret = new jxl.write.Boolean(colIdx, curRow, (Boolean) cellObj);
            break;
         case Types.INTEGER:
            ret = new jxl.write.Number(colIdx, curRow, (float) (Integer) cellObj);
            break;
         case Types.SMALLINT:
         case Types.TINYINT:
            ret = new jxl.write.Number(colIdx, curRow, (float) (Short) cellObj);
            break;
         case Types.DECIMAL:
            ret = new jxl.write.Number(colIdx, curRow, (float) (Integer) cellObj);
            break;
         case Types.NUMERIC:
            ret = new jxl.write.Number(colIdx, curRow, ((BigDecimal) cellObj).floatValue());
            break;
         case Types.FLOAT:
            ret = new jxl.write.Number(colIdx, curRow, (float) (Integer) cellObj);
            break;
         case Types.DOUBLE:
            ret = new jxl.write.Number(colIdx, curRow, new Float((Double) cellObj));
            break;
         case Types.REAL:
            ret = new jxl.write.Number(colIdx, curRow, (Float) cellObj);
            break;
         case Types.BIGINT:
            ret = new jxl.write.Number(colIdx, curRow, (float) (Integer) cellObj);
            break;
         case Types.DATE:
         case Types.TIMESTAMP:
         case Types.TIME:
            /* Work arround some UTC and Daylight saving offsets */
            long time = (((java.util.Date) cellObj).getTime());

            Calendar cal = Calendar.getInstance();
            cal.setTime((java.util.Date) cellObj);

            int offset = (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET));
            java.util.Date xlsUTCDate = new java.util.Date(time + offset);
            ret = new jxl.write.DateTime(colIdx, curRow, xlsUTCDate, jxl.write.DateTime.GMT);
            break;
         case Types.CHAR:
         case Types.VARCHAR:
         case Types.LONGVARCHAR:
            cellObj =
               CellComponentFactory.renderObject(cellObj,
                  col.getColumnDisplayDefinition());
            ret = new jxl.write.Label(colIdx, curRow, getDataXLSAsString(cellObj));
            break;
         default:
            Class c = cellObj.getClass();
            String s = c.getName();
            cellObj = CellComponentFactory.renderObject(cellObj, col.getColumnDisplayDefinition());
            ret = new jxl.write.Label(colIdx, curRow, getDataXLSAsString(cellObj));
      }
      return ret;
   }


   private String getDataXLSAsString(Object cellObj)
   {
      if (cellObj == null)
      {
         return "";
      }
      else
      {
         return  cellObj.toString().trim();
      }
   }



   private boolean writeCSV(File file, TableExportCsvController ctrl, boolean includeHeaders, int nbrSelCols, int[] selCols, int nbrSelRows, int[] selRows)
      throws IOException
   {
      FileWriter fw = new FileWriter(file);
      BufferedWriter bw = new BufferedWriter(fw);

      String separator = ctrl.getSeparatorChar();


      if (includeHeaders)
      {
         for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
         {
            String columnName = _table.getColumnName(selCols[colIdx]);
            bw.write(getDataCSV(separator, columnName));
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
            bw.write(getDataCSV(separator, cellObj));

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

   private String getDataCSV(String sepChar, Object cellObj)
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
