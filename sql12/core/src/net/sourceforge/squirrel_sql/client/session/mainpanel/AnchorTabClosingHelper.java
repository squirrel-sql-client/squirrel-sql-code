package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnchorTabClosingHelper
{
   private enum CloseAction
   {
      NON_ANCHORED_ONLY, ALL, CANCELED
   }


   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AnchorTabClosingHelper.class);

   private final TabIconManager _tabIconManager;
   private final JTabbedPane _tabbedExecutionsPanel;

   private CloseAction _closeAction = null;

   public AnchorTabClosingHelper(TabIconManager tabIconManager, JTabbedPane tabbedExecutionsPanel)
   {
      _tabIconManager = tabIconManager;
      _tabbedExecutionsPanel = tabbedExecutionsPanel;
   }

   public void prepareClosing(List<JComponent> tabsToPrepare)
   {
      if(0 == tabsToPrepare.size())
      {
         return;
      }
      else if(1 == tabsToPrepare.size())
      {
         prepareSingleTab(tabsToPrepare.get(0));
      }
      else
      {
         prepareMultipleTabs(tabsToPrepare);
      }
   }

   private void prepareSingleTab(JComponent tab)
   {
      int indexOfTab = TabbedExcutionPanelUtil.getIndexOfTab(tab, _tabbedExecutionsPanel);

      _closeAction = CloseAction.NON_ANCHORED_ONLY;

      if (_tabIconManager.getAnchorIcon() == _tabbedExecutionsPanel.getIconAt(indexOfTab))
      {
         String msg = s_stringMgr.getString("AnchorTabClosingHelper.removeAnchoredPanel.close.anchored.msg");
         String title = s_stringMgr.getString("AnchorTabClosingHelper.removeAnchoredPanel.close.anchored.title");

         if(JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(_tabbedExecutionsPanel, msg, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE))
         {
            _closeAction = CloseAction.ALL;
         }
      }
   }

   private void prepareMultipleTabs(List<JComponent> tabsToPrepare)
   {
      for (JComponent tab : tabsToPrepare)
      {
         int indexOfTab = TabbedExcutionPanelUtil.getIndexOfTab(tab, _tabbedExecutionsPanel);

         if (_tabIconManager.getAnchorIcon() == _tabbedExecutionsPanel.getIconAt(indexOfTab))
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
               _closeAction = CloseAction.NON_ANCHORED_ONLY;
               break;
            }
            else if (selectIndex == 1)
            {
               _closeAction = CloseAction.ALL;
               break;
            }
            else
            {
               _closeAction = CloseAction.CANCELED;
               return;
            }
         }
      }
   }

   public boolean shouldClose(JComponent tab)
   {
      if(null == _closeAction)
      {
         prepareClosing(Collections.singletonList(tab));
      }

      if(_closeAction == CloseAction.CANCELED)
      {
         return false;
      }


      int indexOfTab = TabbedExcutionPanelUtil.getIndexOfTab(tab, _tabbedExecutionsPanel);

      if (_tabIconManager.getAnchorIcon() == _tabbedExecutionsPanel.getIconAt(indexOfTab))
      {
         return _closeAction == CloseAction.ALL;
      }
      else
      {
         return true;
      }
   }

   public boolean confirmSqlPanelClose(ArrayList<JComponent> allTabs)
   {
      for (JComponent tab : allTabs)
      {
         int indexOfTab = TabbedExcutionPanelUtil.getIndexOfTab(tab, _tabbedExecutionsPanel);

         if (_tabIconManager.getAnchorIcon() == _tabbedExecutionsPanel.getIconAt(indexOfTab))
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

}
