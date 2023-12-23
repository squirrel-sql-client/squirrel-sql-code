package net.sourceforge.squirrel_sql.client.mainframe.action.modifyaliases;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class ModifyMultipleAliasesDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ModifyMultipleAliasesDlg.class);
   final JTextArea txtChangeReport;
   final JButton btnEditAliases;
   final JButton btnApplyChanges;

   public ModifyMultipleAliasesDlg()
   {
      super(Main.getApplication().getMainFrame(), s_stringMgr.getString("ModifyMultipleAliasesDlg.multi.aliases.modification.title"));

      getContentPane().setLayout(new BorderLayout());

      txtChangeReport = new JTextArea();
      txtChangeReport.setEditable(false);
      getContentPane().add(new JScrollPane(txtChangeReport), BorderLayout.CENTER);

      JPanel pnlSouth = new JPanel(new GridLayout(1, 2));

      btnEditAliases = new JButton(s_stringMgr.getString("ModifyMultipleAliasesDlg.edit.template.alias"));
      pnlSouth.add(btnEditAliases);

      btnApplyChanges = new JButton(s_stringMgr.getString("ModifyMultipleAliasesDlg.apply.changes"));
      pnlSouth.add(btnApplyChanges);


      getContentPane().add(pnlSouth, BorderLayout.SOUTH);
   }
}
