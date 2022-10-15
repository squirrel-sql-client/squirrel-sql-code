package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import javax.swing.JPanel;

public class TableExportSelectionPanelController implements ExportSelectionPanelController
{
   private TableExportSelectionPanel _exportSelectionPanel;

   public TableExportSelectionPanelController()
   {
      _exportSelectionPanel = new TableExportSelectionPanel();
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
   }

   @Override
   public boolean isExportComplete()
   {
      return _exportSelectionPanel.radComplete.isSelected();
   }

   @Override
   public JPanel getPanel()
   {
      return _exportSelectionPanel;
   }
}
