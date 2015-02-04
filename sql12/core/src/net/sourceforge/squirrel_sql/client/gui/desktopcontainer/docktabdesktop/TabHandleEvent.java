package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import java.awt.event.ActionEvent;

public class TabHandleEvent
{
   private TabHandle _tabHandle;
   private ActionEvent _e;
   private boolean _wasAddedToToMainApplicationWindow;
   private boolean _widgetMovedButNotCreated;

   public TabHandleEvent(TabHandle tabHandle, ActionEvent e)
   {
      this(tabHandle, e, false, false);
   }

   public TabHandleEvent(TabHandle tabHandle, ActionEvent e, boolean wasAddedToToMainApplicationWindow, boolean widgetMovedButNotCreated)
   {
      _tabHandle = tabHandle;
      _e = e;
      _wasAddedToToMainApplicationWindow = wasAddedToToMainApplicationWindow;
      _widgetMovedButNotCreated = widgetMovedButNotCreated;
   }

   public TabHandle getTabHandle()
   {
      return _tabHandle;
   }

   public boolean isWasAddedToToMainApplicationWindow()
   {
      return _wasAddedToToMainApplicationWindow;
   }

   public boolean isWidgetMovedButNotCreated()
   {
      return _widgetMovedButNotCreated;
   }
}
