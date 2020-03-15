package net.sourceforge.squirrel_sql.client.gui.db.aliastransfer;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class AliasImportUpdateDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasImportUpdateDlg.class);

   JRadioButton optUpdateOlderMatches;
   JRadioButton optUpdateAllMatches;
   JButton btnUpdate;
   JButton btnCancel;
   private final ButtonGroup _buttonGroup;


   public AliasImportUpdateDlg(JDialog parent)
   {
      super(parent);
      setTitle(s_stringMgr.getString("AliasImportUpdateDlg.title"));

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("AliasImportUpdateDlg.description")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,0,5), 0,0);
      optUpdateOlderMatches = new JRadioButton(s_stringMgr.getString("AliasImportUpdateDlg.opt.update.older"));
      getContentPane().add(optUpdateOlderMatches, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2,5,0,5), 0,0);
      optUpdateAllMatches = new JRadioButton(s_stringMgr.getString("AliasImportUpdateDlg.opt.update.all"));
      getContentPane().add(optUpdateAllMatches, gbc);

      _buttonGroup = new ButtonGroup();
      _buttonGroup.add(optUpdateOlderMatches);
      _buttonGroup.add(optUpdateAllMatches);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createButtonPanel(), gbc);
   }

   private JPanel createButtonPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnUpdate = new JButton(s_stringMgr.getString("AliasImportUpdateDlg.button.update"));
      ret.add(btnUpdate, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnCancel = new JButton(s_stringMgr.getString("AliasImportUpdateDlg.button.cancel"));
      ret.add(btnCancel , gbc);

      return ret;
   }
}
