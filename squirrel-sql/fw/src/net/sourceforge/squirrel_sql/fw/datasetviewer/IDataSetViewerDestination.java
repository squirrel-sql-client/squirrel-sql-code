package net.sourceforge.squirrel_sql.fw.datasetviewer;
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
import java.util.List;

public interface IDataSetViewerDestination {
	final static int MAX_COLUMN_WIDTH = 50;

	/**
	 * Clear the output.
	 */
	void clear();
	
	/**
	 * Specify the column headings to use.
	 * 
	 * @param	hdgs	Column headings to use.
	 */
	void setColumnDefinitions(ColumnDisplayDefinition[] hdgs);
	
	/**
	 * Specify whether to show the column headings.
	 * 
	 * @param	show	<TT>true</TT> if headibgs to be shown else <TT>false</TT>.
	 */
	void showHeadings(boolean show);

	/**
	 * Add a row.
	 * 
	 * @param	row		Array of objects specifying the row data.
	 */
	void addRow(Object[] row);

	/**
	 * Called once all rows have been added..
	 */
	void allRowsAdded();

	/**
	 * Indicates that the output display should scroll to the top.
	 */
	void moveToTop();
}
