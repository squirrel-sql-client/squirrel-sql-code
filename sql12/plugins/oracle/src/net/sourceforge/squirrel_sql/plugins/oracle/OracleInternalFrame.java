package net.sourceforge.squirrel_sql.plugins.oracle;

import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.SessionDialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import java.beans.PropertyVetoException;

public class OracleInternalFrame extends SessionDialogWidget
{
   private static final String PREF_KEY_ORACLE_FRAME_REPL = "@@";

   private static final String PREF_KEY_ORACLE_FRAME_WIDTH = "Squirrel.oracle." + PREF_KEY_ORACLE_FRAME_REPL + "_WIDTH";
   private static final String PREF_KEY_ORACLE_FRAME_HEIGHT = "Squirrel.oracle." + PREF_KEY_ORACLE_FRAME_REPL + "_HEIGHT";
   private static final String PREF_KEY_ORACLE_FRAME_STAY_ON_TOP = "Squirrel.oracle." + PREF_KEY_ORACLE_FRAME_REPL + "_STAY_ON_TOP";
   private static final String PREF_KEY_ORACLE_FRAME_AUTO_REFRESH_SEC = "Squirrel.oracle." + PREF_KEY_ORACLE_FRAME_REPL + "_AUTO_REFRESH_SEC";

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(OracleInternalFrame.class);

   private static final ILogger s_log = LoggerController.createLogger(OracleInternalFrame.class);

   private String _repl = "";

   public OracleInternalFrame(ISession session, String title)
   {
      super(title, true, true, true, true, session);
      setBorder(BorderFactory.createRaisedBevelBorder());
   }

   protected void initFromPrefs(String repl, OracleInternalFrameCallback callBack)
   {
      _repl = repl;
      final int width = Props.getInt(PREF_KEY_ORACLE_FRAME_WIDTH.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), 400);
      final int height = Props.getInt(PREF_KEY_ORACLE_FRAME_HEIGHT.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), 200);
      final boolean stayOnTop = Props.getBoolean(PREF_KEY_ORACLE_FRAME_STAY_ON_TOP.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), false);
      int autoRefeshPeriod = Props.getInt(PREF_KEY_ORACLE_FRAME_AUTO_REFRESH_SEC.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), 10);
      autoRefeshPeriod = Math.max(1,autoRefeshPeriod);

      callBack.createPanelAndToolBar(stayOnTop, autoRefeshPeriod);

      SwingUtilities.invokeLater(() -> sizeAndDisplay(width, height));
   }

   private void sizeAndDisplay(int width, int height)
   {
      Rectangle rectMain = getSession().getApplication().getMainFrame().getDesktopContainer().getBounds();
      Rectangle rect = new Rectangle();

      rect.width = Math.min(rectMain.width, width);
      rect.height = Math.min(rectMain.height, height);

      try
      {
         setMaximum(false);
      }
      catch (PropertyVetoException e)
      {
         s_log.error(e);
      }
      setBounds(rect);

      DialogWidget.centerWithinDesktop(this);
      setVisible(true);
   }

   protected void internalFrameClosing(boolean stayOnTop, int autoRefreshPeriod)
   {
      Rectangle rect = getBounds();

      if (rect != null)
      {
         Props.putInt(PREF_KEY_ORACLE_FRAME_WIDTH.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), rect.width);
         Props.putInt(PREF_KEY_ORACLE_FRAME_HEIGHT.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), rect.height);
      }
      Props.putBoolean(PREF_KEY_ORACLE_FRAME_STAY_ON_TOP.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), stayOnTop);
      Props.putInt(PREF_KEY_ORACLE_FRAME_AUTO_REFRESH_SEC.replaceAll(PREF_KEY_ORACLE_FRAME_REPL, _repl), autoRefreshPeriod);
   }
}
