package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.mainframe.SquirrelDesktopManager;

import javax.swing.*;
import java.awt.*;

public interface IDesktopContainer
{
   void addWidget(DialogWidget widget);
   void addWidget(DockWidget widget);
   void addWidget(TabWidget widget);

   IWidget[] getAllWidgets();
   IWidget getSelectedWidget();

   Dimension getRequiredSize();

   void setPreferredSize(Dimension size);

   void revalidate();

   Dimension getSize();

   void putClientProperty(String key, String value);

   void setDesktopManager(SquirrelDesktopManager desktopManager);

   JComponent getComponent();

   Rectangle getBounds();
}
