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
import java.sql.Statement;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.datasetviewer.BaseDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableInfoDataSet;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

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

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(TableInfoTab.class);

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
	 * @see BaseObjectPanelTab#clear()
	 */
	public void clear()
	{
		((MyComponent)getComponent()).clear();
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
		private IDataSetViewer _viewer;

		MyComponent() {
			super(new BorderLayout());
		}

		void clear()
		{
			if(_rowCount != null) _rowCount.setText("");
			if(_viewer != null) _viewer.clear();
		}
		void load(ISession session, final ITableInfo ti) {
			try {
				// Lazily create the user interface.
				if (!_fullyCreated) {
					createUserInterface();
					_fullyCreated = true;
				}

				final long nbrRows;
				// Row count.
				Statement stmt = session.getSQLConnection().createStatement();
				try {
					ResultSet rs = stmt.executeQuery(
							"select count(*) from " + ti.getQualifiedName());
					if (rs.next()) {
						nbrRows = rs.getLong(1);
					}
					else
					{
						nbrRows = 0;
					}
				} finally {
					stmt.close();
				}

				// Table information viewer.
				Runnable run = new Runnable()
				{
					public void run()
					{
						_rowCount.setText("" + nbrRows);
						_ds.setTableInfo(ti);
						try
						{
							_viewer.show(_ds);
						} catch(DataSetException dse)
						{
							_rowCount.setText("<error>");
							s_log.error("Error", dse);
						}
					}
				};
				SwingUtilities.invokeLater(run);

			} catch (Exception ex) {
				_rowCount.setText("<error>");
				s_log.error("Error", ex);
			}
		}

		private void createUserInterface() throws DataSetException {
			ISession session = getSession();

			// Panel displays the row count for the table.
			final JPanel pnl = new JPanel();
			pnl.add(new JLabel("Row count:"));
			pnl.add(_rowCount);

			// Panel displays table info.
			String destClassName = session.getProperties().getTableOutputClassName();
			_viewer = BaseDataSetViewerDestination.getInstance(destClassName);
			_ds = new TableInfoDataSet();
			Runnable run = new Runnable()
			{
				public void run()
				{
					add(pnl, BorderLayout.NORTH);
					add(new JScrollPane(_viewer.getComponent()), BorderLayout.CENTER);
				}
			};
			SwingUtilities.invokeLater(run);
		}
	}
}

