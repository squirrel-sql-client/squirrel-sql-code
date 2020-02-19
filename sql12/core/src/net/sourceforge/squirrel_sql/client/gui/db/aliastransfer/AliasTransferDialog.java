package net.sourceforge.squirrel_sql.client.gui.db.aliastransfer;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class AliasTransferDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasTransferDialog.class);

   JTree treeExportedAliases;
   JButton btnExport;
   JButton btnImport;

   public AliasTransferDialog(MainFrame mainFrame)
   {
      super(mainFrame, s_stringMgr.getString("AliasTransferDialog.import.export.aliases"));

      treeExportedAliases = new JTree();

      getRootPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,3,5), 0,0);
      getRootPane().add(new MultipleLineLabel(s_stringMgr.getString("AliasTransferDialog.dnd.label")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,5), 0,0);
      getRootPane().add(new JScrollPane(treeExportedAliases), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,5,5), 0,0);
      getRootPane().add(createBottomPanel(), gbc);
   }

   private JPanel createBottomPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0);
      btnExport = new JButton(s_stringMgr.getString("AliasTransferDialog.export.button"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SAVE));
      ret.add(btnExport, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,0,0), 0,0);
      btnImport = new JButton(s_stringMgr.getString("AliasTransferDialog.import.button"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.OPEN));
      ret.add(btnImport, gbc);
      return ret;
   }
}
