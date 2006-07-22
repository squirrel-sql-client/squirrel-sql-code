/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.client.plugin;

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;

public class MockPlugin implements IPlugin {

    private File pluginAppSettingsFolder = null;
    
    public MockPlugin(String settingsFolder) {
        pluginAppSettingsFolder = new File(settingsFolder);
    }
    
    public void load(IApplication app) throws PluginException {
        // TODO Auto-generated method stub

    }

    public void initialize() throws PluginException {
        // TODO Auto-generated method stub

    }

    public void unload() {
        // TODO Auto-generated method stub

    }

    public String getInternalName() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDescriptiveName() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getAuthor() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getContributors() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getWebSite() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getHelpFileName() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getChangeLogFileName() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getLicenceFileName() {
        // TODO Auto-generated method stub
        return null;
    }

    public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
        // TODO Auto-generated method stub
        return null;
    }

    public INewSessionPropertiesPanel[] getNewSessionPropertiesPanels() {
        // TODO Auto-generated method stub
        return null;
    }

    public File getPluginAppSettingsFolder() 
        throws IOException, IllegalStateException 
    {
        return pluginAppSettingsFolder;
    }

    public File getPluginUserSettingsFolder() throws IllegalStateException,
            IOException {
        return pluginAppSettingsFolder;
    }

    public Object getExternalService() {
        // TODO Auto-generated method stub
        return null;
    }

	public void aliasCopied(SQLAlias source, SQLAlias target) {
		// TODO Auto-generated method stub
		
	}

	public void aliasRemoved(SQLAlias alias) {
		// TODO Auto-generated method stub
		
	}

	public IAliasPropertiesPanelController[] getAliasPropertiesPanelControllers(SQLAlias alias) {
		// TODO Auto-generated method stub
		return null;
	}

}
