/*
 * Copyright (C) 2011 Rob Manning
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

package net.sourceforge.squirrel_sql.client.session.action.dbdiff.prefs;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.JsonMarshalUtil;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class DBDiffPreferencesPanel extends JPanel implements IGlobalPreferencesPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DBDiffPreferencesPanel.class);

	JRadioButton tabularPresentationRadioButton = null;

	JRadioButton sideBySidePresentationRadioButton = null;

	JRadioButton internalDiffWindowRadioButton = null;

	JRadioButton externalDiffToolRadionButton = null;

	JLabel externalDiffToolCommandLabel = null;

	JTextField externalDiffToolCommandTextField = null;

	JCheckBox sortColumnsCheckBox = null; 

	public DBDiffPreferencesPanel()
	{
	}

	@Override
	public void initialize(IApplication app)
	{
		createGUI();
		loadData();
	}

	@Override
	public void uninitialize(IApplication app)
	{

	}

	private void createGUI()
	{
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc;

		gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,3,0), 0,0);
		add(new MultipleLineLabel(s_stringMgr.getString("DBDiffPreferencesPanel.configure.table.diff.presentation")), gbc);

		gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
		add(createTopPanel(), gbc);

	}

	private JPanel createTopPanel()
	{
		final JPanel ret = new JPanel(new GridBagLayout());
		ret.setBorder(BorderFactory.createEtchedBorder());

		tabularPresentationRadioButton = new JRadioButton(s_stringMgr.getString("DBDiffPreferencesPanel.tabularPresentationRadioButtonLabel"));

		addSideBySidePresentationRadioButton(ret, 0, 0);

		final ButtonGroup tabularsidebyside = new ButtonGroup();
		tabularsidebyside.add(tabularPresentationRadioButton);
		tabularsidebyside.add(sideBySidePresentationRadioButton);

		addInternalDiffWindowRadioButton(ret, 0, 1);
		addExternalDiffToolRadioButton(ret, 0, 2);

		final ButtonGroup internalExternalButtonGroup = new ButtonGroup();
		internalExternalButtonGroup.add(internalDiffWindowRadioButton);
		internalExternalButtonGroup.add(externalDiffToolRadionButton);

		addExternalDiffToolCommandLabel(ret, 0, 3);
		addExternalDiffToolCommandTextField(ret, 1, 3);

		addSortColumnsCheckBoxLabel(ret, 0, 4);
		
		addTabularPresentationRadioButton(ret, 0, 5);

		return ret;
	}

	private void addSortColumnsCheckBoxLabel(JPanel panel, int col, int row)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; // Span across two columns
		c.insets = new Insets(5, 20, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		sortColumnsCheckBox = new JCheckBox(s_stringMgr.getString("DBDiffPreferencesPanel.sortColumnsForSideBySideComparisonLabel"));
		sortColumnsCheckBox.setToolTipText(s_stringMgr.getString("DBDiffPreferencesPanel.sortColumnsForSideBySideComparisonToolTip"));
		panel.add(sortColumnsCheckBox, c);
	}

	private void addTabularPresentationRadioButton(JPanel panel, int col, int row)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; // Span across two columns
		c.insets = new Insets(10, 0, 5, 0);
		c.anchor = GridBagConstraints.WEST;
		tabularPresentationRadioButton.setToolTipText(s_stringMgr.getString("DBDiffPreferencesPanel.tabularPresentationRadioButtonToolTipText"));
		tabularPresentationRadioButton.addActionListener(e -> updateEnableState());
		panel.add(tabularPresentationRadioButton, c);
	}

	private void addSideBySidePresentationRadioButton(JPanel panel, int col, int row)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; // Span across two columns
		c.insets = new Insets(5, 0, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		sideBySidePresentationRadioButton = new JRadioButton(s_stringMgr.getString("DBDiffPreferencesPanel.sideBySidePresentationRadionButtonLabel"));
		sideBySidePresentationRadioButton.setToolTipText(s_stringMgr.getString("DBDiffPreferencesPanel.sideBySidePresentationRadionButtonToolTip"));
		sideBySidePresentationRadioButton.addActionListener(e -> updateEnableState());
		panel.add(sideBySidePresentationRadioButton, c);
	}

	private void addExternalDiffToolRadioButton(JPanel panel, int col, int row)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; // Span across two columns
		c.insets = new Insets(5, 20, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		externalDiffToolRadionButton = new JRadioButton(s_stringMgr.getString("DBDiffPreferencesPanel.externalDiffToolRadionButtonLabel"));
		externalDiffToolRadionButton.setToolTipText(s_stringMgr.getString("DBDiffPreferencesPanel.externalDiffToolRadionButtonToolTip"));
		externalDiffToolRadionButton.addActionListener(e -> updateEnableState());
		panel.add(externalDiffToolRadionButton, c);
	}

	private void addInternalDiffWindowRadioButton(JPanel panel, int col, int row)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; // Span across two columns
		c.insets = new Insets(5, 20, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		internalDiffWindowRadioButton = new JRadioButton(s_stringMgr.getString("DBDiffPreferencesPanel.internalDiffWindowRadionButtonLabel"));
		internalDiffWindowRadioButton.setToolTipText(s_stringMgr.getString("DBDiffPreferencesPanel.internalDiffWindowRadionButtonToolTip"));
		internalDiffWindowRadioButton.addActionListener(e -> updateEnableState());
		panel.add(internalDiffWindowRadioButton, c);
	}

	private void addExternalDiffToolCommandLabel(JPanel panel, int col, int row)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.insets = new Insets(5, 45, 0, 0);
		externalDiffToolCommandLabel = new JLabel(s_stringMgr.getString("DBDiffPreferencesPanel.externalDiffToolCommandLabel"));
		externalDiffToolCommandLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		externalDiffToolCommandLabel.setToolTipText(s_stringMgr.getString("DBDiffPreferencesPanel.externalDiffToolCommandToolTip"));
		panel.add(externalDiffToolCommandLabel, c);
	}

	private void addExternalDiffToolCommandTextField(JPanel panel, int col, int row)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.ipadx = 40; // Increases component width by 20 pixels
		c.insets = new Insets(5, 5, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		externalDiffToolCommandTextField = new JTextField(10);
		externalDiffToolCommandTextField.setHorizontalAlignment(SwingConstants.LEFT);
		externalDiffToolCommandTextField.setToolTipText(s_stringMgr.getString("DBDiffPreferencesPanel.externalDiffToolCommandToolTip"));
		panel.add(externalDiffToolCommandTextField, c);
	}

	private void loadData()
	{
		DBDiffPreferenceBean prefs = Main.getApplication().getDBDiffState().getDBDiffPreferenceBean();

		tabularPresentationRadioButton.setSelected(prefs.isUseTabularDiffPresentation());
		sideBySidePresentationRadioButton.setSelected(!prefs.isUseTabularDiffPresentation());

		externalDiffToolRadionButton.setSelected(prefs.isUseExternalGraphicalDiffTool());
		internalDiffWindowRadioButton.setSelected(!prefs.isUseExternalGraphicalDiffTool());

		sortColumnsCheckBox.setSelected(prefs.isSortColumnsForSideBySideComparison());

		externalDiffToolCommandTextField.setText(prefs.getGraphicalToolCommand());

		updateEnableState();
	}

	private void updateEnableState()
	{
		if(tabularPresentationRadioButton.isSelected())
		{
			internalDiffWindowRadioButton.setEnabled(false);
			externalDiffToolRadionButton.setEnabled(false);
			externalDiffToolCommandLabel.setEnabled(false);
			externalDiffToolCommandTextField.setEnabled(false);
			sortColumnsCheckBox.setEnabled(false);
		}

		if(sideBySidePresentationRadioButton.isSelected())
		{
			internalDiffWindowRadioButton.setEnabled(true);
			externalDiffToolRadionButton.setEnabled(true);

			sortColumnsCheckBox.setEnabled(true);

			externalDiffToolCommandLabel.setEnabled(externalDiffToolRadionButton.isSelected());
			externalDiffToolCommandTextField.setEnabled(externalDiffToolRadionButton.isSelected());
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#applyChanges()
	 */
	@Override
	public void applyChanges()
	{
		DBDiffPreferenceBean prefs = new DBDiffPreferenceBean();
		prefs.setUseTabularDiffPresentation(tabularPresentationRadioButton.isSelected());
		prefs.setUseExternalGraphicalDiffTool(externalDiffToolRadionButton.isSelected());
		prefs.setGraphicalToolCommand(externalDiffToolCommandTextField.getText());
		prefs.setSortColumnsForSideBySideComparison(sortColumnsCheckBox.isSelected());

		File dbDiffPrefsJsonBeanFile = new ApplicationFiles().getDBDiffPrefsJsonBeanFile();

		JsonMarshalUtil.writeObjectToFile(dbDiffPrefsJsonBeanFile, prefs);

		Main.getApplication().getDBDiffState().writeDBDiffPreferenceBean(prefs);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getPanelComponent()
	 */
	@Override
	public Component getPanelComponent()
	{
		return this;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getHint()
	 */
	@Override
	public String getHint()
	{
		// i18n[DBDiffPreferencesPanel.hint=DB Diff]
		return s_stringMgr.getString("DBDiffPreferencesPanel.hint");
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		// i18n[DBDiffPreferencesPanel.title=DB Diff]
		return s_stringMgr.getString("DBDiffPreferencesPanel.title");
	}

}
