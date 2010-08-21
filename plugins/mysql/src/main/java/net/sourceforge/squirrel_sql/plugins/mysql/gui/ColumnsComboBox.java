package net.sourceforge.squirrel_sql.plugins.mysql.gui;
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
import javax.swing.JComboBox;
/**
 * TODO: Get rid of this class.
 * This combobox contains all the columns in an SQL table.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ColumnsComboBox extends JComboBox
{
	private ColumnsComboBox()
	{
		super();
	}

//	public ColumnsComboBox(SQLConnection conn, ITableInfo ti)
//		throws SQLException
//	{
//		super(conn.getSQLMetaData().getColumnInfo(ti));
//		setRenderer(new CellRenderer());
//	}
//
//	/**
//	 * This renderer uses the unqualified column name as the text to display
//	 * in the combo.
//	 */
//	private static final class CellRenderer extends BasicComboBoxRenderer
//	{
//		CellRenderer()
//		{
//			super();
//			setOpaque(true);
//		}
//
//		public Component getListCellRendererComponent(JList list, Object value,
//						int index, boolean isSelected, boolean cellHasFocus)
//		{
//			setText(((TableColumnInfo)value).getColumnName());
//			return this;
//		}
//	}
}
