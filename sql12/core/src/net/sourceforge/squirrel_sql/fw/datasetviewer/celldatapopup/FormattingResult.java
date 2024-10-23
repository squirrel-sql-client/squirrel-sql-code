package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

public class FormattingResult
{
   private final String _result;
   private final boolean _success;

   public FormattingResult(String result, boolean success)
   {
      _result = result;
      _success = success;
   }

   public String getResult()
   {
      return _result;
   }

   public boolean isSuccess()
   {
      return _success;
   }
}
