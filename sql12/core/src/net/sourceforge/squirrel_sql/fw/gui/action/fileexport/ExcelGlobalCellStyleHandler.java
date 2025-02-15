package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelGlobalCellStyleHandler
{
   private final TableExportPreferences _prefs;
   private final Workbook _workbook;
   private CellStyle _globalCellStyle = null;
   private CellStyle _headerCellStyle;

   public ExcelGlobalCellStyleHandler(TableExportPreferences prefs, Workbook workbook)
   {
      _prefs = prefs;
      _workbook = workbook;

      initGlobalCellStyle(prefs);
      initHeaderCellStyle(prefs, workbook);
   }

   private void initHeaderCellStyle(TableExportPreferences prefs, Workbook workbook)
   {
      if(false == prefs.isExcelHeaderFontNoSelection())
      {
         Font headerFont;
         headerFont = _workbook.createFont();
         FontInfo fontInfo = ExcelFontUtil.toFontHeaderInfo(prefs, false);

         headerFont.setFontHeightInPoints((short) fontInfo.getSize());
         headerFont.setFontName(fontInfo.getFamily());

         headerFont.setBold(fontInfo.isBold() || prefs.isExcelFirstRowBold());

         headerFont.setItalic(fontInfo.isItalic());
         _headerCellStyle = _workbook.createCellStyle();
         _headerCellStyle.setFont(headerFont);
      }
      else if(prefs.isExcelFirstRowBold())
      {
         if(null == _headerCellStyle)
         {
            _headerCellStyle = _workbook.createCellStyle();
         }

         Font boldFont = workbook.createFont();
         boldFont.setBold(true);
         _headerCellStyle.setFont(boldFont);
      }

      if(prefs.isExcelFirstRowCentered())
      {
         if(null == _headerCellStyle)
         {
            _headerCellStyle = _workbook.createCellStyle();
         }
         _headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
      }
   }

   private void initGlobalCellStyle(TableExportPreferences prefs)
   {
      if(false == prefs.isExcelFontNoSelection())
      {
         Font font = _workbook.createFont();
         FontInfo fontInfo = ExcelFontUtil.toFontInfo(prefs, false);

         font.setFontHeightInPoints((short) fontInfo.getSize());
         font.setFontName(fontInfo.getFamily());
         font.setBold(fontInfo.isBold());
         font.setItalic(fontInfo.isItalic());
         _globalCellStyle = _workbook.createCellStyle();
         _globalCellStyle.setFont(font);
      }
   }

   public void handleStyle(Cell cell)
   {
      if(null != _globalCellStyle)
      {
         cell.setCellStyle(_globalCellStyle);
      }
   }

   public CellStyle createCellStyle()
   {
      CellStyle cellStyle = _workbook.createCellStyle();

      if(null != _globalCellStyle)
      {
         cellStyle.cloneStyleFrom(_globalCellStyle);
      }

      return cellStyle;
   }

   public XSSFCellStyle createXSSFCellStyle()
   {
      XSSFCellStyle cellStyle = ((XSSFWorkbook)_workbook).createCellStyle();
      if(null != _globalCellStyle)
      {
         cellStyle.cloneStyleFrom(_globalCellStyle);
      }
      return cellStyle;
   }

   public CellStyle getHeaderCellStyle()
   {
      return _headerCellStyle;
   }
}
