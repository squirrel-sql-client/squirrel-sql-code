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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Exports {@link IExportData} to a Excel file.
 * <b>Note:</b> This class is the result of a refactoring task. The code was
 * taken from TableExportCsvCommand.
 *
 * @author Stefan Willinger
 */
public class DataExportExcelWriter extends AbstractDataExportFileWriter
{

   private Workbook workbook; // The streaming api export for (very) large xlsx files
   private Sheet sheet; // We write the data to this sheet
   private File file; // File where the export is written to
   private boolean withHeader = false;
   private HashMap<String, CellStyle> formatCache = null;

   /**
    * @param file
    * @param prefs
    * @param includeHeaders
    * @param exportFormat
    * @param progressController
    */
   public DataExportExcelWriter(File file, TableExportPreferences prefs, ProgressAbortCallback progressController)
   {
      super(file, prefs, progressController);
   }

   private Cell getXlsCell(ColumnDisplayDefinition colDef, int colIdx, int curRow, Object cellObj)
   {
      Row row = sheet.getRow(curRow);
      if (row == null)
      {
         row = sheet.createRow(curRow);
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
      CreationHelper creationHelper = workbook.getCreationHelper();
      CellStyle cellStyle;
      if (formatCache == null)
      {
         cellStyle = workbook.createCellStyle();
         cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat(format));
         formatCache = new HashMap<String, CellStyle>();
         formatCache.put(format, cellStyle);
      }
      else
      {
         cellStyle = formatCache.get(format);
         if (cellStyle == null)
         {
            cellStyle = workbook.createCellStyle();
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

   /**
    * @throws java.io.IOException
    * @see AbstractDataExportFileWriter#beforeWorking()
    */
   @Override
   protected void beforeWorking(File file) throws IOException
   {
      if (getPrefs().isFormatXLSOld())
      {
         this.workbook = new HSSFWorkbook(); // See https://gist.github.com/madan712/3912272
      }
      else
      {
         this.workbook = new SXSSFWorkbook(100); // keep 100 rows in memory, exceeding rows will be flushed to disk
      }

      this.file = file;
      this.sheet = workbook.createSheet("Squirrel SQL Export");
   }

   /**
    * @throws java.lang.Exception
    * @see AbstractDataExportFileWriter#addHeaderCell(int,
    * java.lang.String)
    */
   @Override
   protected void addHeaderCell(int colIdx, String columnName) throws Exception
   {
      this.withHeader = true;
      Row headerRow = sheet.getRow(0);
      if (headerRow == null)
      {
         headerRow = sheet.createRow(0);
      }
      Cell cell = headerRow.createCell(colIdx);
      cell.setCellValue(columnName);
   }

   /**
    * @see AbstractDataExportFileWriter#addCell(TableExportController,
    * int, int,
    * IExportDataCell)
    */
   @Override
   protected void addCell(IExportDataCell cell)
   {
      if (getPrefs().isUseGlobalPrefsFormating())
      {
         getXlsCell(cell.getColumnDisplayDefinition(), cell.getColumnIndex(), calculateRowIdx(cell), cell.getObject());
      }
      else
      {
         getXlsCell(null, cell.getColumnIndex(), calculateRowIdx(cell), cell.getObject());
      }
   }

   /**
    * @param cell
    * @return
    */
   private int calculateRowIdx(IExportDataCell cell)
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

   /**
    * @throws java.lang.Exception
    * @see AbstractDataExportFileWriter#afterWorking()
    */
   @Override
   protected void afterWorking() throws Exception
   {
      FileOutputStream out = new FileOutputStream(this.file);
      this.workbook.write(out);
      out.close();

      // dispose of temporary files backing this workbook on disk
      if (workbook instanceof SXSSFWorkbook)
      {
         ((SXSSFWorkbook) workbook).dispose();
      }
   }

}
