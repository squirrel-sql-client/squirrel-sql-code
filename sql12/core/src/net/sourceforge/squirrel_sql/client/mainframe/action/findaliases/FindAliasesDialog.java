package net.sourceforge.squirrel_sql.client.mainframe.action.findaliases;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class FindAliasesDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FindAliasesDialog.class);

   JTextField txtToSearch = new JTextField();
   JList<AliasSearchWrapper> lstResult = new JList<>();
   JCheckBox chkRememberLastSearch = new JCheckBox(s_stringMgr.getString("FindAliasesCtrl.remember.last.search"));
   JCheckBox chkIncludeAliasFolders = new JCheckBox(s_stringMgr.getString("FindAliasesCtrl.include.alias.folders"));

   JCheckBox chkLeaveOpen = new JCheckBox(s_stringMgr.getString("FindAliasesCtrl.leave.open"));


   JButton btnConnect = new JButton(s_stringMgr.getString("FindAliasesCtrl.connect"));
   JButton btnGoto = new JButton(s_stringMgr.getString("FindAliasesCtrl.goto"));
   JButton btnClose = new JButton(s_stringMgr.getString("FindAliasesCtrl.close"));


   public FindAliasesDialog()
   {
      super(Main.getApplication().getMainFrame(), s_stringMgr.getString("FindAliasesCtrl.find.alias"), false);

      Container pane = getContentPane();
      pane.setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      pane.add(new JLabel(s_stringMgr.getString("FindAliasesCtrl.enter.text")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      pane.add(txtToSearch, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,5), 0,0);
      pane.add(createToCheckBoxPanel(), gbc);

      gbc = new GridBagConstraints(0,3,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10,5,5,5), 0,0);
      pane.add(new JScrollPane(lstResult), gbc);

      gbc = new GridBagConstraints(0,4,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      pane.add(chkLeaveOpen, gbc);

      gbc = new GridBagConstraints(0,5,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      pane.add(createButtonPanel(), gbc);

   }

   private JPanel createToCheckBoxPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(chkRememberLastSearch, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,10,0,0), 0,0);
      ret.add(chkIncludeAliasFolders, gbc);

      return ret;
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0,0,0,5), 0,0);
      ret.add(btnConnect, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0,0,0,5), 0,0);
      ret.add(btnGoto, gbc);

      gbc = new GridBagConstraints(2,0,1,1,1,0,GridBagConstraints.NORTHWEST,GridBagConstraints.HORIZONTAL, new Insets(0,0,0,5), 0,0);
      ret.add(new JPanel(), gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.NORTHWEST,GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      ret.add(btnClose, gbc);

      return ret;
   }

}
