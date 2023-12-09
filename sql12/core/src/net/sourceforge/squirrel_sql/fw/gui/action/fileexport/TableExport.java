package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import javax.swing.*;
import java.io.File;

/**
 * Command for exporting a {@link JTable} into a file.
 * <p><b>Note:</b> The structure of this class has be really changed.
 * The content of this class was split up into the package <pre>net.sourceforge.squirrel_sql.fw.gui.action.export</pre>
 *
 * @author Stefan Willinger
 * @see net.sourceforge.squirrel_sql.fw.gui.action.export
 */
public class TableExport
{
   private Exporter _exporter;

   public TableExport(JTable table)
   {
      final ExportController exportController = new ExportController(new ExportSourceAccess(table) , SwingUtilities.windowForComponent(table), ExportDialogType.UI_TABLE_EXPORT);
      exportController.showDialog();
      _exporter = new Exporter(() -> null, new ExportControllerProxy(exportController));
   }

   public void export()
   {
      _exporter.export();
   }

   public File getTargetFile()
   {
      return _exporter.getSingleExportTargetFile();
   }

   public long getWrittenRows()
   {
      return _exporter.getWrittenRows();
   }
}
