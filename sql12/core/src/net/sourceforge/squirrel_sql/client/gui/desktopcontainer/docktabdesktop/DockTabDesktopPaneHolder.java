package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import java.util.ArrayList;
import javax.swing.JTabbedPane;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.TabWidget;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallTabButton;

public interface DockTabDesktopPaneHolder
{
   void setSelected(boolean b);

   void tabDragedAndDroped();

   void addTabWidgetAt(TabWidget widget, int tabIndex, ArrayList<SmallTabButton> externalButtons, boolean widgetMovedButNotCreated);

   RemoveTabHandelResult removeTabHandel(int tabIndex);

   boolean isMyTabbedPane(JTabbedPane tabbedPane);

   void addTabWidget(TabWidget widget, ArrayList<SmallTabButton> externalButtons, boolean widgetMovedButNotCreated);
}
