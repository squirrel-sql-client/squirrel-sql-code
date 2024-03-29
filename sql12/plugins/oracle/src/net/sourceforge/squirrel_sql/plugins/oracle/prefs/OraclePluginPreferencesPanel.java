/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.oracle.prefs;

import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TimeZone;

/**
 * Adds the preference widget for allowing the user to specify whether or not Oracle 10g recycle bin tables
 * should be hidden.
 * 
 * @author manningr
 */
public class OraclePluginPreferencesPanel extends PluginQueryTokenizerPreferencesPanel
{

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(OraclePluginPreferencesPanel.class);

	static interface i18n
	{
		// i18n[OraclePluginPreferencesPanel.hideRecycleBinCheckBoxLabel=Remove
		// Recycle Bin Tables from the Object Tree]
		String HIDE_RECYCLE_BIN_CB_LABEL =
			s_stringMgr.getString("OraclePluginPreferencesPanel.hideRecycleBinCheckBoxLabel");

		// i18n[OraclePluginPreferencesPanel.hideRecycleBinCheckBoxToolTip=Recycle
		// Bin tables are a Flashback Database Option in Oracle 10g]
		String HIDE_RECYCLE_BIN_CB_TT =
			s_stringMgr.getString("OraclePluginPreferencesPanel.hideRecycleBinCheckBoxToolTip");

		// i18n[OraclePluginPreferencesPanel.sessionTimezoneLabel=Initialize Session Timezone]
		String SESSION_TIMEZONE_LABEL = 
			s_stringMgr.getString("OraclePluginPreferencesPanel.sessionTimezoneLabel");
		
		// i18n[OraclePluginPreferencesPanel.sessionTimezoneTT=Set the session timezone to view 
		//'WITH LOCAL TIMEZONE' data types]
		String SESSION_TIMEZONE_TT = s_stringMgr.getString("OraclePluginPreferencesPanel.sessionTimezoneTT");
		
		// i18n[OraclePluginPreferencesPanel.showErrorOffsetLabel=Show Syntax
		// Error Offset in SQL Editor]
		String SHOW_ERROR_OFFSET_LABEL =
			s_stringMgr.getString("OraclePluginPreferencesPanel.showErrorOffsetLabel");

		// i18n[OraclePluginPreferencesPanel.showErrorOffsetTT=Creates and uses a
		// user-defined function that is used to determine the syntax error token]
		String SHOW_ERROR_OFFSET_TT = s_stringMgr.getString("OraclePluginPreferencesPanel.showErrorOffsetTT");
				
	}

	/** The checkbox for specifying exclusion of recycle bin tables */
	private final static JCheckBox excludeRecycleBinTablesCheckBox =
		new JCheckBox(i18n.HIDE_RECYCLE_BIN_CB_LABEL);

	private final static JCheckBox showErrorOffsetCheckBox = new JCheckBox(i18n.SHOW_ERROR_OFFSET_LABEL);


	private final static JCheckBox initSessionTimezoneCheckBox = new JCheckBox(i18n.SESSION_TIMEZONE_LABEL);

	private final static JTextField sessionTimezoneTextField = new JTextField();

	/**
	 * Construct a new PreferencesPanel.
	 * 
	 * @param prefs
	 * @param databaseName
	 */
	public OraclePluginPreferencesPanel(PluginQueryTokenizerPreferencesManager prefsMgr)
	{
		super(prefsMgr, "Oracle");
		setName("OraclePluginPreferencesPanel");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel#createTopPanel()
	 */
	@Override
	protected JPanel createTopPanel()
	{
		JPanel result = super.createTopPanel();
		int lastY = super.lastY;
		addRecycleBinCheckBox(result, 0, lastY++);
		addShowErrorOffsetCheckBox(result, 0, lastY++);
		addSessionTimezoneCheckBox(result, 0, lastY++);
		addSessionTimezoneTextField(result, 0, lastY++);
		return result;
	}

	private void addRecycleBinCheckBox(JPanel result, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; // Span across two columns
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, 5, 0, 0);
		excludeRecycleBinTablesCheckBox.setToolTipText(i18n.HIDE_RECYCLE_BIN_CB_TT);
		result.add(excludeRecycleBinTablesCheckBox, c);
	}

	private void addShowErrorOffsetCheckBox(JPanel result, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; // Span across two columns
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, 5, 0, 0);
		showErrorOffsetCheckBox.setToolTipText(i18n.SHOW_ERROR_OFFSET_TT);
		result.add(showErrorOffsetCheckBox, c);
	}

	private void addSessionTimezoneCheckBox(JPanel result, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; // Span across two columns
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, 5, 0, 0);
		initSessionTimezoneCheckBox.setToolTipText(i18n.SESSION_TIMEZONE_TT);
		initSessionTimezoneCheckBox.setName("initSessionTimezoneCheckBox");
		result.add(initSessionTimezoneCheckBox, c);
		
		initSessionTimezoneCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				sessionTimezoneTextField.setEnabled(initSessionTimezoneCheckBox.isSelected());
			}
		});
	}

	private void addSessionTimezoneTextField(JPanel result, int col, int row)
	{
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, LEFT_INDENT_INSET_SIZE, 0, 0);
		sessionTimezoneTextField.setToolTipText(i18n.SESSION_TIMEZONE_TT);
		sessionTimezoneTextField.setName("sessionTimezoneTextField");
		result.add(sessionTimezoneTextField, c);
	}
	
	
	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel#loadData()
	 */
	@Override
	protected void loadData()
	{
		super.loadData();
		IQueryTokenizerPreferenceBean prefs = _prefsManager.getPreferences();
		OraclePreferenceBean oraclePrefs = (OraclePreferenceBean) prefs;
		excludeRecycleBinTablesCheckBox.setSelected(oraclePrefs.isExcludeRecycleBinTables());
		showErrorOffsetCheckBox.setSelected(oraclePrefs.isShowErrorOffset());
		if (oraclePrefs.getInitSessionTimezone()) {
			initSessionTimezoneCheckBox.setSelected(true);
			sessionTimezoneTextField.setEnabled(true);
		} else {
			initSessionTimezoneCheckBox.setSelected(false);
			sessionTimezoneTextField.setEnabled(false);
		}
		if (oraclePrefs.getSessionTimezone() != null && !"".equals(oraclePrefs.getSessionTimezone())) {
			sessionTimezoneTextField.setText(oraclePrefs.getSessionTimezone());
		} else {
			sessionTimezoneTextField.setText(TimeZone.getDefault().getID());
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel#save()
	 */
	@Override
	protected void save()
	{
		IQueryTokenizerPreferenceBean prefs = _prefsManager.getPreferences();
		OraclePreferenceBean oraclePrefs = (OraclePreferenceBean) prefs;
		oraclePrefs.setExcludeRecycleBinTables(excludeRecycleBinTablesCheckBox.isSelected());
		oraclePrefs.setShowErrorOffset(showErrorOffsetCheckBox.isSelected());
		oraclePrefs.setInitSessionTimezone(initSessionTimezoneCheckBox.isSelected());
		if (initSessionTimezoneCheckBox.isSelected()) {
			oraclePrefs.setSessionTimezone(sessionTimezoneTextField.getText());
		}
		super.save();
	}

}
