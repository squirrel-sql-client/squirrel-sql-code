package net.sourceforge.squirrel_sql.client.session.editexternal;

import java.awt.Frame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

public class EditFileExternallyInitCtrl
{

   public static final String PREF_KEY_MILLIS = "EditFileExternallyInitCtrl.millis";
   public static final String PREF_KEY_COMMAND = "EditFileExternallyInitCtrl.command";

   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(EditFileExternallyInitCtrl.class);

   private final EditFileExternallyInitDlg _dlg;
   private boolean _ok;


   public EditFileExternallyInitCtrl(Frame owningFrame)
   {
      _dlg = new EditFileExternallyInitDlg(owningFrame);

      GUIUtils.initLocation(_dlg, 500, 200);
      GUIUtils.enableCloseByEscape(_dlg);

      _dlg.txtMillis.setInt(Props.getInt(PREF_KEY_MILLIS, 500));
      _dlg.txtCommand.setText(Props.getString(PREF_KEY_COMMAND, "emacs +@line:@col @file"));

      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> close());

      SwingUtilities.invokeLater(() -> _dlg.txtMillis.requestFocus());

      _dlg.setVisible(true);
   }

   private void onOk()
   {
      if(StringUtils.isBlank(_dlg.txtCommand.getText()))
      {
         JOptionPane.showConfirmDialog(_dlg, s_stringMgr.getString("EditFileExternallyInitCtrl.command.empty"));
         return;
      }

      Props.putInt(PREF_KEY_MILLIS, _dlg.txtMillis.getInt());
      Props.putString(PREF_KEY_COMMAND, _dlg.txtCommand.getText());

      _ok = true;
      close();
   }


   private void close()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   public boolean isOk()
   {
      return _ok;
   }

   public String getCliCommand()
   {
      return _dlg.txtCommand.getText();
   }

   public int getDelay()
   {
      return _dlg.txtMillis.getInt();
   }
}
