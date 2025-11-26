package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import javax.swing.JOptionPane;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import org.apache.commons.lang3.StringUtils;

public class AliasPasswordEncryptionDefineCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasPasswordEncryptionDefineCtrl.class);

   private final AliasPasswordEncryptionDefineDlg _dlg;
   private boolean _ok;

   public AliasPasswordEncryptionDefineCtrl()
   {
      this(null);
   }

   public AliasPasswordEncryptionDefineCtrl(AliasPasswordEncryptionLoginDlg changedPasswordParent)
   {
      _dlg = new AliasPasswordEncryptionDefineDlg(changedPasswordParent);

      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> closeDlg());

      GUIUtils.enableCloseByEscape(_dlg);
      GUIUtils.initLocation(_dlg, 400,210);
      GUIUtils.forceFocus(_dlg.txtPassword1);
      _dlg.setVisible(true);
   }

   private void onOk()
   {

      if(   null == _dlg.txtPassword1.getPassword() || 0 == _dlg.txtPassword1.getPassword().length
         || null == _dlg.txtPassword2.getPassword() || 0 == _dlg.txtPassword2.getPassword().length)
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("AliasPasswordEncryptionDefineCtrl.password.empty"));
         return;
      }

      String password1 = new String(_dlg.txtPassword1.getPassword());
      String password2 = new String(_dlg.txtPassword2.getPassword());
      if(false == StringUtils.equals(password1, password2))
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("AliasPasswordEncryptionDefineCtrl.passwords.do.not.match"));
         return;
      }

      _ok = true;
      closeDlg();
   }

   private void closeDlg()
   {
      _dlg.setVisible(false);
      _dlg.dispose();
   }

   public boolean isOk()
   {
      return _ok;
   }

   public String getKeyPassword()
   {
      return new String(_dlg.txtPassword1.getPassword());
   }
}
