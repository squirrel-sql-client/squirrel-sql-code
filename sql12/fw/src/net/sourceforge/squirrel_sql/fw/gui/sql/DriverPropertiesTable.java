package net.sourceforge.squirrel_sql.fw.gui.sql;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.awt.Component;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;

class DriverPropertiesTable extends JTable
{
	DriverPropertiesTable()
	{
		super(new DriverPropertiesTableModel(new DriverPropertyInfo[0]));
		init();
	}

	DriverPropertiesTable(DriverPropertyInfo[] props)
	{
		super(new DriverPropertiesTableModel(props));
		init();
	}

	DriverPropertiesTable(Driver driver, String url)
		throws SQLException
	{
		this(driver, url, null);
	}

	DriverPropertiesTable(Driver driver, String url,
							SQLDriverProperty[] override)
		throws SQLException
	{
		super(new DriverPropertiesTableModel(driver, url, override));
		init();
	}

	DriverPropertiesTableModel getTypedModel()
	{
		return (DriverPropertiesTableModel)getModel();
	}

	private void init()
	{
		setColumnModel(new PropertiesTableColumnModel());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		getTableHeader().setResizingAllowed(true);
		getTableHeader().setReorderingAllowed(false);
	}

	private final class PropertiesTableColumnModel
								extends DefaultTableColumnModel
	{
		PropertiesTableColumnModel()
		{
			super();
			int idx = 0;

			TableColumn tc = new TableColumn(idx++);
			tc.setHeaderValue("Name");
			addColumn(tc);

			tc = new TableColumn(idx++);
			tc.setHeaderValue("Required");
			addColumn(tc);

			tc = new TableColumn(idx++, 75, null, new ValueCellEditor());
			tc.setHeaderValue("Value");
			addColumn(tc);

			tc = new TableColumn(idx++);
			tc.setHeaderValue("Description");
			addColumn(tc);
		}
	}

	private final class ValueCellEditor extends DefaultCellEditor
	{
		ValueCellEditor()
		{
			super(new JTextField());
			setClickCountToStart(1);
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
												boolean isSelected, int row,
												int col)
		{
			if (col != 2)
			{
				throw new IllegalStateException("Editor used for cell other than value");
			}

			DriverPropertyInfo prop = getTypedModel().getDriverPropertyInfo()[row];
			if (prop.choices != null && prop.choices.length > 0)
			{
				final JComboBox cmb = new JComboBox(prop.choices);
				if (prop.value != null)
				{
					cmb.setSelectedItem(prop.value);
				}
				return cmb;
			}
			return super.getTableCellEditorComponent(table, value, isSelected, row, col);
		}
	}
}

