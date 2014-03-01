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

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dataimport.prefs.DataImportPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.dataimport.prefs.PreferencesManager;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * This is the preferences panel for the dataimport plugin.
 * 
 * @author Thorsten Mürell
 */
public class PreferencesPanel extends JPanel {
	private static final long serialVersionUID = 7648092437088098470L;

	DataImportPreferenceBean prefs = null;
    
    JCheckBox truncateCheckBox = null;
    
    /** Logger for this class. */
    private final static ILogger log = 
        LoggerController.createLogger(PreferencesPanel.class);    
    
    /** Internationalized strings for this class. */
    private static final StringManager stringMgr =
        StringManagerFactory.getStringManager(PreferencesPanel.class);
    
    /**
     * Standard constructor.
     * 
     * @param prefs The preferences container bean
     */
    public PreferencesPanel(DataImportPreferenceBean prefs) {
        super();
        this.prefs = prefs;
        createGUI();
        loadData();
    }
    
    private void createGUI() {
		final FormLayout layout = new FormLayout(
				// Columns
				"left:pref:grow",
				// Rows
				"12dlu");

		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		builder.setDefaultDialogBorder();
		
		//i18n[PreferencesPanel.truncateTable=Truncate table before inserting data]
		truncateCheckBox = new JCheckBox(stringMgr.getString("PreferencesPanel.truncateTable"));

		int y = 1;
		builder.add(truncateCheckBox, cc.xy(1, y));

		add(builder.getPanel());
    }
    
    private void loadData() {
    	truncateCheckBox.setSelected(prefs.isUseTruncate());
    }
    
    private void save() {
        prefs.setUseTruncate(truncateCheckBox.isSelected());
        
        PreferencesManager.savePrefs();
    }

    /**
     * Applies the changes
     */
    public void applyChanges() {
        save();
    }
    
    /**
     * Returns the panel component, i.e. this.
     * 
     * @return The preferences panel component
     */
    public Component getPanelComponent() {
        return this;
    }



}
