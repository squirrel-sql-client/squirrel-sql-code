package net.sourceforge.squirrel_sql.client.gui.mainframe;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.action.ChanneledAction;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IDesktopContainer;
import net.sourceforge.squirrel_sql.client.mainframe.action.*;
import net.sourceforge.squirrel_sql.client.mainframe.action.findaliases.FindAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.findaliases.FindAliasAltAcceleratorAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.findprefs.FindInPreferencesAction;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.action.*;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.action.ImportTableDataAction;
import net.sourceforge.squirrel_sql.client.session.action.dbdiff.actions.CompareToClipboardAction;
import net.sourceforge.squirrel_sql.client.session.action.file.*;
import net.sourceforge.squirrel_sql.client.session.action.multicaret.CaretAddAction;
import net.sourceforge.squirrel_sql.client.session.action.multicaret.CaretRemoveAction;
import net.sourceforge.squirrel_sql.client.session.action.reconnect.ReconnectAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.GitCommitSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionManageAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionOpenAction;
import net.sourceforge.squirrel_sql.client.session.action.savedsession.SessionSaveAction;
import net.sourceforge.squirrel_sql.client.session.action.sqlscript.SQLScriptMenuFactory;
import net.sourceforge.squirrel_sql.client.session.action.worksheettypechoice.NewSQLWorksheetAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.*;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
/**
 * Menu bar for <CODE>MainFrame</CODE>.
 */
final class MainFrameMenuBar extends JMenuBar
{

   public interface IMenuIDs
	{
		int PLUGINS_MENU = 1;
		int SESSION_MENU = 2;
	}

	private static final ILogger s_log = LoggerController.createLogger(MainFrameMenuBar.class);

	private final IApplication _app;
//	private final JMenu _editMenu;
	private final JMenu _pluginsMenu;
	private final JMenu _sessionMenu;
	private final JMenu _windowsMenu;

   private JMenu _aliasesMenu;
   private JMenu _driversMenu;
   
   private final ActionCollection _actions;

	private JCheckBoxMenuItem _showLoadedDriversOnlyItem;

	/** Listener to changes to application properties. */
	private SquirrelPropertiesListener _propsLis;


	MainFrameMenuBar(IApplication app, IDesktopContainer desktopContainer, ActionCollection actions)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}

		if (desktopContainer == null)
		{
			throw new IllegalArgumentException("Null JDesktopPane passed");
		}

		if (actions == null)
		{
			throw new IllegalArgumentException("Null ActionCollection passed");
		}

		Resources rsrc = app.getResources();

		if (rsrc == null)
		{
			throw new IllegalStateException("No Resources object in IApplication");
		}

		_actions = actions;

		_app = app;

		add(createApplicationFileMenu(rsrc));
      add(_driversMenu = createDriversMenu(rsrc));
      add(_aliasesMenu = createAliasesMenu(rsrc));
		add(_pluginsMenu = createPluginsMenu(rsrc));
		add(_sessionMenu = createSessionMenu(rsrc));
		add(_windowsMenu = createWindowsMenu(rsrc, desktopContainer));
		add(createHelpMenu(rsrc));
	}

	/**
	 * Component has been added to its parent so setup required listeners.
	 */
	public void addNotify()
	{
		super.addNotify();
		propertiesChanged(null);
		if (_propsLis == null)
		{
			_propsLis = new SquirrelPropertiesListener();
			_app.getSquirrelPreferences().addPropertyChangeListener(_propsLis);
		}
	}

	/**
	 * Component has been removed from its parent so remove required listeners.
	 */
	public void removeNotify()
	{
		super.removeNotify();
		if (_propsLis != null)
		{
			_app.getSquirrelPreferences().removePropertyChangeListener(_propsLis);
			_propsLis = null;
		}
	}

	JMenu getWindowsMenu()
	{
		return _windowsMenu;
	}

	JMenu getSessionMenu()
	{
		return _sessionMenu;
	}

	void addToMenu(int menuId, JMenu menu)
	{
		if (menu == null)
		{
			throw new IllegalArgumentException("Null JMenu passed");
		}

		switch (menuId)
		{
			case IMenuIDs.PLUGINS_MENU :
			{
				_pluginsMenu.add(menu);
				break;
			}

			case IMenuIDs.SESSION_MENU :
			{
				_sessionMenu.add(menu);
				break;
			}

			default:
			{
				throw new IllegalArgumentException("Invalid menuId passed: " + menuId);
			}
		}
	}

	void addToMenu(int menuId, Action action)
	{
		if (action == null)
		{
			throw new IllegalArgumentException("Null Action passed");
		}

		switch (menuId)
		{
			case IMenuIDs.PLUGINS_MENU :
			{
				_pluginsMenu.add(action);
				break;
			}

			case IMenuIDs.SESSION_MENU :
			{
				_sessionMenu.add(action);
				break;
			}

			default :
			{
				throw new IllegalArgumentException("Invalid menuId passed: " + menuId);
			}
		}
	}

	/**
	 * Add a component to the end of the menu.
	 *
	 * @param	menuId	Defines the menu to add the component to. @see IMenuIDs
	 * @param	comp	Component to add to menu.
	 *
	 * @throws	IllegalArgumentException if null <TT>Component</TT> passed or
	 * 			an invalid <TT>menuId</TT> passed.
	 */
	void addToMenu(int menuId, Component comp)
	{
		if (comp == null)
		{
			throw new IllegalArgumentException("Component == null");
		}

		switch (menuId)
		{
			case IMenuIDs.PLUGINS_MENU :
			{
				_pluginsMenu.add(comp);
				break;
			}

			case IMenuIDs.SESSION_MENU :
			{
				_sessionMenu.add(comp);
				break;
			}

			default :
			{
				throw new IllegalArgumentException("Invalid menuId passed: " + menuId);
			}

		}
	}

	private JMenu createApplicationFileMenu(Resources rsrc)
	{
		JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.APP_FILE_MENU);
		addToMenu(rsrc, GlobalPreferencesAction.class, menu);
		addToMenu(rsrc, NewSessionPropertiesAction.class, menu);
		addToMenu(rsrc, FindInPreferencesAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, AliasPopUpMenuAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, SessionOpenAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, DumpApplicationAction.class, menu);
      addToMenu(rsrc, SavePreferencesAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, ExitAction.class, menu);

		return menu;
	}

   public void setEnabledAliasesMenu(boolean b)
   {
      _aliasesMenu.setEnabled(b);
   }

   public void setEnabledDriversMenu(boolean b)
   {
      _driversMenu.setEnabled(b);
   }



   private JMenu createSessionMenu(Resources rsrc)
	{
		JMenu menu = rsrc.createScrollMenu(SquirrelResources.IMenuResourceKeys.SESSION);
		addToMenu(rsrc, SessionPropertiesAction.class, menu);
		addToMenu(rsrc, DumpSessionAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, ToolsPopupAction.class, menu);
		addToMenu(rsrc, RefreshSchemaInfoAction.class, menu);
		addToMenu(rsrc, FindColumnsAction.class, menu);
		addToMenu(rsrc, ExecuteSqlAction.class, menu);
		addToMenu(rsrc, ExecuteAllSqlsAction.class, menu);
      menu.add(createTransactionMenu(rsrc));
      addToMenu(rsrc, SQLFilterAction.class, menu);
      menu.addSeparator();
      addToMenu(rsrc, ViewObjectAtCursorInObjectTreeAction.class, menu);
		menu.addSeparator();
      menu.add(createSessionWindowFileMenu(rsrc));
		addToMenu(rsrc, ChangeTrackAction.class, menu);
      menu.addSeparator();
		menu.add(createSavedSessionMenu(rsrc));
      menu.addSeparator();
		addToMenu(rsrc, ShowNativeSQLAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, ReconnectAction.class, menu);
		addToMenu(rsrc, CloseSessionWindowAction.class, menu);
		addToMenu(rsrc, CloseSessionAction.class, menu);
	   addToMenu(rsrc, RenameSessionAction.class, menu);
		addToMenu(rsrc, GoToAliasSessionAction.class, menu);
		menu.add(createSQLResultTabMenu(rsrc));
		menu.addSeparator();
		addToMenu(rsrc, PreviousSessionAction.class, menu);
		addToMenu(rsrc, NextSessionAction.class, menu);
		addToMenu(rsrc, SessionPopUpMenuAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, PreviousSqlAction.class, menu);
		addToMenu(rsrc, NextSqlAction.class, menu);
		addToMenu(rsrc, SelectSqlAction.class, menu);
		addToMenu(rsrc, GoToLastEditLocationAction.class, menu);
		menu.addSeparator();
      addToMenu(rsrc, CompareToClipboardAction.class, menu);
      addToMenu(rsrc, FormatSQLAction.class, menu);

		addToMenu(rsrc, InQuotesAction.class, menu);
		addToMenu(rsrc, RemoveQuotesAction.class, menu);
		addToMenu(rsrc, ConvertToStringBuilderAction.class, menu);
		addToMenu(rsrc, EscapeDateAction.class, menu);
		addToMenu(rsrc, CutSqlAction.class, menu);
		addToMenu(rsrc, CopySqlAction.class, menu);
		addToMenu(rsrc, DeleteSqlAction.class, menu);
		addToMenu(rsrc, DeleteCurrentLineAction.class, menu);
		addToMenu(rsrc, RemoveNewLinesAction.class, menu);

		menu.addSeparator();
		addToMenu(rsrc, UndoAction.class, menu);
		addToMenu(rsrc, RedoAction.class, menu);

		menu.addSeparator();
		addToMenu(rsrc, CaretAddAction.class, menu);
		addToMenu(rsrc, CaretRemoveAction.class, menu);


		menu.addSeparator();
		addToMenu(rsrc, EditWhereColsAction.class, menu);
		menu.addSeparator();
      addToMenu(rsrc, NewSQLWorksheetAction.class, menu);
      addToMenu(rsrc, NewObjectTreeAction.class, menu);
      addToMenu(rsrc, NewAliasConnectionAction.class, menu);
      menu.addSeparator();
		addToMenu(rsrc, PasteFromHistoryAction.class, menu);
		addToMenu(rsrc, PasteFromHistoryAltAcceleratorAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, ToggleMinimizeResultsAction.class, menu);
		menu.addSeparator();
		addToMenuAsCheckBoxMenuItem(rsrc, ToggleObjectTreeBesidesEditorAction.class, menu);
		menu.addSeparator();
		menu.add(SQLScriptMenuFactory.getSessionMenu());
		menu.addSeparator();
		addToMenu(rsrc, ImportTableDataAction.class, menu);
		menu.addSeparator();

		menu.setEnabled(false);
		return menu;
	}


	private JMenu createPluginsMenu(Resources rsrc)
   {
      JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.PLUGINS);
      addToMenu(rsrc, DisplayPluginSummaryAction.class, menu);
      menu.addSeparator();
      return menu;
   }

	private JMenu createAliasesMenu(Resources rsrc)
	{
		JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.ALIASES);
		addToMenu(rsrc, ConnectToAliasAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, CreateAliasAction.class, menu);
		addToMenu(rsrc, ModifyAliasAction.class, menu);
		addToMenu(rsrc, DeleteAliasAction.class, menu);
		addToMenu(rsrc, CopyAliasAction.class, menu);
      menu.addSeparator();
		addToMenu(rsrc, AliasPropertiesAction.class, menu);
      menu.addSeparator();
		addToMenu(rsrc, AliasFileOpenAction.class, menu);
      menu.addSeparator();
      addToMenu(rsrc, FindAliasAction.class, menu);
      addToMenu(rsrc, FindAliasAltAcceleratorAction.class, menu);
		menu.addSeparator();
      addToMenu(rsrc, SortAliasesAction.class, menu);
      addToMenu(rsrc, ColorAliasAction.class, menu);
      menu.addSeparator();
      addToMenuAsCheckBoxMenuItem(rsrc, ToggleTreeViewAction.class, menu);
      addToMenu(rsrc, NewAliasFolderAction.class, menu);
      addToMenu(rsrc, CopyToPasteAliasFolderAction.class, menu);
      addToMenu(rsrc, CutAliasFolderAction.class, menu);
      addToMenu(rsrc, PasteAliasFolderAction.class, menu);
      addToMenu(rsrc, CollapseAllAliasFolderAction.class, menu);
      addToMenu(rsrc, ExpandAllAliasFolderAction.class, menu);
      addToMenu(rsrc, CollapseSelectedAliasFolderAction.class, menu);
      addToMenu(rsrc, ExpandSelectedAliasFolderAction.class, menu);
		menu.addSeparator();
      addToMenu(rsrc, TransferAliasAction.class, menu);
		menu.addSeparator();
      addToMenu(rsrc, ModifyMultipleAliasesAction.class, menu);
		return menu;
	}

	private JMenu createDriversMenu(Resources rsrc)
	{
		JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.DRIVERS);
		addToMenu(rsrc, CreateDriverAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, ModifyDriverAction.class, menu);
		addToMenu(rsrc, DeleteDriverAction.class, menu);
		addToMenu(rsrc, CopyDriverAction.class, menu);
      addToMenu(rsrc, ShowDriverWebsiteAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, InstallDefaultDriversAction.class, menu);
		menu.addSeparator();
		_showLoadedDriversOnlyItem = addToMenuAsCheckBoxMenuItem(rsrc, ShowLoadedDriversOnlyAction.class, menu);
		return menu;
	}

	private JMenu createWindowsMenu(Resources rsrc, IDesktopContainer desktopPane)
	{
		JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.WINDOWS);
		addToMenu(rsrc, ViewAliasesAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, FindAliasAction.class, menu);
		addToMenu(rsrc, FindAliasAltAcceleratorAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, SessionOpenAction.class, menu);
		addToMenu(rsrc, ViewDriversAction.class, menu);
		addToMenu(rsrc, ViewLogsAction.class, menu);
      if (_app.getDesktopStyle().isInternalFrameStyle())
      {
         menu.addSeparator();
         addDesktopPaneActionToMenu(rsrc, TileAction.class, menu, desktopPane);
         addDesktopPaneActionToMenu(rsrc, TileHorizontalAction.class, menu, desktopPane);
         addDesktopPaneActionToMenu(rsrc, TileVerticalAction.class, menu, desktopPane);
         addDesktopPaneActionToMenu(rsrc, CascadeAction.class, menu, desktopPane);
         addDesktopPaneActionToMenu(rsrc, MaximizeAction.class, menu, desktopPane);
         menu.addSeparator();
      }
      addToMenu(rsrc, CloseAllSessionsAction.class, menu);
      addToMenu(rsrc, CloseAllButCurrentSessionsAction.class, menu);
		menu.addSeparator();
		return menu;
	}

	private JMenu createHelpMenu(Resources rsrc)
	{
		JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.HELP);
		addToMenu(rsrc, ViewHelpAction.class, menu);

		menu.addSeparator();
		addToMenu(rsrc, AboutAction.class, menu);

		return menu;
	}

	private JMenu createSQLResultTabMenu(Resources rsrc)
	{
		JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.RESULTS_TABS);

		addToMenu(rsrc, GotoPreviousResultsTabAction.class, menu);
		addToMenu(rsrc, GotoNextResultsTabAction.class, menu);
		addToMenu(rsrc, ToggleCurrentSQLResultTabStickyAction.class, menu);
		addToMenu(rsrc, ToggleCurrentSQLResultTabAnchoredAction.class, menu);

		menu.addSeparator();
		addToMenu(rsrc, CreateResultTabFrameAction.class, menu);
		addToMenu(rsrc, FindInResultAction.class, menu);
		addToMenu(rsrc, FindResultColumnAction.class, menu);
		addToMenu(rsrc, MarkDuplicatesToggleAction.class, menu);
		addToMenu(rsrc, RerunCurrentSQLResultTabAction.class, menu);

		menu.addSeparator();
		addToMenu(rsrc, CloseAllSQLResultTabsAction.class, menu);
		addToMenu(rsrc, CloseCurrentSQLResultTabAction.class, menu);
		addToMenu(rsrc, CloseAllSQLResultTabsButCurrentAction.class, menu);
		addToMenu(rsrc, CloseAllSQLResultTabsToLeftAction.class, menu);
		addToMenu(rsrc, CloseAllSQLResultTabsToRightAction.class, menu);
		addToMenu(rsrc, CloseAllSQLResultWindowsAction.class, menu);

		return menu;
	}

   private JMenu createSessionWindowFileMenu(Resources rsrc)
   {
      JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.FILE);
      addToMenu(rsrc, FileNewAction.class, menu);
      addToMenu(rsrc, FileDetachAction.class, menu);
      addToMenu(rsrc, FileOpenAction.class, menu);
      addToMenu(rsrc, FileOpenRecentAction.class, menu);
      addToMenu(rsrc, FileSaveAction.class, menu);
      addToMenu(rsrc, FileSaveAsAction.class, menu);
      addToMenu(rsrc, FileSaveAllAction.class, menu);
      addToMenu(rsrc, FileCloseAction.class, menu);
      addToMenu(rsrc, FileAppendAction.class, menu);
      addToMenu(rsrc, FilePrintAction.class, menu);
      addToMenu(rsrc, FileReloadAction.class, menu);
      return menu;
   }

	private JMenu createSavedSessionMenu(Resources rsrc)
	{
		JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.SAVED_SESSION);
		addToMenu(rsrc, SessionSaveAction.class, menu);
		addToMenu(rsrc, GitCommitSessionAction.class, menu);
		addToMenu(rsrc, SessionOpenAction.class, menu);
		addToMenu(rsrc, SessionManageAction.class, menu);
		return menu;
	}


	private Component createTransactionMenu(Resources rsrc)
   {
      JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.TRANSACTION);
      addToMenuAsCheckBoxMenuItem(rsrc, ToggleAutoCommitAction.class, menu);
      addToMenu(rsrc, CommitAction.class, menu);
      addToMenu(rsrc, RollbackAction.class, menu);
      return menu;
   }


   @SuppressWarnings("unchecked")
   private Action addDesktopPaneActionToMenu(Resources rsrc, Class actionClass, JMenu menu, IDesktopContainer desktopContainer)
	{
		Action act = addToMenu(rsrc, actionClass, menu);
		if (act != null)
		{
			if (act instanceof IHasJDesktopPane)
			{
				((IHasJDesktopPane)act).setDesktopContainer(desktopContainer);
			}
			else
			{
				s_log.error("Tryimg to add non IHasJDesktopPane (" + actionClass.getName()+ ") in MainFrameMenuBar.addDesktopPaneActionToMenu");
			}
		}
		return act;
	}
   
    @SuppressWarnings("unchecked")
	private Action addToMenu(Resources rsrc, Class actionClass, JMenu menu)
	{
		Action act = _actions.get(actionClass);
		if (act != null)
		{
			JMenuItem menuItem = rsrc.addToMenu(act, menu);

			if(act instanceof ChanneledAction)
			{
				((ChanneledAction)act).getActionChannel().addBoundMenuItem(menuItem);
			}

		}
		else
		{
			s_log.error("Could not retrieve instance of "
							+ actionClass.getName()
							+ ") in MainFrameMenuBar.addToMenu");
		}

		return act;
	}

	private JCheckBoxMenuItem addToMenuAsCheckBoxMenuItem(Resources rsrc, Class actionClass, JMenu menu)
	{
		Action act = _actions.get(actionClass);
		if (act != null)
		{
			JCheckBoxMenuItem mnu = rsrc.addToMenuAsCheckBoxMenuItem(act, menu);
         if(act instanceof IToggleAction)
         {
            ((IToggleAction)act).getToggleComponentHolder().addToggleableComponent(mnu);
         }
         return mnu;
		}
		s_log.error("Could not retrieve instance of " + actionClass.getName() + ") in MainFrameMenuBar.addToMenu");
      return null;
	}

	/**
	 * Application properties have changed so update this object.
	 *
	 * @param	propName	Name of property that has changed or <TT>null</TT>
	 * 						if multiple properties have changed.
	 */
	private void propertiesChanged(String propName)
	{
		if (propName == null || propName.equals(SquirrelPreferences.IPropertyNames.SHOW_LOADED_DRIVERS_ONLY))
		{
			boolean show = _app.getSquirrelPreferences().getShowLoadedDriversOnly();
			_showLoadedDriversOnlyItem.setSelected(show);
		}
	}


	/**
	 * Listener for changes to Squirrel Properties.
	 */
	private class SquirrelPropertiesListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			final String propName = evt != null ? evt.getPropertyName() : null;
			propertiesChanged(propName);
		}
	}
}
