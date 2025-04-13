package net.sourceforge.squirrel_sql.client.globalsearch;

public class SearchExecutorResult
{
   public boolean hasResult()
   {
      return true;
   }

   public String getCellTextTillFirstOccurence()
   {
      return "This is ";
   }

   public String getFirstMatchingText()
   {
      return "just";
   }

   public String getCellTextAfterFirstOccurence()
   {
      return " a test.";
   }
}
