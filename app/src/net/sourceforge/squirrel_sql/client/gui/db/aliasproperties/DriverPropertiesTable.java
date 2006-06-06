package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;
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
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

class DriverPropertiesTable extends JTable
								implements DriverPropertiesTableModel.IColumnIndexes
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DriverPropertiesTable.class);

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

			TableColumn tc = new TableColumn(IDX_NAME);
			tc.setHeaderValue(s_stringMgr.getString("DriverPropertiesTable.name"));
			addColumn(tc);

			tc = new TableColumn(IDX_SPECIFY);
			tc.setHeaderValue(s_stringMgr.getString("DriverPropertiesTable.specify"));
			addColumn(tc);

			tc = new TableColumn(IDX_VALUE, 75, null, new ValueCellEditor());
			tc.setHeaderValue(s_stringMgr.getString("DriverPropertiesTable.value"));
			addColumn(tc);

			tc = new TableColumn(IDX_REQUIRED);
			tc.setHeaderValue(s_stringMgr.getString("DriverPropertiesTable.required"));
			addColumn(tc);

			tc = new TableColumn(IDX_DESCRIPTION);
			tc.setHeaderValue(s_stringMgr.getString("DriverPropertiesTable.description"));
			addColumn(tc);
		}
	}

	private final class ValueCellEditor extends DefaultCellEditor
	{
		private final JTextField _textEditor = new JTextField();
		private final JComboBox _comboEditor = new JComboBox();
		private JComponent _currentEditor;

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
				_comboEditor.removeAllItems();
				for (int i = 0; i < prop.choices.length; ++i)
				{
					_comboEditor.addItem(prop.choices[i]);
				}
				if (sdp.getValue() != null)
				{
					_comboEditor.setSelectedItem(sdp.getValue());
				}
				_currentEditor = _comboEditor;
			}
			else
			{
				_textEditor.setText(sdp.getValue());
				_currentEditor = _textEditor;
			}
			return _currentEditor;
		}

		public Object getCellEditorValue()
		{
			if (_currentEditor == _comboEditor)
			{
				return _comboEditor.getSelectedItem();
			}
			return _textEditor.getText();
		}
	}
}
