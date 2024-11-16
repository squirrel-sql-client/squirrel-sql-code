package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

public enum DisplayAsciiMode
{
   ASCII_NO, ASCII_DEFAULT, ASCII_NO_ADDITIONAL_SPACES;

   public static boolean isShowAscii(DisplayAsciiMode mode)
   {
      return mode == ASCII_DEFAULT || mode == ASCII_NO_ADDITIONAL_SPACES;
   }
}
