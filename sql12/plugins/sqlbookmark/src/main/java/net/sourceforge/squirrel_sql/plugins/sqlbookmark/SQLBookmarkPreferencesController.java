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

import java.util.Iterator;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.*;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

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

   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(SQLBookmarkPreferencesController.class);


   /**
    * The main panel for preference administration
    */
   protected SQLBookmarkPreferencesPanel _pnlPrefs;

   /**
    * Handle to the main application
    */
   protected IApplication _app;

   /**
    * Handle to the plugin
    */
   protected SQLBookmarkPlugin _plugin;
   private DefaultMutableTreeNode _nodeSquirrelMarks;
   private DefaultMutableTreeNode _nodeUserMarks;



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
      this._app = app;

      // i18n[sqlbookmark.btnTextEdit=Edit]
      _pnlPrefs.btnEdit.setText(s_stringMgr.getString("sqlbookmark.btnTextEdit"));

      DefaultMutableTreeNode root = new DefaultMutableTreeNode("");

      // i18n[sqlbookmark.nodeSquirrelMarks=SQuirreL bookmarks]
      _nodeSquirrelMarks = new DefaultMutableTreeNode(s_stringMgr.getString("sqlbookmark.nodeSquirrelMarks"));
      // i18n[sqlbookmark.nodeUserMarks=User bookmarks]
      _nodeUserMarks = new DefaultMutableTreeNode(s_stringMgr.getString("sqlbookmark.nodeUserMarks"));

      root.add(_nodeUserMarks);
      root.add(_nodeSquirrelMarks);
      DefaultTreeModel dtm = new DefaultTreeModel(root);
      _pnlPrefs.treBookmarks.setModel(dtm);
      _pnlPrefs.treBookmarks.setRootVisible(false);

      _pnlPrefs.treBookmarks.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

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

      _pnlPrefs.treBookmarks.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener()
      {
         public void valueChanged(TreeSelectionEvent e)
         {
            onTreeSelectionChanged(e);
         }
      });



      _pnlPrefs.btnRun.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRun();
         }
      });


      _pnlPrefs.btnAdd.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAdd();
         }
      });
      _pnlPrefs.btnEdit.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onEdit();
         }
      });
      _pnlPrefs.btnDel.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onDelete();
         }
      });

      _pnlPrefs.btnUp.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onUp();
         }
      });

      _pnlPrefs.btnDown.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onDown();
         }
      });


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

   private void onTreeSelectionChanged(TreeSelectionEvent e)
   {
      if(null == e.getPath())
      {
         return;
      }

      DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();

      if(false == dmtn.getUserObject() instanceof Bookmark)
      {
         return;
      }

      if(dmtn.getParent() == _nodeSquirrelMarks)
      {
         _pnlPrefs.btnUp.setEnabled(false);
         _pnlPrefs.btnDown.setEnabled(false);
         _pnlPrefs.btnDel.setEnabled(false);
         // i18n[sqlbookmark.btnTextView=View]
         _pnlPrefs.btnEdit.setText(s_stringMgr.getString("sqlbookmark.btnTextView"));
      }
      else
      {
         _pnlPrefs.btnUp.setEnabled(true);
         _pnlPrefs.btnDown.setEnabled(true);
         _pnlPrefs.btnDel.setEnabled(true);
         // i18n[sqlbookmark.btnTextEdit=Edit]
         _pnlPrefs.btnEdit.setText(s_stringMgr.getString("sqlbookmark.btnTextEdit"));
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
         _app.getMessageHandler().showErrorMessage(s_stringMgr.getString("sqlbookmark.noRunSelection"));
         return;
      }



      ISQLPanelAPI[] apis = _plugin.getSQLPanelAPIsListeningForBookmarks();

      if(0 == apis.length)
      {
         // i18n[sqlbookmark.noSQLPanel=To run a bookmark you must open this window\nusing the "Edit Bookmarks" toolbar button of a Session window.\nThe bookmars SQL Code will then be written to the Session's SQL editor.]
         JOptionPane.showMessageDialog(_app.getMainFrame(), s_stringMgr.getString("sqlbookmark.noSQLPanel"));
         return;
      }

      Bookmark bm = (Bookmark) selNode.getUserObject();

      for (int i = 0; i < apis.length; i++)
      {
         ISQLPanelAPI api = apis[i];
         new RunBookmarkCommand(_app.getMainFrame(), api.getSession(), bm, _plugin ,api.getSQLEntryPanel()).execute();
      }

   }



   public void onAdd()
   {
      BookmarEditController ctrlr = new BookmarEditController(_app.getMainFrame(), null, true);

      if (ctrlr.isCancelled())
      {
         return;
      }

      DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(ctrlr.getBookmark());
      _nodeUserMarks.add(newChild);

      ((DefaultTreeModel)_pnlPrefs.treBookmarks.getModel()).nodeStructureChanged(_nodeUserMarks);

      selectNode(newChild);
   }

   private void selectNode(DefaultMutableTreeNode toSel)
   {
      TreeNode[] pathToRoot = ((DefaultTreeModel) _pnlPrefs.treBookmarks.getModel()).getPathToRoot(toSel);
      _pnlPrefs.treBookmarks.setSelectionPath(new TreePath(pathToRoot));
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

      if (null == selNode || false == selNode.getUserObject() instanceof Bookmark)
      {
         // i18n[sqlbookmark.noEditSelection=Please select the bookmark to edit]
         _app.getMessageHandler().showErrorMessage(s_stringMgr.getString("sqlbookmark.noEditSelection"));
         return;
      }

      boolean editable = selNode.getParent() == _nodeUserMarks;

      BookmarEditController ctrlr = new BookmarEditController(_app.getMainFrame(), (Bookmark) selNode.getUserObject(), editable);

      if(ctrlr.isCancelled())
      {
         return;
      }

      selNode.setUserObject(ctrlr.getBookmark());
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
         _app.getMessageHandler().showErrorMessage(s_stringMgr.getString("sqlbookmark.noDeleteSelection"));
         return;
      }


      // i18n[sqlbookmark.deleteConfirm=Do you really wish to delete the selected bookmark?]
      int ret = JOptionPane.showConfirmDialog(_app.getMainFrame(), s_stringMgr.getString("sqlbookmark.deleteConfirm"));
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

      selectNode(nextSel);

   }

   private void onUp()
   {
      DefaultMutableTreeNode selNode = null;
      if(null != _pnlPrefs.treBookmarks.getSelectionPath())
      {
         selNode = (DefaultMutableTreeNode) _pnlPrefs.treBookmarks.getSelectionPath().getLastPathComponent();
      }

      if (  null == selNode
         || false == selNode.getUserObject() instanceof Bookmark
         || 0 == _nodeUserMarks.getIndex(selNode))
      {
         return;
      }

      int selIx = _nodeUserMarks.getIndex(selNode);

      _nodeUserMarks.insert(selNode, selIx - 1);

      ((DefaultTreeModel)_pnlPrefs.treBookmarks.getModel()).nodeStructureChanged(_nodeUserMarks);

      selectNode(selNode);

   }


   private void onDown()
   {
      DefaultMutableTreeNode selNode = null;
      if(null != _pnlPrefs.treBookmarks.getSelectionPath())
      {
         selNode = (DefaultMutableTreeNode) _pnlPrefs.treBookmarks.getSelectionPath().getLastPathComponent();
      }

      if (  null == selNode
         || false == selNode.getUserObject() instanceof Bookmark
         || _nodeUserMarks.getChildCount() - 1 == _nodeUserMarks.getIndex(selNode))
      {
         return;
      }

      int selIx = _nodeUserMarks.getIndex(selNode);
      _nodeUserMarks.insert(selNode, selIx + 1);

      ((DefaultTreeModel)_pnlPrefs.treBookmarks.getModel()).nodeStructureChanged(_nodeUserMarks);

      selectNode(selNode);

   }

}
