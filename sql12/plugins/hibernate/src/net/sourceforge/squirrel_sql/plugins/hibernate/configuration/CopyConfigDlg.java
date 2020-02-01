package net.sourceforge.squirrel_sql.plugins.hibernate.configuration;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class CopyConfigDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CopyConfigDlg.class);

   JTextField txtNewName = new JTextField();

   JButton btnOk = new JButton(s_stringMgr.getString("CopyConfigDlg.ok"));
   JButton btnCancel = new JButton(s_stringMgr.getString("CopyConfigDlg.cancel"));;

   public CopyConfigDlg(Frame owningFrame)
   {
      super(owningFrame, s_stringMgr.getString("CopyConfigDlg.copy.config.enter.new.config.name.title"), true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,2,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("CopyConfigDlg.copy.config.enter.new.config.name")), gbc);

      gbc = new GridBagConstraints(0,1,2,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(txtNewName, gbc);


      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      getContentPane().add(btnOk, gbc);

      gbc = new GridBagConstraints(1,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      getContentPane().add(btnCancel, gbc);

   }
}
