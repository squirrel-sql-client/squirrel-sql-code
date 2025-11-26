package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import javax.swing.JOptionPane;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class AliasPasswordEncryptionLoginCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasPasswordEncryptionLoginCtrl.class);

   private final AliasPasswordEncryptionLoginDlg _dlg;
   private boolean _ok;
   private boolean _removePassword;
   private boolean _changePassword;
   private String _newKeyPassword;

   public AliasPasswordEncryptionLoginCtrl()
   {
      _dlg = new AliasPasswordEncryptionLoginDlg();

      _dlg.btnOk.addActionListener(e -> onOk());
      _dlg.btnCancel.addActionListener(e -> closeDlg());
      _dlg.btnRemoveKeyPassword.addActionListener(e -> onRemoveKeyPassword());

      _dlg.btnChangeKeyPassword.addActionListener(e -> onChangeKeyPassword());

      GUIUtils.enableCloseByEscape(_dlg);
      GUIUtils.initLocation(_dlg, 480,150);
      GUIUtils.forceFocus(_dlg.txtPassword);
      _dlg.setVisible(true);
   }

   private void onOk()
   {
      if(null == _dlg.txtPassword.getPassword() || 0 == _dlg.txtPassword.getPassword().length)
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("AliasPasswordEncryptionLoginCtrl.password.empty"));
         return;
      }

      String keyPassword = new String(_dlg.txtPassword.getPassword());
      if(false == Main.getApplication().getAliasKeyPasswordManager().verifyKeyPassword(keyPassword))
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("AliasPasswordEncryptionLoginCtrl.incorrect.password"));
         return;
      }

      _ok = true;
      closeDlg();
   }

   private void onRemoveKeyPassword()
   {
      if(Main.getApplication().getAliasKeyPasswordManager().hasOpenAliasFrames())
      {
         JOptionPane.showMessageDialog(Main.getApplication().getMainFrame(), s_stringMgr.getString("AliasPasswordEncryptionLoginCtrl.close.all.open.alias.dialogs.for.remove"));
         return;
      }

      if(null == _dlg.txtPassword.getPassword() || 0 == _dlg.txtPassword.getPassword().length)
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("AliasPasswordEncryptionLoginCtrl.password.empty"));
         return;
      }

      String keyPassword = new String(_dlg.txtPassword.getPassword());
      if(false == Main.getApplication().getAliasKeyPasswordManager().verifyKeyPassword(keyPassword))
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("AliasPasswordEncryptionLoginCtrl.incorrect.password"));
         return;
      }

      _removePassword = true;
      closeDlg();
   }

   private void onChangeKeyPassword()
   {
      if(null == _dlg.txtPassword.getPassword() || 0 == _dlg.txtPassword.getPassword().length)
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("AliasPasswordEncryptionLoginCtrl.enter.current.password.first"));
         return;
      }

      if(false == Main.getApplication().getAliasKeyPasswordManager().verifyKeyPassword(new String(_dlg.txtPassword.getPassword())))
      {
         JOptionPane.showMessageDialog(_dlg, s_stringMgr.getString("AliasPasswordEncryptionLoginCtrl.current.password.not.correct"));
         return;
      }

      AliasPasswordEncryptionDefineCtrl newPasswordCtrl = new AliasPasswordEncryptionDefineCtrl(_dlg);

      if(newPasswordCtrl.isOk())
      {
         _changePassword = true;
         _newKeyPassword = newPasswordCtrl.getKeyPassword();
      }
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
      return new String(_dlg.txtPassword.getPassword());
   }

   public boolean isRemoveKeyPassword()
   {
      return _removePassword;
   }

   public boolean isChangeKeyPassword()
   {
      return _changePassword;
   }

   public String getNewKeyPassword()
   {
      return _newKeyPassword;
   }
}
