package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import net.sourceforge.squirrel_sql.client.IApplication;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ScrollableTabHandler
{
   private DesktopTabbedPane _tabbedPane;
   private boolean _useScrollableTabbedPanesForSessionTabs;

   public ScrollableTabHandler(IApplication app, DesktopTabbedPane tabbedPane)
   {
      _useScrollableTabbedPanesForSessionTabs = app.getSquirrelPreferences().getUseScrollableTabbedPanesForSessionTabs();

      if(false == _useScrollableTabbedPanesForSessionTabs)
      {
         return;
      }

      _tabbedPane = tabbedPane;

      _tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);


   }

   public void tabAdded()
   {
      if(false == _useScrollableTabbedPanesForSessionTabs)
      {
         return;
      }

      SwingUtilities.invokeLater( new Runnable()
      {
         public void run()
         {
            Action action = _tabbedPane.getActionMap().get("scrollTabsForwardAction");
            action.actionPerformed(new ActionEvent(_tabbedPane, ActionEvent.ACTION_PERFORMED, ""));
         }
      });
   }

   public void tabRemoved()
   {
      if(false == _useScrollableTabbedPanesForSessionTabs)
      {
         return;
      }

      SwingUtilities.invokeLater( new Runnable()
      {
         public void run()
         {
            Action action = _tabbedPane.getActionMap().get("scrollTabsBackwardAction");
            action.actionPerformed(new ActionEvent(_tabbedPane, ActionEvent.ACTION_PERFORMED, ""));
         }
      });
   }
}
