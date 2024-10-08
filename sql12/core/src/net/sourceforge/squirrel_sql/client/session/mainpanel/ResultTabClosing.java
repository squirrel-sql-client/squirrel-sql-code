package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.mainpanel.custompanel.CustomResultPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabheader.ResultTabComponent;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

public class ResultTabClosing
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultTabClosing.class);

   private final TabIconManager _tabIconManager;
   private final JTabbedPane _tabbedExecutionsPanel;

   public ResultTabClosing(TabIconManager tabIconManager, JTabbedPane tabbedExecutionsPanel)
   {
      _tabIconManager = tabIconManager;
      _tabbedExecutionsPanel = tabbedExecutionsPanel;
   }

   public boolean confirmSqlPanelClose(ArrayList<JComponent> allTabs)
   {
      for (JComponent tab : allTabs)
      {
         if (isAnchored(tab))
         {
            String msg = s_stringMgr.getString("AnchorTabClosingHelper.removeAnchoredPanel.close.sqlpanel.with.anchored.msg");
            String title = s_stringMgr.getString("AnchorTabClosingHelper.removeAnchoredPanel.close.sqlpanel.with.anchored.title");

            if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(Main.getApplication().getMainFrame(), msg, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE))
            {
               return true;
            }
            else
            {
               return false;
            }
         }
      }

      return true;
   }


   public void closeTabs(List<JComponent> tabs)
   {
      closeTabs(tabs, false);
   }

   public void closeTabs(List<JComponent> tabs, boolean isMemoryCleanUp)
   {
      boolean closeAll = true;

      if(false == isMemoryCleanUp && 0 < tabs.stream().filter(t -> isAnchored(t)).count())
      {
         String[] selectionValues =
               {
                     s_stringMgr.getString("AnchorTabClosingHelper.removeAnchoredPanel.opt.remove.non.anchored.only"),
                     s_stringMgr.getString("AnchorTabClosingHelper.removeAnchoredPanel.opt.remove.all"),
                     s_stringMgr.getString("AnchorTabClosingHelper.removeAnchoredPanel.opt.remove.cancel")
               };


         int selectIndex = JOptionPane.showOptionDialog(
               _tabbedExecutionsPanel,
               s_stringMgr.getString("AnchorTabClosingHelper.removeAnchoredPanel"),
               s_stringMgr.getString("AnchorTabClosingHelper.removeAnchoredPanel.title"),
               JOptionPane.DEFAULT_OPTION,
               JOptionPane.QUESTION_MESSAGE,
               null,
               selectionValues,
               selectionValues[0]);

         if (selectIndex == 0)
         {
            closeAll = false;
         }
         else if (selectIndex == 1)
         {
            closeAll = true;
         }
         else
         {
            return;
         }
      }


      for (JComponent tab : tabs)
      {
         if (closeAll || false == isAnchored(tab))
         {
            _closeTab(tab);
         }
      }
   }

   void closeTab(JComponent tab)
   {
      if(isAnchored(tab))
      {
         String msg = s_stringMgr.getString("AnchorTabClosingHelper.removeAnchoredPanel.close.anchored.msg");
         String title = s_stringMgr.getString("AnchorTabClosingHelper.removeAnchoredPanel.close.anchored.title");

         if(JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(Main.getApplication().getMainFrame(), msg, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE))
         {
            return;
         }
      }


      _closeTab(tab);
   }

   /**
    * Closes the tab silently
    */
   public void closeTabAt(int index)
   {
      JComponent tab = (JComponent) _tabbedExecutionsPanel.getComponentAt(index);
      _closeTab(tab);
   }



   public boolean isAnchored(JComponent tab)
   {
      int indexOfTab = TabbedExcutionPanelUtil.getIndexOfTab(tab, _tabbedExecutionsPanel);
      return isAnchoredAt(indexOfTab);
   }

   public boolean isAnchoredAt(int indexOfTab)
   {
      return _tabIconManager.getAnchorIcon() == getIconAt(indexOfTab);
   }

   private Icon getIconAt(int indexOfTab)
   {
      if(null == _tabbedExecutionsPanel.getTabComponentAt(indexOfTab))
      {
         return _tabbedExecutionsPanel.getIconAt(indexOfTab);
      }
      else if(_tabbedExecutionsPanel.getTabComponentAt(indexOfTab) instanceof ResultTabComponent)
      {
         return ((ResultTabComponent)_tabbedExecutionsPanel.getTabComponentAt(indexOfTab)).getIcon();
      }
      else if(_tabbedExecutionsPanel.getTabComponentAt(indexOfTab) instanceof JLabel)
      {
         return ((JLabel)_tabbedExecutionsPanel.getTabComponentAt(indexOfTab)).getIcon();
      }
      else
      {
         // Introduced for
         // java.lang.ClassCastException:
         // class net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.ButtonTabComponent cannot be cast to class javax.swing.JLabel
         //  at net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTabClosing.getIconAt(ResultTabClosing.java:154)
         //  at net.sourceforge.squirrel_sql.client.session.mainpanel.ResultTabClosing.isAnchoredAt(ResultTabClosing.java:143)
         //  at net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecutorPanel.checkResultTabLimit(SQLResultExecutorPanel.java:817)
         //  at net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecutorPanel.addResultsTab(SQLResultExecutorPanel.java:748)
         //  at net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecutorPanel.onAddResultsTab(SQLResultExecutorPanel.java:690)
         //  at net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecutorPanel$2.lambda$addResultsTab$0(SQLResultExecutorPanel.java:310)
         return null;
      }
   }

   private void _closeTab(Component tab)
   {
      if (tab instanceof ErrorPanel)
      {
         _tabbedExecutionsPanel.remove(tab);
      }
      else if (tab instanceof ResultTab)
      {
         ((ResultTab) tab).disposeTab();
         _tabbedExecutionsPanel.remove(tab);
      }
      else if (tab instanceof CancelPanel)
      {
         ((CancelPanel) tab).closeBtn.doClick();
      }
      else if (tab instanceof CustomResultPanel )
      {
         _tabbedExecutionsPanel.remove(tab);
         ((CustomResultPanel) tab).dispose();
      }
   }
}
