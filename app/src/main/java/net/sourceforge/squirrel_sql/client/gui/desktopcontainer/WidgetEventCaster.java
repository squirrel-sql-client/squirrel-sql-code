package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import java.util.ArrayList;

public class WidgetEventCaster
{
   private ArrayList<WidgetListener> _listeners = new ArrayList<WidgetListener>();

   public void addDockWidgetListener(WidgetListener widgetListener)
   {
      _listeners.add(widgetListener);
   }

   public void removeDockWidgetListener(WidgetListener widgetListener)
   {
      _listeners.remove(widgetListener);
   }

   public void addDialogWidgetListener(WidgetListener widgetListener)
   {
      _listeners.add(widgetListener);
   }

   public void removeDialogWidgetListener(WidgetListener widgetListener)
   {
      _listeners.remove(widgetListener);
   }

   public void addTabWidgetListener(WidgetListener widgetListener)
   {
      _listeners.add(widgetListener);
   }

   public void removeTabWidgetListener(WidgetListener widgetListener)
   {
      _listeners.remove(widgetListener);
   }


   public void fireWidgetOpened(WidgetEvent widgetEvent)
   {
      WidgetListener[] clone = _listeners.toArray(new WidgetListener[_listeners.size()]);

      for (WidgetListener listener : clone)
      {
         listener.widgetOpened(widgetEvent);
      }
   }

   public void fireWidgetClosing(WidgetEvent widgetEvent)
   {
      WidgetListener[] clone = _listeners.toArray(new WidgetListener[_listeners.size()]);

      for (WidgetListener listener : clone)
      {
         listener.widgetClosing(widgetEvent);
      }
   }

   public void fireWidgetClosed(WidgetEvent widgetEvent)
   {
      WidgetListener[] clone = _listeners.toArray(new WidgetListener[_listeners.size()]);

      for (WidgetListener listener : clone)
      {
         listener.widgetClosed(widgetEvent);
      }
   }

   public void fireWidgetIconified(WidgetEvent widgetEvent)
   {
      WidgetListener[] clone = _listeners.toArray(new WidgetListener[_listeners.size()]);

      for (WidgetListener listener : clone)
      {
         listener.widgetIconified(widgetEvent);
      }
   }

   public void fireWidgetDeiconified(WidgetEvent widgetEvent)
   {
      WidgetListener[] clone = _listeners.toArray(new WidgetListener[_listeners.size()]);

      for (WidgetListener listener : clone)
      {
         listener.widgetDeiconified(widgetEvent);
      }
   }

   public void fireWidgetActivated(WidgetEvent widgetEvent)
   {
      WidgetListener[] clone = _listeners.toArray(new WidgetListener[_listeners.size()]);

      for (WidgetListener listener : clone)
      {
         listener.widgetActivated(widgetEvent);
      }
   }

   public void fireWidgetDeactivated(WidgetEvent widgetEvent)
   {
      WidgetListener[] clone = _listeners.toArray(new WidgetListener[_listeners.size()]);

      for (WidgetListener listener : clone)
      {
         listener.widgetDeactivated(widgetEvent);
      }
   }

}
