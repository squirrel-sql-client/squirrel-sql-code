package net.sourceforge.squirrel_sql.plugins.dbinfo;
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;

/*
?? Need to add Source tab when stored proc selected in object tree and
   execute DBIndo.getProcSourceSql() to retrieve src.
   
   Associate a specific .xml file with a driver.
*/

/**
 * The Database Info plugin class.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DBInfoPlugin extends DefaultSessionPlugin {
    /** This plugins resources. */
    private PluginResources _resources;

    /** Folder DBInfo stores XML files. */
    private File _dataFolder;

    /**
     * Return the internal name of this plugin.
     *
     * @return  the internal name of this plugin.
     */
    public String getInternalName() {
        return "dbinfo";
    }

    /**
     * Return the descriptive name of this plugin.
     *
     * @return  the descriptive name of this plugin.
     */
    public String getDescriptiveName() {
        return "Database Info Plugin";
    }

    /**
     * Returns the current version of this plugin.
     *
     * @return  the current version of this plugin.
     */
    public String getVersion() {
        return "0.1";
    }

    /**
     * Returns the authors name.
     *
     * @return  the authors name.
     */
    public String getAuthor() {
        return "??";
    }

    /**
     * Initialize this plugin.
     */
    public synchronized void initialize() throws PluginException {
        super.initialize();

        _resources = new PluginResources("net.sourceforge.squirrel_sql.plugins.dbinfo.dbinfo", this);

        createActions();

        // Folder that stores data files.
        try {
	        _dataFolder = new File(getPluginAppSettingsFolder(), "data");
        } catch (IOException ex) {
        	throw new PluginException(ex);
        }
        if (!_dataFolder.exists()) {
            _dataFolder.mkdir();
        }
    }

    /**
     * Application is shutting down.
     */
    public void unload() {
        super.unload();
    }

    private void createActions() {
        final IApplication app = getApplication();
        ActionCollection coll = app.getActionCollection();
        Action action = new ShowDBInfoFilesAction(app, _resources);
        coll.add(action);
        app.addToMenu(IApplication.IMenuIDs.PLUGINS_MENU, action);
    }
}
