package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import java.util.EventListener;

public interface WidgetListener extends EventListener
{
   public void widgetOpened(WidgetEvent evt);

   public void widgetClosing(WidgetEvent evt);

   public void widgetClosed(WidgetEvent evt);

   public void widgetIconified(WidgetEvent evt);

   public void widgetDeiconified(WidgetEvent evt);

   public void widgetActivated(WidgetEvent evt);

   public void widgetDeactivated(WidgetEvent evt);
}
