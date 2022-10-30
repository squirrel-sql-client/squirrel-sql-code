package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.JPanel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TableExportSelectionPanelController implements ExportSelectionPanelController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(TableExportSelectionPanelController.class);


   private TableExportSelectionPanel _exportSelectionPanel;

   public TableExportSelectionPanelController()
   {
      _exportSelectionPanel = new TableExportSelectionPanel();

      _exportSelectionPanel.radComplete.addActionListener(e -> updateUI());
      _exportSelectionPanel.radSelection.addActionListener(e -> updateUI());
      _exportSelectionPanel.radMultipleSQLRes.addActionListener(e -> updateUI());

      _exportSelectionPanel.btnEdit.addActionListener(e -> onEdit());

      _exportSelectionPanel.lstSQLResultsToExport.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e)
         {
            onListClicked(e);
         }
      });
   }

   private void onEdit()
   {
      final SqlResultListEntry selectedValue = _exportSelectionPanel.lstSQLResultsToExport.getSelectedValue();

      if(null == selectedValue)
      {
         final String msg = s_stringMgr.getString("TableExportSelectionPanelController.no.sql.result.selected");
         Main.getApplication().getMessageHandler().showWarningMessage(msg);
         return;
      }

      GUIUtils.getOwningWindow(_exportSelectionPanel);

      final EditExcelTabOrFileNameCtrl ctrl =
            new EditExcelTabOrFileNameCtrl(GUIUtils.getOwningWindow(_exportSelectionPanel), selectedValue.toString());

      if(ctrl.isOk())
      {
         selectedValue.setUserEnteredSqlResultName(ctrl.getNewSqlResultName());
      }

      //((DefaultListModel)_exportSelectionPanel.lstSQLResultsToExport.getModel()).elem
   }

   private void onListClicked(MouseEvent e)
   {
      if( 1 != e.getButton())
      {
         return;
      }

      if(1 == e.getClickCount())
      {
         final SqlResultListEntry selEntry = _exportSelectionPanel.lstSQLResultsToExport.getSelectedValue();

         if(null == selEntry)
         {
            return;
         }
         selEntry.getHandle().indicateTabComponent();
      }
      else if(2 == e.getClickCount())
      {
         final SqlResultListEntry selEntry = _exportSelectionPanel.lstSQLResultsToExport.getSelectedValue();

         if(null == selEntry)
         {
            return;
         }
         selEntry.getHandle().selectResultTab();
         selEntry.getHandle().indicateTabComponent();

      }
   }

   @Override
   public void writeControlsToPrefs(TableExportPreferences prefs)
   {
      prefs.setExportMultipleSQLResults(_exportSelectionPanel.radMultipleSQLRes.isSelected());
      prefs.setExportComplete(prefs.isExportComplete());
   }

   @Override
   public void initPanel(TableExportPreferences prefs)
   {
      if(prefs.isExportComplete())
      {
         _exportSelectionPanel.radComplete.setSelected(true);
      }
      else
      {
         _exportSelectionPanel.radSelection.setSelected(true);
      }

      if(prefs.isExportMultipleSQLResults())
      {
         _exportSelectionPanel.radMultipleSQLRes.setSelected(true);
      }

      updateUI();
   }

   private void updateUI()
   {
      _exportSelectionPanel.lstSQLResultsToExport.setEnabled(_exportSelectionPanel.radMultipleSQLRes.isSelected());
      _exportSelectionPanel.btnUp.setEnabled(_exportSelectionPanel.radMultipleSQLRes.isSelected());
      _exportSelectionPanel.btnDown.setEnabled(_exportSelectionPanel.radMultipleSQLRes.isSelected());
      _exportSelectionPanel.btnEdit.setEnabled(_exportSelectionPanel.radMultipleSQLRes.isSelected());
      _exportSelectionPanel.btnDelete.setEnabled(_exportSelectionPanel.radMultipleSQLRes.isSelected());
      _exportSelectionPanel.btnInfo.setEnabled(_exportSelectionPanel.radMultipleSQLRes.isSelected());

      if(_exportSelectionPanel.radMultipleSQLRes.isSelected())
      {
         List<SqlResultTabHandle> sqlResultTabHandel = Main.getApplication().getMultipleSqlResultExportChannel().getSqlResultTabHandles();

         int[] indexCounter = new int[1];
         final List<SqlResultListEntry> listEntries = sqlResultTabHandel.stream().map(h -> new SqlResultListEntry(h, ++indexCounter[0])).collect(Collectors.toList());

         final SqlResultListEntry selEntry = _exportSelectionPanel.lstSQLResultsToExport.getSelectedValue();

         _exportSelectionPanel.lstSQLResultsToExport.setListData(listEntries.toArray(new SqlResultListEntry[0]));

         if(null != selEntry)
         {
            final Optional<SqlResultListEntry> match = listEntries.stream().filter(e -> e.toString().equals(selEntry.toString())).findFirst();

            if(match.isPresent())
            {
               _exportSelectionPanel.lstSQLResultsToExport.setSelectedValue(selEntry, true);
            }
         }
         else if(false == listEntries.isEmpty())
         {
            _exportSelectionPanel.lstSQLResultsToExport.setSelectedIndex(0);
         }

      }
      else
      {
         _exportSelectionPanel.lstSQLResultsToExport.setListData(new SqlResultListEntry[0]);
      }
   }

   @Override
   public boolean isExportComplete()
   {
      return _exportSelectionPanel.radComplete.isSelected();
   }

   @Override
   public void updateExportDestinationInfo(String exportFileNameText, boolean destinationIsExcel)
   {
      _exportSelectionPanel.txtExportFileOrDir.setText(s_stringMgr.getString("TableExportSelectionPanelController.export.location.unspecified"));

      if(StringUtilities.isEmpty(exportFileNameText, true))
      {
         return;
      }

      final File file = new File(exportFileNameText);

      if(file.isDirectory())
      {
         if(false == destinationIsExcel)
         {
            _exportSelectionPanel.txtExportFileOrDir.setText(s_stringMgr.getString("TableExportSelectionPanelController.export.dir", file.getAbsolutePath()));
         }
      }
      else
      {
         if(false == destinationIsExcel )
         {
            if(null != file.getParentFile())
            {
               _exportSelectionPanel.txtExportFileOrDir.setText(s_stringMgr.getString("TableExportSelectionPanelController.export.dir", file.getParentFile().getAbsolutePath()));
            }
         }
         else
         {
            _exportSelectionPanel.txtExportFileOrDir.setText(s_stringMgr.getString("TableExportSelectionPanelController.export.file", file.getAbsolutePath()));
         }
      }
   }

   @Override
   public JPanel getPanel()
   {
      return _exportSelectionPanel;
   }
}
