package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.action.CommitAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.GotoNextResultsTabAction;
import net.sourceforge.squirrel_sql.client.session.action.GotoPreviousResultsTabAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.RollbackAction;
import net.sourceforge.squirrel_sql.client.session.action.SQLFilterAction;
import net.sourceforge.squirrel_sql.client.session.action.EditWhereColsAction;
import net.sourceforge.squirrel_sql.client.session.action.ShowNativeSQLAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ObjectTreeTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
/**
 * This tabbed panel is the main panel within the session window.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class MainPanel extends JPanel
{
	/**
	 * IDs of tabs.
	 */
	public interface ITabIndexes
	{
		int OBJECT_TREE_TAB = 0;
		int SQL_TAB = 1;
	}
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MainPanel.class);

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(MainPanel.class);

	/** Current session. */
	private ISession _session;

	/** The tabbed pane. */
	private final JTabbedPane _tabPnl = UIFactory.getInstance().createTabbedPane();

	/** Listener to the sessions properties. */
	private PropertyChangeListener _propsListener;

	/** Listener for changes to the tabbed panel. */
	private ChangeListener _tabPnlListener;

	/**
	 * Collection of <TT>IMainPanelTab</TT> objects displayed in
	 * this tabbed panel.
	 */
	private List _tabs = new ArrayList();

	/**
	 * ctor specifying the current session.
	 *
	 * @param	session		Current session.
	 *
	 * @throws	IllegalArgumentException
	 *			If a <TT>null</TT> <TT>ISession</TT> passed.
	 */
	MainPanel(ISession session)
	{
		super(new BorderLayout());

		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		_session = session;

		addMainPanelTab(new ObjectTreeTab());
		addMainPanelTab(new SQLTab(_session));

		add(_tabPnl, BorderLayout.CENTER);
	
		propertiesHaveChanged(null);

		// Refresh the currently selected tab.
		((IMainPanelTab)_tabs.get(getTabbedPane().getSelectedIndex())).select();
	}

	public void addNotify()
	{
		super.addNotify();

		if (_propsListener == null)
		{
			_propsListener = new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent evt)
				{
					propertiesHaveChanged(evt.getPropertyName());
				}
			};
			_session.getProperties().addPropertyChangeListener(_propsListener);
		}

		_tabPnlListener = new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt)
			{
				performStateChanged();
			}
		};
		_tabPnl.addChangeListener(_tabPnlListener);
	}

	public void removeNotify()
	{
		super.removeNotify();

		if (_propsListener != null)
		{
			_session.getProperties().removePropertyChangeListener(_propsListener);
			_propsListener = null;
		}

		if (_tabPnlListener != null)
		{
			_tabPnl.removeChangeListener(_tabPnlListener);
			_tabPnlListener = null;
		}
	}

	/**
	 * Add a tab to this panel. If a tab with this title already exists it is
	 * removed from the tabbed pane and the passed tab inserted in its
	 * place. New tabs are inserted at the end.
	 *
	 * @param	tab	 The tab to be added.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>ITablePanelTab</TT> passed.
	 */
	public void addMainPanelTab(IMainPanelTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IMainPanelTab passed");
		}
		tab.setSession(_session);
		final String title = tab.getTitle();
		int idx = _tabPnl.indexOfTab(title);
		if (idx != -1)
		{
			_tabPnl.removeTabAt(idx);
			_tabs.set(idx, tab);
		}
		else
		{
			idx = _tabPnl.getTabCount();
			_tabs.add(tab);
		}
		_tabPnl.insertTab(title, null, tab.getComponent(), tab.getHint(), idx);
	}

	void updateState()
	{
		final ActionCollection actions = _session.getApplication().getActionCollection();
		if (_tabPnl.getSelectedIndex() == ITabIndexes.SQL_TAB)
		{
			actions.get(ExecuteSqlAction.class).setEnabled(true);
			actions.get(GotoNextResultsTabAction.class).setEnabled(true);
			actions.get(GotoPreviousResultsTabAction.class).setEnabled(true);
			actions.get(ShowNativeSQLAction.class).setEnabled(true);
			actions.get(RefreshObjectTreeAction.class).setEnabled(false);
			actions.get(SQLFilterAction.class).setEnabled(false);
			actions.get(EditWhereColsAction.class).setEnabled(false);
		}
		else
		{
			actions.get(ExecuteSqlAction.class).setEnabled(false);
			actions.get(GotoNextResultsTabAction.class).setEnabled(false);
			actions.get(GotoPreviousResultsTabAction.class).setEnabled(false);
			actions.get(ShowNativeSQLAction.class).setEnabled(false);
			actions.get(RefreshObjectTreeAction.class).setEnabled(true);
			actions.get(SQLFilterAction.class).setEnabled(true);
			actions.get(EditWhereColsAction.class).setEnabled(true);
		}
		final boolean isAutoCommit = _session.getProperties().getAutoCommit();
		actions.get(CommitAction.class).setEnabled(!isAutoCommit);
		actions.get(RollbackAction.class).setEnabled(!isAutoCommit);
	}

	/**
	 * The passed session is closing so tell each tab.
	 *
	 * @param	session		Session being closed.
	 */
	void sessionClosing(ISession session)
	{
		for (Iterator it = _tabs.iterator(); it.hasNext();)
		{
			try
			{
				((IMainPanelTab)it.next()).sessionClosing(session);
			}
			catch (Throwable th)
			{
				String msg = s_stringMgr.getString("MainPanel.error.sessionclose");
				_session.getApplication().showErrorDialog(msg, th);
				s_log.error(msg, th);
			}
		}
	}

	/**
	 * Session properties have changed so update GUI if required.
	 *
	 * @param	propertyName	Name of property that has changed.
	 */
	private void propertiesHaveChanged(String propertyName)
	{
		SessionProperties props = _session.getProperties();
		if (propertyName == null
			|| propertyName.equals(SessionProperties.IPropertyNames.MAIN_TAB_PLACEMENT))
		{
			_tabPnl.setTabPlacement(props.getMainTabPlacement());
		}
	}

	private void performStateChanged()
	{
		updateState();
		int idx = _tabPnl.getSelectedIndex();
		if (idx != -1)
		{
			((IMainPanelTab)_tabs.get(idx)).select();
		}
	}

	ObjectTreePanel getObjectTreePanel()
	{
		ObjectTreeTab tab = (ObjectTreeTab)_tabs.get(ITabIndexes.OBJECT_TREE_TAB);
		return (ObjectTreePanel)tab.getComponent();
	}

	SQLPanel getSQLPanel()
	{
		return ((SQLTab)_tabs.get(ITabIndexes.SQL_TAB)).getSQLPanel();
	}

	/**
	 * Retrieve the tabbed pane for this component.
	 * 
	 * @return	The tabbed pane.
	 */
	JTabbedPane getTabbedPane()
	{
		return _tabPnl;
	}
}
