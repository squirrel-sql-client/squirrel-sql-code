package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import java.io.File;

public class MultipleSqlResultExportDestinationInfo
{
   private boolean _destinationExcel;
   private File _exportDir;
   private File _excelExportFile;

   private MultipleSqlResultExportDestinationInfo()
   {
   }

   public static MultipleSqlResultExportDestinationInfo createExportDir(File file)
   {
      MultipleSqlResultExportDestinationInfo ret = new MultipleSqlResultExportDestinationInfo();
      ret._destinationExcel = false;
      ret._exportDir = file;
      return ret;
   }

   public static MultipleSqlResultExportDestinationInfo createExcelExportFile(File file)
   {
      MultipleSqlResultExportDestinationInfo ret = new MultipleSqlResultExportDestinationInfo();
      ret._destinationExcel = true;
      ret._excelExportFile = file;
      return ret;
   }

   public boolean isDestinationExcel()
   {
      return _destinationExcel;
   }

   public File getExportDir()
   {
      return _exportDir;
   }

   public File getExcelExportFile()
   {
      return _excelExportFile;
   }
}
