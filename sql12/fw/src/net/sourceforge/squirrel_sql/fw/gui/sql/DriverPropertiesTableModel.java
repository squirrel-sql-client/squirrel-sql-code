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
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

class DriverPropertiesTableModel extends AbstractTableModel
{
	interface IColumnIndexes
	{
		int NAME = 0;
		int REQUIRED = 1;
		int VALUE = 2;
		int DESCRIPTION = 3;
	}

	/** Number of columns in model. */
	private final int COLUMN_COUNT = 4;
	
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(DriverPropertiesTableModel.class);

	private DriverPropertyInfo[] _props = new DriverPropertyInfo[0];

	DriverPropertiesTableModel(DriverPropertyInfo[] props)
	{
		super();
		load(props);
	}

	DriverPropertiesTableModel(Driver driver, String url)
		throws SQLException
	{
		load(driver, url);
	}

	public Object getValueAt(int row, int col)
	{
		switch (col)
		{
			case IColumnIndexes.NAME:
				return _props[row].name;
			case IColumnIndexes.REQUIRED:
				return Boolean.valueOf(_props[row].required);
			case IColumnIndexes.VALUE:
				return _props[row].value;
			case IColumnIndexes.DESCRIPTION:
				return _props[row].description;
			default:
				s_log.error("Invalid column index: " + col);
				return "???????";
		}
	}

	public int getRowCount()
	{
		return _props.length;
	}

	public int getColumnCount()
	{
		return COLUMN_COUNT;
	}

	public Class getColumnClass(int col)
	{
		switch (col)
		{
			case IColumnIndexes.NAME:
				return String.class;
			case IColumnIndexes.REQUIRED:
				return Boolean.class;
			case IColumnIndexes.VALUE:
				return String.class;
			case IColumnIndexes.DESCRIPTION:
				return String.class;
			default:
				s_log.error("Invalid column index: " + col);
				return Object.class;
		}
	}

	public boolean isCellEditable(int row, int col)
	{
		return col == IColumnIndexes.VALUE;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		if (columnIndex != IColumnIndexes.VALUE)
		{
			throw new IllegalStateException("Can only edit value column. Trying to edit " + columnIndex);
		}

		_props[rowIndex].value = aValue.toString();
	}

	DriverPropertyInfo[] getDriverPropertyInfo()
	{
		return _props;
	}

	final void load(Driver driver, String url)
		throws SQLException
	{
		load(driver.getPropertyInfo(url, new Properties()));
	}

	final void load(DriverPropertyInfo[] props)
	{
		if (props == null)
		{
			throw new IllegalArgumentException("DriverPropertyInfo[] == null");
		}

		final int origSize = getRowCount();
		if (origSize > 0)
		{
			fireTableRowsDeleted(0, origSize - 1);
		}

		_props = props;
		if (_props.length > 0)
		{
			fireTableRowsInserted(0, _props.length - 1);
		}
	}
}

