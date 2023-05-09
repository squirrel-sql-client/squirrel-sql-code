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

package net.sourceforge.squirrel_sql.plugins.dbdiff.prefs;

import net.sourceforge.squirrel_sql.client.util.IOptionPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DBDiffPreferencesPanel extends JPanel implements IOptionPanel
{

	DBDiffPreferenceBean _prefs = null;

	IPluginPreferencesManager pluginPreferencesManager = null;

	JRadioButton tabularPresentationRadioButton = null;

	JRadioButton sideBySidePresentationRadioButton = null;

	JRadioButton internalDiffWindowRadioButton = null;

	JRadioButton externalDiffToolRadionButton = null;

	JLabel externalDiffToolCommandLabel = null;

	JTextField externalDiffToolCommandTextField = null;

	JCheckBox sortColumnsCheckBox = null; 
	
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DBDiffPreferencesPanel.class);

	public DBDiffPreferencesPanel(IPluginPreferencesManager pluginPreferencesManager)
	{
		this.pluginPreferencesManager = pluginPreferencesManager;
		_prefs = (DBDiffPreferenceBean) pluginPreferencesManager.getPreferences();
		createGUI();
		loadData();
	}

	private void createGUI()
	{
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc;

		gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,3,0), 0,0);
		add(new JLabel(s_stringMgr.getString("DBDiffPreferencesPanel.configure.table.diff.presentation")), gbc);

		gbc = new GridBagConstraints(0,1,1,1,0,0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0);
		add(createTopPanel(), gbc);

	}

	private JPanel createTopPanel()
	{
		final JPanel ret = new JPanel(new GridBagLayout());
		ret.setBorder(BorderFactory.createEtchedBorder());

		final String cbLabel = s_stringMgr.getString("DBDiffPreferencesPanel.tabularPresentationRadioButtonLabel");
		tabularPresentationRadioButton = new JRadioButton(cbLabel);

		addSideBySidePresentationRadioButton(ret, 0, 0);

		final ButtonGroup tabularsidebyside = new ButtonGroup();
		tabularsidebyside.add(tabularPresentationRadioButton);
		tabularsidebyside.add(sideBySidePresentationRadioButton);

		addInternalDiffWindowRadionButton(ret, 0, 1);
		addExternalDiffToolRadioButton(ret, 0, 2);

		final ButtonGroup internalExternalButtonGroup = new ButtonGroup();
		internalExternalButtonGroup.add(internalDiffWindowRadioButton);
		internalExternalButtonGroup.add(externalDiffToolRadionButton);

		addexternalDiffToolCommandLabel(ret, 0, 3);
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
		final String sortColumnsCheckBoxLabel = 
			s_stringMgr.getString("DBDiffPreferencesPanel.sortColumnsForSideBySideComparisonLabel");
		final String sortColumnsCheckBoxToolTip =
			s_stringMgr.getString("DBDiffPreferencesPanel.sortColumnsForSideBySideComparisonToolTip");
		sortColumnsCheckBox = new JCheckBox(sortColumnsCheckBoxLabel);
		sortColumnsCheckBox.setToolTipText(sortColumnsCheckBoxToolTip);
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
		final String cbToolTipText =
			s_stringMgr.getString("DBDiffPreferencesPanel.tabularPresentationRadioButtonToolTipText");
		tabularPresentationRadioButton.setToolTipText(cbToolTipText);
		tabularPresentationRadioButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource() == tabularPresentationRadioButton)
				{
					internalDiffWindowRadioButton.setEnabled(false);
					externalDiffToolRadionButton.setEnabled(false);
					externalDiffToolCommandLabel.setEnabled(false);
					externalDiffToolCommandTextField.setEnabled(false);
				}

			}

		});
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
		final String cbLabelStr = s_stringMgr.getString("DBDiffPreferencesPanel.sideBySidePresentationRadionButtonLabel");
		final String toolTipText = s_stringMgr.getString("DBDiffPreferencesPanel.sideBySidePresentationRadionButtonToolTip");
		sideBySidePresentationRadioButton = new JRadioButton(cbLabelStr);
		sideBySidePresentationRadioButton.setToolTipText(toolTipText);
		sideBySidePresentationRadioButton.addActionListener(e -> onRadSideBySide(e));
		panel.add(sideBySidePresentationRadioButton, c);
	}

	private void onRadSideBySide(ActionEvent e)
	{
		if (e.getSource() == sideBySidePresentationRadioButton)
		{
			if (!internalDiffWindowRadioButton.isSelected() && !externalDiffToolRadionButton.isSelected())
			{
				internalDiffWindowRadioButton.setSelected(true);
			}
			internalDiffWindowRadioButton.setEnabled(true);
			externalDiffToolRadionButton.setEnabled(true);
			externalDiffToolCommandLabel.setEnabled(true);
			externalDiffToolCommandTextField.setEnabled(true);
		}
	}

	private void addExternalDiffToolRadioButton(JPanel panel, int col, int row)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; // Span across two columns
		c.insets = new Insets(5, 20, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		final String cbLabelStr =
			s_stringMgr.getString("DBDiffPreferencesPanel.externalDiffToolRadionButtonLabel");
		final String toolTipText =
			s_stringMgr.getString("DBDiffPreferencesPanel.externalDiffToolRadionButtonToolTip");
		externalDiffToolRadionButton = new JRadioButton(cbLabelStr);
		externalDiffToolRadionButton.setToolTipText(toolTipText);
		externalDiffToolRadionButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource() == externalDiffToolRadionButton)
				{
					externalDiffToolCommandLabel.setEnabled(true);
					externalDiffToolCommandTextField.setEnabled(true);
				}
			}

		});
		panel.add(externalDiffToolRadionButton, c);
	}

	private void addInternalDiffWindowRadionButton(JPanel panel, int col, int row)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; // Span across two columns
		c.insets = new Insets(5, 20, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		final String cbLabelStr =
			s_stringMgr.getString("DBDiffPreferencesPanel.internalDiffWindowRadionButtonLabel");
		final String toolTipText =
			s_stringMgr.getString("DBDiffPreferencesPanel.internalDiffWindowRadionButtonToolTip");
		internalDiffWindowRadioButton = new JRadioButton(cbLabelStr);
		internalDiffWindowRadioButton.setToolTipText(toolTipText);
		internalDiffWindowRadioButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource() == internalDiffWindowRadioButton)
				{
					externalDiffToolCommandLabel.setEnabled(false);
					externalDiffToolCommandTextField.setEditable(false);
				}
			}
		});
		panel.add(internalDiffWindowRadioButton, c);
	}

	private void addexternalDiffToolCommandLabel(JPanel panel, int col, int row)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.insets = new Insets(5, 45, 0, 0);
		final String commitLabel = s_stringMgr.getString("DBDiffPreferencesPanel.externalDiffToolCommandLabel");
		externalDiffToolCommandLabel = new JLabel(commitLabel);
		externalDiffToolCommandLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		final String commitlabelToolTipText =
			s_stringMgr.getString("DBDiffPreferencesPanel.externalDiffToolCommandToolTip");
		externalDiffToolCommandLabel.setToolTipText(commitlabelToolTipText);
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
		final String commitlabelToolTipText =
			s_stringMgr.getString("DBDiffPreferencesPanel.externalDiffToolCommandToolTip");
		externalDiffToolCommandTextField.setToolTipText(commitlabelToolTipText);
		panel.add(externalDiffToolCommandTextField, c);
	}

	private void loadData()
	{
		if (_prefs.isUseTabularDiffPresenation())
		{
			tabularPresentationRadioButton.setSelected(true);
		}
		else
		{
			sideBySidePresentationRadioButton.setSelected(true);
			if (_prefs.isUseExternalGraphicalDiffTool())
			{
				externalDiffToolRadionButton.setSelected(true);
				externalDiffToolCommandLabel.setEnabled(true);
				externalDiffToolCommandTextField.setEnabled(true);
			}
			else
			{
				internalDiffWindowRadioButton.setSelected(true);
				externalDiffToolCommandLabel.setEnabled(false);
				externalDiffToolCommandTextField.setEnabled(false);
			}
		}

	}

	private void save()
	{
		_prefs.setUseTabularDiffPresenation(tabularPresentationRadioButton.isSelected());
		_prefs.setUseExternalGraphicalDiffTool(externalDiffToolRadionButton.isSelected());
		if (externalDiffToolRadionButton.isSelected())
		{
			_prefs.setGraphicalToolCommand(externalDiffToolCommandTextField.getText());
		}
		_prefs.setSortColumnsForSideBySideComparison(sortColumnsCheckBox.isSelected());
		
		pluginPreferencesManager.savePrefs();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#applyChanges()
	 */
	@Override
	public void applyChanges()
	{
		save();
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
