package net.sourceforge.squirrel_sql.fw.gui.sql;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.sql.DriverPropertyInfo;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;

class DriverPropertiesTable extends JTable
								implements DriverPropertiesTableModel.IColumnIndexes
{
//	DriverPropertiesTable()
//	{
//		super(new DriverPropertiesTableModel(new DriverPropertyInfo[0]));
//		init();
//	}

	DriverPropertiesTable(SQLDriverPropertyCollection props)
	{
		super(new DriverPropertiesTableModel(props));
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

			TableColumn tc = new TableColumn(IDX_SPECIFY, 75, null, new SpecifiedCellEditor());
			tc.setHeaderValue("Specify");
			addColumn(tc);

			tc = new TableColumn(IDX_NAME);
			tc.setHeaderValue("Name");
			addColumn(tc);

			tc = new TableColumn(IDX_REQUIRED);
			tc.setHeaderValue("Required");
			addColumn(tc);

			tc = new TableColumn(IDX_VALUE, 75, null, new ValueCellEditor());
			tc.setHeaderValue("Value");
			addColumn(tc);

			tc = new TableColumn(IDX_DESCRIPTION);
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
			if (col != IDX_VALUE)
			{
				throw new IllegalStateException("Editor used for cell other than value");
			}

			SQLDriverPropertyCollection coll = getTypedModel().getSQLDriverProperties();
			SQLDriverProperty sdp = coll.getDriverProperty(row);
			DriverPropertyInfo prop = sdp.getDriverPropertyInfo();
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

	private final class SpecifiedCellEditor extends DefaultCellEditor
	{
		SpecifiedCellEditor()
		{
			super(new JTextField());
			setClickCountToStart(1);
		}

		public Component getTableCellEditorComponent(JTable table, Object value,
												boolean isSelected, int row,
												int col)
		{
			if (col != IDX_SPECIFY)
			{
				throw new IllegalStateException("Editor used for cell other than specify");
			}

			SQLDriverPropertyCollection coll = getTypedModel().getSQLDriverProperties();
			SQLDriverProperty sdp = coll.getDriverProperty(row);
			DriverPropertyInfo prop = sdp.getDriverPropertyInfo();
			final JComboBox cmb = new JComboBox(new Object[] {Boolean.TRUE, Boolean.FALSE});
			cmb.setSelectedItem(sdp != null ? new Boolean(sdp.isSpecified()) : Boolean.FALSE);
			return cmb;
		}
	}
}

