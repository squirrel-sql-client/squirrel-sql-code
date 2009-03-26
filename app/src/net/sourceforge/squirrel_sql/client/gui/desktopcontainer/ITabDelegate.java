package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

public interface ITabDelegate extends IDelegateBase
{
   void addTabWidgetListener(WidgetListener widgetListener);
   void removeTabWidgetListener(WidgetListener widgetListener);
}