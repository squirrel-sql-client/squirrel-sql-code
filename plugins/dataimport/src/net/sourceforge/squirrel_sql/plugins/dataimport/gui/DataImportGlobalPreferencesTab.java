package net.sourceforge.squirrel_sql.plugins.dataimport.gui;
/*
 * Copyright (C) 2007 Thorsten Mürell
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

import java.awt.Component;

import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.prefs.PreferencesManager;

/**
 * This is the global preferences panel for the dataimport plugin.
 * 
 * @author Thorsten Mürell
 */
public class DataImportGlobalPreferencesTab implements IGlobalPreferencesPanel {

    PreferencesPanel prefs = null;
    private JScrollPane myscrolledPanel;

    private static final StringManager stringMgr =
        StringManagerFactory.getStringManager(DataImportGlobalPreferencesTab.class);    
   
    /**
     * Standard constructor
     */
    public DataImportGlobalPreferencesTab() {
    	prefs = new PreferencesPanel(PreferencesManager.getPreferences());
        myscrolledPanel = new JScrollPane(prefs);  
        myscrolledPanel.getVerticalScrollBar().setUnitIncrement(10);
    }
    
	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel#initialize(net.sourceforge.squirrel_sql.client.IApplication)
	 */
	public void initialize(IApplication app) {
        /* Do Nothing */
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel#uninitialize(net.sourceforge.squirrel_sql.client.IApplication)
	 */
	public void uninitialize(IApplication app) {
        /* Do Nothing */
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#applyChanges()
	 */
	public void applyChanges() {
        if (prefs != null) {
            prefs.applyChanges();
        }
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getHint()
	 */
	public String getHint() {
        // i18n[DataImportGlobalPreferencesTab.hint=Preferences for data import]
        return stringMgr.getString("DataImportGlobalPreferencesTab.hint"); 
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getPanelComponent()
	 */
	public Component getPanelComponent() {
        return myscrolledPanel;
	}

	/* (non-Javadoc)
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getTitle()
	 */
	public String getTitle() {
        //i18n[DataImportGlobalPreferencesTab.title=Data import]
        return stringMgr.getString("DataImportGlobalPreferencesTab.title");
	}

}
