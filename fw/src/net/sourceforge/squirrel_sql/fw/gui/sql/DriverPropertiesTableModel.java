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
import javax.swing.table.AbstractTableModel;

import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

class DriverPropertiesTableModel extends AbstractTableModel
{
	interface IColumnIndexes
	{
		int IDX_SPECIFY = 0;
		int IDX_NAME = 1;
		int IDX_REQUIRED = 2;
		int IDX_VALUE = 3;
		int IDX_DESCRIPTION = 4;
	}

	/** Number of columns in model. */
	private final int COLUMN_COUNT = 5;
	
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(DriverPropertiesTableModel.class);

	private SQLDriverPropertyCollection _props = new SQLDriverPropertyCollection();
//	
//	DriverPropertiesTableModel(DriverPropertyInfo[] props)
//	{
//		super();
//		load(props, null);
//	}

//	DriverPropertiesTableModel(Driver driver, String url)
//		throws SQLException
//	{
//		this(driver, url, null);
//	}

	DriverPropertiesTableModel(SQLDriverPropertyCollection props)
	{
		super();
		if (props == null)
		{
			throw new IllegalArgumentException("SQLDriverPropertyCollection[] == null");
		}

		load(props);
	}

	public Object getValueAt(int row, int col)
	{
		final SQLDriverProperty sdp = _props.getDriverProperty(row);
		switch (col)
		{
			case IColumnIndexes.IDX_SPECIFY:
				// Use valueof when min supported JDK is 1.4
				return new Boolean(sdp.isSpecified());

			case IColumnIndexes.IDX_NAME:
				return sdp.getDriverPropertyInfo().name;

			case IColumnIndexes.IDX_REQUIRED:
				// Use valueof when min supported JDK is 1.4
				//return Boolean.valueOf(_props[row].required);
				return new Boolean(sdp.getDriverPropertyInfo().required);
			case IColumnIndexes.IDX_VALUE:
				return sdp.getDriverPropertyInfo().value;
			case IColumnIndexes.IDX_DESCRIPTION:
				return sdp.getDriverPropertyInfo().description;
			default:
				s_log.error("Invalid column index: " + col);
				return "???????";
		}
	}

	public int getRowCount()
	{
		return _props.size();
	}

	public int getColumnCount()
	{
		return COLUMN_COUNT;
	}

	public Class getColumnClass(int col)
	{
		switch (col)
		{
			case IColumnIndexes.IDX_SPECIFY:
				return Boolean.class;
			case IColumnIndexes.IDX_NAME:
				return String.class;
			case IColumnIndexes.IDX_REQUIRED:
				return Boolean.class;
			case IColumnIndexes.IDX_VALUE:
				return String.class;
			case IColumnIndexes.IDX_DESCRIPTION:
				return String.class;
			default:
				s_log.error("Invalid column index: " + col);
				return Object.class;
		}
	}

	public boolean isCellEditable(int row, int col)
	{
		return col == IColumnIndexes.IDX_SPECIFY || col == IColumnIndexes.IDX_VALUE;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if (columnIndex == IColumnIndexes.IDX_VALUE)
		{
			final SQLDriverProperty sdp = _props.getDriverProperty(rowIndex);
			sdp.setValue(aValue.toString());
		}
		else if (columnIndex == IColumnIndexes.IDX_SPECIFY)
		{
			final SQLDriverProperty sdp = _props.getDriverProperty(rowIndex);
			Boolean bool = Boolean.valueOf(aValue.toString());
			sdp.setIsSpecified(bool.booleanValue());
		}
		else
		{
			throw new IllegalStateException("Can only edit value/specify column. Trying to edit " + columnIndex);
		}
	}

	SQLDriverPropertyCollection getSQLDriverProperties()
	{
		return _props;
	}

//	synchronized SQLDriverProperty getSQLDriverProperty(String key)
//	{
//		return (SQLDriverProperty)_override.get(key);
//	}

	private final void load(SQLDriverPropertyCollection props)
	{
		final int origSize = getRowCount();
		if (origSize > 0)
		{
			fireTableRowsDeleted(0, origSize - 1);
		}

		_props = props;
		final int newSize = getRowCount();
		if (newSize > 0)
		{
			fireTableRowsInserted(0, newSize - 1);
		}
	}
}

