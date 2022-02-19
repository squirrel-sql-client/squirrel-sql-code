package net.sourceforge.squirrel_sql.client.gui;

public class ViewLogSheetAppendState
{
   private char _levelChar = ' ';

   /**
    * If an unknown levelChar is passed we keep the previous one.
    */
   public void setLevelChar(char levelChar)
   {
      char previousLevelChar = _levelChar;
      _levelChar = levelChar;

      if(isUnknown())
      {
         _levelChar = previousLevelChar;
      }
   }

   public boolean isError()
   {
      return 'E' == _levelChar;
   }

   public boolean isWarn()
   {
      return 'W' == _levelChar;
   }

   public boolean isInfo()
   {
      return 'I' == _levelChar;
   }

   public boolean isDebug()
   {
      return 'D' == _levelChar;
   }

   public boolean isUnknown()
   {
      return !isDebug() && !isInfo() && !isWarn() && !isError();
   }
}
