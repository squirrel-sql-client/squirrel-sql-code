package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import java.awt.event.ActionEvent;

public class TabHandleEvent
{
   private TabHandle _tabHandle;
   private ActionEvent _e;

   public TabHandleEvent(TabHandle tabHandle, ActionEvent e)
   {
      _tabHandle = tabHandle;
      _e = e;
   }

   public TabHandle getTabHandle()
   {
      return _tabHandle;
   }
}
