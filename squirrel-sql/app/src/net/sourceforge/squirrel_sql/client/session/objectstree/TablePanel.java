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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ColumnPriviligesTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ColumnsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ContentsTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ExportedKeysTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ImportedKeysTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.IndexesTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.ITablePanelTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.PrimaryKeyTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.RowIDTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.TableInfoTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.TablePriviligesTab;
import net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel.VersionColumnsTab;

public class TablePanel extends JTabbedPane {
	/** Current session. */
	private ISession _session;

	/**
	 * Describes the DB table urrently selected. If <TT>null</TT>
	 * then no table is selected.
	 */
	private ITableInfo _ti;

	/**
	 * Collection of <TT>ITablePanelTab</TT> objects displayed in
	 * this tabbed panel.
	 */
	private List _tabs = new ArrayList();

	/** Listens to changes in <CODE>_props</CODE>. */
	private MyPropertiesListener _propsListener;

	/**
	 * Ctor specifying the session.
	 *
	 * @param   session	 The current session.
	 *
	 * @throws  IllegalArgumentException
	 *		  Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	public TablePanel(ISession session) throws IllegalArgumentException {
		super();
		if (session == null) {
			throw new IllegalArgumentException("null ISession passed");
		}
		_session = session;
		createUserInterface();
	}

	/**
	 * Set the <TT>ITableInfo</TT> object that specifies the table that
	 * is to have its information displayed.
	 *
	 * @param   value   <TT>ITableInfo</TT> object that specifies the currently
	 *				  selected table. This can be <TT>null</TT>.
	 */
	public synchronized void setTableInfo(ITableInfo value) {
		_ti = value;
		for (Iterator it = _tabs.iterator(); it.hasNext();) {
			((ITablePanelTab)it.next()).setTableInfo(value);
		}

		// Refresh the currently selected tab.
		((ITablePanelTab)_tabs.get(getSelectedIndex())).select();
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
	public void addTablePanelTab(ITablePanelTab tab) throws IllegalArgumentException {
		if (tab == null) {
			throw new IllegalArgumentException("Null ITablePanelTab passed");
		}
		tab.setSession(_session);
		tab.setTableInfo(_ti);
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

	private void propertiesHaveChanged(PropertyChangeEvent evt) {
		/*
		String propName = evt.getPropertyName();
		if (propName.equals(SessionProperties.IPropertyNames.CONTENTS_OUTPUT_CLASS_NAME)) {
			MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.CONTENTS_TAB_TITLE);
			addContentsViewerTab(viewer);
			viewer.load();
		} else if (propName.equals(SessionProperties.IPropertyNames.TABLE_OUTPUT_CLASS_NAME)) {
			MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.TABLE_TAB_TITLE);
			addTableInfoViewerTab(viewer);
			viewer.load();
		} else if (propName.equals(SessionProperties.IPropertyNames.COLUMNS_OUTPUT_CLASS_NAME)) {
			MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.COL_TAB_TITLE);
			addColumnsViewerTab(viewer);
			viewer.load();
		} else if (propName.equals(SessionProperties.IPropertyNames.PRIM_KEY_OUTPUT_CLASS_NAME)) {
			MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.PRIMARY_KEY_TITLE);
			addPrimaryKeyViewerTab(viewer);
			viewer.load();
		} else if (propName.equals(SessionProperties.IPropertyNames.EXP_KEYS_OUTPUT_CLASS_NAME)) {
			MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.EXP_KEY_TAB_TITLE);
			addExportedKeysViewerTab(viewer);
			viewer.load();
		} else if (propName.equals(SessionProperties.IPropertyNames.IMP_KEYS_OUTPUT_CLASS_NAME)) {
			MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.IMP_KEY_TAB_TITLE);
			addImportedKeysViewerTab(viewer);
			viewer.load();
		} else if (propName.equals(SessionProperties.IPropertyNames.INDEXES_OUTPUT_CLASS_NAME)) {
			MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.IDX_TAB_TITLE);
			addIndexesViewerTab(viewer);
			viewer.load();
		} else if (propName.equals(SessionProperties.IPropertyNames.PRIVILIGES_OUTPUT_CLASS_NAME)) {
			MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.PRIV_TAB_TITLE);
			addTablePriviligesViewerTab(viewer);
			viewer.load();
		} else if (propName.equals(SessionProperties.IPropertyNames.COLUMN_PRIVILIGES_OUTPUT_CLASS_NAME)) {
			MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.COLPRIV_TAB_TITLE);
			addColumnPriviligesViewerTab(viewer);
			viewer.load();
		} else if (propName.equals(SessionProperties.IPropertyNames.ROWID_OUTPUT_CLASS_NAME)) {
			MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.ROWID_TAB_TITLE);
			addRowIdViewerTab(viewer);
			viewer.load();
		} else if (propName.equals(SessionProperties.IPropertyNames.VERSIONS_OUTPUT_CLASS_NAME)) {
			MyBaseViewer viewer = (MyBaseViewer)_viewersMap.get(i18n.VERS_TAB_TITLE);
			addVersionColumnsViewerTab(viewer);
			viewer.load();
		}
		*/
	}

	private void createUserInterface() {
		addTablePanelTab(new TableInfoTab());
		addTablePanelTab(new ContentsTab());
		addTablePanelTab(new ColumnsTab());
		addTablePanelTab(new PrimaryKeyTab());
		addTablePanelTab(new ExportedKeysTab());
		addTablePanelTab(new ImportedKeysTab());
		addTablePanelTab(new IndexesTab());
		addTablePanelTab(new TablePriviligesTab());
		addTablePanelTab(new ColumnPriviligesTab());
		addTablePanelTab(new RowIDTab());
		addTablePanelTab(new VersionColumnsTab());

		_propsListener = new MyPropertiesListener(/*this*/);
		_session.getProperties().addPropertyChangeListener(_propsListener);
		addChangeListener(new TabbedPaneListener());
	}

	private class MyPropertiesListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			TablePanel.this.propertiesHaveChanged(evt);
		}
	}

	private class TabbedPaneListener implements ChangeListener {
		public void stateChanged(ChangeEvent evt) {
			Object src = evt.getSource();
			if (src instanceof JTabbedPane) {
				int idx = ((JTabbedPane)src).getSelectedIndex();
				if (idx != -1) {
					((ITablePanelTab)_tabs.get(getSelectedIndex())).select();
				}
			}
		}
	}
}
