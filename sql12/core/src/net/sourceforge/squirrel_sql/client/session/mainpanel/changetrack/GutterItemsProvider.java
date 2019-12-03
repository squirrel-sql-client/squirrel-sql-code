package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.filemanager.IFileEditorAPI;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

   private String _changeTrackBase;

   public GutterItemsProvider(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel, IFileEditorAPI fileEditorAPI, GutterItemsProviderListener gutterItemsProviderListener)
   {
      _sqlEntry = sqlEntry;
      _changeTrackPanel = changeTrackPanel;
      _gutterItemsProviderListener = gutterItemsProviderListener;
      _changeTrackingTrigger = new Timer(300, e -> onTriggerChangeTracking());
      _changeTrackingTrigger.setRepeats(false);

      fileEditorAPI.getFileHandler().setChangeTrackBaseListener(changeTrackBase -> onChangeTrackBaseChanged(changeTrackBase));

//      sqlEntry.getTextComponent().addKeyListener(new KeyAdapter()
//      {
//         public void keyTyped(KeyEvent e)
//         {
//            triggerChangeTracking();
//         }
//      });

      sqlEntry.getTextComponent().getDocument().addDocumentListener(new DocumentListener() {
         @Override
         public void insertUpdate(DocumentEvent e)
         {
            triggerChangeTracking();
         }

         @Override
         public void removeUpdate(DocumentEvent e)
         {
            triggerChangeTracking();
         }

         @Override
         public void changedUpdate(DocumentEvent e)
         {
            triggerChangeTracking();
         }
      });
   }

   private void onChangeTrackBaseChanged(String changeTrackBase)
   {
      _changeTrackBase = changeTrackBase;
      triggerChangeTracking();
   }


   private void triggerChangeTracking()
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
         List<GutterItem> gutterItems = GutterItemsCreator.createGutterItems(_sqlEntry, _changeTrackPanel, _changeTrackBase);

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
