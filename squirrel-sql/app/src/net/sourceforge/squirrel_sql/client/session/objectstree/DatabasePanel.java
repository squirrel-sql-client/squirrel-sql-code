package net.sourceforge.squirrel_sql.client.session.objectstree;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.gui.SquirrelTabbedPane;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.DataTypesTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.DateTimeFunctionsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.IDatabasePanelTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.KeywordsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.MetaDataTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.NumericFunctionsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.StringFunctionsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.databasepanel.SystemFunctionsTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class DatabasePanel extends SquirrelTabbedPane {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(DatabasePanel.class);

	/** Current session. */
	private ISession _session;

	/**
	 * Collection of <TT>IDatabasePanelTab</TT> objects displayed in
	 * this tabbed panel.
	 */
	private List _tabs = new ArrayList();

	/** Listens to changes in session properties. */
	private MyPropertiesListener _propsListener;

	/**
	 * ctor specifying the current session.
	 *
	 * @param	session		Current session.
	 *
	 * @throws	IllegalArgumentException	If a <TT>null</TT>
											<TT>ISession</TT> passed.
	 */
	public DatabasePanel(ISession session) {
		super(getPreferences(session));

		_session = session;

		createUserInterface();

		// Refresh the currently selected tab.
		((IDatabasePanelTab)_tabs.get(getSelectedIndex())).select();
	}

	/**
	 * Add a tab to this panel. If a tab with this title already exists it is
	 * removed from the tabbed pane and the passed tab inserted in its
	 * place. New tabs are inserted at the end.
	 *
	 * @param	tab	 The tab to be added.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IDatabasePanelTab</TT> passed.
	 */
	public void addDatabasePanelTab(IDatabasePanelTab tab) {
		if (tab == null) {
			throw new IllegalArgumentException("Null IDatabasePanelTab passed");
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
		addDatabasePanelTab(new NumericFunctionsTab());
		addDatabasePanelTab(new StringFunctionsTab());
		addDatabasePanelTab(new SystemFunctionsTab());
		addDatabasePanelTab(new DateTimeFunctionsTab());
		addDatabasePanelTab(new KeywordsTab());

		_propsListener = new MyPropertiesListener();
		_session.getProperties().addPropertyChangeListener(_propsListener);

		addChangeListener(new TabbedPaneListener());
	}

	private static SquirrelPreferences getPreferences(ISession session) {
		if (session == null) {
			throw new IllegalArgumentException("ISession == null");
		}
		return session.getApplication().getSquirrelPreferences();
	}

	private class MyPropertiesListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			DatabasePanel.this.propertiesHaveChanged(evt.getPropertyName());
		}
	}

	private class TabbedPaneListener implements ChangeListener {
		public void stateChanged(ChangeEvent evt) {
			Object src = evt.getSource();
			if (src instanceof SquirrelTabbedPane) {
				int idx = ((SquirrelTabbedPane)src).getSelectedIndex();
				if (idx != -1) {
					((IDatabasePanelTab)_tabs.get(getSelectedIndex())).select();
				}
			}
		}
	}
}
