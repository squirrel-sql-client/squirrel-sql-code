package net.sourceforge.squirrel_sql.client.gui.db.aliastransfer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallToolTipInfoButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class AliasTransferDlg extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AliasTransferDlg.class);

   JTree treeExportedAliases;
   JButton btnExport;
   JButton btnImport;
   JButton btnUpdate;

   public AliasTransferDlg(MainFrame mainFrame)
   {
      super(mainFrame, s_stringMgr.getString("AliasTransferDialog.import.export.aliases"));

      treeExportedAliases = new JTree();

      getContentPane().setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,3,5), 0,0);
      getContentPane().add(new MultipleLineLabel(s_stringMgr.getString("AliasTransferDialog.dnd.label")), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,5), 0,0);
      getContentPane().add(new JScrollPane(treeExportedAliases), gbc);

      gbc = new GridBagConstraints(0,2,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5), 0,0);
      getContentPane().add(createBottomPanel(), gbc);
   }

   private JPanel createBottomPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
      btnExport = new JButton(s_stringMgr.getString("AliasTransferDialog.export.button"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SAVE));
      ret.add(btnExport, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnImport = new JButton(s_stringMgr.getString("AliasTransferDialog.import.button"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.OPEN));
      ret.add(btnImport, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      SmallToolTipInfoButton info = new SmallToolTipInfoButton(s_stringMgr.getString("AliasTransferDialog.import.export.description"), 20000);
      ret.add(info.getButton(), gbc);

      gbc = new GridBagConstraints(3,0,1,1,1,0, GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(0,5,0,0), 0,0);
      ret.add(new JPanel(), gbc);

      gbc = new GridBagConstraints(4,0,1,1,0,0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,5,0,0), 0,0);
      btnUpdate = new JButton(s_stringMgr.getString("AliasTransferDialog.update.button"), Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.MAGIC_WAND));
      ret.add(btnUpdate, gbc);

      return ret;
   }
}
