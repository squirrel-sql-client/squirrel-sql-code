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
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

public class DataSetViewer {

	private static IColumnRenderer s_dftColumnRenderer = new DefaultColumnRenderer();
	private static IDataSetViewerDestination s_nullResults = new NullDataSetViewerDestination();

	private boolean _showAllColumns = true;

	private IDataSetViewerDestination _dest;

	public DataSetViewer() {
		super();
		setDestination(s_nullResults);
	}

	public DataSetViewer(IDataSetViewerDestination dest) {
		super();
		setDestination(dest);
	}
	
	public IDataSetViewerDestination setDestination(String destClassName) throws DataSetException {
		IDataSetViewerDestination dest = null;
		try {
			Class destClass = Class.forName(destClassName);
//			if (IDataSetViewerDestination.class.isAssignableFrom(destClass) &&
//				Component.class.isAssignableFrom(destClass)) {
				dest = (IDataSetViewerDestination)destClass.newInstance();
//			}

		} catch (Exception ex) {
			throw new DataSetException(ex);
		}
		if (dest == null) {
			dest = new DataSetViewerTablePanel();
		}
		setDestination(dest);
		return _dest;
	}

	public void setDestination(IDataSetViewerDestination dest) {
		_dest = dest != null ? dest : s_nullResults;
	}

	public Component getDestinationComponent() {
		return _dest.getComponent();
	}

	public synchronized void show(IDataSet ds) throws DataSetException {
		show(ds, null);
	}

	public synchronized void show(IDataSet ds, IMessageHandler msgHandler) throws DataSetException {
		_dest.clear();
		_dest.setColumnDefinitions(ds.getDataSetDefinition().getColumnDefinitions());
		final int colCount = ds.getColumnCount();
		while (ds.next(msgHandler)) {
			addRow(ds, colCount);
		}
		_dest.allRowsAdded();
		_dest.moveToTop();
	}
	
	protected void clearDestination() {
		_dest.clear();
	}

	protected void addRow(IDataSet ds, int columnCount) throws DataSetException {
		Object[] row = new Object[columnCount];
		for (int i = 0; i < columnCount; ++i) {
			IColumnRenderer renderer = getColumnRenderer(i);
			if (renderer != null) {
				Object obj = ds.get(i);
				if (obj != null) {
					row[i] = renderer.renderObject(obj);
				} else {
					row[i] = renderer.renderNull();
				}
			}
		}
		addRow(row);
	}

	protected void addRow(Object[] row) {
		_dest.addRow(row);
	}

	private IColumnRenderer getColumnRenderer(int idx) {
		return s_dftColumnRenderer;
	}

	protected static class DefaultColumnRenderer implements IColumnRenderer {
		public Object renderObject(Object obj) {
			return obj.toString();
		}
		public Object renderNull() {
			return "<null>"; // i18n
		}

	}

	private static class NullDataSetViewerDestination
			extends BaseDataSetViewerDestination
			implements IDataSetViewerDestination {
		public void clear() {
		}

		public void addRow(Object[] row) {
		}

		public Component getComponent() {
			return null;
		}
	}

}