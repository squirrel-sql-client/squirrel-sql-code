package net.sourceforge.squirrel_sql.client.gui.db.encryption;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class AliasPasswordEncryptionLoginDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasPasswordEncryptionLoginDlg.class);

   final JPasswordField txtPassword = new JPasswordField();
   final JButton btnOk = new JButton(s_stringMgr.getString("AliasPasswordEncryptionDlg.ok"));
   final JButton btnCancel = new JButton(s_stringMgr.getString("AliasPasswordEncryptionDlg.cancel"));
   final JButton btnChangeKeyPassword = new JButton(s_stringMgr.getString("AliasPasswordEncryptionDlg.change.key.password"));
   final JButton btnRemoveKeyPassword = new JButton(s_stringMgr.getString("AliasPasswordEncryptionDlg.remove.key.password"));

   public AliasPasswordEncryptionLoginDlg()
   {
      super(Main.getApplication().getMainFrame(), s_stringMgr.getString("AliasPasswordEncryptionDlg.enter.encryption.key.password"), true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0, 1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      getContentPane().add(createDescriptionPanel(), gbc);

      gbc = new GridBagConstraints(0,1, 1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,100), 0,0);
      getContentPane().add(txtPassword, gbc);

      gbc = new GridBagConstraints(0,2, 1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(20,5,5,5), 0,0);
      getContentPane().add(createButtonPanel(), gbc);

      getRootPane().setDefaultButton(btnOk);
   }

   private static JPanel createDescriptionPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      String descr;
      if(Main.getApplication().getAliasKeyPasswordManager().isLoggedIn())
      {
         descr = s_stringMgr.getString("AliasPasswordEncryptionDlg.enter.encryption.key.for.encrypted.Alias.passwords.already.logged.in");
      }
      else
      {
         descr = s_stringMgr.getString("AliasPasswordEncryptionDlg.enter.encryption.key.for.encrypted.Alias.passwords");
      }
      ret.add(new JLabel(descr), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,2,0,0), 0,0);
      ret.add(AliasKeyPasswordInfo.getSmallInfoButton(), gbc);

      return ret;
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0, 1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(btnOk, gbc);

      gbc = new GridBagConstraints(1,0, 1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      ret.add(btnCancel, gbc);

      gbc = new GridBagConstraints(2,0, 1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      gbc = new GridBagConstraints(3,0, 1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(btnChangeKeyPassword, gbc);

      gbc = new GridBagConstraints(4,0, 1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      ret.add(btnRemoveKeyPassword, gbc);



      return ret;
   }
}
