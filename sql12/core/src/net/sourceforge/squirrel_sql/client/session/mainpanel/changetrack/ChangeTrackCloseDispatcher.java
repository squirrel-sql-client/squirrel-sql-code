package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import java.util.ArrayList;
import java.util.List;

public class ChangeTrackCloseDispatcher
{
   private List<ChangeTrackCloseListener> _changeTrackCloseListeners = new ArrayList<>();

   public void close()
   {
      for (ChangeTrackCloseListener changeTrackCloseListener : _changeTrackCloseListeners.toArray(new ChangeTrackCloseListener[0]))
      {
         changeTrackCloseListener.changeTrackClosed();
      }
   }

   public void addChangeTrackCloseListener(ChangeTrackCloseListener changeTrackCloseListener)
   {
      _changeTrackCloseListeners.remove(changeTrackCloseListener);
      _changeTrackCloseListeners.add(changeTrackCloseListener);
   }

   public void removeChangeTrackCloseListener(ChangeTrackCloseListener changeTrackCloseListener)
   {
      _changeTrackCloseListeners.remove(changeTrackCloseListener);
   }

}
