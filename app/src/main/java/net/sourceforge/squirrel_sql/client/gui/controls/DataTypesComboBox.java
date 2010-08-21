package net.sourceforge.squirrel_sql.client.gui.controls;
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
import java.sql.SQLException;

import javax.swing.JComboBox;

import net.sourceforge.squirrel_sql.fw.sql.DataTypeInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
/**
 * This combobox contains all the data types in a database.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DataTypesComboBox extends JComboBox
{
	/**
	 * Ctor.
	 *
	 * @param	conn	Connection to the database.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT>SQLConnection</TT> passed.
	 *
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public DataTypesComboBox(ISQLConnection conn)
		throws SQLException
	{
		super(getData(conn));
//		setRenderer(new CellRenderer());
	}

	public DataTypeInfo getDataTypeAt(int idx)
	{
		return (DataTypeInfo)getItemAt(idx);
	}

	private static DataTypeInfo[] getData(ISQLConnection conn)
		throws SQLException
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}

		return conn.getSQLMetaData().getDataTypes();
	}

	/**
	 * This renderer uses the unqualified column name as the text to display
	 * in the combo.
	 */
//	private static final class CellRenderer extends BasicComboBoxRenderer
//	{
//		public Component getListCellRendererComponent(JList list, Object value,
//						int index, boolean isSelected, boolean cellHasFocus)
//		{
//			setText(((TableColumnInfo)value).getColumnName());
//			return this;
//		}
//	}
}
