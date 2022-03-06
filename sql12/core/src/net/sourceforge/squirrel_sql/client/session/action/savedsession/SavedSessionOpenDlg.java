package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JDialog;
import java.awt.Frame;

public class SavedSessionOpenDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SavedSessionOpenDlg.class);

   public SavedSessionOpenDlg(Frame parentFrame, boolean showWillDiscardExistingSqlPanelsWarning)
   {
      setTitle(s_stringMgr.getString("SavedSessionOpenDlg.title"));
   }
}
