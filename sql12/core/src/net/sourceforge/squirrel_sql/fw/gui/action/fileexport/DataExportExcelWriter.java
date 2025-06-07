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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
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
   private boolean _withHeader = false;
   private HashMap<String, CellStyle> _formatCache = null;
   private ExcelGlobalCellStyleHandler _globalCellStyleHandler;

   public DataExportExcelWriter(File file, TableExportPreferences prefs, ProgressAbortCallback progressController)
   {
      _fileExportService = new FileExportService(file, prefs, progressController);
   }

   public long write(ExportDataInfoList exportDataInfoList, TableExportPreferences prefs) throws Exception
   {
      long rowsCount = 0;

      _fileExportService.progress(s_stringMgr.getString("DataExportExcelWriter.beginWriting.file", _fileExportService.getFile()));

      beforeWorking();

      _globalCellStyleHandler = new ExcelGlobalCellStyleHandler(prefs, _workbook);

      for (ExportDataInfo exportDataInfo : exportDataInfoList.getExportDataInfos())
      {
         _sheet = _workbook.createSheet(exportDataInfo.getExcelSheetTabName());

         rowsCount += _writeExcelTab(exportDataInfo.getExportData());

         if(prefs.isExcelFirstRowFrozen())
         {
            _sheet.createFreezePane(0, 1);
         }

         if(prefs.isExcelAutoFilter())
         {
            _sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, exportDataInfo.getExportData().getColumnCount() - 1));
         }

         if(prefs.isExcelExportSQLStatementInAdditionalSheet() && null != exportDataInfo.getResultSetExportData())
         {
            ExcelSQLStatementSheet.createSqlStatementSheet(exportDataInfo, _workbook);
         }
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
         _withHeader = true;

         Iterator<String> headerColumns = data.getHeaderColumns();
         ExcelHeaderRowBuilder excelHeaderRowBuilder = new ExcelHeaderRowBuilder(_globalCellStyleHandler, _sheet);

         int colIdx = 0;
         while (headerColumns.hasNext())
         {

            //Cell cell = headerRow.createCell(colIdx);
            Cell cell = excelHeaderRowBuilder.createHeaderCell(colIdx);
            cell.setCellValue(headerColumns.next());

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
      Cell cell = row.createCell(colIdx);
      _globalCellStyleHandler.handleStyle(cell);

      if (null == cellObj || null == colDef)
      {
         cell.setCellValue(getDataXLSAsString(cellObj));
         return cell;
      }

      int colType = colDef.getSqlType();

      switch (colType)
      {
         case Types.BIT:
         case Types.BOOLEAN:
            cell.setCellValue((Boolean) cellObj);
            break;
         case Types.INTEGER:
            cell.setCellValue(((Number) cellObj).intValue());
            break;
         case Types.SMALLINT:
         case Types.TINYINT:
            cell.setCellValue(((Number) cellObj).shortValue());
            break;
         case Types.NUMERIC:
         case Types.DECIMAL:
         case Types.FLOAT:
         case Types.DOUBLE:
         case Types.REAL:
            cell.setCellValue(((Number) cellObj).doubleValue());
            break;
         case Types.BIGINT:
            cell.setCellValue(Long.parseLong(cellObj.toString()));
            break;
         case Types.DATE:
            makeTemporalCell(cell, (Date) cellObj, "m/d/yy");
            break;
         case Types.TIMESTAMP:
            makeTemporalCell(cell, (Date) cellObj, "m/d/yy h:mm");
            break;
         case Types.TIME:
            makeTemporalCell(cell, (Date) cellObj, "h:mm");
            break;
         case Types.CHAR:
         case Types.VARCHAR:
         case Types.LONGVARCHAR:
            cellObj = CellComponentFactory.renderObject(cellObj, colDef);
            cell.setCellValue(getDataXLSAsString(cellObj));
            break;
         default:
            cellObj = CellComponentFactory.renderObject(cellObj, colDef);
            cell.setCellValue(getDataXLSAsString(cellObj));
      }

      return cell;
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
      if (_formatCache == null)
      {
         cellStyle = _globalCellStyleHandler.createCellStyle();
         cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
         _formatCache = new HashMap<>();
         _formatCache.put(format, cellStyle);
      }
      else
      {
         cellStyle = _formatCache.get(format);
         if (cellStyle == null)
         {
            cellStyle = _globalCellStyleHandler.createCellStyle();
            cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
            _formatCache.put(format, cellStyle);
         }
      }
      retVal.setCellStyle(cellStyle);
      if (null != cellObj)
      {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(cellObj);
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

      ExcelCellColorer.color(excelCell, cell.getExcelExportColor(), _globalCellStyleHandler);
   }

   private int calculateRowIdx(ExportCellData cell)
   {
      if (this._withHeader)
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
      _workbook.write(out);
      out.flush();
      out.close();

      _workbook.close();
   }

}
