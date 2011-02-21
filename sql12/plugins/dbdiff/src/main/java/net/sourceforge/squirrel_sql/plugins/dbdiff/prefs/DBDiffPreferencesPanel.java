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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.sourceforge.squirrel_sql.client.util.IOptionPanel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DBDiffPreferencesPanel extends JPanel implements IOptionPanel
{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

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

		super();
		this.pluginPreferencesManager = pluginPreferencesManager;
		_prefs = (DBDiffPreferenceBean) pluginPreferencesManager.getPreferences();
		createGUI();
		loadData();
	}

	private void createGUI()
	{
		this.setLayout(new GridBagLayout());
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; // Column 0
		c.gridy = 0; // Row 0
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = .40;
		add(createTopPanel(), c);

	}

	private JPanel createTopPanel()
	{
		final JPanel result = new JPanel(new GridBagLayout());
		// i18n[DBDiffPreferencesPanel.presentationOptionsBorderLabel=Presentation Options]
		final String tranferOptionsBorderLabel =
			s_stringMgr.getString("DBDiffPreferencesPanel.presentationOptionsBorderLabel");
		result.setBorder(getTitledBorder(tranferOptionsBorderLabel));
		final String cbLabel =
			s_stringMgr.getString("DBDiffPreferencesPanel.tabularPresentationRadioButtonLabel");
		tabularPresentationRadioButton = new JRadioButton(cbLabel);

		addSideBySidePresentationRadionButton(result, 0, 0);

		final ButtonGroup tabularsidebyside = new ButtonGroup();
		tabularsidebyside.add(tabularPresentationRadioButton);
		tabularsidebyside.add(sideBySidePresentationRadioButton);

		addInternalDiffWindowRadionButton(result, 0, 1);
		addExternalDiffToolRadioButton(result, 0, 2);

		final ButtonGroup internalExternalButtonGroup = new ButtonGroup();
		internalExternalButtonGroup.add(internalDiffWindowRadioButton);
		internalExternalButtonGroup.add(externalDiffToolRadionButton);

		addexternalDiffToolCommandLabel(result, 0, 3);
		addExternalDiffToolCommandTextField(result, 1, 3);

		addSortColumnsCheckBoxLabel(result, 0, 4);
		
		addTabularPresentationRadioButton(result, 0, 5);

		return result;
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

	private void addSideBySidePresentationRadionButton(JPanel panel, int col, int row)
	{
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = col;
		c.gridy = row;
		c.gridwidth = 2; // Span across two columns
		c.insets = new Insets(10, 0, 0, 0);
		c.anchor = GridBagConstraints.WEST;
		final String cbLabelStr =
			s_stringMgr.getString("DBDiffPreferencesPanel.sideBySidePresentationRadionButtonLabel");
		final String toolTipText =
			s_stringMgr.getString("DBDiffPreferencesPanel.sideBySidePresentationRadionButtonToolTip");
		sideBySidePresentationRadioButton = new JRadioButton(cbLabelStr);
		sideBySidePresentationRadioButton.setToolTipText(toolTipText);
		sideBySidePresentationRadioButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
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
		});
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

	private Border getTitledBorder(String title)
	{
		final CompoundBorder border =
			new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new TitledBorder(title));
		return border;
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
		if (tabularPresentationRadioButton.isSelected())
		{
			_prefs.setUseTabularDiffPresenation(true);
		}
		else
		{
			_prefs.setUseTabularDiffPresenation(false);
		}

		if (externalDiffToolRadionButton.isSelected())
		{
			_prefs.setUseExternalGraphicalDiffTool(true);
			_prefs.setGraphicalToolCommand(externalDiffToolCommandTextField.getText());
		}
		else
		{
			_prefs.setUseExternalGraphicalDiffTool(false);
		}

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
