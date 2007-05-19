package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTabbedPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This is the tabbed panel displayed when a node is selected in the
 * object tree.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class ObjectTreeTabbedPane
{
    
    /** Logger for this class. */
    private final static ILogger log = 
                      LoggerController.createLogger(ObjectTreeTabbedPane.class);
    
	/** Keys to client properties stored in the component. */
	interface IClientPropertiesKeys
	{
		String TABBED_PANE_OBJ = ObjectTreeTabbedPane.class.getName() + "/TabPaneObj";
	}

	/** The tabbed pane. */
	private final JTabbedPane _tabPnl = UIFactory.getInstance().createTabbedPane();

	/** Application API. */
	private final IApplication _app;

	/** ID of the session for this window. */
	private final IIdentifier _sessionId;

	/**
	 * Collection of <TT>IObjectTab</TT> objects displayed in
	 * this tabbed panel.
	 */
	private List<IObjectTab> _tabs = new ArrayList<IObjectTab>();

	ObjectTreeTabbedPane(ISession session)
	{
		super();

		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}

		_sessionId = session.getIdentifier();
		_app = session.getApplication();

		_tabPnl.putClientProperty(IClientPropertiesKeys.TABBED_PANE_OBJ, this);
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

	IObjectTab getTabIfSelected(String title)
	{
		IObjectTab tab = getSelectedTab();
		if ((tab != null) && (tab.getTitle().equals(title)))
		{
			return tab;
		}
		return null;
	}

    IObjectTab getSelectedTab() {
        IObjectTab tab = _tabs.get(_tabPnl.getSelectedIndex());
        return tab;
    }
    
	synchronized void addObjectPanelTab(IObjectTab tab)
	{
		if (tab == null)
		{
			throw new IllegalArgumentException("Null IObjectTab passed");
		}
        // For some reason, when the Oracle plugin adds details tabs for 
        // triggers, the _tabPnl's first tab ends up being the trigger details
        // tab and not the generic database object info tab.  This causes the 
        // _tabs length to be 1 tab greater than the tabs that are actually in 
        // the _tabPnl.  This throws off the selection such that the tab 
        // selected in the tab panel doesn't get rendered until the tab to the
        // right of the selected tab is selected.  This is a work-around for 
        // this problem until I can determine why the DatabaseObjectInfoTab 
        // never makes it into the _tabPnl in the first place.
		if (_tabs.size() == 1 && _tabPnl.getTabCount() == 0) {
            log.debug(
                "addObjectPanelTab: _tabs.size() == 1, but " +
                "_tabPnl.getTabCount() == 0 - adding first tab component to " +
                "the tabbed page");
            IObjectTab firstTab = _tabs.get(0);
            _tabPnl.addTab(firstTab.getTitle(), 
                           null, 
                           firstTab.getComponent(), 
                           firstTab.getHint());
        }
        
        tab.setSession(_app.getSessionManager().getSession(_sessionId));
        final String title = tab.getTitle();
        _tabPnl.addTab(title, null, tab.getComponent(), tab.getHint());
        _tabs.add(tab);
	}

	void selectCurrentTab()
	{
		if (_tabPnl.getParent() != null)
		{
			int idx = _tabPnl.getSelectedIndex();
			if (idx != -1 && idx < _tabs.size())
			{
				IObjectTab tab = _tabs.get(idx);
				if (tab != null)
				{
					tab.select();
				}
			}
		}
	}

	void setDatabaseObjectInfo(IDatabaseObjectInfo dboInfo)
	{
		Iterator<IObjectTab> it = _tabs.iterator();
		while (it.hasNext())
		{
			IObjectTab tab = it.next();
			tab.setDatabaseObjectInfo(dboInfo);
		}
	}

	/**
	 * Rebuild the tabs. This usually means that some kind of configuration
	 * data has changed (I.E. the output type has changed from text to table).
	 */
	synchronized void rebuild()
	{
		final int curTabIdx = _tabPnl.getSelectedIndex();
		final List<IObjectTab> oldTabs = new ArrayList<IObjectTab>();
		oldTabs.addAll(_tabs);
		_tabPnl.removeAll();
		_tabs.clear();
		Iterator<IObjectTab> it = oldTabs.iterator();
		while (it.hasNext())
		{
			final IObjectTab tab = it.next();
			tab.rebuild();
			addObjectPanelTab(tab);
		}
		if (curTabIdx >= 0 && curTabIdx < _tabPnl.getTabCount())
		{
			_tabPnl.setSelectedIndex(curTabIdx);
		}
	}
        
}
