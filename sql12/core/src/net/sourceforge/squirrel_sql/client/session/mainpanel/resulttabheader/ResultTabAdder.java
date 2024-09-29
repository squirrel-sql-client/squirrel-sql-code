package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabheader;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

public class ResultTabAdder
{
   private JTabbedPane _tabbedExecutionsPanel;

   public void setTabbedPane(JTabbedPane tabbedExecutionsPanel)
   {
      _tabbedExecutionsPanel = tabbedExecutionsPanel;
   }

   public int add(String title, JPanel panel)
   {
      return add(title, null, panel, null);
   }

   public int add(String title, ImageIcon tabIcon, JPanel tabPanel, String toolTip)
   {
      _tabbedExecutionsPanel.addTab(title, tabIcon, tabPanel, toolTip);

      int tabIx = _tabbedExecutionsPanel.indexOfComponent(tabPanel);
      final ResultTabComponent tabComponent = createTabComponent(title, tabIcon);
      _tabbedExecutionsPanel.setTabComponentAt(tabIx, tabComponent);


      return tabIx;
   }

   public int insert(String title, ImageIcon tabIcon, JPanel tabPanel, String toolTip, int indexToReplace)
   {
      _tabbedExecutionsPanel.insertTab(title, tabIcon, tabPanel, toolTip, indexToReplace);
      int tabIx = _tabbedExecutionsPanel.indexOfComponent(tabPanel);
      _tabbedExecutionsPanel.setTabComponentAt(tabIx, createTabComponent(title, tabIcon));

      return tabIx;
   }

   private ResultTabComponent createTabComponent(String title, ImageIcon tabIcon)
   {
      return new ResultTabComponent(new JLabel(title, tabIcon, SwingConstants.LEFT));
   }

   public void setIconAt(int tabIx, ImageIcon icon)
   {
      if(_tabbedExecutionsPanel.getTabComponentAt(tabIx) instanceof ResultTabComponent)
      {
         ((ResultTabComponent)_tabbedExecutionsPanel.getTabComponentAt(tabIx)).setIcon(icon);
      }
   }

   public ResultTabComponent getResultTabComponentAt(int tabIx)
   {
      if(_tabbedExecutionsPanel.getTabComponentAt(tabIx) instanceof ResultTabComponent)
      {
         return (ResultTabComponent) _tabbedExecutionsPanel.getTabComponentAt(tabIx);
      }
      return null;
   }
}
