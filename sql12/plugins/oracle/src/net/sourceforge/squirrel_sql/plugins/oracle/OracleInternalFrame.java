package net.sourceforge.squirrel_sql.plugins.oracle;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionDialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.prefs.Preferences;

public class OracleInternalFrame extends SessionDialogWidget
{
   private static final String PREF_KEY_ORACLE_FRAME_REPL = "@@";

   private static final String PREF_KEY_ORACLE_FRAME_X = "Squirrel.oracle." + PREF_KEY_ORACLE_FRAME_REPL + "_X";
   private static final String PREF_KEY_ORACLE_FRAME_Y = "Squirrel.oracle." + PREF_KEY_ORACLE_FRAME_REPL + "_Y";
   private static final String PREF_KEY_ORACLE_FRAME_WIDTH = "Squirrel.oracle." + PREF_KEY_ORACLE_FRAME_REPL + "_WIDTH";
   private static final String PREF_KEY_ORACLE_FRAME_HEIGHT = "Squirrel.oracle." + PREF_KEY_ORACLE_FRAME_REPL + "_HEIGHT";
   private static final String PREF_KEY_ORACLE_FRAME_STAY_ON_TOP = "Squirrel.oracle." + PREF_KEY_ORACLE_FRAME_REPL + "_STAY_ON_TOP";
   private static final String PREF_KEY_ORACLE_FRAME_AUTO_REFRESH_SEC = "Squirrel.oracle." + PREF_KEY_ORACLE_FRAME_REPL + "_AUTO_REFRESH_SEC";

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(OracleInternalFrame.class);

   private static final ILogger s_log =
      LoggerController.createLogger(OracleInternalFrame.class);
   private String _repl = "";


   public OracleInternalFrame(ISession session, String title)
   {
      super(title, true, true, true, true, session);

      setBorder(BorderFactory.createRaisedBevelBorder());
   }

   protected void initFromPrefs(String repl, OracleInternalFrameCallback callBack)
   {
      _repl = repl;
      final int x = Preferences.userRoot().getInt(PREF_KEY_ORACLE_FRAME_X.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), 0);
      final int y = Preferences.userRoot().getInt(PREF_KEY_ORACLE_FRAME_Y.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), 0);
      final int width = Preferences.userRoot().getInt(PREF_KEY_ORACLE_FRAME_WIDTH.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), 400);
      final int height = Preferences.userRoot().getInt(PREF_KEY_ORACLE_FRAME_HEIGHT.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), 200);
      final boolean stayOnTop = Preferences.userRoot().getBoolean(PREF_KEY_ORACLE_FRAME_STAY_ON_TOP.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), false);
      int autoRefeshPeriod = Preferences.userRoot().getInt(PREF_KEY_ORACLE_FRAME_AUTO_REFRESH_SEC.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), 10);
      autoRefeshPeriod = Math.max(1,autoRefeshPeriod);

      callBack.createPanelAndToolBar(stayOnTop, autoRefeshPeriod);


      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            Rectangle rectMain = getSession().getApplication().getMainFrame().getDesktopContainer().getBounds();
            Rectangle rect = new Rectangle();
            rect.x = x;
            if(rectMain.width - x < 50) rect.x = 0;

            rect.y = y;
            if(rectMain.height - y < 50) rect.y = 0;


            rect.width = Math.max(100, width);
            rect.height = Math.max(100, height);
            if(rect.x + rect.width > rectMain.width || rect.y + rect.height > rectMain.height)
            {
               rect.x = 0; rect.width = 400;
               rect.y = 0; rect.height = 200;
            }

            try
            {
               setMaximum(false);
            }
            catch (PropertyVetoException e)
            {
               s_log.error(e);
            }
            setBounds(rect);

            setVisible(true);
         }
      });
   }



   protected void internalFrameClosing(boolean stayOnTop, int autoRefreshPeriod)
   {
      Rectangle rect = getBounds();

      Preferences.userRoot().putInt(PREF_KEY_ORACLE_FRAME_X.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), rect.x);
      Preferences.userRoot().putInt(PREF_KEY_ORACLE_FRAME_Y.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), rect.y);
      Preferences.userRoot().putInt(PREF_KEY_ORACLE_FRAME_WIDTH.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), rect.width);
      Preferences.userRoot().putInt(PREF_KEY_ORACLE_FRAME_HEIGHT.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), rect.height);
      Preferences.userRoot().putBoolean(PREF_KEY_ORACLE_FRAME_STAY_ON_TOP.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), stayOnTop);
      Preferences.userRoot().putInt(PREF_KEY_ORACLE_FRAME_AUTO_REFRESH_SEC.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), autoRefreshPeriod);
   }

   public class OracleToolBar extends ToolBar
   {
      private JCheckBox _stayOnTop;

      protected void addStayOnTop(boolean stayOnTop)
      {
         // i18n[oracle.dboutputStayOnTop=Stay on top]
         _stayOnTop = new JCheckBox(s_stringMgr.getString("oracle.dboutputStayOnTop"), false);
         _stayOnTop.setSelected(stayOnTop);
         _stayOnTop.setVisible(getSession().getApplication().getDesktopStyle().supportsLayers());

         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               onStayOnTopChanged(_stayOnTop.isSelected());
               _stayOnTop.addActionListener(new ActionListener()
               {
                  public void actionPerformed(ActionEvent e)
                  {
                     onStayOnTopChanged(_stayOnTop.isSelected());
                  }
               });
            }
         });


         add(_stayOnTop);
      }

      private void onStayOnTopChanged(boolean selected)
      {
         if(selected)
         {
            OracleInternalFrame.this.setLayer(JLayeredPane.PALETTE_LAYER.intValue());
         }
         else
         {
            OracleInternalFrame.this.setLayer(JLayeredPane.DEFAULT_LAYER.intValue());
         }

         // Needs to be done in both cases because if the window goes back to
         // the default layer it goes back behind all other windows too.
         toFront();
      }

      
      public boolean isStayOnTop()
      {
         return _stayOnTop.isSelected();
      }


   }



}
