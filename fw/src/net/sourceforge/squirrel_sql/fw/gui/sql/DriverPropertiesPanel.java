package net.sourceforge.squirrel_sql.fw.gui.sql;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
/**
 * This panel allows the user to review and maintain
 * the properties for a JDBC driver.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DriverPropertiesPanel extends JPanel
{
	private interface i18n
	{
		String INSTRUCTIONS = "For every driver property that you want to " +
								"specify check the \"Specify\" checkbox " +
								"and enter its value in the \"Value\" column. " +
								"Normally you won't use the \"user\" and " +
								"\"password\" properties as these will be setup " +
								"from the \"user\" and \"password\" entered " +
								"in the connection dialog.";
	}

	/** JTable containing the properties. */
	private DriverPropertiesTable _tbl;

	/**
	 * Display the description for the currently selected property in this
	 * control.
	 */
	private final MultipleLineLabel _descriptionLbl = new MultipleLineLabel();

	public DriverPropertiesPanel(SQLDriverPropertyCollection props)
	{
		super(new GridBagLayout());
		if (props == null)
		{
			throw new IllegalArgumentException("SQLDriverPropertyCollection == null");
		}

		createUserInterface(props);
	}

	/**
	 * Retrieve the database properties.
	 *
	 * @return		the database properties.
	 */
	public SQLDriverPropertyCollection getSQLDriverProperties()
	{
		return _tbl.getTypedModel().getSQLDriverProperties();
	}

	private void createUserInterface(SQLDriverPropertyCollection props)
	{
		_tbl = new DriverPropertiesTable(props);

		final GridBagConstraints gbc = new GridBagConstraints();

		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(2, 2, 2, 2);
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 1.0;

		gbc.gridx = gbc.gridy = 0;
		gbc.weighty = 1.0;
		JScrollPane sp = new JScrollPane(_tbl);
		add(sp, gbc);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 0.0;
		++gbc.gridy;
		add(createInfoPanel(), gbc);

		_tbl.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent evt)
			{
				updateDescription(_tbl.getSelectedRow());
			}
		});

		if (_tbl.getRowCount() > 0)
		{
			_tbl.setRowSelectionInterval(0, 0);
		}
	}

	private void updateDescription(int idx)
	{
		if (idx != -1)
		{
			String desc = (String)_tbl.getValueAt(idx, DriverPropertiesTableModel.IColumnIndexes.IDX_DESCRIPTION);
			_descriptionLbl.setText(desc);
		}
		else
		{
			_descriptionLbl.setText(" ");
		}
	}

	private Box createInfoPanel()
	{
		final Box pnl = Box.createVerticalBox();
		pnl.add(new JSeparator());
		pnl.add(_descriptionLbl);
		pnl.add(new JSeparator());
		pnl.add(new MultipleLineLabel(i18n.INSTRUCTIONS));
		
		return pnl;
	}
}

