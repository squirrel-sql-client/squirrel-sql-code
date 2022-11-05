package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import java.awt.Window;

public class ExportControllerFactory
{
   public static ExportController createExportControllerForTable(Window owner)
   {
      final ExportController exportController = new ExportController(owner, ExportDialogType.UI_TABLE_EXPORT);
      exportController.showDialog();
      return exportController;
   }

   /**
    * Is not expected to be called from EDT.
    * That's why we halt the calling thread to wait for the user to finish the export configuration.
    * To halt the calling thread the export dialog is modal.
    */
   public static ExportController createExportControllerForResultSet(Window owner)
   {
      final ExportController exportController = new ExportController(owner, ExportDialogType.RESULT_SET_EXPORT);
      GUIUtils.processOnSwingEventThread(() -> exportController.showDialog(), true);
      return exportController;
   }
}
