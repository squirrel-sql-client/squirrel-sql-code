package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class ExportDataInfoList
{
   public static final ExportDataInfoList EMPTY = new ExportDataInfoList(Collections.EMPTY_LIST);

   private List<ExportDataInfo> _exportDataInfos;
   private MultipleSqlResultExportDestinationInfo _multipleSqlResultExportDestinationInfo;

   public static ExportDataInfoList single(IExportData data)
   {
      return new ExportDataInfoList(List.of(new ExportDataInfo(data)));
   }

   /**
    * Instaed of calling this from outside use
    * {@link #single(IExportData)} or {@link #EMPTY}.
    */
   private ExportDataInfoList(List<ExportDataInfo> exportDataInfos)
   {
      this(exportDataInfos, null);
   }

   public ExportDataInfoList(List<ExportDataInfo> exportDataInfos, MultipleSqlResultExportDestinationInfo multipleSqlResultExportDestinationInfo)
   {
      _exportDataInfos = exportDataInfos;
      _exportDataInfos.forEach( edi -> edi.setMultipleSqlResultExportDestinationInfo(multipleSqlResultExportDestinationInfo));
      _multipleSqlResultExportDestinationInfo = multipleSqlResultExportDestinationInfo;
   }

   public MultipleSqlResultExportDestinationInfo getMultipleSqlResultExportDestinationInfo()
   {
      return _multipleSqlResultExportDestinationInfo;
   }

   public List<ExportDataInfo> getExportDataInfos()
   {
      return _exportDataInfos;
   }

   public boolean isEmpty()
   {
      return _exportDataInfos.isEmpty();
   }

   public File getFirstExportedFile(TableExportPreferences prefs)
   {
      if(isEmpty())
      {
         return null;
      }

      return _exportDataInfos.get(0).getFile(prefs);
   }
}
