package net.sourceforge.squirrel_sql.fw.gui.action;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import javax.swing.JTable;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class TableCopyCommand implements ICommand {
    private final static String NULL_CELL = "<null>";

    private JTable _table;

    public TableCopyCommand(JTable table) {
        super();
        if (table == null) {
            throw new IllegalArgumentException("Null JTable passed");
        }
        _table = table;
    }

    public void execute() {
        int nbrSelRows = _table.getSelectedRowCount();
        int nbrSelCols = _table.getSelectedColumnCount();
        int[] selRows = _table.getSelectedRows();
        int[] selCols = _table.getSelectedColumns();
        if (selRows.length != 0 && selCols.length != 0) {
            StringBuffer buf = new StringBuffer();
            for (int rowIdx = 0; rowIdx < nbrSelRows; ++rowIdx) {
                for (int colIdx = 0; colIdx < nbrSelCols; ++colIdx) {
                    Object cellObj = _table.getValueAt(selRows[rowIdx], selCols[colIdx]);
                    buf.append(cellObj != null ? cellObj : NULL_CELL);
                    if (colIdx < nbrSelCols - 1) {
                        buf.append('\t');
                    }
                }
                buf.append('\n');
            }
            StringSelection ss = new StringSelection(buf.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
        }
    }
}
