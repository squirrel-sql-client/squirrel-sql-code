package net.sourceforge.squirrel_sql.fw.gui.action;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class TableCopyCommand implements ICommand
{
	private final static String NULL_CELL = "<null>";

	private JTable _table;

	public TableCopyCommand(JTable table)
	{
		super();
		if (table == null)
		{
			throw new IllegalArgumentException("Null JTable passed");
		}
		_table = table;
	}

	public void execute()
	{
		int nbrSelRows = _table.getSelectedRowCount();
		int nbrSelCols = _table.getSelectedColumnCount();
		int[] selRows = _table.getSelectedRows();
		int[] selCols = _table.getSelectedColumns();
		if (selRows.length != 0 && selCols.length != 0)
		{
			TableModel model = _table.getModel();
			StringBuffer buf = new StringBuffer();
			if (nbrSelCols > 1 && nbrSelRows > 1)
			{
				for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
				{
					buf.append(model.getColumnName(selCols[colIdx]));
					if (colIdx < nbrSelCols - 1)
					{
						buf.append('\t');
					}
				}
				buf.append('\n');
			}
			for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
			{
				for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
				{
					Object cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);
					buf.append(cellObj != null ? cellObj : NULL_CELL);
					if (nbrSelCols > 1 && colIdx < nbrSelCols - 1)
					{
						buf.append('\t');
					}
				}
				if (nbrSelRows > 1)
				{
					buf.append('\n');
				}
			}
			StringSelection ss = new StringSelection(buf.toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
		}
	}
}
