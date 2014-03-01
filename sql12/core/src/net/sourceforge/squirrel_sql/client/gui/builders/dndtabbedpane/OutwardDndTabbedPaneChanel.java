package net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane;

import net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane.DnDTabbedPaneData;
import net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane.OutwardDndTabbedPaneChanelListener;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.DockTabDesktopPaneHolder;

import javax.swing.*;

public interface OutwardDndTabbedPaneChanel
{
   DnDTabbedPaneData getDndTabbedPaneData();

   void setDndTabbedPaneData(DnDTabbedPaneData dnDTabbedPaneData);

   void putListener(JTabbedPane tabbedPane, OutwardDndTabbedPaneChanelListener l);

   void dragDropEnd();

   void moveDraggedTabTo(JTabbedPane tabbedPane, int targetTabIndex);
}
