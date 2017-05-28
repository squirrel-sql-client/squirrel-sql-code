package net.sourceforge.squirrel_sql.client.session.mainpanel.multiclipboard;

import java.util.ArrayList;

public class PasteHistory
{
   private ArrayList<String> _history = new ArrayList<String>();

   public void addToPasteHistory(String clipContent)
   {
      _history.remove(clipContent);
      _history.add(0, clipContent);

      while (20  < _history.size())
      {
         _history.remove(20);
      }
   }

   public String[] getHistroy()
   {
      return _history.toArray(new String[_history.size()]);
   }
}
