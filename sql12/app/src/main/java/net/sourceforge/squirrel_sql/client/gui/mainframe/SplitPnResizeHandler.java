package net.sourceforge.squirrel_sql.client.gui.mainframe;

import net.sourceforge.squirrel_sql.client.session.MessagePanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.prefs.Preferences;

public class SplitPnResizeHandler
{
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(SplitPnResizeHandler.class);

   private static final String PREFS_KEY_MESSAGEPANEL_HEIGHT = "squirrelSql_msgPanel_height";

   private boolean m_hasBeenVisible;

   private JSplitPane _splitPn;
   private MessagePanel _msgPnl;

   public SplitPnResizeHandler(JSplitPane splitPn, MessagePanel msgPnl)
   {
      _splitPn = splitPn;
      _msgPnl = msgPnl;

      String key;

      //i18n[MainFrame.saveSize=Save size]
      key = s_stringMgr.getString("MainFrame.saveSize");
      Action saveSplitDividerLocAction = new AbstractAction(key)
      {
         public void actionPerformed(ActionEvent e)
         {
            int msgPanelHeight = _splitPn.getBottomComponent().getSize().height;
            Preferences.userRoot().putInt(PREFS_KEY_MESSAGEPANEL_HEIGHT, msgPanelHeight);
         }
      };
      _msgPnl.addToMessagePanelPopup(saveSplitDividerLocAction);

      //i18n[MainFrame.saveSize0=Save size 0]
      key = s_stringMgr.getString("MainFrame.saveSize0");
      Action save0SplitDividerLocAction = new AbstractAction(key)
      {
         public void actionPerformed(ActionEvent e)
         {
            Preferences.userRoot().putInt(PREFS_KEY_MESSAGEPANEL_HEIGHT, 0);
            setUnexpanded();
         }
      };
      _msgPnl.addToMessagePanelPopup(save0SplitDividerLocAction);


      key = s_stringMgr.getString("MainFrame.restoreSize");
      Action setSplitDividerLocAction = new AbstractAction(key)
      {
         public void actionPerformed(ActionEvent e)
         {
            int prefMsgPanelHeight = Preferences.userRoot().getInt(PREFS_KEY_MESSAGEPANEL_HEIGHT, -1);
            if(-1 != prefMsgPanelHeight)
            {
               if (0 == prefMsgPanelHeight)
               {
                  setUnexpanded();
               }
               else
               {
                  int divLoc = getDividerLocation(prefMsgPanelHeight, _splitPn);
                  _splitPn.setDividerLocation(divLoc);
               }
            }
         }
      };
      _msgPnl.addToMessagePanelPopup(setSplitDividerLocAction);

   }

   private void setUnexpanded()
   {
      _splitPn.setDividerLocation(_splitPn.getMaximumDividerLocation() + 100);
   }

   void resizeSplitOnStartup()
   {

      if(false == m_hasBeenVisible)
      {
         m_hasBeenVisible = true;
         final int prefMsgPanelHeight = Preferences.userRoot().getInt(PREFS_KEY_MESSAGEPANEL_HEIGHT, -1);

         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               if (-1 == prefMsgPanelHeight)
               {
                  int divLoc = getDividerLocation(50, _splitPn);
                  _splitPn.setDividerLocation(divLoc);
               }
               else
               {
                  if (0 == prefMsgPanelHeight)
                  {
                     tryForceUnexpanded();
                  }
                  else
                  {
                     int divLoc = getDividerLocation(prefMsgPanelHeight, _splitPn);
                     _splitPn.setDividerLocation(divLoc);
                  }

               }
            }
         });

      }
   }

   /**
    * Used at startup and almost always makes it to hide the Message panel at startup
    */
   private void tryForceUnexpanded()
   {
      setUnexpanded();
      Runnable runnable = new Runnable()
      {
         public void run()
         {
            setUnexpanded();
         }
      };
      SwingUtilities.invokeLater(runnable);
   }

   private int getDividerLocation(int wantedBottomComponentHeight, JSplitPane splitPn)
   {
      int splitBarSize =
         splitPn.getSize().height -
         splitPn.getBottomComponent().getSize().height -
         splitPn.getTopComponent().getSize().height - 1;

      int divLoc = splitPn.getSize().height - wantedBottomComponentHeight - splitBarSize;
      return divLoc;
   }
}
