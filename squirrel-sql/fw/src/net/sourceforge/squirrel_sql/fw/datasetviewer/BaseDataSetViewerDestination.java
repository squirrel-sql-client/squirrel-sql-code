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
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
/**
 * This provides base behaviour for implemtations of <TT>IDataSetViewerDestination</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public abstract class BaseDataSetViewerDestination implements IDataSetViewer {
	/** Specifies whether to show the column headings. */
	private boolean _showHeadings = true;

	/** Column definitions. */
	protected ColumnDisplayDefinition[] _colDefs = new ColumnDisplayDefinition[0];

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

	public synchronized void show(IDataSet ds) throws DataSetException {
		show(ds, null);
	}

	public synchronized void show(IDataSet ds, IMessageHandler msgHandler) throws DataSetException {
		clear();
		setColumnDefinitions(ds.getDataSetDefinition().getColumnDefinitions());
		final int colCount = ds.getColumnCount();
		while (ds.next(msgHandler)) {
			addRow(ds, colCount);
		}
		allRowsAdded();
		moveToTop();
	}

	protected void addRow(IDataSet ds, int columnCount) throws DataSetException {
		Object[] row = new Object[columnCount];
		for (int i = 0; i < columnCount; ++i) {
			row[i] = formatValue(ds.get(i));
		}
		addRow(row);
	}

	protected Object formatValue(Object object)
	{
		return object;
	}

	protected abstract void allRowsAdded();
	protected abstract void addRow(Object[] row);

	/**
	 * factory method for getting IDataSetViewer instances
	 * If no instance can be made then the default
	 * will be returned.
	 */
	public static IDataSetViewer getInstance(String sName)
	{
		IDataSetViewer dsv = null;
		try
		{
			Class cls = Class.forName(sName);
			dsv = (IDataSetViewer)cls.newInstance();
		}
		catch(Exception e)
		{
			// Log Exception????
		}
		if(dsv == null) return new DataSetViewerTablePanel();
		return dsv;
	}
}

