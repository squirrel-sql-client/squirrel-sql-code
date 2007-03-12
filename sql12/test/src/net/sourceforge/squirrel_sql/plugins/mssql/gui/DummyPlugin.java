/*
 * Copyright (C) 2005 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.mssql.gui;

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.util.IJavaPropertyNames;

public class DummyPlugin implements IPlugin {

    public String getChangeLogFileName() { return null; }
    public String getContributors() { return null; }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getExternalService()
     */
    public Object getExternalService() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getGlobalPreferencePanels()
     */
    public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getHelpFileName()
     */
    public String getHelpFileName() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getLicenceFileName()
     */
    public String getLicenceFileName() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getNewSessionPropertiesPanels()
     */
    public INewSessionPropertiesPanel[] getNewSessionPropertiesPanels() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getPluginAppSettingsFolder()
     */
    public File getPluginAppSettingsFolder() throws IOException, IllegalStateException {
        // TODO Auto-generated method stub
        String filename = System.getProperty(IJavaPropertyNames.USER_HOME)
                          + File.separator + ".squirrel-sql" + File.separator + "plugins"
                          + File.separator + "mssql" + File.separator;
        System.out.println("filename="+filename);
        return new File(filename); 
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getPluginUserSettingsFolder()
     */
    public File getPluginUserSettingsFolder() throws IllegalStateException, IOException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getWebSite()
     */
    public String getWebSite() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#initialize()
     */
    public void initialize() throws PluginException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#load(net.sourceforge.squirrel_sql.client.IApplication)
     */
    public void load(IApplication app) throws PluginException {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#unload()
     */
    public void unload() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getInternalName()
     */
    public String getInternalName() {
        return "dbcopy";
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getDescriptiveName()
     */
    public String getDescriptiveName() {
        return "DBCopy Plugin";
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getAuthor()
     */
    public String getAuthor() {
        return "Rob Manning";
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.plugin.IPlugin#getVersion()
     */
    public String getVersion() {
        return "0.13";
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
