package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.mainframe.action.findprefs.PreferencesAddressBook;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.filemanager.IFileEditorAPI;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.RevisionListController;
import net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack.revisionlist.RevisionListControllerChannel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.List;

public class ChangeTracker
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ChangeTracker.class);

   private final ISQLEntryPanel _sqlEntry;
   private ChangeTrackPanel _changeTrackPanel;
   private GutterItemsManager _gutterItemsManager;

   private boolean _enabled;
   private IFileEditorAPI _fileEditorAPI;
   private ChangeTrackCloseDispatcher _changeTrackCloseDispatcher = new ChangeTrackCloseDispatcher();

   public ChangeTracker(ISQLEntryPanel sqlEntry)
   {
      _sqlEntry = sqlEntry;

      _enabled = Main.getApplication().getSquirrelPreferences().isEnableChangeTracking();

      if (false == _enabled)
      {
         return;
      }

      JScrollPane scrollPane = sqlEntry.getTextAreaEmbeddedInScrollPane();
      _changeTrackPanel = new ChangeTrackPanel(scrollPane, g -> onPaintLeftGutter(g), g -> onPaintRightGutter(g));

   }

   public boolean isEnabled()
   {
      return _enabled;
   }

   public void initChangeTracking(IFileEditorAPI fileEditorAPI)
   {
      if(false == _enabled)
      {
         return;
      }

      _fileEditorAPI = fileEditorAPI;

      _gutterItemsManager = new GutterItemsManager(_sqlEntry, _changeTrackPanel, fileEditorAPI);

      _changeTrackPanel.trackingGutterLeft.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            onLeftGutterMousePressed(e, true);
         }

         @Override
         public void mouseReleased(MouseEvent e)
         {
            onLeftGutterMousePressed(e, false);
         }
      });
      _changeTrackPanel.trackingGutterLeft.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent e)
         {
            onLeftGutterMouseMoved(e);
         }
      });

      _changeTrackPanel.trackingGutterRight.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e)
         {
            onRightGutterMousePressed(e);
         }
      });
      _changeTrackPanel.trackingGutterRight.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent e)
         {
            onRightGutterMouseMoved(e);
         }
      });

      _sqlEntry.setTextAreaPaintListener(() -> _changeTrackPanel.requestGutterRepaint());
   }

   public void reInitChangeTrackingOnFileMoved()
   {
      initChangeTracking(_fileEditorAPI);
   }


   private void onLeftGutterMouseMoved(MouseEvent e)
   {
      _gutterItemsManager.leftGutterMouseMoved(e, _changeTrackPanel.trackingGutterLeft);
   }

   private void onLeftGutterMousePressed(MouseEvent me, boolean mousePressed)
   {
      if(me.isPopupTrigger())
      {
         JPopupMenu popupMenu = new JPopupMenu();

         JMenuItem menuItem;
         menuItem = new JMenuItem(s_stringMgr.getString("ChangeTracker.open.preferences"));
         menuItem.addActionListener(e -> onOpenChangeTrackPreferences());
         popupMenu.add(menuItem);

         if (     ChangeTrackTypeEnum.getPreference() == ChangeTrackTypeEnum.GIT
               && null != _fileEditorAPI.getFileHandler().getFile()
               && GitHandler.isInRepository(_fileEditorAPI.getFileHandler().getFile()))
         {
            menuItem = new JMenuItem(s_stringMgr.getString("ChangeTracker.open.git.revisions", _fileEditorAPI.getFileHandler().getFile().getName()));
            menuItem.addActionListener(e -> onShowGitRevisions(_fileEditorAPI.getFileHandler().getFile()));
            popupMenu.add(menuItem);
         }

         String changeTrackBase = _gutterItemsManager.getGutterItemsProvider().getChangeTrackBase();
         if(false == StringUtilities.isEmpty(changeTrackBase) && _gutterItemsManager.hasChanges())
         {
            menuItem = new JMenuItem(s_stringMgr.getString("ChangeTracker.revert.all"));
            menuItem.addActionListener(e -> _sqlEntry.setText(changeTrackBase));
            popupMenu.add(menuItem);
         }

         popupMenu.show(_changeTrackPanel.trackingGutterLeft, me.getX(), me.getY());
      }
      else if(mousePressed)
      {
         _gutterItemsManager.leftGutterMousePressed(me, _changeTrackPanel.trackingGutterLeft);
      }
   }

   private void onShowGitRevisions(File file)
   {
      RevisionListControllerChannel revisionListControllerChannel = new RevisionListControllerChannel()
      {
         @Override
         public void replaceEditorContent(String newEditorContent)
         {
            _sqlEntry.setText(newEditorContent);
         }

         @Override
         public void replaceChangeTrackBase(String newChangeTrackBase)
         {
            _gutterItemsManager.getGutterItemsProvider().rebaseChangeTrackingBy(newChangeTrackBase);
         }

         @Override
         public String getEditorContent()
         {
            return _sqlEntry.getText();
         }
      };

      new RevisionListController(_sqlEntry.getTextComponent(), _changeTrackCloseDispatcher, revisionListControllerChannel, file);
   }

   private void onOpenChangeTrackPreferences()
   {
      PreferencesAddressBook.CHANGE_TRACKING_PREFS.jumpTo();
      // GlobalPreferencesSheet.showSheet(SQLPreferencesPanel.class);
   }


   private void onRightGutterMouseMoved(MouseEvent e)
   {
      _gutterItemsManager.rightGutterMouseMoved(e, _changeTrackPanel.trackingGutterRight);
   }

   private void onRightGutterMousePressed(MouseEvent e)
   {
      _gutterItemsManager.rightGutterMousePressed(e);
   }

   private void onPaintRightGutter(Graphics g)
   {
      List<GutterItem> gutterItems = _gutterItemsManager.getLeftGutterItems();

      for (GutterItem gutterItem : gutterItems)
      {
         gutterItem.rightPaint(g);
      }
   }

   private void onPaintLeftGutter(Graphics g)
   {
      List<GutterItem> gutterItems = _gutterItemsManager.getLeftGutterItems();

      for (GutterItem gutterItem : gutterItems)
      {
         gutterItem.leftPaint(g);
      }
   }

   public JComponent embedInTracking()
   {
      if (_enabled)
      {
         return _changeTrackPanel;
      }
      else
      {
         return _sqlEntry.getTextAreaEmbeddedInScrollPane();
      }
   }


   public ChangeTrackTypeEnum getChangeTrackType()
   {
      return _gutterItemsManager.getGutterItemsProvider().getChangeTrackType();
   }

   public void changeTrackTypeChanged(ChangeTrackTypeEnum selectedType)
   {
      _gutterItemsManager.getGutterItemsProvider().changeTrackTypeChanged(selectedType);
   }


   /**
    * The commit button (Ctrl+K) was clicked.
    * In case of {@link ChangeTrackTypeEnum#GIT} this saves the file and commits it to GIT.
    * In case of {@link ChangeTrackTypeEnum#FILE} this does nothing, see also {@link net.sourceforge.squirrel_sql.client.session.action.ChangeTrackAction#actionPerformed(ActionEvent)}.
    * In case of {@link ChangeTrackTypeEnum#MANUAL} this makes the current editor contents the new change track base.
    *
    */
   public void rebaseChangeTrackingOnToolbarButtonOrMenuClicked()
   {
      if(false == _enabled)
      {
         return;
      }

      _gutterItemsManager.getGutterItemsProvider().rebaseChangeTrackingOnToolbarButtonOrMenu();
   }

   public void close()
   {
      _changeTrackCloseDispatcher.close();
   }
}
