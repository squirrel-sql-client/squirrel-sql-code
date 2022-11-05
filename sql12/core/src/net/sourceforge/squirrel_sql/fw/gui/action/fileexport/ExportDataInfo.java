package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.io.File;

public class ExportDataInfo
{
   private IExportData _exportData;
   private String _exportNameFileNormalized;
   private MultipleSqlResultExportDestinationInfo _multipleSqlResultExportDestinationInfo;

   public ExportDataInfo(IExportData exportData)
   {
      this(exportData, null);
   }

   public ExportDataInfo(IExportData exportData, String exportNameFileNormalized)
   {
      _exportData = exportData;
      _exportNameFileNormalized = exportNameFileNormalized;
   }

   public IExportData getExportData()
   {
      return _exportData;
   }

   public void setMultipleSqlResultExportDestinationInfo(MultipleSqlResultExportDestinationInfo multipleSqlResultExportDestinationInfo)
   {
      _multipleSqlResultExportDestinationInfo = multipleSqlResultExportDestinationInfo;
   }

   public File getFile(TableExportPreferences prefs)
   {
      if(   StringUtilities.isEmpty(_exportNameFileNormalized, true)
         || null == _multipleSqlResultExportDestinationInfo)
      {
         return new File(prefs.getFile());
      }

      if(_multipleSqlResultExportDestinationInfo.isDestinationExcel())
      {
         return _multipleSqlResultExportDestinationInfo.getExcelExportFile();
      }
      else
      {
         final String fileNameWithExtension = _exportNameFileNormalized + "." + FileEndings.getByTableExportPreferences(prefs);
         return new File(_multipleSqlResultExportDestinationInfo.getExportDir(), fileNameWithExtension);
      }
   }

   public String getExcelSheetTabName()
   {
      if(StringUtilities.isEmpty(_exportNameFileNormalized, true))
      {
         return DataExportExcelWriter.DEFAULT_EXCEL_EXPORT_SHEET_NAME;
      }
      else
      {
         return _exportNameFileNormalized;
      }

   }
}
