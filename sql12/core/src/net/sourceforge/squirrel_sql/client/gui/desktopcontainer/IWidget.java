package net.sourceforge.squirrel_sql.client.gui.desktopcontainer;

import javax.swing.*;
import java.beans.PropertyChangeListener;

public interface IWidget
{
   void dispose();

   void setTitle(String title);

   void updateUI();

   void setVisible(boolean b);

   public void addNotify();


   /**
    *
    * @return Will return null if tabbed mode is used.
    */
   JInternalFrame getInternalFrame();

   String getTitle();

   void moveToFront();

   void addWidgetListener(WidgetListener widgetListener);

   void removeWidgetListener(WidgetListener widgetListener);

   void putClientProperty(Object key, Object prop);

   Object getClientProperty(Object key);

   boolean isToolWindow();

   boolean isClosed();

   boolean isIcon();
}
