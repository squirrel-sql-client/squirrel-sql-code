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
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import net.sourceforge.squirrel_sql.client.gui.mainframe.IHasJDesktopPane;
import net.sourceforge.squirrel_sql.fw.gui.IToggleAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.SystemProperties;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IDesktopContainer;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DesktopStyle;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.action.CloseAllSQLResultTabsButCurrentAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.*;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.action.*;
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

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(MainFrameMenuBar.class);

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
		JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.SESSION);
		addToMenu(rsrc, SessionPropertiesAction.class, menu);
		addToMenu(rsrc, DumpSessionAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, ToolsPopupAction.class, menu);
		addToMenu(rsrc, RefreshSchemaInfoAction.class, menu);
		addToMenu(rsrc, ExecuteSqlAction.class, menu);
      menu.add(createTransactionMenu(rsrc));
      addToMenu(rsrc, SQLFilterAction.class, menu);
      menu.addSeparator();
      addToMenu(rsrc, ViewObjectAtCursorInObjectTreeAction.class, menu);
		menu.addSeparator();
      menu.add(createFileMenu(rsrc));
      menu.addSeparator();
		addToMenu(rsrc, GotoPreviousResultsTabAction.class, menu);
		addToMenu(rsrc, GotoNextResultsTabAction.class, menu);
		addToMenu(rsrc, ToggleCurrentSQLResultTabStickyAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, ShowNativeSQLAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, ReconnectAction.class, menu);
		addToMenu(rsrc, CloseSessionWindowAction.class, menu);
		addToMenu(rsrc, CloseSessionAction.class, menu);
	    addToMenu(rsrc, RenameSessionAction.class, menu);		
		menu.add(createSQLResultsCloseMenu(rsrc));
		menu.addSeparator();
		addToMenu(rsrc, PreviousSessionAction.class, menu);
		addToMenu(rsrc, NextSessionAction.class, menu);
		menu.addSeparator();
		addToMenu(rsrc, PreviousSqlAction.class, menu);
		addToMenu(rsrc, NextSqlAction.class, menu);
		addToMenu(rsrc, SelectSqlAction.class, menu);
		menu.addSeparator();
      addToMenu(rsrc, FormatSQLAction.class, menu);
      menu.addSeparator();
		addToMenu(rsrc, EditWhereColsAction.class, menu);
		menu.addSeparator();
      addToMenu(rsrc, NewSQLWorksheetAction.class, menu);
      addToMenu(rsrc, NewObjectTreeAction.class, menu);
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
      addToMenu(rsrc, SortAliasesAction.class, menu);
      menu.addSeparator();
      addToMenuAsCheckBoxMenuItem(rsrc, ToggleTreeViewAction.class, menu);
      addToMenu(rsrc, NewAliasFolderAction.class, menu);
      addToMenu(rsrc, CopyToPasteAliasFolderAction.class, menu);
      addToMenu(rsrc, CutAliasFolderAction.class, menu);
      addToMenu(rsrc, PasteAliasFolderAction.class, menu);
      addToMenu(rsrc, CollapseAllAliasFolderAction.class, menu);
      addToMenu(rsrc, ExpandAllAliasFolderAction.class, menu);
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
		addToMenu(rsrc, UpdateAction.class, menu);
		if (!_osxPluginLoaded)
		{
			addToMenu(rsrc, AboutAction.class, menu);
		}

		return menu;
	}

	private JMenu createSQLResultsCloseMenu(Resources rsrc)
	{
		JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.CLOSE_ALL_SQL_RESULTS);
		addToMenu(rsrc, CloseAllSQLResultTabsAction.class, menu);
		addToMenu(rsrc, CloseCurrentSQLResultTabAction.class, menu);
		addToMenu(rsrc, CloseAllSQLResultTabsButCurrentAction.class, menu);
		addToMenu(rsrc, CloseAllSQLResultWindowsAction.class, menu);
		return menu;
	}

   private Component createFileMenu(Resources rsrc)
   {
      JMenu menu = rsrc.createMenu(SquirrelResources.IMenuResourceKeys.FILE);
      addToMenu(rsrc, FileSaveAction.class, menu);
      addToMenu(rsrc, FileSaveAsAction.class, menu);
      addToMenu(rsrc, FileOpenAction.class, menu);
      addToMenu(rsrc, FileNewAction.class, menu);
      addToMenu(rsrc, FileAppendAction.class, menu);
      addToMenu(rsrc, FilePrintAction.class, menu);
      addToMenu(rsrc, FileCloseAction.class, menu);
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
			rsrc.addToMenu(act, menu);
		}
		else
		{
			s_log.error("Could not retrieve instance of "
							+ actionClass.getName()
							+ ") in MainFrameMenuBar.addToMenu");
		}

		return act;
	}

    @SuppressWarnings("unchecked")
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
		s_log.error("Could not retrieve instance of " + actionClass.getName()
							+ ") in MainFrameMenuBar.addToMenu");
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
