package net.sourceforge.squirrel_sql.fw.gui.action;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
import javax.swing.JTable;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class TableSelectAllCellsCommand implements ICommand
{
	private JTable _table;

	public TableSelectAllCellsCommand(JTable table)
	{
		super();
		if (table == null)
		{
			throw new IllegalArgumentException("JTable == null");
		}
		_table = table;
	}

	public void execute()
	{
		//	  _table.requestFocus();
		_table.setRowSelectionInterval(0, _table.getRowCount() - 1);
		_table.setColumnSelectionInterval(0, _table.getColumnCount() - 1);
	}
}
