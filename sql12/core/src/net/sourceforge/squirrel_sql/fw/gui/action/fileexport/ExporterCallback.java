package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;

public interface ExporterCallback
{
   TableExportPreferences getExportPreferences();

   /**
    * Create a instance of {@link ProgressAbortCallback} if necessary.
    * Subclasse may override this.
    * @return returns null.
    */
   ProgressAbortCallback createProgressController();

   /**
    * @param separatorChar
    * @return
    */
   boolean checkMissingData(String separatorChar);

   /**
    * Creates the export data from the original source.
    * @param ctrl the controller to use.
    * @return the data for the export.
    * @throws ExportDataException if any problem occurs while creating the data.
    */
   ExportDataInfoList createExportData(ExportController ctrl) throws ExportDataException;

}
