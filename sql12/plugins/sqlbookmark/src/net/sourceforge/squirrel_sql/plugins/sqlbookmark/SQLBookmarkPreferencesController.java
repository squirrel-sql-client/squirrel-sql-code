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

      _pnlPrefs.lstBookmarks.setModel(new DefaultListModel());
      _pnlPrefs.lstBookmarks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      for (Iterator i = _plugin.getBookmarkManager().iterator(); i.hasNext();)
      {
         Bookmark mark = (Bookmark) i.next();
         DefaultListModel model = (DefaultListModel) _pnlPrefs.lstBookmarks.getModel();
         model.addElement(mark);
      }

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
//	    new BookmarkManager(plugin.userSettingsFolder);

      bookmarks.removeAll();

      DefaultListModel model = (DefaultListModel) _pnlPrefs.lstBookmarks.getModel();
      for (int i = 0; i < model.getSize(); ++i)
      {
         bookmarks.add((Bookmark) model.getElementAt(i));
      }

      // set it as the new bookmark manager.
      //plugin.setBookmarkManager(bookmarks);

      // rebuild the bookmark menu.
      _plugin.rebuildMenu();
      bookmarks.save();
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

   private void onRun()
   {
      int selIx = _pnlPrefs.lstBookmarks.getSelectedIndex();
      if (selIx < 0)
      {
         // i18n[sqlbookmark.noRunSelection=Please select the bookmark to run]
         _app.getMessageHandler().showErrorMessage(s_stringMgr.getString("sqlbookmark.noRunSelection"));
         return;
      }



      ISQLPanelAPI[] apis = _plugin.getSQLPanelAPIsListeningForBookmarks();

      if(0 == apis.length)
      {
         // i18n[sqlbookmark.noSQLPanel=A bookmark can only be run when the bookmarks editor has been opened with the "Edit Bookmarks" toolbar button!]
         JOptionPane.showMessageDialog(_app.getMainFrame(), s_stringMgr.getString("sqlbookmark.noSQLPanel"));
         return;
      }

      Bookmark bm = (Bookmark) _pnlPrefs.lstBookmarks.getModel().getElementAt(selIx);

      for (int i = 0; i < apis.length; i++)
      {
         ISQLPanelAPI api = apis[i];
         new RunBookmarkCommand(_app.getMainFrame(), api.getSession(), bm, _plugin ,api.getSQLEntryPanel()).execute();
      }

   }



   public void onAdd()
   {
      BookmarEditController ctrlr = new BookmarEditController(_app.getMainFrame(), null);

      if (ctrlr.isCancelled())
      {
         return;
      }

      DefaultListModel model = (DefaultListModel) _pnlPrefs.lstBookmarks.getModel();
      model.addElement(ctrlr.getBookmark());
      _pnlPrefs.lstBookmarks.setSelectedIndex(model.size() - 1);
   }

   /**
    * Internal action class for the main preferences tab. Called when
    * user clicks the "Edit" button.
    *
    * @author Joseph Mocker
    */
   public void onEdit()
   {
      int selIx = _pnlPrefs.lstBookmarks.getSelectedIndex();
      if (selIx < 0)
      {
         // i18n[sqlbookmark.noEditSelection=Please select the bookmark to edit]
         _app.getMessageHandler().showErrorMessage(s_stringMgr.getString("sqlbookmark.noEditSelection"));
         return;
      }


      DefaultListModel model = (DefaultListModel) _pnlPrefs.lstBookmarks.getModel();
      BookmarEditController ctrlr = new BookmarEditController(_app.getMainFrame(), (Bookmark) model.get(selIx));

      if(ctrlr.isCancelled())
      {
         return;
      }

      model.setElementAt(ctrlr.getBookmark(), selIx);
   }


   public void onDelete()
   {
      int selIx = _pnlPrefs.lstBookmarks.getSelectedIndex();
      if (selIx < 0)
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

      DefaultListModel model = (DefaultListModel) _pnlPrefs.lstBookmarks.getModel();

      model.removeElementAt(selIx);

      if (selIx < _pnlPrefs.lstBookmarks.getModel().getSize())
      {
         _pnlPrefs.lstBookmarks.setSelectedIndex(selIx);
      }
      else if (0 < _pnlPrefs.lstBookmarks.getModel().getSize() && selIx - 1 < _pnlPrefs.lstBookmarks.getModel().getSize())
      {
         _pnlPrefs.lstBookmarks.setSelectedIndex(selIx - 1);
      }
   }

   private void onUp()
   {

      int selIx = _pnlPrefs.lstBookmarks.getSelectedIndex();
      if (selIx < 1)
      {
         return;
      }

      DefaultListModel model = (DefaultListModel) _pnlPrefs.lstBookmarks.getModel();

      Bookmark mark1 = (Bookmark) model.get(selIx - 1);
      Bookmark mark2 = (Bookmark) model.get(selIx);

      model.setElementAt(mark2, selIx - 1);
      model.setElementAt(mark1, selIx);

      _pnlPrefs.lstBookmarks.setSelectedIndex(selIx - 1);

   }


   private void onDown()
   {
      DefaultListModel model = (DefaultListModel) _pnlPrefs.lstBookmarks.getModel();

      int selIx  = _pnlPrefs.lstBookmarks.getSelectedIndex();
      if (selIx > (model.size() - 2))
      {
         return;
      }

      Bookmark mark1 = (Bookmark) model.get(selIx + 1);
      Bookmark mark2 = (Bookmark) model.get(selIx);

      model.setElementAt(mark2, selIx + 1);
      model.setElementAt(mark1, selIx);

      _pnlPrefs.lstBookmarks.setSelectedIndex(selIx + 1);


   }

}
