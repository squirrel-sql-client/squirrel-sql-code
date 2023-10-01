/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Types;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Exports {@link IExportData} to a Excel file.
 * <b>Note:</b> This class is the result of a refactoring task. The code was
 * taken from TableExportCsvCommand.
 *
 * @author Stefan Willinger
 */
public class DataExportExcelWriter
{
   public static final String DEFAULT_EXCEL_EXPORT_SHEET_NAME = "Squirrel SQL Export";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataExportExcelWriter.class);

   private final FileExportService _fileExportService;
   private Workbook _workbook; // The streaming api export for (very) large xlsx files
   private Sheet _sheet; // We write the data to this sheet
   private boolean withHeader = false;
   private HashMap<String, CellStyle> formatCache = null;

   public DataExportExcelWriter(File file, TableExportPreferences prefs, ProgressAbortCallback progressController)
   {
      _fileExportService = new FileExportService(file, prefs, progressController);
   }

   public long write(ExportDataInfoList exportDataInfoList) throws Exception
   {
      long rowsCount = 0;

      _fileExportService.progress(s_stringMgr.getString("DataExportExcelWriter.beginWriting.file", _fileExportService.getFile()));

      beforeWorking();

      for (ExportDataInfo exportDataInfo : exportDataInfoList.getExportDataInfos())
      {
         this._sheet = _workbook.createSheet(exportDataInfo.getExcelSheetTabName());

         rowsCount += _writeExcelTab(exportDataInfo.getExportData());
      }
      _fileExportService.progress(s_stringMgr.getString("DataExportExcelWriter.finishedLoading", NumberFormat.getInstance().format(rowsCount)));

      _fileExportService.progress(s_stringMgr.getString("DataExportExcelWriter.closingTheFile"));
      // All sheets and cells added. Now write out the workbook
      afterWorking();

      _fileExportService.progress(s_stringMgr.getString("DataExportExcelWriter.done"));

      _fileExportService.setProgressFinished();

      if (_fileExportService.isUserCanceled())
      {
         return -1;
      }
      else
      {
         return rowsCount;
      }

   }

   private long _writeExcelTab(IExportData data)
   {
      if (_fileExportService.getPrefs().isWithHeaders())
      {
         Iterator<String> headers = data.getHeaders();

         int colIdx = 0;
         while (headers.hasNext())
         {
            String columnName = headers.next();
            addHeaderCell(colIdx, columnName);
            colIdx++;
         }
      }

      Iterator<ExportDataRow> rows = data.getRows();

      long rowsCount = 0;

      long begin = System.currentTimeMillis();
      while (rows.hasNext() && _fileExportService.isUserCanceled() == false)
      {
         rowsCount++;
         ExportDataRow aRow = rows.next();
         if (_fileExportService.isStatusUpdateNecessary())
         {
            long secondsPassed = (System.currentTimeMillis() - begin) / 1000;
            _fileExportService.taskStatus(s_stringMgr.getString("DataExportExcelWriter.numberOfRowsCompletedInSeconds", NumberFormat.getInstance().format(rowsCount), secondsPassed));
         }

         Iterator<ExportCellData> cells = aRow.getCells();
         while (cells.hasNext())
         {
            ExportCellData cell = cells.next();
            addCell(cell);
         }
      }

      return rowsCount;
   }

   private Cell writeXlsCell(ColumnDisplayDefinition colDef, int colIdx, int curRow, Object cellObj)
   {
      Row row = _sheet.getRow(curRow);
      if (row == null)
      {
         row = _sheet.createRow(curRow);
      }
      Cell retVal = row.createCell(colIdx);

      if (null == cellObj || null == colDef)
      {
         retVal.setCellValue(getDataXLSAsString(cellObj));
         return retVal;
      }

      int colType = colDef.getSqlType();

      switch (colType)
      {
         case Types.BIT:
         case Types.BOOLEAN:
            retVal.setCellValue((Boolean) cellObj);
            break;
         case Types.INTEGER:
            retVal.setCellValue(((Number) cellObj).intValue());
            break;
         case Types.SMALLINT:
         case Types.TINYINT:
            retVal.setCellValue(((Number) cellObj).shortValue());
            break;
         case Types.NUMERIC:
         case Types.DECIMAL:
         case Types.FLOAT:
         case Types.DOUBLE:
         case Types.REAL:
            retVal.setCellValue(((Number) cellObj).doubleValue());
            break;
         case Types.BIGINT:
            retVal.setCellValue(Long.parseLong(cellObj.toString()));
            break;
         case Types.DATE:
            makeTemporalCell(retVal, (Date) cellObj, "m/d/yy");
            break;
         case Types.TIMESTAMP:
            makeTemporalCell(retVal, (Date) cellObj, "m/d/yy h:mm");
            break;
         case Types.TIME:
            makeTemporalCell(retVal, (Date) cellObj, "h:mm");
            break;
         case Types.CHAR:
         case Types.VARCHAR:
         case Types.LONGVARCHAR:
            cellObj = CellComponentFactory.renderObject(cellObj, colDef);
            retVal.setCellValue(getDataXLSAsString(cellObj));
            break;
         default:
            cellObj = CellComponentFactory.renderObject(cellObj, colDef);
            retVal.setCellValue(getDataXLSAsString(cellObj));
      }

      return retVal;
   }

   /*
    * note POI which we use for the excel export has a limit of 4000 styles
    * therefore formatCache has been introduced to re-use styles across cells.
    * This resolves bug https://sourceforge.net/p/squirrel-sql/bugs/1177/
    */
   private void makeTemporalCell(Cell retVal, Date cellObj, String format)
   {
      CreationHelper creationHelper = _workbook.getCreationHelper();
      CellStyle cellStyle;
      if (formatCache == null)
      {
         cellStyle = _workbook.createCellStyle();
         cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
         formatCache = new HashMap<String, CellStyle>();
         formatCache.put(format, cellStyle);
      }
      else
      {
         cellStyle = formatCache.get(format);
         if (cellStyle == null)
         {
            cellStyle = _workbook.createCellStyle();
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
            formatCache.put(format, cellStyle);
         }
      }
      retVal.setCellStyle(cellStyle);
      if (null != cellObj)
      {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime((Date) cellObj);
         retVal.setCellValue(calendar);
      }
   }

   private String getDataXLSAsString(Object cellObj)
   {
      if (cellObj == null)
      {
         return "";
      }
      else
      {
         return cellObj.toString().trim();
      }
   }

   private void beforeWorking()
   {
      if (_fileExportService.getPrefs().isFormatXLSOld())
      {
         this._workbook = new HSSFWorkbook(); // See https://gist.github.com/madan712/3912272
      }
      else
      {
         if(_fileExportService.getPrefs().isUseColoring())
         {
            // See class ExcelCellColorer on how this will take care the Excel gets colored.
            this._workbook = new XSSFWorkbook();
         }
         else
         {
            this._workbook = new SXSSFWorkbook(100); // keep 100 rows in memory, exceeding rows will be flushed to disk
         }
      }
   }

   private void addHeaderCell(int colIdx, String columnName)
   {
      this.withHeader = true;
      Row headerRow = _sheet.getRow(0);
      if (headerRow == null)
      {
         headerRow = _sheet.createRow(0);
      }
      Cell cell = headerRow.createCell(colIdx);
      cell.setCellValue(columnName);
   }

   private void addCell(ExportCellData cell)
   {
      final Cell excelCell;
      if (_fileExportService.getPrefs().isUseGlobalPrefsFormating())
      {
         excelCell = writeXlsCell(cell.getColumnDisplayDefinition(), cell.getColumnIndex(), calculateRowIdx(cell), cell.getObject());
      }
      else
      {
         excelCell = writeXlsCell(null, cell.getColumnIndex(), calculateRowIdx(cell), cell.getObject());
      }

      ExcelCellColorer.color(excelCell, cell.getExcelExportColor());
   }

   private int calculateRowIdx(ExportCellData cell)
   {
      if (this.withHeader)
      {
         return cell.getRowIndex() + 1;
      }
      else
      {
         return cell.getRowIndex();
      }
   }

   private void afterWorking() throws Exception
   {
      FileOutputStream out = new FileOutputStream(_fileExportService.getFile());
      this._workbook.write(out);
      out.close();

      // dispose of temporary files backing this workbook on disk
      if (_workbook instanceof SXSSFWorkbook)
      {
         ((SXSSFWorkbook) _workbook).dispose();
      }
   }

}
