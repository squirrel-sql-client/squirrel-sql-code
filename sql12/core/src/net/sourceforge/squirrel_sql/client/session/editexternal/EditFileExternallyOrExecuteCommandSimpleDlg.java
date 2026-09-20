package net.sourceforge.squirrel_sql.client.session.editexternal;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class EditFileExternallyOrExecuteCommandSimpleDlg extends JDialog
{
   private static StringManager s_stringMgr = StringManagerFactory.getStringManager(EditFileExternallyOrExecuteCommandSimpleDlg.class);

   final JComboBox cboCommand = new JComboBox();
   final JButton btnOk = new JButton(s_stringMgr.getString("EditFileExternallyOrExecuteCommandSimpleDlg.ok"));
   final JButton btnCancel = new JButton(s_stringMgr.getString("EditFileExternallyOrExecuteCommandSimpleDlg.cancel"));

   public EditFileExternallyOrExecuteCommandSimpleDlg(Frame owningFrame)
   {
      super(owningFrame, s_stringMgr.getString("EditFileExternallyOrExecuteCommandSimpleDlg.title"), true);

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,2,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15,5,0,5), 0,0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("EditFileExternallyOrExecuteCommandSimpleDlg.command")), gbc);

      gbc = new GridBagConstraints(0,3,2,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      getContentPane().add(cboCommand, gbc);

      gbc = new GridBagConstraints(0,4,2,1,0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20,0,5,0), 0,0);
      getContentPane().add(createOkCancelPanel(), gbc);

      gbc = new GridBagConstraints(0,5,2,1,1,1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0);
      getContentPane().add(new JPanel(), gbc);

      getRootPane().setDefaultButton(btnOk);

   }

   private JPanel createOkCancelPanel()
   {
      JPanel ret = new JPanel(new GridLayout(1,2,10,0));
      ret.add(btnOk);
      ret.add(btnCancel);
      return ret;
   }
}
