package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.util.Iterator;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.gui.SquirrelTabbedPane;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
/**
 * This is the tabbed panel displayed when a node is selected in the
 * object tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ObjectTreeTabbedPane extends SquirrelTabbedPane
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(ObjectTreeTabbedPane.class);

	/** Current session. */
	private ISession _session;

	/** Listens to changes in session properties. */
	private SessionPropertiesListener _propsListener;

	/**
	 * Collection of <TT>IObjectTab</TT> objects displayed in
	 * this tabbed panel.
	 */
	private List _tabs = new ArrayList();

	ObjectTreeTabbedPane(ISession session)
	{
		super(session.getApplication().getSquirrelPreferences());

		_session = session;
		createUserInterface();
	}

	/**
	 * Component has been added to its parent. Refresh the currently selected
	 * tab.
	 */
	public void addNotify()
	{
		super.addNotify();

		_propsListener = new SessionPropertiesListener();
		_session.getProperties().addPropertyChangeListener(_propsListener);

		final int idx = getSelectedIndex();
		if (idx != -1)
		{
			((IObjectTab)_tabs.get(idx)).select();
		}
	}

	/**
	 * Component has been removed from its parent. get rid of all listeners.
	 */
	public void removeNotify()
	{
		if (_propsListener != null)
		{
			_session.getProperties().removePropertyChangeListener(_propsListener);
			_propsListener = null;
		}
		super.removeNotify();
	}

	void addObjectPanelTab(IObjectTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IObjectTab passed");
		}
		tab.setSession(_session);
		final String title = tab.getTitle();
		_tabs.add(tab);
		addTab(title, null, tab.getComponent(), tab.getHint());
	}

	void selectCurrentTab()
	{
		if (getParent() != null)
		{
			int idx = getSelectedIndex();
			if (idx != -1)
			{
				IObjectTab tab = (IObjectTab)_tabs.get(idx);
				if (tab != null)
				{
					tab.select();
				}
			}
		}
	}

	public void setDatabaseObjectInfo(IDatabaseObjectInfo dboInfo)
	{
		Iterator it = _tabs.iterator();
		while (it.hasNext())
		{
			IObjectTab tab = (IObjectTab)it.next();
			tab.setDatabaseObjectInfo(dboInfo);
		}
	}

	private void createUserInterface()
	{
		addChangeListener(new TabbedPaneListener());
	}

	private void propertiesHaveChanged(String propName)
	{
		if (propName == null
			|| propName.equals(SessionProperties.IPropertyNames.META_DATA_OUTPUT_CLASS_NAME)
			|| propName.equals(SessionProperties.IPropertyNames.SQL_RESULTS_OUTPUT_CLASS_NAME))
		{
			rebuild();
		}
	}

	/**
	 * Rebuild the tabs. This usually means that some kind of configuration
	 * data has changed (I.E. the output type has changed from text to table).
	 */
	private synchronized void rebuild()
	{
		final int curTabIdx = getSelectedIndex();
		final List oldTabs = new ArrayList();
		oldTabs.addAll(_tabs);
		removeAll();
		_tabs.clear();
		Iterator it = oldTabs.iterator();
		while (it.hasNext())
		{
			final IObjectTab tab = (IObjectTab)it.next();
			tab.rebuild();
			addObjectPanelTab(tab);
		}
		if (curTabIdx >= 0 && curTabIdx < getTabCount())
		{
			setSelectedIndex(curTabIdx);
		}
	}

	/**
	 * When a different tab is selected then refresh the newly selected tab.
	 */
	private class TabbedPaneListener implements ChangeListener
	{
		public void stateChanged(ChangeEvent evt)
		{
			selectCurrentTab();
		}
	}

	/**
	 * Listen for changes in session properties.
	 */
	private class SessionPropertiesListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			propertiesHaveChanged(evt.getPropertyName());
		}
	}
}
