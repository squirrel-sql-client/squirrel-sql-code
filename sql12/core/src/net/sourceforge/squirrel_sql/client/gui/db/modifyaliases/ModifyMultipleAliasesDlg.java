package net.sourceforge.squirrel_sql.client.gui.db.modifyaliases;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class ModifyMultipleAliasesDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ModifyMultipleAliasesDlg.class);
   final JTabbedPane _tabbedPane = new JTabbedPane();
   final JTree treeAliasesToModify;
   final JTextArea txtChangeReport = new JTextArea();

   JButton btnEditTemplateAlias;
   JButton btnApplyChanges;
   JButton btnCancel;

   public ModifyMultipleAliasesDlg()
   {
      super(Main.getApplication().getMainFrame(), s_stringMgr.getString("ModifyMultipleAliasesDlg.multi.aliases.modification.title"));

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,0,5), 0,0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("ModifyMultipleAliasesDlg.description")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5,5,0,5), 0,0);
      treeAliasesToModify = new JTree();
      _tabbedPane.addTab(s_stringMgr.getString("ModifyMultipleAliasesDlg.tab.Aliases"), new JScrollPane(treeAliasesToModify));

      txtChangeReport.setEditable(false);
      _tabbedPane.addTab(s_stringMgr.getString("ModifyMultipleAliasesDlg.tab.change.report"), new JScrollPane(txtChangeReport));
      getContentPane().add(_tabbedPane, gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      getContentPane().add(createButtonsPanel(), gbc);
   }

   private JPanel createButtonsPanel()
   {
      JPanel pnlRet = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnEditTemplateAlias = new JButton(s_stringMgr.getString("ModifyMultipleAliasesDlg.edit.selected.template.alias"));
      pnlRet.add(btnEditTemplateAlias, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnApplyChanges = new JButton(s_stringMgr.getString("ModifyMultipleAliasesDlg.modify.multiple.aliases"));
      pnlRet.add(btnApplyChanges, gbc);

      gbc = new GridBagConstraints(2,0,1,1,1,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      pnlRet.add(new JPanel(), gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnCancel = new JButton(s_stringMgr.getString("ModifyMultipleAliasesDlg.cancel"));
      pnlRet.add(btnCancel, gbc);

      return pnlRet;
   }
}
