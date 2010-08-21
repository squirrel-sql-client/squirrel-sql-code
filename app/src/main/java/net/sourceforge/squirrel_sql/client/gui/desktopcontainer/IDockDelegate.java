package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

public interface IDockDelegate extends IDelegateBase
{
   void addDockWidgetListener(WidgetListener widgetListener);
   void removeDockWidgetListener(WidgetListener widgetListener);
}
