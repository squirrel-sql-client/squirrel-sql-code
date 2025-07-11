/*
 * Copyright (C) 2003 Joseph Mocker
 * mock-sf@misfit.dhs.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sourceforge.squirrel_sql.plugins.sqlbookmark;

import java.awt.Component;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.sqlbookmark.exportimport.BookmarkExportImport;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Manage the bookmarks.
 * <p/>
 * The interface to allow a user to manages his/her bookmarks is through
 * this class. The user can add, edit, remove and shift the order of the
 * bookmarks with this user interface. The interface shows up in as
 * a new tab in the Global Preferences dialog.
 *
 * @author Joseph Mocker
 */
public class SQLBookmarkPreferencesController implements IGlobalPreferencesPanel
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SQLBookmarkPreferencesController.class);

   private SQLBookmarkPreferencesPanel _pnlPrefs;


   /**
    * Handle to the plugin
    */
   protected SQLBookmarkPlugin _plugin;
   private DefaultMutableTreeNode _nodeSquirrelMarks;
   private DefaultMutableTreeNode _nodeUserMarks;
   private BookmarksUserFolderHandler _bookmarksUserFolderHandler;


   /**
    * Create the preferences
    */
   public SQLBookmarkPreferencesController(SQLBookmarkPlugin plugin)
   {
      this._plugin = plugin;
   }

   /**
    * Initialize the user interface
    *
    * @param app Handle to the main application.
    */
   public void initialize(IApplication app)
   {
      // i18n[sqlbookmark.btnTextEdit=Edit]
      _pnlPrefs.btnEdit.setText(s_stringMgr.getString("SQLBookmarkPreferencesPanel.sqlbookmark.btnTextEdit"));

      DefaultMutableTreeNode root = new DefaultMutableTreeNode("");

      _nodeSquirrelMarks = new DefaultMutableTreeNode(s_stringMgr.getString("sqlbookmark.nodeSquirrelMarks"));
      _nodeUserMarks = new DefaultMutableTreeNode(s_stringMgr.getString("sqlbookmark.nodeUserMarks"));

      root.add(_nodeUserMarks);
      root.add(_nodeSquirrelMarks);
      DefaultTreeModel dtm = new DefaultTreeModel(root);
      _pnlPrefs.treBookmarks.setModel(dtm);
      _pnlPrefs.treBookmarks.setRootVisible(false);

      _pnlPrefs.treBookmarks.setCellRenderer(new BookmarksTreeCellRenderer());

      _pnlPrefs.treBookmarks.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

      Bookmark[] defaultBookmarks = DefaultBookmarksFactory.getDefaultBookmarks();
      for (int i = 0; i < defaultBookmarks.length; i++)
      {
         _nodeSquirrelMarks.add(new DefaultMutableTreeNode(defaultBookmarks[i]));
      }

      for (Iterator<Bookmark> i = _plugin.getBookmarkManager().iterator(); i.hasNext();)
      {
         Bookmark mark = i.next();
         _nodeUserMarks.add(new DefaultMutableTreeNode(mark));
      }

      _pnlPrefs.treBookmarks.expandPath(new TreePath(dtm.getPathToRoot(_nodeUserMarks)));

      String propDefaultMarksInPopup =
         _plugin.getBookmarkProperties().getProperty(SQLBookmarkPlugin.BOOKMARK_PROP_DEFAULT_MARKS_IN_POPUP, "" + false);

      _pnlPrefs.chkSquirrelMarksInPopup.setSelected(Boolean.valueOf(propDefaultMarksInPopup).booleanValue());


      String useContainsToFilterBookmarks =
         _plugin.getBookmarkProperties().getProperty(SQLBookmarkPlugin.BOOKMARK_PROP_USE_CONTAINS_TO_FILTER_BOOKMARKS, "" + false);

      _pnlPrefs.chkUseContainsToFilterBookmarks.setSelected(Boolean.valueOf(useContainsToFilterBookmarks).booleanValue());

      _pnlPrefs.treBookmarks.getSelectionModel().addTreeSelectionListener(e -> updateButtonsEnabled());

      _bookmarksUserFolderHandler = new BookmarksUserFolderHandler(_pnlPrefs.treBookmarks, _nodeUserMarks);

      _pnlPrefs.btnRun.addActionListener(e -> onRun());
      _pnlPrefs.btnAdd.addActionListener(e -> onAdd());
      _pnlPrefs.btnAddFolder.addActionListener(e -> _bookmarksUserFolderHandler.addFolder());
      _pnlPrefs.btnEdit.addActionListener(e -> onEdit());
      _pnlPrefs.btnDel.addActionListener(e -> onDelete());
      _pnlPrefs.btnUp.addActionListener(e -> onUp());
      _pnlPrefs.btnDown.addActionListener(e -> onDown());

      _pnlPrefs.btnExport.addActionListener(e -> BookmarkExportImport.exportBookMarks(_nodeUserMarks, _pnlPrefs.treBookmarks));
      _pnlPrefs.btnImport.addActionListener(e -> BookmarkExportImport.importBookMarks(_nodeUserMarks, _pnlPrefs.treBookmarks));

      updateButtonsEnabled();

   }

   public void uninitialize(IApplication app)
   {
      _plugin.removeALLSQLPanelsAPIListeningForBookmarks();
   }

   /**
    * Return the title for the tab name
    *
    * @return The tab title.
    */
   public String getTitle()
   {
      return _plugin.getResourceString("prefs.title");
   }

   /**
    * Return the tool tip for the tab
    *
    * @return The tab hint
    */
   public String getHint()
   {
      return _plugin.getResourceString("prefs.hint");
   }

   /**
    * Make the changes active to the rest of the application
    */
   public void applyChanges()
   {
      // create a new bookmark manager
      BookmarkManager bookmarks = _plugin.getBookmarkManager();

      bookmarks.removeAll();

      for (int i = 0; i < _nodeUserMarks.getChildCount(); ++i)
      {
         Bookmark bookmark = (Bookmark) ((DefaultMutableTreeNode) _nodeUserMarks.getChildAt(i)).getUserObject();
         bookmarks.add(bookmark);
      }

      // rebuild the bookmark menu.
      _plugin.rebuildMenu();
      bookmarks.save();


      _plugin.getBookmarkProperties().put(SQLBookmarkPlugin.BOOKMARK_PROP_DEFAULT_MARKS_IN_POPUP, "" + _pnlPrefs.chkSquirrelMarksInPopup.isSelected());

      _plugin.getBookmarkProperties().put(SQLBookmarkPlugin.BOOKMARK_PROP_USE_CONTAINS_TO_FILTER_BOOKMARKS, "" + _pnlPrefs.chkUseContainsToFilterBookmarks.isSelected());

      _plugin.saveBookmarkProperties();
   }

   /**
    * Return the panel that will contain the prefernces ui.
    *
    * @return Panel containing preferences.
    */
   public Component getPanelComponent()
   {
      // this gets called before initialize()
      _pnlPrefs = new SQLBookmarkPreferencesPanel(_plugin);

      return _pnlPrefs;
   }

   private void updateButtonsEnabled()
   {

      final TreePath[] selectionPaths = _pnlPrefs.treBookmarks.getSelectionPaths();
      if(null == selectionPaths || 0 == selectionPaths.length)
      {
         _pnlPrefs.btnUp.setEnabled(false);
         _pnlPrefs.btnDown.setEnabled(false);
         _pnlPrefs.btnDel.setEnabled(false);
         _pnlPrefs.btnEdit.setEnabled(false);
         _pnlPrefs.btnRun.setEnabled(false);
         _pnlPrefs.btnImport.setEnabled(true);
         _pnlPrefs.btnExport.setEnabled(true);
         return;
      }

      _pnlPrefs.btnUp.setEnabled(true);
      _pnlPrefs.btnDown.setEnabled(true);
      _pnlPrefs.btnDel.setEnabled(true);
      _pnlPrefs.btnEdit.setEnabled(true);
      _pnlPrefs.btnRun.setEnabled(true);
      _pnlPrefs.btnImport.setEnabled(true);
      _pnlPrefs.btnExport.setEnabled(true);


      if(1 < selectionPaths.length)
      {
         _pnlPrefs.btnRun.setEnabled(false);
         _pnlPrefs.btnEdit.setEnabled(false);

         if(false == BookmarkTreeUtil.areAllPathsChildrenOf(selectionPaths, _nodeUserMarks))
         {
            _pnlPrefs.btnUp.setEnabled(false);
            _pnlPrefs.btnDown.setEnabled(false);
            _pnlPrefs.btnDel.setEnabled(false);
            _pnlPrefs.btnImport.setEnabled(false);
            _pnlPrefs.btnExport.setEnabled(false);
         }

      }
      else
      {
         DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) selectionPaths[0].getLastPathComponent();

         if(dmtn.getUserObject() instanceof Bookmark)
         {
            if (dmtn.getParent() == _nodeSquirrelMarks)
            {
               _pnlPrefs.btnUp.setEnabled(false);
               _pnlPrefs.btnDown.setEnabled(false);
               _pnlPrefs.btnDel.setEnabled(false);
               _pnlPrefs.btnImport.setEnabled(false);
               _pnlPrefs.btnExport.setEnabled(false);
               _pnlPrefs.btnEdit.setText(s_stringMgr.getString("SQLBookmarkPreferencesPanel.sqlbookmark.btnTextView"));
            }
            else
            {
               _pnlPrefs.btnEdit.setText(s_stringMgr.getString("SQLBookmarkPreferencesPanel.sqlbookmark.btnTextEdit"));
            }
         }
         else if(dmtn.getUserObject() instanceof UserBookmarkFolder)
         {
            _pnlPrefs.btnEdit.setText(s_stringMgr.getString("SQLBookmarkPreferencesPanel.sqlbookmark.btnTextEdit"));
            _pnlPrefs.btnEdit.setEnabled(true);

            _pnlPrefs.btnRun.setEnabled(false);
            //_pnlPrefs.btnUp.setEnabled(false);
            //_pnlPrefs.btnDown.setEnabled(false);
            _pnlPrefs.btnDel.setEnabled(false);
            _pnlPrefs.btnImport.setEnabled(false);
            _pnlPrefs.btnExport.setEnabled(false);

         }
         else
         {
            _pnlPrefs.btnUp.setEnabled(false);
            _pnlPrefs.btnDown.setEnabled(false);
            _pnlPrefs.btnEdit.setEnabled(false);
            _pnlPrefs.btnDel.setEnabled(false);
            _pnlPrefs.btnImport.setEnabled(_nodeUserMarks == dmtn);
            _pnlPrefs.btnExport.setEnabled(false);
         }
      }

   }


   private void onRun()
   {
      DefaultMutableTreeNode selNode = null;
      if(null != _pnlPrefs.treBookmarks.getSelectionPath())
      {
         selNode = (DefaultMutableTreeNode) _pnlPrefs.treBookmarks.getSelectionPath().getLastPathComponent();
      }

      if (null == selNode || false == selNode.getUserObject() instanceof Bookmark)
      {
         // i18n[sqlbookmark.noRunSelection=Please select the bookmark to run]
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("sqlbookmark.noRunSelection"));
         return;
      }



      ISQLPanelAPI[] apis = _plugin.getSQLPanelAPIsListeningForBookmarks();

      if(0 == apis.length)
      {
         // i18n[sqlbookmark.noSQLPanel=To run a bookmark you must open this window\nusing the "Edit Bookmarks" toolbar button of a Session window.\nThe bookmars SQL Code will then be written to the Session's SQL editor.]
         JOptionPane.showMessageDialog(Main.getApplication().getMainFrame(), s_stringMgr.getString("sqlbookmark.noSQLPanel"));
         return;
      }

      Bookmark bm = (Bookmark) selNode.getUserObject();

      for (int i = 0; i < apis.length; i++)
      {
         ISQLPanelAPI api = apis[i];
         new RunBookmarkCommand(Main.getApplication().getMainFrame(), api.getSession(), bm, _plugin ,api.getSQLEntryPanel()).execute();
      }

   }



   public void onAdd()
   {
      BookmarkEditController ctrl = new BookmarkEditController(Main.getApplication().getMainFrame(), null, true, _nodeUserMarks);

      if (ctrl.isCanceled())
      {
         return;
      }

     _bookmarksUserFolderHandler.addBookMarkNode(ctrl.getBookmark());
   }

   /**
    * Internal action class for the main preferences tab. Called when
    * user clicks the "Edit" button.
    *
    * @author Joseph Mocker
    */
   public void onEdit()
   {
      DefaultMutableTreeNode selNode = null;
      if(null != _pnlPrefs.treBookmarks.getSelectionPath())
      {
         selNode = (DefaultMutableTreeNode) _pnlPrefs.treBookmarks.getSelectionPath().getLastPathComponent();
      }

      Runnable showErrMsg = () -> Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("sqlbookmark.noEditSelection"));


      if(null == selNode)
      {
         showErrMsg.run();
         return;
      }

      if (selNode.getUserObject() instanceof Bookmark bookmark)
      {
         boolean editable = BookmarkTreeUtil.isMyPredecessor(selNode, _nodeUserMarks);

         BookmarkEditController ctrl = new BookmarkEditController(Main.getApplication().getMainFrame(), bookmark, editable, _nodeUserMarks);

         if(ctrl.isCanceled())
         {
            return;
         }

         selNode.setUserObject(ctrl.getBookmark());
         ((DefaultTreeModel)_pnlPrefs.treBookmarks.getModel()).nodeChanged(selNode);

      }
      else if (selNode.getUserObject() instanceof UserBookmarkFolder)
      {
         _bookmarksUserFolderHandler.editFolder(selNode);
      }
      else
      {
         showErrMsg.run();
      }
   }


   public void onDelete()
   {

      DefaultMutableTreeNode selNode = null;
      if(null != _pnlPrefs.treBookmarks.getSelectionPath())
      {
         selNode = (DefaultMutableTreeNode) _pnlPrefs.treBookmarks.getSelectionPath().getLastPathComponent();
      }

      if (null == selNode || false == selNode.getUserObject() instanceof Bookmark)
      {
         // i18n[sqlbookmark.noDeleteSelection=Please select the bookmark to delete]
         Main.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("sqlbookmark.noDeleteSelection"));
         return;
      }


      // i18n[sqlbookmark.deleteConfirm=Do you really wish to delete the selected bookmark?]
      int ret = JOptionPane.showConfirmDialog(Main.getApplication().getMainFrame(), s_stringMgr.getString("sqlbookmark.deleteConfirm"));
      if(JOptionPane.YES_OPTION != ret)
      {
         return;
      }


      DefaultMutableTreeNode nextSel = selNode.getNextSibling();
      if(null == nextSel)
      {
         nextSel = selNode.getPreviousSibling();
      }

      _nodeUserMarks.remove(selNode);

      ((DefaultTreeModel)_pnlPrefs.treBookmarks.getModel()).nodeStructureChanged(_nodeUserMarks);

      if (null != nextSel)
      {
         BookmarkTreeUtil.selectNode(_pnlPrefs.treBookmarks, nextSel);
      }

   }

   private void onUp()
   {
      final TreePath[] selPaths = _pnlPrefs.treBookmarks.getSelectionPaths();

      if(null == selPaths || 0 == selPaths.length)
      {
         return;
      }


      final boolean pathsContainTheFirstNode = Arrays.stream(selPaths).anyMatch(p -> null == ((DefaultMutableTreeNode) p.getLastPathComponent()).getPreviousSibling());

      if(pathsContainTheFirstNode)
      {
         return;
      }

      // Note: Up / Down buttons are enabled only when leaves of _nodeUserMarks are selected

      Arrays.sort(selPaths, Comparator.comparingInt(path -> _nodeUserMarks.getIndex((DefaultMutableTreeNode) path.getLastPathComponent())));

      for (TreePath selPath : selPaths)
      {
         DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
         int selIx = _nodeUserMarks.getIndex(selNode);
         _nodeUserMarks.insert(selNode, selIx - 1);
      }
      ((DefaultTreeModel)_pnlPrefs.treBookmarks.getModel()).nodeStructureChanged(_nodeUserMarks);
      _pnlPrefs.treBookmarks.setSelectionPaths(selPaths);

   }


   private void onDown()
   {
      final TreePath[] selPaths = _pnlPrefs.treBookmarks.getSelectionPaths();

      if(null == selPaths || 0 == selPaths.length)
      {
         return;
      }


      final boolean pathsContainTheLastNode = Arrays.stream(selPaths).anyMatch(p -> null == ((DefaultMutableTreeNode) p.getLastPathComponent()).getNextSibling());

      if(pathsContainTheLastNode)
      {
         return;
      }

      // Note: Up / Down buttons are enabled only when without exception kids of _nodeUserMarks are selected

      Arrays.sort(selPaths, Comparator.comparingInt(o -> _nodeUserMarks.getIndex((DefaultMutableTreeNode) o.getLastPathComponent())));

      ArrayUtils.reverse(selPaths);
      for (TreePath selPath : selPaths)
      {
         DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
         int selIx = _nodeUserMarks.getIndex(selNode);
         _nodeUserMarks.insert(selNode, selIx + 1);
      }
      ((DefaultTreeModel)_pnlPrefs.treBookmarks.getModel()).nodeStructureChanged(_nodeUserMarks);
      _pnlPrefs.treBookmarks.setSelectionPaths(selPaths);
   }

}
