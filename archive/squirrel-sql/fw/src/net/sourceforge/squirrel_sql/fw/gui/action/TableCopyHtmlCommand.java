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

public class TableCopyHtmlCommand implements ICommand
{
//	private final static String NULL_CELL = "<null>";

	private JTable _table;

	public TableCopyHtmlCommand(JTable table)
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
			buf.append("<table border=1><tr BGCOLOR=\"#CCCCFF\">");
			for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
			{
				buf.append("<th>");
				buf.append(model.getColumnName(selCols[colIdx]));
				buf.append("</th>");
			}
			buf.append("</tr>\n");
			for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx)
			{
				buf.append("<tr>");
				for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx)
				{
					Object cellObj =
						_table.getValueAt(selRows[rowIdx], selCols[colIdx]);
					buf.append("<td>");
					if (cellObj == null)
						buf.append("&nbsp;");
					else if (cellObj instanceof String)
					{
						String tmp = (String) cellObj;
						if (tmp.trim().equals(""))
							buf.append("&nbsp;");
						else
						{
							for (int i = 0; i < tmp.length(); i++)
							{
								switch (tmp.charAt(i))
								{
									case '<' :
										buf.append("&lt;");
										break;
									case '>' :
										buf.append("&gt;");
										break;
									case '&' :
										buf.append("&amp;");
										break;
									case '"' :
										buf.append("&quot;");
										break;
									default :
										buf.append(tmp.charAt(i));
								}
							}
						}
					}
					else
						buf.append(cellObj);
					buf.append("</td>");
				}
				buf.append("</tr>\n");
			}
			buf.append("</table>");
			StringSelection ss = new StringSelection(buf.toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
				ss,
				ss);
		}
	}
}
