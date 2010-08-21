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
import java.sql.DriverPropertyInfo;

import javax.swing.table.AbstractTableModel;

import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

class DriverPropertiesTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = 1L;

    /** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DriverPropertiesTableModel.class);

	interface IColumnIndexes
	{
		int IDX_NAME = 0;
		int IDX_SPECIFY = 1;
		int IDX_VALUE = 2;
		int IDX_REQUIRED = 3;
		int IDX_DESCRIPTION = 4;
	}

	/** Number of columns in model. */
	private static final int COLUMN_COUNT = 5;

	/** Logger for this class. */
	private static final ILogger s_log =
		LoggerController.createLogger(DriverPropertiesTableModel.class);

	transient private SQLDriverPropertyCollection _props = new SQLDriverPropertyCollection();

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
			case IColumnIndexes.IDX_NAME:
				return sdp.getName();

			case IColumnIndexes.IDX_SPECIFY:
				return Boolean.valueOf(sdp.isSpecified());

			case IColumnIndexes.IDX_VALUE:
				return sdp.getValue();

			case IColumnIndexes.IDX_REQUIRED:
			{
				// Use valueof when min supported JDK is 1.4
				//return Boolean.valueOf(_props[row].required);
				DriverPropertyInfo dpi = sdp.getDriverPropertyInfo();
				if (dpi != null)
				{
					return Boolean.valueOf(dpi.required);
				}
				return Boolean.FALSE;
			}

			case IColumnIndexes.IDX_DESCRIPTION:
			{
				DriverPropertyInfo dpi = sdp.getDriverPropertyInfo();
				if (dpi != null)
				{
					return dpi.description;
				}
				return s_stringMgr.getString("DriverPropertiesTableModel.unknown");
			}

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

	public Class<?> getColumnClass(int col)
	{
		switch (col)
		{
			case IColumnIndexes.IDX_NAME:
				return String.class;
			case IColumnIndexes.IDX_SPECIFY:
				return Boolean.class;
			case IColumnIndexes.IDX_VALUE:
				return String.class;
			case IColumnIndexes.IDX_REQUIRED:
//				return Boolean.class;	// Don't show checkbox for
				return Object.class;	// output only field.
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

	public void setValueAt(Object value, int row, int col)
	{
		if (col == IColumnIndexes.IDX_VALUE)
		{
			final SQLDriverProperty sdp = _props.getDriverProperty(row);
			sdp.setValue(value.toString());
		}
		else if (col == IColumnIndexes.IDX_SPECIFY)
		{
			final SQLDriverProperty sdp = _props.getDriverProperty(row);
			Boolean bool = Boolean.valueOf(value.toString());
			sdp.setIsSpecified(bool.booleanValue());
		}
		else
		{
			throw new IllegalStateException("Can only edit value/specify column. Trying to edit " + col);
		}
	}

	/**
	 * Adds a row to the driver properties table with the specified name, value and description.
	 * 
	 * @param name the name of the driver property.
	 * @param value the value of the driver property.
	 * @param description a description of the driver property.
	 */
	public void addRow(String name, String value, String description) {
		DriverPropertyInfo propInfo = new DriverPropertyInfo(name, value);
		propInfo.description = description;
		SQLDriverProperty newProp = new SQLDriverProperty(propInfo); 
		_props.addDriverProperty(newProp);
		fireTableDataChanged();
	}
	
	/**
	 * Removes the row which contains the specified name value in it's property name column.
	 * 
	 * @param name the name of the driver property.
	 */
	public void removeRow(String name) {
		_props.removeDriverProperty(name);
		fireTableDataChanged();
	}
	
	SQLDriverPropertyCollection getSQLDriverProperties()
	{
		return _props;
	}

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

