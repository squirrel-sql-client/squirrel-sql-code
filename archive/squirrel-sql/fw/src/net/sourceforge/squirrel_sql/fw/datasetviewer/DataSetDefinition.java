package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001 Colin Bell
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
public class DataSetDefinition {
	/**
	 * The collection of <TT>ColumnDisplayDefinition</TT> objects
	 * for this data set.
	 */
	private ColumnDisplayDefinition[] _columnDefs;

	/**
	 * Ctor.
	 *
	 * @param   columnDefs	The <TT>ColumnDisplayDefinition</TT>
	 *						objects that make up this data set.
	 */
	public DataSetDefinition(ColumnDisplayDefinition[] columnDefs) {
		super();
		_columnDefs = columnDefs != null ? columnDefs : new ColumnDisplayDefinition[0];
	}

	/**
	 * Return the <TT>ColumnDisplayDefinition</TT> objects
	 * that make up this data set.
	 *
	 * @return	<TT>ColumnDisplayDefinition</TT> objects
	 *			that make up this data set.
	 */
	public ColumnDisplayDefinition[] getColumnDefinitions() {
		return _columnDefs;
	}
}