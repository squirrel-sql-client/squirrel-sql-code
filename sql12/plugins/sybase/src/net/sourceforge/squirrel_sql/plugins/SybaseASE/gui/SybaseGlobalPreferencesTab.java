/*
 * Copyright (C) 2007 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.SybaseASE.gui;

import java.awt.Component;

import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.SybaseASE.prefs.PreferencesManager;

public class SybaseGlobalPreferencesTab implements IGlobalPreferencesPanel {

    PreferencesPanel prefs = null;
    private JScrollPane _myscrolledPanel;
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SybaseGlobalPreferencesTab.class);    
    
    public SybaseGlobalPreferencesTab() {
        prefs = new PreferencesPanel(PreferencesManager.getPreferences());
        _myscrolledPanel = new JScrollPane(prefs);      
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
        //i18n[OracleGlobalPreferencesTab.title=Oracle]
        return s_stringMgr.getString("OracleGlobalPreferencesTab.title");
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getHint()
     */
    public String getHint() {
        // i18n[OracleGlobalPreferencesTab.hint=Preferences for Oracle]
        return s_stringMgr.getString("OracleGlobalPreferencesTab.hint"); 
    }

    public Component getPanelComponent() {
        return _myscrolledPanel;
    }

}
