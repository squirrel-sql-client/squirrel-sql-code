package net.sourceforge.squirrel_sql.client.session.mainpanel.multiclipboard;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.util.ArrayList;

public class PasteHistory
{
   private ArrayList<String> _history = new ArrayList<>();

   public void addToPasteHistory(String clipContent)
   {
      if(StringUtilities.isEmpty(clipContent) || 30000 < clipContent.length())
      {
         return;
      }

      _history.remove(clipContent);
      _history.add(0, clipContent);

      while (20  < _history.size())
      {
         _history.remove(20);
      }
   }

   public String[] getHistory()
   {
      return _history.toArray(new String[_history.size()]);
   }
}
