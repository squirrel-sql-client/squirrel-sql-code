package net.sourceforge.squirrel_sql.client.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModificationAwareSessionTitle
{
   private List<ModificationAwareSessionTitleChangeListener> _listeners = new ArrayList<>();

   private String _title;

   public String getTitle()
   {
      return _title;
   }

   public void setTitle(String newTitle)
   {
      if(Objects.equals(_title, newTitle))
      {
         return;
      }

      String oldVal = _title;
      _title = newTitle;

      ModificationAwareSessionTitleChangeListener[] buf = _listeners.toArray(new ModificationAwareSessionTitleChangeListener[0]);
      for (int i = 0; i < buf.length; i++)
      {
         buf[i].titleChanged(oldVal, _title);
      }
   }

   public void addListener(ModificationAwareSessionTitleChangeListener listener)
   {
      _listeners.remove(listener);
      _listeners.add(listener);
   }

   public void removeListener(ModificationAwareSessionTitleChangeListener titleChangeListener)
   {
      _listeners.remove(titleChangeListener);
   }
}
