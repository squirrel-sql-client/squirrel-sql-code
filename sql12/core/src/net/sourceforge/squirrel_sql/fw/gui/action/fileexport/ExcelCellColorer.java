package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.awt.Color;

/**
 * See https://poi.apache.org/components/spreadsheet/quick-guide.html#CustomColors
 */
public class ExcelCellColorer
{
   public static void color(Cell excelCell, Color color, ExcelGlobalCellStyleHandler cellStyleHandler)
   {
      if(null == color)
      {
         return;
      }

      if(excelCell instanceof XSSFCell)
      {
         colorXSSFCell((XSSFCell)excelCell, color, cellStyleHandler);
      }
      else if (excelCell instanceof SXSSFCell)
      {
         // Just to document we have not yet found a way to color cells of SXSSFWorkbook.
         // Note a SXSSFWorkbook is capable writing very big Excel sheets (up to 1048565 rows).
      }
      else if (excelCell instanceof HSSFCell)
      {
         // Coloring of Excel 97 documents is not implemented yet.
         // There is a (rather complicated) description in
         // https://poi.apache.org/components/spreadsheet/quick-guide.html#CustomColors
      }
   }

   private static void colorXSSFCell(XSSFCell excelCell, Color color, ExcelGlobalCellStyleHandler cellStyleHandler)
   {
      // This will result in just one color
      // XSSFCellStyle cellStyle = excelCell.getCellStyle();
      // if(cellStyle == null)
      // {
      //    cellStyle = excelCell.getSheet().getWorkbook().createCellStyle();
      // }

      XSSFCellStyle cellStyle = cellStyleHandler.createXSSFCellStyle();

      final XSSFColor xssfColor = new XSSFColor(color, new DefaultIndexedColorMap());
      cellStyle.setFillForegroundColor(xssfColor);
      //cellStyle.setFillBackgroundColor(xssfColor);

      cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      excelCell.setCellStyle(cellStyle);
   }
}
