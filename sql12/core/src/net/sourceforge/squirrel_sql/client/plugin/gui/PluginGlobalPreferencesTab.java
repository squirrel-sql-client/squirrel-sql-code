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
package net.sourceforge.squirrel_sql.client.plugin.gui;

import java.awt.Component;

import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;

public class PluginGlobalPreferencesTab implements IGlobalPreferencesPanel {

    protected PluginQueryTokenizerPreferencesPanel _prefs = null;

    private JScrollPane _myscrolledPanel;

    private String _title = null;
    
    private String _hint = null;
    
    public PluginGlobalPreferencesTab(PluginQueryTokenizerPreferencesPanel prefsPanel) {
        _myscrolledPanel = new JScrollPane(prefsPanel);
        _prefs = prefsPanel;
    }

    public void initialize(IApplication app) {
        /* Do Nothing */
    }

    public void uninitialize(IApplication app) {
        /* Do Nothing */
    }

    public void applyChanges() {
        if (_prefs != null) {
            _prefs.applyChanges();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getTitle()
     */
    public String getTitle() {
        return _title;
    }

    public void setTitle(String title) {
        this._title = title;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getHint()
     */
    public String getHint() {
        return _hint;
    }

    public void setHint(String hint) {
        this._hint = hint;
    }
    
    public Component getPanelComponent() {
        return _myscrolledPanel;
    }

}
