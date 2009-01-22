package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class DesktopTabbedPane extends JTabbedPane
{
   @Override
   public Dimension getMinimumSize()
   {
      return new Dimension(0, super.getMinimumSize().height);
   }

   @Override
   public Dimension getMaximumSize()
   {
      return new Dimension(100000, super.getMaximumSize().height);
   }

   @Override
   public void setTitleAt(int index, String title)
   {
      ButtonTabComponent btc = (ButtonTabComponent) getTabComponentAt(index);
      btc.setTitle(title);
   }

   @Override
   public void setIconAt(int index, Icon icon)
   {
      ButtonTabComponent btc = (ButtonTabComponent) getTabComponentAt(index);
      btc.setIcon(icon);
   }

   @Override
   public void addTab(String title, Icon icon, Component component, String tip)
   {
      super.addTab(title, icon, component, tip);
      int index = indexOfComponent(component);
      setTabComponentAt(index, new ButtonTabComponent(this, title, icon));
   }


   public void doProcessMouseEvent(MouseEvent e)
   {
      super.processMouseEvent(e);
   }
}



