package net.sourceforge.squirrel_sql.client.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ProxySettingsAddDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ProxySettingsAddDlg.class);

   final JTextField txtName;
   final JButton btnOK = new JButton(s_stringMgr.getString("ProxySettingsAddDlg.ok"));
   final JButton btnCancel = new JButton(s_stringMgr.getString("ProxySettingsAddDlg.cancel"));

   public ProxySettingsAddDlg(Window parent)
   {
      super(parent, s_stringMgr.getString("ProxySettingsAddDlg.add.proxy.setting"), ModalityType.APPLICATION_MODAL);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("ProxySettingsAddDlg.enter.name.of.new.proxy.setting")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,50), 0,0);
      txtName = new JTextField();
      getContentPane().add(txtName, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createButtonPanel(), gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0);
      getContentPane().add(new JPanel(), gbc);

      getRootPane().setDefaultButton(btnOK);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(btnOK, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      ret.add(btnCancel, gbc);

      return ret;
   }
}
