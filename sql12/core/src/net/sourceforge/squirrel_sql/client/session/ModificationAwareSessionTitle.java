package net.sourceforge.squirrel_sql.client.session;

import java.util.Objects;

public class ModificationAwareSessionTitle
{
   private ModificationAwareSessionTitleChangeListener _listener;

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

      if(null != _listener)
      {
         _listener.titleChanged(oldVal, _title);
      }
   }

   public void setListener(ModificationAwareSessionTitleChangeListener listener)
   {
      _listener = listener;
   }
}
