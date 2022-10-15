package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.Window;

public class ExportControllerFactory
{
   public static TableExportController createExportControllerForTable(Window owner)
   {
      return new TableExportController(owner, new TableExportSelectionPanelController(), false, true);
   }

   /**
    * Is not expected to be called from EDT.
    * That's why we halt the calling thread to wait for the user to finish the export configuration.
    */
   public static TableExportController createExportControllerForResultSet(Window owner)
   {
      final TableExportController[] buf = new TableExportController[1];
      GUIUtils.processOnSwingEventThread(() -> buf[0] = new TableExportController(owner, new ResultSetExportSelectionPanelController(), true, false), true);
      return buf[0];
   }
}
