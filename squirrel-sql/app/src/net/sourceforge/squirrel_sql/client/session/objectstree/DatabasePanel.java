package net.sourceforge.squirrel_sql.client.session.objectstree;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.DataTypesTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.IDatabasePanelTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.MetaDataTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class DatabasePanel extends JTabbedPane {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(DatabasePanel.class);

	/** Current session. */
	private ISession _session;
	
	/**
	 * Collection of <TT>IDatabasePanelTab</TT> objects displayed in
	 * this tabbed panel.
	 */
	private List _tabs = new ArrayList();

	/** Listens to changes in <CODE>_props</CODE>. */
	private MyPropertiesListener _propsListener;

	/**
	 * ctor specifying the properties for this window.
	 */
	public DatabasePanel(ISession session) {
		super();
		_session = session;

		createUserInterface();
		propertiesHaveChanged(null);
		// Refresh the currently selected tab.
		((IDatabasePanelTab)_tabs.get(getSelectedIndex())).select();
	}

	/**
	 * Add a tab to this panel. If a tab with this title already exists it is
	 * removed from the tabbed pane and the passed tab inserted in its
	 * place. New tabs are inserted at the end.
	 *
	 * @param   tab	 The tab to be added.
	 *
	 * @throws  IllegalArgumentException
	 *		  Thrown if a <TT>null</TT> <TT>ITablePanelTab</TT> passed.
	 */
	public void addDatabasePanelTab(IDatabasePanelTab tab) throws IllegalArgumentException {
		if (tab == null) {
			throw new IllegalArgumentException("Null ITablePanelTab passed");
		}
		tab.setSession(_session);
		final String title = tab.getTitle();
		int idx = indexOfTab(title);
		if (idx != -1) {
			removeTabAt(idx);
			_tabs.set(idx, tab);
		} else {
			idx = getTabCount();
			_tabs.add(tab);
		}
		insertTab(title, null, tab.getComponent(), tab.getHint(), idx);
	}

	private void propertiesHaveChanged(String propName) {
		if (propName == null ||
				propName.equals(SessionProperties.IPropertyNames.META_DATA_OUTPUT_CLASS_NAME)) {
			addDatabasePanelTab(new MetaDataTab());
		}
		if (propName == null ||
				propName.equals(SessionProperties.IPropertyNames.DATA_TYPES_OUTPUT_CLASS_NAME)) {
			addDatabasePanelTab(new DataTypesTab());
		}
	}

	private void createUserInterface() {
		addDatabasePanelTab(new MetaDataTab());
		addDatabasePanelTab(new DataTypesTab());

		_propsListener = new MyPropertiesListener();
		_session.getProperties().addPropertyChangeListener(_propsListener);

		addChangeListener(new TabbedPaneListener());
	}

	private void addResultSetViewerTab(String title, String description,
												MyBaseViewer viewer,
												String destClassName) {
		try {
			viewer.setDestination(destClassName);
		} catch (Exception ex) {
			s_log.error("Error occured setting destination", ex);
			viewer.setDestination(new DataSetViewerTablePanel());
		}
		Component comp = viewer.getDestinationComponent();
		int idx = indexOfTab(title);
		if (idx != -1) {
			removeTabAt(idx);
			insertTab(title, null, new JScrollPane(comp), description, idx);
		} else {
			addTab(title, null, new JScrollPane(comp), description);
		}
	}

	private abstract class MyBaseViewer extends DataSetViewer {
		private boolean _hasBeenBuilt = false;

		abstract void loadImpl(SQLConnection conn) throws DataSetException,
										BaseSQLException, SQLException;

		void load(SQLConnection conn) {
			if (!_hasBeenBuilt) {
				try {
					clearDestination();
					loadImpl(conn);
				} catch (Exception ex) {
					IMessageHandler msgHandler = _session.getMessageHandler();
					msgHandler.showMessage("Error in: " + getClass().getName());
					msgHandler.showMessage(ex);
				}
				_hasBeenBuilt = true;
			}
		}

		public void setHasBeenBuilt(boolean value) {
			_hasBeenBuilt = value;
		}
	}

	private class MyPropertiesListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			DatabasePanel.this.propertiesHaveChanged(evt.getPropertyName());
		}
	}

	private class TabbedPaneListener implements ChangeListener {
		public void stateChanged(ChangeEvent evt) {
			Object src = evt.getSource();
			if (src instanceof JTabbedPane) {
				int idx = ((JTabbedPane)src).getSelectedIndex();
				if (idx != -1) {
					((IDatabasePanelTab)_tabs.get(getSelectedIndex())).select();
				}
			}
		}
	}
}
