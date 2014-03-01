package net.sourceforge.squirrel_sql.client.session.mainpanel;

import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ReadMoreResultsHandlerListener;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReadMoreResultsHandler
{
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

      _executorService = Executors.newFixedThreadPool(1);

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
                  catch (Exception e)
                  {
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
