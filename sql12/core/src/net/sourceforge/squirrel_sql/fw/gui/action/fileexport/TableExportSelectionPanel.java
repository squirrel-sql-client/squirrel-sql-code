package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class TableExportSelectionPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableExportSelectionPanel.class);


   JRadioButton radComplete;
   JRadioButton radSelection;
   JRadioButton radMultipleSQLRes;

   public TableExportSelectionPanel()
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      // i18n[TableExportCsvDlg.exportCompleteTable=Export complete table]
      radComplete = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.exportCompleteTable"));
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
      add(radComplete, gbc);

      // i18n[TableExportCsvDlg.exportSelection=Export selection]
      radSelection = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.exportSelection"));
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(radSelection, gbc);

      radMultipleSQLRes = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.exportMultipleSQLResults"));
      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(radMultipleSQLRes, gbc);

      gbc = new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
      add(new JPanel(), gbc);

      ButtonGroup bg = new ButtonGroup();
      bg.add(radComplete);
      bg.add(radSelection);
      bg.add(radMultipleSQLRes);
   }
}
