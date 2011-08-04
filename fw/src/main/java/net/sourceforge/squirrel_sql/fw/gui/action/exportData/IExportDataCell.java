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
package net.sourceforge.squirrel_sql.fw.gui.action.exportData;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

/**
 * The data cell.
 * Each cell knows its coordinates (row and column).
 * The data cell may but must not have a {@link ColumnDisplayDefinition}. 
 * @author Stefan Willinger
 *
 */
public interface IExportDataCell {
	/**
	 * The value-object of the cell.
	 */
	Object getObject();
	/**
	 * The {@link ColumnDisplayDefinition} which is responsible for the object.
	 * This maybe null.
	 * @return the columnDisplayDefinition of this cell or null.
	 */
	ColumnDisplayDefinition getColumnDisplayDefinition();
	/**
	 * @return The index of the row.
	 */
	int getRowIndex();
	/**
	 * 
	 * @return The index of the column.
	 */
	int getColumnIndex();
}
