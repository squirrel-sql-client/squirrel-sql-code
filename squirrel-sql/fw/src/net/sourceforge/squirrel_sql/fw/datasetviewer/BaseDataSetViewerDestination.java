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
/**
 * This provides base behaviour for implemtations of <TT>IDataSetViewerDestination</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public abstract class BaseDataSetViewerDestination implements IDataSetViewerDestination {
	/** Specifies whether to show the column headings. */
	private boolean _showHeadings = true;

	/** Column definitions. */
	private ColumnDisplayDefinition[] _colDefs = new ColumnDisplayDefinition[0];

	/**
	 * Specify the column definitions to use.
	 * 
	 * @param	hdgs	Column definitions to use.
	 */
	public void setColumnDefinitions(ColumnDisplayDefinition[] colDefs) {
		_colDefs = colDefs != null ? colDefs : new ColumnDisplayDefinition[0];
	}
	
	/**
	 * Return the column definitions to use.
	 * 
	 * @return the column definitions to use.
	 */
	public ColumnDisplayDefinition[] getColumnDefinitions() {
		return _colDefs;
	}
	
	/**
	 * Specify whether to show the column headings.
	 * 
	 * @param	show	<TT>true</TT> if headibgs to be shown else <TT>false</TT>.
	 */
	public void showHeadings(boolean show) {
		_showHeadings = show;
	}

	/**
	 * Return whether to show the column headings.
	 * 
	 * @return whether to show the column headings.
	 */
	public boolean getShowHeadings() {
		return _showHeadings;
	}

	public void allRowsAdded() {
	}

	public void moveToTop() {
	}
}

