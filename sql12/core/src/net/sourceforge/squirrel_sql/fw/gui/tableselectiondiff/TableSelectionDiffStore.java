package net.sourceforge.squirrel_sql.fw.gui.tableselectiondiff;

public class TableSelectionDiffStore
{
   private String _markdown;

   public void storeMarkdown(String markdown)
   {
      _markdown = markdown;
   }

   public String retrieveAndClearMarkdown()
   {
      try
      {
         return _markdown;
      }
      finally
      {
         _markdown = null;
      }
   }
}
