package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.plaf.metal.MetalTabbedPaneUI;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.dndtabbedpane.DnDTabbedPane;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.ButtonTabComponent;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallTabButton;

public class DesktopTabbedPane extends DnDTabbedPane
{
    boolean _hideTabBar = false;
    JLabel _titleLabel;

   public DesktopTabbedPane(IApplication app)
   {
      super(app.getMultipleWindowsHandler().getOutwardDndTabbedPaneChanel());
      _hideTabBar = app.getSquirrelPreferences().getUseNewFramePerConnection();
      setPaintScrollArea(false);
      setPaintGhost(true);
       setUI(new MetalTabbedPaneUI() {
           @Override
           protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
               if (!_hideTabBar) {
                   super.paintContentBorder(g, tabPlacement, selectedIndex);
               }
           }
           @Override
           protected int calculateTabAreaHeight(int tab_placement, int run_count, int max_tab_height) {
               if (_hideTabBar && DesktopTabbedPane.this.getTabCount() <= 1) {
                   return 0;
               } else {
                   return super.calculateTabAreaHeight(tab_placement, run_count, max_tab_height);
               }
           }
       });
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
      _titleLabel.setText(title.replaceAll("^[0-9]* - ", ""));
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



