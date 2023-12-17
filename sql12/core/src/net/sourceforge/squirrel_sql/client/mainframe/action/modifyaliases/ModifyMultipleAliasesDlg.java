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

   public ModifyMultipleAliasesDlg()
   {
      super(Main.getApplication().getMainFrame(), s_stringMgr.getString("ModifyMultipleAliasesDlg.multi.aliases.modification.title"));

      getContentPane().setLayout(new BorderLayout());

      txtChangeReport = new JTextArea();
      txtChangeReport.setEditable(false);
      getContentPane().add(new JScrollPane(txtChangeReport), BorderLayout.CENTER);

      btnEditAliases = new JButton(s_stringMgr.getString("ModifyMultipleAliasesDlg.edit.aliases"));
      getContentPane().add(btnEditAliases, BorderLayout.SOUTH);
   }
}
