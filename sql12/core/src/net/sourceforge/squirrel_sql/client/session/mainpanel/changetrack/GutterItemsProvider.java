package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GutterItemsProvider
{
   private Timer _changeTrackingTrigger;
   private ExecutorService _changeTrackingExecutorService = Executors.newSingleThreadExecutor();

   private volatile boolean _changeTrackingIsBeingExecuted;
   private final ISQLEntryPanel _sqlEntry;
   private final ChangeTrackPanel _changeTrackPanel;
   private GutterItemsProviderListener _gutterItemsProviderListener;

   public GutterItemsProvider(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel, GutterItemsProviderListener gutterItemsProviderListener)
   {
      _sqlEntry = sqlEntry;
      _changeTrackPanel = changeTrackPanel;
      _gutterItemsProviderListener = gutterItemsProviderListener;
      _changeTrackingTrigger = new Timer(1000, e -> onTriggerChangeTracking());
      _changeTrackingTrigger.setRepeats(false);

      sqlEntry.getTextComponent().addKeyListener(new KeyAdapter()
      {
         public void keyTyped(KeyEvent e)
         {
            onTriggerChangeTracking(e);
         }
      });
   }


   private void onTriggerChangeTracking(KeyEvent e)
   {
      _changeTrackingTrigger.restart();
   }

   private void onTriggerChangeTracking()
   {
      if(_changeTrackingIsBeingExecuted)
      {
         _changeTrackingTrigger.restart();
         return;
      }

      _changeTrackingIsBeingExecuted = true;

      _changeTrackingExecutorService.submit(() -> onRunChangeTrackTask());
   }

   private void onRunChangeTrackTask()
   {
      try
      {
         List<GutterItem> gutterItems = ChangeTrackTask.getGutterItems(_sqlEntry, _changeTrackPanel);

         SwingUtilities.invokeLater(() -> _gutterItemsProviderListener.updateGutterItems(gutterItems));
      }
      catch (Throwable t)
      {
         SwingUtilities.invokeLater(() -> {throw Utilities.wrapRuntime(t);});
      }
      finally
      {
         _changeTrackingIsBeingExecuted = false;
      }
   }

}
