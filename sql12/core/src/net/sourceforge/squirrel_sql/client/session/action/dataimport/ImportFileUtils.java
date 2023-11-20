package net.sourceforge.squirrel_sql.client.session.action.dataimport;

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

   public static String getFileNameWithoutEnding(String importFileName)
   {

      if(importFileName.toLowerCase().endsWith(".xls"))
      {
         return importFileName.substring(0, importFileName.length() - 4);
      }
      else if(importFileName.toLowerCase().endsWith(".xlsx"))
      {
         return importFileName.substring(0, importFileName.length() - 5);
      }
      else if(importFileName.toLowerCase().endsWith(".csv"))
      {
         return importFileName.substring(0, importFileName.length() - 4);
      }
      else
      {
         return importFileName;
      }
   }
}
