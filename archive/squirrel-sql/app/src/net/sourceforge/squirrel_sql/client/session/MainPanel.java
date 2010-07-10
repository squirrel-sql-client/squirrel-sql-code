package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001-2002 Colin Bell
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.SquirrelTabbedPane;
import net.sourceforge.squirrel_sql.client.plugin.SessionPluginInfo;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.action.CommitAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.RollbackAction;
import net.sourceforge.squirrel_sql.client.session.action.ShowNativeSQLAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ObjectTreeTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class MainPanel extends SquirrelTabbedPane
{
	/**
	 * IDs of tabs.
	 */
	public interface ITabIndexes
	{
		int OBJECT_TREE_TAB = 0;
		int SQL_TAB = 1;
	}

	/** Logger for this class. */
	private static final ILogger s_log = LoggerController.createLogger(MainPanel.class);

	/** Current session. */
	private ISession _session;

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
	 * @throws	IllegalArgumentException	If a <TT>null</TT>
											<TT>ISession</TT> passed.
	 */
	public MainPanel(ISession session)
	{
		super(getPreferences(session));

		_session = session;

		createUserInterface();

		// Refresh the currently selected tab.
		((IMainPanelTab)_tabs.get(getSelectedIndex())).select();
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
		int idx = indexOfTab(title);
		if (idx != -1)
		{
			removeTabAt(idx);
			_tabs.set(idx, tab);
		}
		else
		{
			idx = getTabCount();
			_tabs.add(tab);
		}
		insertTab(title, null, tab.getComponent(), tab.getHint(), idx);
	}

	void updateState()
	{
		final ActionCollection actions = _session.getApplication().getActionCollection();
		if (getSelectedIndex() == ITabIndexes.SQL_TAB)
		{
			actions.get(ExecuteSqlAction.class).setEnabled(true);
			actions.get(ShowNativeSQLAction.class).setEnabled(true);
			boolean isAutoCommit = _session.getProperties().getAutoCommit();
			actions.get(CommitAction.class).setEnabled(!isAutoCommit);
			actions.get(RollbackAction.class).setEnabled(!isAutoCommit);
			actions.get(RefreshObjectTreeAction.class).setEnabled(false);
		}
		else
		{
			actions.get(ExecuteSqlAction.class).setEnabled(false);
			actions.get(ShowNativeSQLAction.class).setEnabled(false);
			actions.get(CommitAction.class).setEnabled(false);
			actions.get(RollbackAction.class).setEnabled(false);
			actions.get(RefreshObjectTreeAction.class).setEnabled(true);		}
	}

	void sessionClosing(ISession session)
	{
		// TODO: ALlow for (and report) errors that occur in the plugins.
		for (Iterator it = _tabs.iterator(); it.hasNext();)
		{
			((IMainPanelTab)it.next()).sessionClosing(session);
		}
	}

	private void createUserInterface()
	{
		//TODO: uncomment this when we no longer support JDK1.3
		//setFocusable(false);

		addMainPanelTab(new ObjectTreeTab());
		addMainPanelTab(new SQLTab(_session));

		addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent evt)
			{
				updateState();
				int idx = getSelectedIndex();
				if (idx != -1)
				{
					((IMainPanelTab) _tabs.get(getSelectedIndex())).select();
				}
			}
		});
	}

	private static SquirrelPreferences getPreferences(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		return session.getApplication().getSquirrelPreferences();
	}

	public ObjectTreePanel getObjectTreePanel()
	{
		ObjectTreeTab tab = (ObjectTreeTab)_tabs.get(ITabIndexes.OBJECT_TREE_TAB);
		return (ObjectTreePanel)tab.getComponent();
	}

	public SQLPanel getSQLPanel()
	{
		return ((SQLTab)_tabs.get(ITabIndexes.SQL_TAB)).getSQLPanel();
	}
}
