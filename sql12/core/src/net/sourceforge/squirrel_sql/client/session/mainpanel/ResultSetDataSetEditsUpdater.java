package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerEditableTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.ArrayList;

public class ResultSetDataSetEditsUpdater
{
   private static ILogger s_log = LoggerController.createLogger(ResultSetDataSetEditsUpdater.class);


   public static void updateEdits(IDataSetViewer sourceEditedDataSetViewer, ResultSetDataSet targetResultSetDataSet)
   {
      if(false == sourceEditedDataSetViewer instanceof DataSetViewerEditableTablePanel)
      {
         s_log.error(new IllegalArgumentException("Parameter sourceEditedDataSetViewer is not an instance of DataSetViewerEditableTablePanel but an instance of "
               + (null != sourceEditedDataSetViewer ? sourceEditedDataSetViewer.getClass().getName() : "<is null!?>")
               + " how could it have possibly been edited? No updates done!"));

         return;
      }

      DataSetViewerEditableTablePanel source = (DataSetViewerEditableTablePanel) sourceEditedDataSetViewer;

      if(source.getColumnDefinitions().length != targetResultSetDataSet.getColumnCount())
      {
         s_log.error(new IllegalArgumentException("Number of source columns (" + source.getColumnDefinitions().length + ")" +
               " does not match number of target columns (" + targetResultSetDataSet.getColumnCount() + "). No updates done!"));

         return;
      }

      ArrayList<Object[]> updatedRows = new ArrayList<>();
      for (int i = 0; i < source.getRowCount(); i++)
      {
         updatedRows.add(source.getRow(i));
      }

      targetResultSetDataSet.replaceDataOnUserEdits(updatedRows);
   }
}
