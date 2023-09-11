package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.TabHandle;

import javax.swing.*;
import java.awt.*;

public interface IDialogDelegate extends IDelegateBase
{
   void addDialogWidgetListener(WidgetListener l);

   void removeDialogWidgetListener(WidgetListener l);

   JRootPane getRootPane();

   Window getParentWindow();

   int getWidth();

   int getHeight();

   TabHandle getTabHandle();
}
