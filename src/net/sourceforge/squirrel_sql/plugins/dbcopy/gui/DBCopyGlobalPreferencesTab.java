/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.dbcopy.gui;

import java.awt.Component;

import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;

public class DBCopyGlobalPreferencesTab implements IGlobalPreferencesPanel {

    PreferencesPanel prefs = null;
    private JScrollPane _myscrolledPanel;
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DBCopyGlobalPreferencesTab.class);    
    
    public DBCopyGlobalPreferencesTab() {
        prefs = new PreferencesPanel(PreferencesManager.getPreferences());
        _myscrolledPanel = new JScrollPane(prefs);  
        _myscrolledPanel.getVerticalScrollBar().setUnitIncrement(10);
    }
    
    public void initialize(IApplication app) {
        /* Do Nothing */
    }

    public void uninitialize(IApplication app) {
        /* Do Nothing */
    }    
    
    public void applyChanges() {
        if (prefs != null) {
            prefs.applyChanges();
        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getTitle()
     */
    public String getTitle() {
        //i18n[DBCopyGlobalPreferencesTab.title=DB Copy]
        return s_stringMgr.getString("DBCopyGlobalPreferencesTab.title");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getHint()
     */
    public String getHint() {
        // i18n[DBCopyGlobalPreferencesTab.hint=Preferences for DB Copy]
        return s_stringMgr.getString("DBCopyGlobalPreferencesTab.hint"); 
    }

    public Component getPanelComponent() {
        return _myscrolledPanel;
    }

}
