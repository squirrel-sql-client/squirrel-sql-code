package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

public class FormattingResult
{
   private final String _result;
   private final boolean _success;
   private final FormattingResultType _formattingResultType;

   public FormattingResult(String result, boolean success, FormattingResultType formattingResultType)
   {
      _result = result;
      _success = success;
      _formattingResultType = formattingResultType;
   }

   public String getResult()
   {
      return _result;
   }

   public boolean isSuccess()
   {
      return _success;
   }

   public FormattingResultType getFormattingResultType()
   {
      return _formattingResultType;
   }
}
