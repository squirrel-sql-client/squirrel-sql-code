package net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel;
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
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.util.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class ResultSetPanel extends JScrollPane {
	private boolean _fullyCreated = false;
	private ResultSetDataSet _ds;
	private DataSetViewer _viewer;
	//private IDataSetViewerDestination _dest;

	public void load(ISession session, ResultSet rs, int[] cols,
						String destClassName) {
		try {
			// Lazily create the user interface.
			if (!_fullyCreated) {
				createUserInterface(session, destClassName);
				_fullyCreated = true;
			}

			try {
				_ds.setResultSet(rs, cols);
				_viewer.show(_ds);
			} finally {
				rs.close();
			}

		} catch (Exception ex) {
			Logger log = session.getApplication().getLogger();
			log.showMessage(Logger.ILogTypes.ERROR, ex);
		}
	}

	/**
	 * Create a viewer panel for an <T>IDataSet</TT>. If the passed class
	 * name is invalid return a <TT>import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel</TT>.
	 *
	 * @param	destClassName	Class Name of panel to be created. This class
	 *							must have a default constructor.
	 *
	 * @return	The newly created panel.
	 */
	/*
	protected IDataSetViewerDestination createDestination(String destClassName) {
		IDataSetViewerDestination dest = null;
		try {
			Class destClass = Class.forName(destClassName);
			if (IDataSetViewerDestination.class.isAssignableFrom(destClass) &&
					Component.class.isAssignableFrom(destClass)) {
				dest = (IDataSetViewerDestination)destClass.newInstance();
			}

		} catch (Exception ignore) {
		}
		if (dest == null) {
			dest = new DataSetViewerTablePanel();
		}
		return dest;
	}
*/

	private void createUserInterface(ISession session, String destClassName)
			throws DataSetException {
		_viewer = new DataSetViewer();
//		_dest = createDestination(destClassName);
//		_viewer.setDestination(_dest);
		_viewer.setDestination(destClassName);
		_ds = new ResultSetDataSet();
//		setViewportView((Component)_dest);
		setViewportView(_viewer.getDestinationComponent());
	}
}

