package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ReadMoreResultsHandlerListener;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReadMoreResultsHandler
{
   private final static ILogger s_log = LoggerController.createLogger(ReadMoreResultsHandler.class);

   private ISession _session;
   private final JLabel _lblLoading;
   private final ImageIcon _loadingGif;
   private ExecutorService _executorService;
   private Future<SwingWorker<SwingWorker, Object>> _future;


   public ReadMoreResultsHandler(ISession session)
   {
      _session = session;

      _loadingGif = _session.getApplication().getResources().getIcon(SquirrelResources.IImageNames.LOADING_GIF);
      _lblLoading = new JLabel(_loadingGif);
      _lblLoading.setVisible(false);

      _executorService = Executors.newSingleThreadExecutor();

   }

   public void readMoreResults(final ResultSetDataSet rsds, final ReadMoreResultsHandlerListener readChannelCallBack)
   {
      if(null != _future && false == _future.isDone())
      {
         return;
      }

      _lblLoading.setVisible(true);

      SwingWorker<SwingWorker, Object> sw =
            new SwingWorker<SwingWorker, Object>()
            {
               @Override
               protected SwingWorker doInBackground()
               {
                     rsds.readMoreResults();
                     return this;
               }

               @Override
               protected void done()
               {
                  try
                  {
                     get();
                     onReadMoreResultsDone(readChannelCallBack);
                  }
                  catch (Throwable e)
                  {
                     // The "throw" below didn't reach our standard exception handling
                     // although this done() is executed on the EDT.
                     Main.getApplication().getMessageHandler().showErrorMessage(e);
                     s_log.error(e);

                     throw new RuntimeException(e);
                  }
               }
            };

      _future = (Future<SwingWorker<SwingWorker, Object>>) _executorService.submit(sw);
   }

   private void onReadMoreResultsDone(net.sourceforge.squirrel_sql.fw.datasetviewer.ReadMoreResultsHandlerListener readChannelCallBack)
   {
      try
      {
         _lblLoading.setVisible(false);
         readChannelCallBack.moreResultsHaveBeenRead();
      }
      catch (Throwable t)
      {
         throw new RuntimeException(t);
      }
   }

   public JLabel getLoadingLabel()
   {
      return _lblLoading;
   }

}
