/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.squirrel_sql.plugin;

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
        return "Nimbus-dark-theme";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    /**
     * Should configure in load() method instead ofinitalize(), or tree icon doesn't work.
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

}
