package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

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
         if(fileNameToTest.toLowerCase().endsWith("." + fileEnding._ending.toLowerCase()))
         {
            return true;
         }
      }

      return false;
   }

   public String get()
   {
      return _ending;
   }
}
