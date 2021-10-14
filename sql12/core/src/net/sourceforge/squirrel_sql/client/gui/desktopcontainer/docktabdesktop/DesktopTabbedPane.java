package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import javax.swing.Icon;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane.DnDTabbedPane;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.ButtonTabComponent;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallTabButton;

public class DesktopTabbedPane extends DnDTabbedPane
{
   public DesktopTabbedPane(IApplication app)
   {
      super(app.getMultipleWindowsHandler().getOutwardDndTabbedPaneChanel());
      setPaintScrollArea(false);
      setPaintGhost(true);

      GUIUtils.listenToMouseWheelClickOnTab(this, (tabIndex, tabComponent) -> ((ButtonTabComponent)tabComponent).doClickClose());
   }


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
      super.addTab("", null, component, tip);
      int index = indexOfComponent(component);
      setTabComponentAt(index, new ButtonTabComponent(this, title, icon));
   }

   @Override
   public void insertTab(String title, Icon icon, Component component, String tip, int index)
   {
      super.insertTab("", null, component, tip, index);
      setTabComponentAt(index, new ButtonTabComponent(this, title, icon));
   }


   public void doProcessMouseEvent(MouseEvent e)
   {
      super.processMouseEvent(e);
   }

   public void addSmallTabButtonAt(int index, SmallTabButton smallTabButton)
   {
      ButtonTabComponent btc = (ButtonTabComponent) getTabComponentAt(index);
      btc.addSmallTabButton(smallTabButton);
   }

   public void removeSmallTabButtonAt(int index, SmallTabButton smallTabButton)
   {
      ButtonTabComponent btc = (ButtonTabComponent) getTabComponentAt(index);
      btc.removeSmallTabButton(smallTabButton);
   }
}



