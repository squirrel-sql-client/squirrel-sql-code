package net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel;
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
import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfoDataSet;
import net.sourceforge.squirrel_sql.fw.util.Logger;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * This is the tab for table information.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class TableInfoTab extends BaseTablePanelTab {
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		String TITLE = "Info";
		String HINT = "Basic information";
	}

	/** Component to be displayed. */
	private MyComponent _comp;

	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle() {
		return i18n.TITLE;
	}

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint() {
		return i18n.HINT;
	}

	/**
	 * Return the component to be displayed in the panel.
	 *
	 * @return	The component to be displayed in the panel.
	 */
	public synchronized Component getComponent() {
		if (_comp == null) {
			_comp = new MyComponent();
		}
		return _comp;
	}

	/**
	 * Refresh the component displaying the <TT>ITableInfo</TT> object.
	 */
	public synchronized void refreshComponent() throws IllegalStateException {
		ISession session = getSession();
		if (session == null) {
			throw new IllegalStateException("Null ISession");
		}
		ITableInfo ti = getTableInfo();
		if ( ti == null) {
			throw new IllegalStateException("Null ITableInfo");
		}
		((MyComponent)getComponent()).load(session, ti);
	}

	/**
	 * Component for this tab.
	 */
	private class MyComponent extends JPanel {
		private boolean _fullyCreated = false;
		private JLabel _rowCount = new JLabel("");
		private TableInfoDataSet _ds;
		private DataSetViewer _viewer;
		private IDataSetViewerDestination _dest;

		MyComponent() {
			super(new BorderLayout());
		}

		void load(ISession session, ITableInfo ti) {
			try {
				// Lazily create the user interface.
				if (!_fullyCreated) {
					createUserInterface();
					_fullyCreated = true;
				}

				// Row count.
				Statement stmt = session.getSQLConnection().createStatement();
				try {
					ResultSet rs = stmt.executeQuery(
							"select count(*) from " + ti.getQualifiedName());
					long nbrRows = 0;
					if (rs.next()) {
						nbrRows = rs.getLong(1);
					}
					_rowCount.setText("" + nbrRows);
				} finally {
					stmt.close();
				}

				// Table information viewer.
				_ds.setTableInfo(ti);
				_viewer.show(_ds);

			} catch (Exception ex) {
				_rowCount.setText("<error>");
				Logger log = session.getApplication().getLogger();
				log.showMessage(Logger.ILogTypes.ERROR, ex);
			}
		}

		private void createUserInterface() throws DataSetException {
			ISession session = getSession();

			// Panel displays the row count for the table.
			JPanel pnl = new JPanel();
			pnl.add(new JLabel("Row count:"));
			pnl.add(_rowCount);
			add(pnl, BorderLayout.NORTH);

			// Panel displays table info.
			String destClassName = session.getProperties().getTableOutputClassName();
			_dest = createDestination(destClassName);
			_viewer = new DataSetViewer();
			_viewer.setDestination(_dest);
			_ds = new TableInfoDataSet();
			add(new JScrollPane((Component)_dest), BorderLayout.CENTER);
		}
	}
}

