/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.squirrel_sql.plugin.nimbusdark;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;

/**
 * Squirrel-SQL Nimbus LnF dark theme plugin
 *
 * @author Wayne Zhang
 */
public class NimbusDarkThemePlugin extends DefaultPlugin {

    @Override
    public String getAuthor() {
        return "Wayne Zhang";
    }

    @Override
    public String getDescriptiveName() {
        return "Nimbus Dark Theme";
    }

    @Override
    public String getInternalName() {
        return "nimbusdark";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

	/**
	 * Returns the name of the change log for the plugin. This should
	 * be a text or HTML file residing in the getPluginAppSettingsFolder
	 * directory.
	 *
	 * @return	the changelog file name or null if plugin doesn't have
	 * 			a change log.
	 */
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	/**
	 * Returns the name of the Help file for the plugin. This should
	 * be a text or HTML file residing in the getPluginAppSettingsFolder
	 * directory.
	 *
	 * @return	the Help file name or null if plugin doesn't have
	 * 			a help file.
	 */
	public String getHelpFileName()
	{
		return "doc/readme.txt";
	}

	/**
	 * Returns the name of the Licence file for the plugin. This should
	 * be a text or HTML file residing in the getPluginAppSettingsFolder
	 * directory.
	 *
	 * @return	the Licence file name or null if plugin doesn't have
	 * 			a licence file.
	 */
	public String getLicenceFileName()
	{
		return "licence.txt";
	}    
    /**
     * Should configure in load() method instead oninitalize(), or tree icon doesn't work.
     * 
     * I guess tree is built already when initialize() called.
     * 
     * @param app application to load
     * @throws PluginException when load failed
     */
    @Override
    public void load(IApplication app) throws PluginException {
        super.load(app);
        
        new NimbusDarkTheme().configTheme();
    }

    @Override
    public void unload() {
        super.unload();
        
        new NimbusDarkTheme().resetDefaultTheme();
    }
}
