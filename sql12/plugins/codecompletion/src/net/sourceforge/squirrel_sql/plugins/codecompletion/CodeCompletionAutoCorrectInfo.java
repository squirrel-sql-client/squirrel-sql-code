package net.sourceforge.squirrel_sql.plugins.codecompletion;



public class CodeCompletionAutoCorrectInfo extends CodeCompletionInfo
{
   private String _toCorrect;
   private String _correction;

   public CodeCompletionAutoCorrectInfo(String toCorrect, String correction)
   {
      _toCorrect = toCorrect;
      _correction = correction;
   }

   public String getCompareString()
   {
      return _toCorrect;
   }

   public String toString()
   {
      return _correction + " ;-)";
   }

   public String getCompletionString()
   {
      return _correction;
   }
}
