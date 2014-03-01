/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.gui.action;

import javax.swing.JTable;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

/**
 * Abstract base class for commands, which depends on a {@link JTable}
 * @author Stefan Willinger
 *
 */
public abstract class AbstractTableDependedCommand implements ICommand{
	private JTable table;

	public AbstractTableDependedCommand(JTable table)
	{
		super();
		setTable(table);
		
	}

	/**
	 * @return the _table
	 */
	public JTable getTable() {
		return this.table;
	}

	/**
	 * @param _table the _table to set
	 */
	public void setTable(JTable table) {
		if (table == null)
		{
			throw new IllegalArgumentException("JTable == null");
		}
		this.table = table;
	}
}
