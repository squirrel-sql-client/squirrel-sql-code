package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.SmallTabButton;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop.TabHandle;

public interface ITabDelegate extends IDelegateBase
{
   void addTabWidgetListener(WidgetListener widgetListener);
   void removeTabWidgetListener(WidgetListener widgetListener);

   void addSmallTabButton(SmallTabButton smallTabButton);
   void removeSmallTabButton(SmallTabButton smallTabButton);

   TabHandle getTabHandle();
}