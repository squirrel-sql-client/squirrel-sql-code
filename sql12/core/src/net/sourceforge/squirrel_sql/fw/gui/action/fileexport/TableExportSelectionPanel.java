package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallToolTipInfoButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class TableExportSelectionPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableExportSelectionPanel.class);

   JRadioButton radComplete;
   JRadioButton radSelection;
   JRadioButton radMultipleSQLRes;

   JList<SqlResultListEntry> lstSQLResultsToExport;
   JButton btnUp;
   JButton btnDown;
   JButton btnEdit;
   JButton btnDelete;
   JTextField txtExportFileOrDir;
   SmallToolTipInfoButton btnInfo;

   public TableExportSelectionPanel()
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;

      radComplete = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.exportCompleteTable"));
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
      add(radComplete, gbc);

      radSelection = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.exportSelection"));
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(radSelection, gbc);

      radMultipleSQLRes = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.exportMultipleSQLResults"));
      radMultipleSQLRes.setToolTipText(s_stringMgr.getString("TableExportCsvDlg.exportMultipleSQLResults.tooltip"));
      gbc = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      add(radMultipleSQLRes, gbc);


      gbc = new GridBagConstraints(0, 1, 3, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0);
      add(createSQLResultExportListPanel(), gbc);


      ButtonGroup bg = new ButtonGroup();
      bg.add(radComplete);
      bg.add(radSelection);
      bg.add(radMultipleSQLRes);

      setBorder(BorderFactory.createEtchedBorder());
   }

   private JPanel createSQLResultExportListPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
      lstSQLResultsToExport = new JList<>();
      lstSQLResultsToExport.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      ret.add(new JScrollPane(lstSQLResultsToExport), gbc);

      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(createRightSideListEditButtons(), gbc);



      gbc = new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0);
      txtExportFileOrDir = GUIUtils.styleTextFieldToCopyableLabel(new JTextField());
      ret.add(txtExportFileOrDir, gbc);

      return GUIUtils.setPreferredHeight(ret, 150);
   }

   private JPanel createRightSideListEditButtons()
   {
      JPanel ret = new JPanel(new GridBagLayout());


      GridBagConstraints gbc;
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      btnUp = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.ARROW_UP));
      btnUp.setToolTipText(s_stringMgr.getString("TableExportSelectionPanel.edit.excel.table_sheet.order.tooltip"));
      ret.add(GUIUtils.styleAsToolbarButton(btnUp), gbc);

      gbc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      btnDown = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.ARROW_DOWN));
      btnDown.setToolTipText(s_stringMgr.getString("TableExportSelectionPanel.edit.excel.table_sheet.order.tooltip"));
      ret.add(GUIUtils.styleAsToolbarButton(btnDown), gbc);

      gbc = new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 0, 0, 0), 0, 0);
      btnEdit = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.EDIT));
      btnEdit.setToolTipText(s_stringMgr.getString("TableExportSelectionPanel.edit.excel.table_sheet.name.tooltip"));
      ret.add(GUIUtils.styleAsToolbarButton(btnEdit), gbc);

      gbc = new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 0, 0, 0), 0, 0);
      btnDelete = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.DELETE));
      btnDelete.setToolTipText(s_stringMgr.getString("TableExportSelectionPanel.remove.sql.result.tooltip"));
      ret.add(GUIUtils.styleAsToolbarButton(btnDelete), gbc);

      gbc = new GridBagConstraints(1, 4, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(3, 0, 0, 0), 0, 0);
      btnInfo = new SmallToolTipInfoButton(s_stringMgr.getString("TableExportSelectionPanel.info.button"));
      ret.add(btnInfo.getButton(), gbc);

      return ret;
   }
}
