package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.JPanel;
import java.io.File;

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
   }

   @Override
   public void writeControlsToPrefs(TableExportPreferences prefs)
   {
      _exportSelectionPanel.radComplete.isSelected();
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
