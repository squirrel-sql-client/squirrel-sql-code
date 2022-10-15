package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import javax.swing.JPanel;

public interface ExportSelectionPanelController
{
   JPanel getPanel();

   void writeControlsToPrefs(TableExportPreferences prefs);

   void initPanel(TableExportPreferences prefs);

   boolean isExportComplete();
}
