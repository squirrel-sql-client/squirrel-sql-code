package net.sourceforge.squirrel_sql.plugins.dataimport;

import java.io.File;

public class ImportFileUtils
{
   public static ImportFileType determineType(File f)
   {
      if (f.getName().toLowerCase().endsWith(".xls") || f.getName().toLowerCase().endsWith(".xlsx"))
      {
         return ImportFileType.XLS;
      }

      return ImportFileType.CSV;
   }

   public static String getFileNameWithoutEnding(File importFile)
   {

      if(importFile.getName().toLowerCase().endsWith(".xls"))
      {
         return importFile.getName().substring(0, importFile.getName().length() - 4);
      }
      else if(importFile.getName().toLowerCase().endsWith(".xlsx"))
      {
         return importFile.getName().substring(0, importFile.getName().length() - 5);
      }
      else if(importFile.getName().toLowerCase().endsWith(".csv"))
      {
         return importFile.getName().substring(0, importFile.getName().length() - 4);
      }
      else
      {
         return importFile.getName();
      }
   }
}
