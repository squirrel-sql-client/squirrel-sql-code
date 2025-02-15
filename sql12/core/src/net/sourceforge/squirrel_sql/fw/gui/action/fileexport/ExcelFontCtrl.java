package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.Font;

public class ExcelFontCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ExcelFontCtrl.class);

   private FontInfo _fontInfo;
   private FontInfo _headerFontInfo;

   private boolean _noSelection;
   private boolean _headerNoSelection;

   public void initDataAndLabels(TableExportPreferences prefs, MultipleLineLabel lblExcelFontName, MultipleLineLabel lblExcelHeaderFontName)
   {
      _noSelection = prefs.isExcelFontNoSelection();
      _fontInfo =  ExcelFontUtil.toFontInfo(prefs, _noSelection);
      initFontLabel(lblExcelFontName, null == _fontInfo ? null : _fontInfo.createFont(), _noSelection);

      _headerNoSelection = prefs.isExcelHeaderFontNoSelection();
      _headerFontInfo =  ExcelFontUtil.toFontHeaderInfo(prefs, _headerNoSelection);
      initHeaderFontLabel(lblExcelHeaderFontName, null == _headerFontInfo ? null : _headerFontInfo.createFont(), _headerNoSelection);
   }


   public void writeToPrefs(TableExportPreferences prefs)
   {
      prefs.setExcelFontNoSelection(_noSelection);
      fontInfoToPrefs(prefs);

      prefs.setExcelHeaderFontNoSelection(_headerNoSelection);
      headerFontInfoToPrefs(prefs);
   }

   private void fontInfoToPrefs(TableExportPreferences prefs)
   {
      if(false == _noSelection)
      {
         prefs.setExcelFontFamily(_fontInfo.getFamily());
         prefs.setExcelFontSize(_fontInfo.getSize());
         prefs.setExcelFontBold(_fontInfo.isBold());
         prefs.setExcelFontItalic(_fontInfo.isItalic());
      }
   }

   private void headerFontInfoToPrefs(TableExportPreferences prefs)
   {
      if(false == _headerNoSelection)
      {
         prefs.setExcelHeaderFontFamily(_headerFontInfo.getFamily());
         prefs.setExcelHeaderFontSize(_headerFontInfo.getSize());
         prefs.setExcelHeaderFontBold(_headerFontInfo.isBold());
         prefs.setExcelHeaderFontItalic(_headerFontInfo.isItalic());
      }
   }

   public void initFontLabel(MultipleLineLabel lblFontName, Font font, boolean noSelection)
   {
      FontInfo fontInfo = null;
      lblFontName.setText(s_stringMgr.getString("ExcelFontInfo.font.default"));
      if(false == noSelection)
      {
         fontInfo = new FontInfo(font);
         lblFontName.setText(fontInfo.toString());
      }
      _fontInfo = fontInfo;
      _noSelection = noSelection;
   }

   public void initHeaderFontLabel(MultipleLineLabel lblHeaderFontName, Font font, boolean noSelection)
   {
      FontInfo fontInfo = null;
      lblHeaderFontName.setText(s_stringMgr.getString("ExcelFontInfo.font.default"));
      if(false == noSelection)
      {
         fontInfo = new FontInfo(font);
         lblHeaderFontName.setText(fontInfo.toString());
      }
      _headerFontInfo = fontInfo;
      _headerNoSelection = noSelection;
   }

   public Font getFont()
   {
      if(null != _fontInfo)
      {
         return _fontInfo.createFont();
      }
      return null;
   }

   public Font getHeaderFont()
   {
      if(null != _headerFontInfo)
      {
         return _headerFontInfo.createFont();
      }
      return null;
   }
}
