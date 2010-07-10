package net.sourceforge.squirrel_sql.client.mainframe;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import net.sourceforge.squirrel_sql.fw.gui.action.IHasJDesktopPane;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.mainframe.action.AboutAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CascadeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CopyAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CopyDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CreateAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.CreateDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DeleteAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DeleteDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.DisplayPluginSummaryAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ExitAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.GlobalPreferencesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.MaximizeAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyAliasAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ModifyDriverAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.TileAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewAliasesAction;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewDriversAction;
//import net.sourceforge.squirrel_sql.client.resources.Resources;
import net.sourceforge.squirrel_sql.client.session.action.RefreshTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.SessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.fw.util.Logger;

/**
 * Menu bar for <CODE>MainFrame</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
final class MainFrameMenuBar extends JMenuBar {
    public interface IMenuIDs {
        int PLUGINS_MENU = 1;
        int SESSION_MENU = 2;
    }

    private interface IMenuResourceKeys {
        String ALIASES = "aliases";
        String DRIVERS = "drivers";
//      String EDIT = "edit";
        String FILE = "file";
        String HELP = "help";
        String PLUGINS = "plugins";
        String SESSION = "session";
        String WINDOWS = "windows";
    }

    private IApplication _app;

    private JMenu _driversMenu;
    private JMenu _aliasesMenu;
    private JMenu _pluginsMenu;
    private JMenu _sessionMenu;
    private JMenu _windowsMenu;
    private ActionCollection _actions;

    private static int INTERNAL_FRAME_MENU_IDX = 2;

    /**
     * Ctor.
     */
    MainFrameMenuBar(IApplication app, JDesktopPane desktopPane, ActionCollection actions) {
        super();
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        if (desktopPane == null) {
            throw new IllegalArgumentException("Null JDesktopPane passed");
        }
        if (actions == null) {
            throw new IllegalArgumentException("Null ActionCollection passed");
        }
        Resources rsrc = app.getResources();
        if (rsrc == null) {
            throw new IllegalStateException("No Resources object in IApplication");
        }

        _actions = actions;
        _app = app;

        add(createFileMenu(rsrc));
//      add(createEditMenu());
        add(createDriversMenu(rsrc));
        add(createAliasesMenu(rsrc));
        add(createPluginsMenu(rsrc));
        add(createSessionMenu(rsrc));
        add(createWindowsMenu(rsrc, desktopPane));
        add(createHelpMenu(rsrc));
    }

    JMenu getWindowsMenu() {
        return _windowsMenu;
    }

    JMenu getSessionMenu() {
        return _sessionMenu;
    }

    void addToMenu(int menuId, JMenu menu) throws IllegalArgumentException {
        if (menu == null) {
            throw new IllegalArgumentException("Null JMenu passed");
        }
        switch (menuId) {
            case IMenuIDs.PLUGINS_MENU: {
                _pluginsMenu.add(menu);
                break;
            }
            case IMenuIDs.SESSION_MENU: {
                _sessionMenu.add(menu);
                break;
            }
            default: {
                // How do we want to hanle this??
            }
        }
    }

    void addToMenu(int menuId, Action action) {
        if (action == null) {
            throw new IllegalArgumentException("Null Action passed");
        }
        switch (menuId) {
            case IMenuIDs.PLUGINS_MENU: {
                _pluginsMenu.add(action);
                break;
            }
            case IMenuIDs.SESSION_MENU: {
                _sessionMenu.add(action);
                break;
            }
            default: {
                // How do we want to hanle this??
            }
        }
    }

    private JMenu createFileMenu(Resources rsrc) {
        JMenu menu = rsrc.createMenu(IMenuResourceKeys.FILE);
        addToMenu(rsrc, GlobalPreferencesAction.class, menu);
        menu.addSeparator();
        addToMenu(rsrc, ExitAction.class, menu);
        return menu;
    }

//  private JMenu createEditMenu() {
//      JMenu menu = s_res.createMenu(MenuResourceKeys.EDIT);
//      addToMenu(GlobalPreferencesAction.class, menu);
//      return menu;
//  }

    private JMenu createSessionMenu(Resources rsrc) {
        JMenu menu = rsrc.createMenu(IMenuResourceKeys.SESSION);
        addToMenu(rsrc, SessionPropertiesAction.class, menu);
        menu.addSeparator();
        addToMenu(rsrc, RefreshTreeAction.class, menu);
        addToMenu(rsrc, ExecuteSqlAction.class, menu);
        menu.setEnabled(false);
        _sessionMenu = menu;
        return menu;
    }

    private JMenu createPluginsMenu(Resources rsrc) {
        JMenu menu = rsrc.createMenu(IMenuResourceKeys.PLUGINS);
        addToMenu(rsrc, DisplayPluginSummaryAction.class, menu);
        menu.addSeparator();
        _pluginsMenu = menu;
        return menu;
    }

    private JMenu createAliasesMenu(Resources rsrc) {
        JMenu menu = rsrc.createMenu(IMenuResourceKeys.ALIASES);
        addToMenu(rsrc, ConnectToAliasAction.class, menu);
        menu.addSeparator();
        addToMenu(rsrc, CreateAliasAction.class, menu);
        menu.addSeparator();
        addToMenu(rsrc, ModifyAliasAction.class, menu);
        addToMenu(rsrc, DeleteAliasAction.class, menu);
        addToMenu(rsrc, CopyAliasAction.class, menu);
        _aliasesMenu = menu;
        return menu;
    }

    private JMenu createDriversMenu(Resources rsrc) {
        JMenu menu = rsrc.createMenu(IMenuResourceKeys.DRIVERS);
        addToMenu(rsrc, CreateDriverAction.class, menu);
        menu.addSeparator();
        addToMenu(rsrc, ModifyDriverAction.class, menu);
        addToMenu(rsrc, DeleteDriverAction.class, menu);
        addToMenu(rsrc, CopyDriverAction.class, menu);
        _driversMenu = menu;
        return menu;
    }

    private JMenu createWindowsMenu(Resources rsrc, JDesktopPane desktopPane) {
        JMenu menu = rsrc.createMenu(IMenuResourceKeys.WINDOWS);

        addToMenu(rsrc, ViewAliasesAction.class, menu);
        addToMenu(rsrc, ViewDriversAction.class, menu);
        menu.addSeparator();

        addDesktopPaneActionToMenu(rsrc, TileAction.class, menu, desktopPane);
        addDesktopPaneActionToMenu(rsrc, CascadeAction.class, menu, desktopPane);
        addDesktopPaneActionToMenu(rsrc, MaximizeAction.class, menu, desktopPane);

        menu.addSeparator();

        _windowsMenu = menu;
        return menu;
    }

    private JMenu createHelpMenu(Resources rsrc) {
        JMenu menu = rsrc.createMenu(IMenuResourceKeys.HELP);
        addToMenu(rsrc, AboutAction.class, menu);
        return menu;
    }

    private Action addDesktopPaneActionToMenu(Resources rsrc, Class actionClass,
                                        JMenu menu, JDesktopPane desktopPane) {
        Action act = addToMenu(rsrc, actionClass, menu);
        if (act != null) {
            if (act instanceof IHasJDesktopPane) {
                ((IHasJDesktopPane)act).setJDesktopPane(desktopPane);
            } else {
                _app.getLogger().showMessage(Logger.ILogTypes.ERROR, "Tryimg to add non IHasJDesktopPane (" + actionClass.getName() + ") in MainFrameMenuBar.addDesktopPaneActionToMenu");
            }
        }
        return act;
    }

    private Action addToMenu(Resources rsrc, Class actionClass, JMenu menu) {
        Action act = _actions.get(actionClass);
        if (act != null) {
            rsrc.addToMenu(act, menu);
        } else {
            _app.getLogger().showMessage(Logger.ILogTypes.ERROR, "Could not retrieve instance of " + actionClass.getName() + ") in MainFrameMenuBar.addToMenu");
        }
        return act;
    }

}
