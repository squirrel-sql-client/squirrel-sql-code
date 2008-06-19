/*
 * Copyright (C) 2008 Michael Romankiewicz
 * mirommail(at)web.de
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
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui;

import java.awt.Component;

import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref.PreferencesManager;

public class FirebirdManagerGlobalPreferencesTab implements IGlobalPreferencesPanel {

    private PreferencesPanel firebirdManagerPreferences = null;
    private JScrollPane scrollPanePreferences;
    
    private static final StringManager stringManager =
        StringManagerFactory.getStringManager(FirebirdManagerGlobalPreferencesTab.class);    
    
    public FirebirdManagerGlobalPreferencesTab() {
        firebirdManagerPreferences = new PreferencesPanel(PreferencesManager.getGlobalPreferences());
        scrollPanePreferences = new JScrollPane(firebirdManagerPreferences);  
        scrollPanePreferences.getVerticalScrollBar().setUnitIncrement(10);
    }

	/**
	 * I18n texts
	 */
    private interface i18n {
    	String GLOBAL_PREFERENCES_TAB_TITLE = stringManager.getString("global.preferences.tab.title");
    	String GLOBAL_PREFERENCES_TAB_TOOLTIP = stringManager.getString("global.preferences.tab.tooltip"); 
    }
    
    public void initialize(IApplication app) {
        // nothing to do
    }

    public void uninitialize(IApplication app) {
        // nothing to do
    }    
    
    public void applyChanges() {
        if (firebirdManagerPreferences != null) {
            firebirdManagerPreferences.applyChanges();
        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getTitle()
     */
    public String getTitle() {
        return i18n.GLOBAL_PREFERENCES_TAB_TITLE;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getHint()
     */
    public String getHint() {
        return i18n.GLOBAL_PREFERENCES_TAB_TOOLTIP; 
    }

    public Component getPanelComponent() {
        return scrollPanePreferences;
    }

}
