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
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class DriverPropertiesTableModel extends AbstractTableModel
{
	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(DriverPropertiesTableModel.class);

	private DriverPropertyInfo[] _props = new DriverPropertyInfo[0];

	public DriverPropertiesTableModel(DriverPropertyInfo[] props)
	{
		super();
		load(props);
	}

	public DriverPropertiesTableModel(Driver driver, String url)
		throws SQLException
	{
		load(driver, url);
	}

	public Object getValueAt(int row, int col)
	{
		switch (col)
		{
			case 0:	return _props[row].name;
			case 1:	return Boolean.valueOf(_props[row].required);
			case 2:	return _props[row].value;
			case 3:	return _props[row].description;

			default:	s_log.error("Invalid column index: " + col);
						return "???????";
				
		}
	}

	public int getRowCount()
	{
		return _props.length;
	}

	public int getColumnCount()
	{
		return 4;
	}

//		public String getColumnName(int col)
//		{
//			return _colDefs != null ? _colDefs[col].getLabel() : super.getColumnName(col);
//		}

	public final void load(Driver driver, String url)
		throws SQLException
	{
		load(driver.getPropertyInfo(url, new Properties()));
	}

	public final void load(DriverPropertyInfo[] props)
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

	public Class getColumnClass(int col)
	{
		switch (col)
		{
			case 0:	return String.class;
			case 1:	return Boolean.class;
			case 2:	return String.class;
			case 3:	return String.class;

			default:
				s_log.error("Invalid column index: " + col);
				return Object.class;
		}
	}

	public boolean isCellEditable(int row, int col)
	{
		return col == 2;
	}

	DriverPropertyInfo[] getDriverPropertyInfo()
	{
		return _props;
	}

//		private void createColumns()
//		{
//			addColumn("Name");
//			addColumn("Required");
//			addColumn("Value");
//			addColumn("Description");
//		}
}


