package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.buttontabcomponent.SmallToolTipInfoButton;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

public class TableExportSelectionPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableExportSelectionPanel.class);

   JRadioButton radCompleteTableOrSingleFile;

   /////////////////////////////////////////////////////////////
   // Not null for ExportDialogType.UI_TABLE_EXPORT only
   JRadioButton radSelectionInUiTable;
   //
   //////////////////////////////////////////////////////////////

   /////////////////////////////////////////////////////////////
   // Not null for ExportDialogType.RESULT_SET_EXPORT only
   JCheckBox chkLimitRows;
   IntegerField txtLimitRows;
   //
   //////////////////////////////////////////////////////////////

   JRadioButton radMultipleSQLRes;

   JList<SqlResultListEntry> lstSQLResultsToExport;
   JButton btnUp;
   JButton btnDown;
   JButton btnEdit;
   JButton btnDelete;
   JButton btnSaveNames;
   JButton btnApplySavedNames;
   JTextField txtExportFileOrDir;
   SmallToolTipInfoButton btnInfo;

   public TableExportSelectionPanel(ExportDialogType exportDialogType)
   {
      super(new GridBagLayout());

      GridBagConstraints gbc;


      JPanel pnlButtons;
      if(exportDialogType == ExportDialogType.UI_TABLE_EXPORT)
      {
         pnlButtons = createButtonPanelForUiTableExport();
      }
      else if(exportDialogType == ExportDialogType.RESULT_SET_EXPORT)
      {
         pnlButtons = createButtonPanelForResultSetExport();
      }
      else
      {
         throw new IllegalArgumentException("Unknown exportDialogType: " + exportDialogType);
      }

      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0);
      add(pnlButtons, gbc);


      gbc = new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 5, 5, 5), 0, 0);
      add(createSQLResultExportListPanel(), gbc);


      ButtonGroup bg = new ButtonGroup();
      bg.add(radCompleteTableOrSingleFile);

      if(exportDialogType == ExportDialogType.UI_TABLE_EXPORT)
      {
         bg.add(radSelectionInUiTable);
      }

      bg.add(radMultipleSQLRes);

      setBorder(BorderFactory.createEtchedBorder());
      setBorder(BorderFactory.createLineBorder(Color.red));
   }

   private JPanel createButtonPanelForResultSetExport()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      // Left
      radCompleteTableOrSingleFile = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.export.single.file"));
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
      ret.add(radCompleteTableOrSingleFile, gbc);

      radMultipleSQLRes = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.exportMultipleSQLResults"));
      radMultipleSQLRes.setToolTipText(s_stringMgr.getString("TableExportCsvDlg.exportMultipleSQLResults.tooltip"));
      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0);
      ret.add(radMultipleSQLRes, gbc);


      // Right
      JPanel pnlLimit = new JPanel(new GridLayout(1,2,5,5));
      chkLimitRows = new JCheckBox(s_stringMgr.getString("ResultSetExportDialog.limitRows"));
      pnlLimit.add(chkLimitRows);
      txtLimitRows = new IntegerField(5);
      pnlLimit.add(txtLimitRows);
      gbc = new GridBagConstraints(1, 0, 1, GridBagConstraints.REMAINDER, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(pnlLimit, gbc);

      return ret;
   }

   private JPanel createButtonPanelForUiTableExport()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;
      radCompleteTableOrSingleFile = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.exportCompleteTable"));
      gbc = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0);
      ret.add(radCompleteTableOrSingleFile, gbc);

      radSelectionInUiTable = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.exportSelection"));
      gbc = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(radSelectionInUiTable, gbc);

      radMultipleSQLRes = new JRadioButton(s_stringMgr.getString("TableExportCsvDlg.exportMultipleSQLResults"));
      radMultipleSQLRes.setToolTipText(s_stringMgr.getString("TableExportCsvDlg.exportMultipleSQLResults.tooltip"));
      gbc = new GridBagConstraints(0, 1, 3, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0);
      ret.add(radMultipleSQLRes, gbc);


      gbc = new GridBagConstraints(2, 0, 1, GridBagConstraints.REMAINDER, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(new JPanel(), gbc);

      return ret;
   }

   private JPanel createSQLResultExportListPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
      lstSQLResultsToExport = new JList<>();
      lstSQLResultsToExport.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      ret.add(GUIUtils.setPreferredHeight(new JScrollPane(lstSQLResultsToExport), 50), gbc);

      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      ret.add(createEditButtonsAtBottomOfList(), gbc);


      gbc = new GridBagConstraints(0, 2, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0);
      txtExportFileOrDir = GUIUtils.styleTextFieldToCopyableLabel(new JTextField());
      ret.add(txtExportFileOrDir, gbc);

      return ret;
   }

   private JPanel createEditButtonsAtBottomOfList()
   {
      JPanel ret = new JPanel(new GridBagLayout());


      GridBagConstraints gbc;
      gbc = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      btnUp = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.ARROW_UP));
      btnUp.setToolTipText(s_stringMgr.getString("TableExportSelectionPanel.edit.excel.table_sheet.order.tooltip"));
      ret.add(GUIUtils.styleAsToolbarButton(btnUp), gbc);

      gbc = new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
      btnDown = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.ARROW_DOWN));
      btnDown.setToolTipText(s_stringMgr.getString("TableExportSelectionPanel.edit.excel.table_sheet.order.tooltip"));
      ret.add(GUIUtils.styleAsToolbarButton(btnDown), gbc);

      gbc = new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0);
      btnEdit = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.EDIT));
      btnEdit.setToolTipText(s_stringMgr.getString("TableExportSelectionPanel.edit.excel.table_sheet.name.tooltip"));
      ret.add(GUIUtils.styleAsToolbarButton(btnEdit), gbc);

      gbc = new GridBagConstraints(3, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0);
      btnDelete = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.DELETE));
      btnDelete.setToolTipText(s_stringMgr.getString("TableExportSelectionPanel.remove.sql.result.tooltip"));
      ret.add(GUIUtils.styleAsToolbarButton(btnDelete), gbc);

      gbc = new GridBagConstraints(4, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0);
      btnSaveNames = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.SAVE));
      btnSaveNames.setToolTipText(s_stringMgr.getString("TableExportSelectionPanel.save.names.tooltip"));
      ret.add(GUIUtils.styleAsToolbarButton(btnSaveNames), gbc);

      gbc = new GridBagConstraints(5, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0);
      btnApplySavedNames = new JButton(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.OPEN));
      btnApplySavedNames.setToolTipText(s_stringMgr.getString("TableExportSelectionPanel.apply.saved.names.tooltip"));
      ret.add(GUIUtils.styleAsToolbarButton(btnApplySavedNames), gbc);

      gbc = new GridBagConstraints(6, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 3, 0, 0), 0, 0);
      btnInfo = new SmallToolTipInfoButton(s_stringMgr.getString("TableExportSelectionPanel.info.button"));
      ret.add(btnInfo.getButton(), gbc);

      return ret;
   }
}
