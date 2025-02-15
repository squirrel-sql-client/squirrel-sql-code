package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;

public class ExcelFontUtil
{
   public static FontInfo toFontInfo(TableExportPreferences prefs, boolean noSelection)
   {
      if(noSelection)
      {
         return null;
      }

      FontInfo ret = new FontInfo();
      ret.setFamily(prefs.getExcelFontFamily());
      ret.setSize(prefs.getExcelFontSize());
      ret.setIsBold(prefs.isExcelFontBold());
      ret.setIsItalic(prefs.isExcelFontItalic());

      return ret;
   }

   public static FontInfo toFontHeaderInfo(TableExportPreferences prefs, boolean headerNoSelection)
   {
      if(headerNoSelection)
      {
         return null;
      }

      FontInfo ret = new FontInfo();
      ret.setFamily(prefs.getExcelHeaderFontFamily());
      ret.setSize(prefs.getExcelHeaderFontSize());
      ret.setIsBold(prefs.isExcelHeaderFontBold());
      ret.setIsItalic(prefs.isExcelHeaderFontItalic());

      return ret;
   }
}
