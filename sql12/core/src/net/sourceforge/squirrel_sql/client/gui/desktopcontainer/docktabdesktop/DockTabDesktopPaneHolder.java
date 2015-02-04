package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.TabWidget;

import javax.swing.*;
import java.util.ArrayList;

public interface DockTabDesktopPaneHolder
{
   void setSelected(boolean b);

   void tabDragedAndDroped();

   void addTabWidgetAt(TabWidget widget, int tabIndex, ArrayList<SmallTabButton> externalButtons, boolean widgetMovedButNotCreated);

   RemoveTabHandelResult removeTabHandel(int tabIndex);

   boolean isMyTabbedPane(JTabbedPane tabbedPane);

   void addTabWidget(TabWidget widget, ArrayList<SmallTabButton> externalButtons, boolean widgetMovedButNotCreated);
}
