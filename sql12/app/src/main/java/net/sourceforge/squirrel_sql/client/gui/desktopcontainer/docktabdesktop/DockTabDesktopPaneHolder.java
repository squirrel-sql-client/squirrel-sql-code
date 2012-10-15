package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

public interface DockTabDesktopPaneHolder
{
   DockTabDesktopPane getDockTabDesktopPane();

   void setSelected(boolean b);

   void tabDragedAndDroped();
}
