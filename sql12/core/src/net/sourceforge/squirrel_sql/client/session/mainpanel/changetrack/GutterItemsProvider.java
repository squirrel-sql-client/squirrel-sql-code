package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.filemanager.FileHandlerListener;
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
   private ChangeTrackTypeEnum _currentChangeTrackType = ChangeTrackTypeEnum.getPreference();

   private Timer _changeTrackingTrigger;
   private ExecutorService _changeTrackingExecutorService = Executors.newSingleThreadExecutor();

   private volatile boolean _changeTrackingIsBeingExecuted;
   private final ISQLEntryPanel _sqlEntry;
   private final ChangeTrackPanel _changeTrackPanel;
   private IFileEditorAPI _fileEditorAPI;
   private GutterItemsProviderListener _gutterItemsProviderListener;

   private String _changeTrackBase;
   private FileHandlerListener _fileChangeListener;
   private FileHandlerListener _gitFileChangeListener;

   public GutterItemsProvider(ISQLEntryPanel sqlEntry, ChangeTrackPanel changeTrackPanel, IFileEditorAPI fileEditorAPI, GutterItemsProviderListener gutterItemsProviderListener)
   {
      _sqlEntry = sqlEntry;
      _changeTrackPanel = changeTrackPanel;
      _fileEditorAPI = fileEditorAPI;
      _gutterItemsProviderListener = gutterItemsProviderListener;
      _changeTrackingTrigger = new Timer(300, e -> onTriggerChangeTracking());
      _changeTrackingTrigger.setRepeats(false);

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

      _fileChangeListener = newChangeTrackBase -> updateChangeTrackBase(newChangeTrackBase);
      _gitFileChangeListener = newChangeTrackBase -> updateGitChangeTracking(false);
      rebaseGutterItems(RebaseGutterItemsCallInfo.BUTTON_SELECTED);
   }

   public void rebaseGutterItems(RebaseGutterItemsCallInfo callInfo)
   {
      switch(_currentChangeTrackType)
      {
         case MANUAL:
            _fileEditorAPI.getFileHandler().setFileHandlerListener(null);

            if (callInfo == RebaseGutterItemsCallInfo.BUTTON_CLICKED ) // Manually rebase only takes place, when the button is clicked.
            {
               updateChangeTrackBase(_fileEditorAPI.getText());
            }
            break;
         case FILE:
            _fileEditorAPI.getFileHandler().setFileHandlerListener(_fileChangeListener);
            break;
         case GIT:
            _fileEditorAPI.getFileHandler().setFileHandlerListener(_gitFileChangeListener);

            // If BUTTON_CLICKED. -> Commit
            updateGitChangeTracking(callInfo == RebaseGutterItemsCallInfo.BUTTON_CLICKED);
            break;
         default:
            throw new IllegalStateException("Unknown ChangeTrackType: " + _changeTrackBase);
      }
   }

   public ChangeTrackTypeEnum getChangeTrackType()
   {
      return _currentChangeTrackType;
   }

   public void rebaseChangeTrackingOnToolbarButtonOrMenu()
   {
      rebaseGutterItems(RebaseGutterItemsCallInfo.BUTTON_CLICKED);
   }

   public void changeTrackTypeChanged(ChangeTrackTypeEnum selectedType)
   {
      _currentChangeTrackType = selectedType;
      rebaseGutterItems(RebaseGutterItemsCallInfo.BUTTON_SELECTED);
   }

   public String getChangeTrackBase()
   {
      return _changeTrackBase;
   }

   public void rebaseChangeTrackingBy(String newChangeTrackBase)
   {
      updateChangeTrackBase(newChangeTrackBase);
   }

   private void updateGitChangeTracking(boolean commitToGit)
   {
      String gitChangeTrackBase = GitHandler.getChangeTrackBaseFromGit(_fileEditorAPI, commitToGit);
      updateChangeTrackBase(gitChangeTrackBase);
   }

   private void updateChangeTrackBase(String newChangeTrackBase)
   {
      _changeTrackBase = newChangeTrackBase;
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
