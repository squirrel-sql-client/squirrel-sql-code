package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;

public class EditExcelTabOrFileNameDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(EditExcelTabOrFileNameDlg.class);
   final JComboBox cboSqlResultName = new JComboBox();
   JButton btnOk = new JButton(s_stringMgr.getString("EditExcelTabOrFileNameDlg.ok"));
   JButton btnCancel = new JButton(s_stringMgr.getString("EditExcelTabOrFileNameDlg.cancel"));

   public EditExcelTabOrFileNameDlg(Window owningWindow)
   {
      super(owningWindow, s_stringMgr.getString("EditExcelTabOrFileNameDlg.title"), ModalityType.APPLICATION_MODAL);
      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      getContentPane().add(new JLabel(s_stringMgr.getString("EditExcelTabOrFileNameDlg.enter.new.name")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      cboSqlResultName.setEditable(true);
      getContentPane().add(cboSqlResultName, gbc);

      gbc = new GridBagConstraints(0,2,1,1,1,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      getContentPane().add(createButtonsPanel(), gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,1, GridBagConstraints.NORTHWEST, GridBagConstraints.VERTICAL, new Insets(5,5,0,5), 0,0);
      getContentPane().add(new JPanel(), gbc);

      getRootPane().setDefaultButton(btnOk);
   }

   private JPanel createButtonsPanel()
   {
      JPanel ret = new JPanel(new GridLayout(1,2,5,5));
      ret.add(btnOk);
      ret.add(btnCancel);
      return ret;
   }
}
