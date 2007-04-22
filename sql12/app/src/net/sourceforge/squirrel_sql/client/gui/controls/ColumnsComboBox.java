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
import java.awt.Component;
import java.sql.SQLException;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
/**
 * This combobox contains all the columns in an SQL table.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ColumnsComboBox extends JComboBox
{
	/**
	 * Ctor.
	 *
	 * @param	conn	Connection to the database containing the table.
	 * @param	ti		Pointer to the table we want to display columns for.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT>SQLConnection</TT> or <TT>ITableInfo</TT>
	 * 			passed in.
	 *
	 * @throws	SQLException	Thrown if an SQL error occurs.
	 */
	public ColumnsComboBox(ISQLConnection conn, ITableInfo ti)
		throws SQLException
	{
		super(getData(conn, ti));
		setRenderer(new CellRenderer());
	}

	public TableColumnInfo getSelectedColumn()
	{
		return (TableColumnInfo)getSelectedItem();
	}

	private static TableColumnInfo[] getData(ISQLConnection conn, ITableInfo ti)
		throws SQLException
	{
		if (conn == null)
		{
			throw new IllegalArgumentException("SQLConnection == null");
		}
		if (ti == null)
		{
			throw new IllegalArgumentException("ITableInfo == null");
		}

		return conn.getSQLMetaData().getColumnInfo(ti);
	}

	/**
	 * This renderer uses the unqualified column name as the text to display
	 * in the combo.
	 */
	private static final class CellRenderer extends BasicComboBoxRenderer
	{
		public Component getListCellRendererComponent(JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus)
		{
			setOpaque(true);
			setText(((TableColumnInfo)value).getColumnName());
			return this;
		}
	}
}
