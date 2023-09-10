package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import org.apache.commons.lang3.StringUtils;

public enum FileEndings
{
   CSV("csv"),
   XLSX("xlsx"),
   XLS("xls"),
   XML("xml"),
   JSON("json");

   private String _ending;

   FileEndings(String ending)
   {
      _ending = ending;
   }

   public static boolean fileEndsWithOneOf(String fileNameToTest)
   {
      if(StringUtilities.isEmpty(fileNameToTest, true))
      {
         return false;
      }

      for (FileEndings fileEnding : values())
      {
         if (fileEnding.fileEndsWith(fileNameToTest))
         {
            return true;
         }
      }

      return false;
   }

   public boolean fileEndsWith(String fileNameToTest)
   {
      return StringUtils.endsWithIgnoreCase(fileNameToTest, "." + _ending);
   }

   public static String getByTableExportPreferences(TableExportPreferences prefs)
   {
      if(prefs.isFormatCSV())
      {
         return CSV.get();
      }
      else if(prefs.isFormatJSON())
      {
         return JSON.get();
      }
      else if(prefs.isFormatXML())
      {
         return XML.get();
      }
      else if(prefs.isFormatXLS())
      {
         return XLSX.get();
      }
      else if(prefs.isFormatXLSOld())
      {
         return XLS.get();
      }
      else
      {
         throw new IllegalStateException("Could not find file ending by TableExportPreferences");
      }
   }

   public String get()
   {
      return _ending;
   }
}
