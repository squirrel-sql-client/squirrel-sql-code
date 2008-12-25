package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

public interface TabHandleListener
{
   void tabClosing(TabHandleEvent tabHandleEvent);

   void tabClosed(TabHandleEvent tabHandleEvent);

   void tabAdded(TabHandleEvent tabHandleEvent);

   void tabSelected(TabHandleEvent tabHandleEvent);

   void tabDeselected(TabHandleEvent tabHandleEvent);
}
