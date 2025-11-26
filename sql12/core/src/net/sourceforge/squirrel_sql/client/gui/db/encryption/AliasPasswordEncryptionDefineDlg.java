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

public class AliasPasswordEncryptionDefineDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasPasswordEncryptionDefineDlg.class);

   final JPasswordField txtPassword1 = new JPasswordField();
   final JPasswordField txtPassword2 = new JPasswordField();
   final JButton btnOk = new JButton(s_stringMgr.getString("AliasPasswordEncryptionDefineDlg.ok"));
   final JButton btnCancel = new JButton(s_stringMgr.getString("AliasPasswordEncryptionDefineDlg.cancel"));

   public AliasPasswordEncryptionDefineDlg(AliasPasswordEncryptionLoginDlg changePasswordParent)
   {
      super(null != changePasswordParent ? changePasswordParent : Main.getApplication().getMainFrame());

      String titleText;
      if(null == changePasswordParent)
      {
         titleText = s_stringMgr.getString("AliasPasswordEncryptionDefineDlg.define.key.password");
      }
      else
      {
         titleText = s_stringMgr.getString("AliasPasswordEncryptionDefineDlg.change.key.password");
      }

      setTitle(titleText);

      setModal(true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 5), 0, 0);
      getContentPane().add(createDescriptionPanel(null != changePasswordParent), gbc);

      gbc = new GridBagConstraints(0,1, 1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,50), 0,0);
      getContentPane().add(txtPassword1, gbc);


      gbc = new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(10, 5, 0, 5), 0, 0);
      getContentPane().add(new JLabel(s_stringMgr.getString("AliasPasswordEncryptionDefineDlg.reenter.password")), gbc);

      gbc = new GridBagConstraints(0,3, 1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,50), 0,0);
      getContentPane().add(txtPassword2, gbc);


      gbc = new GridBagConstraints(0,4, 1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(20,5,5,5), 0,0);
      getContentPane().add(createButtonPanel(), gbc);

      getRootPane().setDefaultButton(btnOk);
   }

   private static JPanel createDescriptionPanel(boolean changePassword)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      String labelText;
      if(changePassword)
      {
         labelText = s_stringMgr.getString("AliasPasswordEncryptionDefineDlg.change.encryption.key.for.encrypted.Alias.passwords");
      }
      else
      {
         labelText = s_stringMgr.getString("AliasPasswordEncryptionDefineDlg.define.encryption.key.for.encrypted.Alias.passwords");
      }

      ret.add(new JLabel(labelText), gbc);

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

      gbc = new GridBagConstraints(2,0, 1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      return ret;
   }

}
