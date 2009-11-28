package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

/*
 * Copyright (C) 2009 Rob Manning
 * manningr@users.sourceforge.net
 * 
 * Based on initial work from Colin Bell
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

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAliasColorProperties;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

/**
 * This panel allows the user to review and maintain the color properties for an Alias. Background colors can
 * be configured for an Alias, so that each time a session is created, certain component background colors can
 * appear differently from sessions created using other Aliases. This allows the user to get visual clues from
 * SQuirreL about the session that they are interacting with to avoid confusing production database sessions
 * and development ones.
 */
public class ColorPropertiesPanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ColorPropertiesPanel.class);

	// Toolbar

	private JCheckBox toolbarBackgroundColorChk = new JCheckBox("");

	private JButton toolbarBackgroundColorBtn = new JButton("Choose Toolbar Color");

	private JLabel toolBarBackgroundLbl = new JLabel("Background Color");

	private JPanel toolbarBackgroundColorPnl = new JPanel();

	private Color toolbarBackgroundColor = null;

	private JCheckBox syncComponentBackgroundColorChk =
		new JCheckBox("Use toolbar background color for all components");

	// Object Tree

	private JCheckBox objectTreeBackgroundColorChk = new JCheckBox("");

	private JButton objectTreeBackgroundColorBtn = new JButton("Choose Object Tree Color");

	private JLabel objectTreeBackgroundLbl = new JLabel("Background Color");

	private JPanel objectTreeBackgroundColorPnl = new JPanel();

	private Color objectTreeBackgroundColor = null;

	// Status Bar

	private JCheckBox statusBarBackgroundColorChk = new JCheckBox("");

	private JButton statusBarBackgroundColorBtn = new JButton("Choose Status Bar Color");

	private JLabel statusBarBackgroundLbl = new JLabel("Background Color");

	private JPanel statusBarBackgroundColorPnl = new JPanel();

	private Color statusBarBackgroundColor = null;

	private interface i18n
	{
		String INSTRUCTIONS = s_stringMgr.getString("ColorPropertiesPanel.instructions");
	}

	private SQLAliasColorProperties _props = null;

	public ColorPropertiesPanel(SQLAliasColorProperties props)
	{
		Utilities.checkNull("ColorPropertiesPanel.init", "props", props);

		this._props = props;

		createUserInterface();
	}

	/**
	 * Retrieve the database properties.
	 * 
	 * @return the database properties.
	 */
	public SQLAliasColorProperties getSQLAliasColorProperties()
	{
		if (toolbarBackgroundColorChk.isSelected())
		{
			if (toolbarBackgroundColor != null)
			{
				_props.setOverrideToolbarBackgroundColor(true);
				_props.setToolbarBackgroundColorRgbValue(toolbarBackgroundColor.getRGB());
			}
		}
		else
		{
			_props.setOverrideToolbarBackgroundColor(false);
		}
		if (objectTreeBackgroundColorChk.isSelected())
		{
			if (objectTreeBackgroundColor != null)
			{
				_props.setOverrideObjectTreeBackgroundColor(true);
				_props.setObjectTreeBackgroundColorRgbValue(objectTreeBackgroundColor.getRGB());
			}
		}
		else
		{
			_props.setOverrideObjectTreeBackgroundColor(false);
		}
		if (statusBarBackgroundColorChk.isSelected())
		{
			if (statusBarBackgroundColor != null)
			{
				_props.setOverrideStatusBarBackgroundColor(true);
				_props.setStatusBarBackgroundColorRgbValue(statusBarBackgroundColor.getRGB());
			}
		}
		else
		{
			_props.setOverrideStatusBarBackgroundColor(false);
		}
		return _props;
	}

	private void createUserInterface()
	{
		setLayout(new GridBagLayout());

		final GridBagConstraints gbc = new GridBagConstraints();

		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		gbc.weighty = 0;

		// Instructions
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		add(createInfoPanel(), gbc);

		prepareNewRow(gbc);

		addToolbarColorComponents(gbc);

		prepareNewRow(gbc);

		addSyncComponentColorsWithToolbarCheckBox(gbc);

		prepareNewRow(gbc);

		addObjectTreeColorComponents(gbc);

		prepareNewRow(gbc);

		addStatusBarColorComponents(gbc);
	}

	private void addSyncComponentColorsWithToolbarCheckBox(final GridBagConstraints gbc)
	{
		++gbc.gridx;
		gbc.gridwidth = 2;
		add(syncComponentBackgroundColorChk, gbc);

		syncComponentBackgroundColorChk.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (syncComponentBackgroundColorChk.isSelected())
				{
					objectTreeBackgroundColorChk.setSelected(true);
					objectTreeBackgroundColor = toolbarBackgroundColor;
					objectTreeBackgroundColorBtn.setEnabled(true);
					objectTreeBackgroundColorPnl.setBackground(toolbarBackgroundColor);
					objectTreeBackgroundColorPnl.setEnabled(true);
					objectTreeBackgroundLbl.setEnabled(true);
					statusBarBackgroundColorChk.setSelected(true);
					statusBarBackgroundColor = toolbarBackgroundColor;
					statusBarBackgroundColorBtn.setEnabled(true);
					statusBarBackgroundColorPnl.setBackground(toolbarBackgroundColor);
					statusBarBackgroundColorPnl.setEnabled(true);
					statusBarBackgroundLbl.setEnabled(true);
				}
			}
		});
	}

	private void addStatusBarColorComponents(GridBagConstraints gbc)
	{
		// Object Tree Color checkbox
		statusBarBackgroundColorChk.setSelected(_props.isOverrideStatusBarBackgroundColor());
		add(statusBarBackgroundColorChk, gbc);

		// Set object tree color button
		++gbc.gridx;
		add(statusBarBackgroundColorBtn, gbc);

		// Set object tree color panel
		++gbc.gridx;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		statusBarBackgroundColorChk.setSelected(_props.isOverrideStatusBarBackgroundColor());
		statusBarBackgroundColorBtn.setEnabled(_props.isOverrideObjectTreeBackgroundColor());
		statusBarBackgroundColorPnl.add(statusBarBackgroundLbl);
		statusBarBackgroundColorPnl.setEnabled(_props.isOverrideStatusBarBackgroundColor());
		statusBarBackgroundLbl.setEnabled(_props.isOverrideStatusBarBackgroundColor());
		if (_props.isOverrideStatusBarBackgroundColor())
		{
			statusBarBackgroundColor = new Color(_props.getStatusBarBackgroundColorRgbValue());
			statusBarBackgroundColorPnl.setBackground(statusBarBackgroundColor);
		}
		add(statusBarBackgroundColorPnl, gbc);

		statusBarBackgroundColorChk.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (statusBarBackgroundColorChk.isSelected())
				{
					statusBarBackgroundColorBtn.setEnabled(true);
					statusBarBackgroundColorPnl.setEnabled(true);
					statusBarBackgroundLbl.setEnabled(true);
				}
				else
				{
					statusBarBackgroundColorBtn.setEnabled(false);
					statusBarBackgroundColorPnl.setEnabled(false);
					statusBarBackgroundLbl.setEnabled(false);
				}
			}
		});

		statusBarBackgroundColorBtn.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Color startColor = statusBarBackgroundColor == null ? Color.WHITE : statusBarBackgroundColor;
				Color newColor =
					JColorChooser.showDialog(null, "Select Background Color for Status Bar", startColor);
				if (newColor != null)
				{
					statusBarBackgroundColor = newColor;
					statusBarBackgroundColorPnl.setBackground(newColor);
					statusBarBackgroundColorPnl.validate();
				}
			}
		});
	}

	private void prepareNewRow(final GridBagConstraints gbc)
	{
		gbc.gridx = 0;
		++gbc.gridy;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.gridwidth = 1;
	}

	private void addObjectTreeColorComponents(final GridBagConstraints gbc)
	{
		// Object Tree Color checkbox
		objectTreeBackgroundColorChk.setSelected(_props.isOverrideObjectTreeBackgroundColor());
		add(objectTreeBackgroundColorChk, gbc);

		// Set object tree color button
		++gbc.gridx;
		add(objectTreeBackgroundColorBtn, gbc);

		// Set object tree color panel
		++gbc.gridx;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		objectTreeBackgroundColorChk.setSelected(_props.isOverrideObjectTreeBackgroundColor());
		objectTreeBackgroundColorBtn.setEnabled(_props.isOverrideObjectTreeBackgroundColor());
		objectTreeBackgroundColorPnl.add(objectTreeBackgroundLbl);
		objectTreeBackgroundColorPnl.setEnabled(_props.isOverrideObjectTreeBackgroundColor());
		objectTreeBackgroundLbl.setEnabled(_props.isOverrideObjectTreeBackgroundColor());
		if (_props.isOverrideObjectTreeBackgroundColor())
		{
			objectTreeBackgroundColor = new Color(_props.getObjectTreeBackgroundColorRgbValue());
			objectTreeBackgroundColorPnl.setBackground(objectTreeBackgroundColor);
		}
		add(objectTreeBackgroundColorPnl, gbc);

		objectTreeBackgroundColorChk.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (objectTreeBackgroundColorChk.isSelected())
				{
					objectTreeBackgroundColorBtn.setEnabled(true);
					objectTreeBackgroundColorPnl.setEnabled(true);
					objectTreeBackgroundLbl.setEnabled(true);
				}
				else
				{
					objectTreeBackgroundColorBtn.setEnabled(false);
					objectTreeBackgroundColorPnl.setEnabled(false);
					objectTreeBackgroundLbl.setEnabled(false);
				}
			}
		});

		objectTreeBackgroundColorBtn.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Color startColor = objectTreeBackgroundColor == null ? Color.WHITE : objectTreeBackgroundColor;
				Color newColor =
					JColorChooser.showDialog(null, "Select Background Color for Object Tree", startColor);
				if (newColor != null)
				{
					objectTreeBackgroundColor = newColor;
					objectTreeBackgroundColorPnl.setBackground(newColor);
					objectTreeBackgroundColorPnl.validate();
				}
			}
		});

	}

	private void addToolbarColorComponents(final GridBagConstraints gbc)
	{
		// Toolbar Color Checkbox
		toolbarBackgroundColorChk.setSelected(_props.isOverrideToolbarBackgroundColor());
		add(toolbarBackgroundColorChk, gbc);

		// Set toolbar color button
		++gbc.gridx;
		add(toolbarBackgroundColorBtn, gbc);

		// Set toolbar color panel
		++gbc.gridx;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		toolbarBackgroundColorChk.setSelected(_props.isOverrideToolbarBackgroundColor());
		toolbarBackgroundColorBtn.setEnabled(_props.isOverrideToolbarBackgroundColor());
		toolbarBackgroundColorPnl.add(toolBarBackgroundLbl);
		toolbarBackgroundColorPnl.setEnabled(_props.isOverrideToolbarBackgroundColor());
		toolBarBackgroundLbl.setEnabled(_props.isOverrideToolbarBackgroundColor());
		if (_props.isOverrideToolbarBackgroundColor())
		{
			toolbarBackgroundColor = new Color(_props.getToolbarBackgroundColorRgbValue());
			toolbarBackgroundColorPnl.setBackground(toolbarBackgroundColor);
		}
		add(toolbarBackgroundColorPnl, gbc);

		toolbarBackgroundColorChk.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (toolbarBackgroundColorChk.isSelected())
				{
					toolbarBackgroundColorBtn.setEnabled(true);
					toolbarBackgroundColorPnl.setEnabled(true);
					toolBarBackgroundLbl.setEnabled(true);
				}
				else
				{
					toolbarBackgroundColorBtn.setEnabled(false);
					toolbarBackgroundColorPnl.setEnabled(false);
					toolBarBackgroundLbl.setEnabled(false);
				}
			}
		});

		toolbarBackgroundColorBtn.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				Color startColor = toolbarBackgroundColor == null ? Color.WHITE : toolbarBackgroundColor;
				Color newColor =
					JColorChooser.showDialog(null, "Select Background Color for Toolbar", startColor);
				if (newColor != null)
				{
					toolbarBackgroundColor = newColor;
					toolbarBackgroundColorPnl.setBackground(newColor);
					toolbarBackgroundColorPnl.validate();
				}
			}
		});

	}

	private Box createInfoPanel()
	{
		final Box pnl = new Box(BoxLayout.X_AXIS);
		pnl.add(new MultipleLineLabel(i18n.INSTRUCTIONS));
		return pnl;
	}

}
