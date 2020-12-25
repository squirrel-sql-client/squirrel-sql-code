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
import net.sourceforge.squirrel_sql.client.mainframe.action.AboutAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasFileOpenAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.AliasPropertiesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CascadeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CloseAllButCurrentSessionsAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CloseAllSessionsAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CollapseAllAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CollapseSelectedAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ColorAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CopyAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CopyDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CopyToPasteAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CreateAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CreateDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CutAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DeleteAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DeleteDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DisplayPluginSummaryAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DumpApplicationAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ExitAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ExpandAllAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ExpandSelectedAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.GlobalPreferencesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.InstallDefaultDriversAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.MaximizeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.NewAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.NewSessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.PasteAliasFolderAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.SavePreferencesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ShowDriverWebsiteAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ShowLoadedDriversOnlyAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.SortAliasesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileHorizontalAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileVerticalAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ToggleTreeViewAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TransferAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewAliasesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewDriversAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewHelpAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewLogsAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.findaliases.FindAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.findaliases.FindAliasAltAcceleratorAction;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.action.ChangeTrackAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsButCurrentAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsToLeftAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsToRightAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultWindowsAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseCurrentSQLResultTabAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.CloseSessionWindowAction;
import net.sourceforge.squirrel_sql.client.session.action.CommitAction;
import net.sourceforge.squirrel_sql.client.session.action.ConvertToStringBuilderAction;
import net.sourceforge.squirrel_sql.client.session.action.CopySqlAction;
import net.sourceforge.squirrel_sql.client.session.action.CutSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.DumpSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.EditWhereColsAction;
import net.sourceforge.squirrel_sql.client.session.action.EscapeDateAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteAllSqlsAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.FormatSQLAction;
import net.sourceforge.squirrel_sql.client.session.action.GoToLastEditLocationAction;
import net.sourceforge.squirrel_sql.client.session.action.GotoNextResultsTabAction;
import net.sourceforge.squirrel_sql.client.session.action.GotoPreviousResultsTabAction;
import net.sourceforge.squirrel_sql.client.session.action.InQuotesAction;
import net.sourceforge.squirrel_sql.client.session.action.NewAliasConnectionAction;
import net.sourceforge.squirrel_sql.client.session.action.NewObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.NextSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.NextSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.PasteFromHistoryAction;
import net.sourceforge.squirrel_sql.client.session.action.PasteFromHistoryAltAcceleratorAction;
import net.sourceforge.squirrel_sql.client.session.action.PreviousSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.PreviousSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshSchemaInfoAction;
import net.sourceforge.squirrel_sql.client.session.action.RemoveNewLinesAction;
import net.sourceforge.squirrel_sql.client.session.action.RemoveQuotesAction;
import net.sourceforge.squirrel_sql.client.session.action.RenameSessionAction;
import net.sourceforge.squirrel_sql.client.session.action.RollbackAction;
import net.sourceforge.squirrel_sql.client.session.action.SQLFilterAction;
import net.sourceforge.squirrel_sql.client.session.action.SelectSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.SessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.session.action.ShowNativeSQLAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleAutoCommitAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleCurrentSQLResultTabAnchoredAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleCurrentSQLResultTabStickyAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleMinimizeResultsAction;
import net.sourceforge.squirrel_sql.client.session.action.ToggleObjectTreeBesidesEditorAction;
import net.sourceforge.squirrel_sql.client.session.action.ToolsPopupAction;
import net.sourceforge.squirrel_sql.client.session.action.ViewObjectAtCursorInObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileAppendAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileCloseAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileDetachAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileNewAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileOpenAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileOpenRecentAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FilePrintAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileReloadAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileSaveAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileSaveAllAction;
import net.sourceforge.squirrel_sql.client.session.action.file.FileSaveAsAction;
import net.sourceforge.squirrel_sql.client.session.action.reconnect.ReconnectAction;
import net.sourceforge.squirrel_sql.client.session.action.worksheettypechoice.NewSQLWorksheetAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.CreateResultTabFrameAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.FindColumnAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.FindInResultAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.MarkDuplicatesToggleAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabactions.RerunCurrentSQLResultTabAction;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.fw.util.SystemProperties;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
/**
 * Menu bar for <CODE>MainFrame</CODE>.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
@SuppressWarnings("serial")
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

	private final boolean _osxPluginLoaded;

	/**
	 * Ctor.
	 */
	MainFrameMenuBar(IApplication app, IDesktopContainer desktopContainer, ActionCollection actions)
	{
		super();
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
		_osxPluginLoaded = isOsxPluginLoaded();

		add(createOsxFileMenu(rsrc));
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

	private JMenu createOsxFileMenu(Resources rsrc)
	{
		JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.OSX_FILE);
		if (!_osxPluginLoaded)
		{
			addToMenu(rsrc, GlobalPreferencesAction.class, menu);
		}
		addToMenu(rsrc, NewSessionPropertiesAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, DumpApplicationAction.class, menu);
        addToMenu(rsrc, SavePreferencesAction.class, menu);
		menu.addSeparator();
		if (!_osxPluginLoaded)
		{
			addToMenu(rsrc, ExitAction.class, menu);
		}

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
		addToMenu(rsrc, ExecuteSqlAction.class, menu);
		addToMenu(rsrc, ExecuteAllSqlsAction.class, menu);
      menu.add(createTransactionMenu(rsrc));
      addToMenu(rsrc, SQLFilterAction.class, menu);
      menu.addSeparator();
      addToMenu(rsrc, ViewObjectAtCursorInObjectTreeAction.class, menu);
		menu.addSeparator();
      menu.add(createFileMenu(rsrc));
		addToMenu(rsrc, ChangeTrackAction.class, menu);
      menu.addSeparator();
		addToMenu(rsrc, ShowNativeSQLAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, ReconnectAction.class, menu);
		addToMenu(rsrc, CloseSessionWindowAction.class, menu);
		addToMenu(rsrc, CloseSessionAction.class, menu);
	   addToMenu(rsrc, RenameSessionAction.class, menu);
		menu.add(createSQLResultTabMenu(rsrc));
		menu.addSeparator();
		addToMenu(rsrc, PreviousSessionAction.class, menu);
		addToMenu(rsrc, NextSessionAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, PreviousSqlAction.class, menu);
		addToMenu(rsrc, NextSqlAction.class, menu);
		addToMenu(rsrc, SelectSqlAction.class, menu);
		addToMenu(rsrc, GoToLastEditLocationAction.class, menu);
		menu.addSeparator();
      addToMenu(rsrc, FormatSQLAction.class, menu);

		addToMenu(rsrc, InQuotesAction.class, menu);
		addToMenu(rsrc, RemoveQuotesAction.class, menu);
		addToMenu(rsrc, ConvertToStringBuilderAction.class, menu);
		addToMenu(rsrc, EscapeDateAction.class, menu);
		addToMenu(rsrc, CutSqlAction.class, menu);
		addToMenu(rsrc, CopySqlAction.class, menu);
		addToMenu(rsrc, RemoveNewLinesAction.class, menu);


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
		_showLoadedDriversOnlyItem = addToMenuAsCheckBoxMenuItem(rsrc,
									ShowLoadedDriversOnlyAction.class, menu);
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
		if (!_osxPluginLoaded)
		{
			addToMenu(rsrc, AboutAction.class, menu);
		}

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
		addToMenu(rsrc, FindColumnAction.class, menu);
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

   private Component createFileMenu(Resources rsrc)
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

   private Component createTransactionMenu(Resources rsrc)
   {
      JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.TRANSACTION);
      addToMenuAsCheckBoxMenuItem(rsrc, ToggleAutoCommitAction.class, menu);
      addToMenu(rsrc, CommitAction.class, menu);
      addToMenu(rsrc, RollbackAction.class, menu);
      return menu;
   }


   @SuppressWarnings("unchecked")
   private Action addDesktopPaneActionToMenu(Resources rsrc, Class actionClass,
											JMenu menu, IDesktopContainer desktopContainer)
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
				s_log.error("Tryimg to add non IHasJDesktopPane ("
						+ actionClass.getName()
						+ ") in MainFrameMenuBar.addDesktopPaneActionToMenu");
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
		if (propName == null
			|| propName.equals(SquirrelPreferences.IPropertyNames.SHOW_LOADED_DRIVERS_ONLY))
		{
			boolean show = _app.getSquirrelPreferences().getShowLoadedDriversOnly();
			_showLoadedDriversOnlyItem.setSelected(show);
		}
	}

	// TODO: This is a nasty quick hack. Needs an API to do this.
	private boolean isOsxPluginLoaded()
	{
		if (SystemProperties.isRunningOnOSX())
		{
			final IPluginManager mgr = _app.getPluginManager();
			PluginInfo[] ar = mgr.getPluginInformation();
			for (int i = 0; i < ar.length; ++i)
			{
				if (ar[i].getInternalName().equals("macosx"))
				{
					return ar[i].isLoaded();
				}
			}
		}
		return false;
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
