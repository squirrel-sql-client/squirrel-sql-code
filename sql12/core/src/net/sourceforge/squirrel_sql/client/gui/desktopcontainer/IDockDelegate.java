package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.TabHandle;

public interface IDockDelegate extends IDelegateBase
{
   void addDockWidgetListener(WidgetListener widgetListener);
   void removeDockWidgetListener(WidgetListener widgetListener);

   TabHandle getTabHandle();
}
