package net.sourceforge.squirrel_sql.fw.datasetviewer;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

final class DataSetScrollingPanelUpdater implements Runnable
{
   private static final ILogger s_log = LoggerController.createLogger(DataSetScrollingPanelUpdater.class);

   private final IDataSetViewer _viewer;
   private final IDataSet _ds;

   DataSetScrollingPanelUpdater(IDataSetViewer viewer, IDataSet ds)
   {
      _viewer = viewer;
      _ds = ds;
   }

   public void run()
   {
      try
      {
         if (null == _ds)
         {
            _viewer.show(new EmptyDataSet());
         }
         else
         {
            _viewer.show(_ds);
         }
      }
      catch (Throwable th)
      {
         s_log.error("Error processing a DataSet", th);
      }
   }
}
