package net.sourceforge.squirrel_sql.client;
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
import java.io.File;
import java.io.IOException;

import javax.swing.Action;
import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.Logger;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.db.DataCache;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

/**
 * Defines the API to do callbacks on the application. I'm not sure
 * about this one.
 */
public interface IApplication {
    public interface IMenuIDs extends MainFrame.IMenuIDs {
    }

    Logger getLogger();
    IPlugin getDummyAppPlugin();
    PluginManager getPluginManager();

    ActionCollection getActionCollection();

    SQLDriverManager getSQLDriverManager();

    DataCache getDataCache();


    SquirrelPreferences getSquirrelPreferences();

    SquirrelResources getResources();

    MainFrame getMainFrame();

    void addToMenu(int menuId, JMenu menu);
    public void addToMenu(int menuId, Action action);

    void startup();
    void shutdown();
}


