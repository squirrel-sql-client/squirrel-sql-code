package net.sourceforge.squirrel_sql.client.session.mainpanel;
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
import java.awt.Component;

import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class ObjectsTab extends BaseMainPanelTab {
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		String TAB_TITLE = "Objects";
		String TAB_DESC = "Show database objects";
	}

	public ObjectsTab(ISession session) {
		super();
		setSession(session);
	}

	/** Component to be displayed. */
	private ObjectsPanel _comp;

	/**
	 * @see IMainPanelTab#getTitle()
	 */
	public String getTitle() {
		return i18n.TAB_TITLE;
	}

	/**
	 * @see IMainPanelTab#getHint()
	 */
	public String getHint() {
		return i18n.TAB_DESC;
	}

	/**
	 * Return the component to be displayed in the panel.
	 *
	 * @return	The component to be displayed in the panel.
	 */
	public synchronized Component getComponent() {
		if (_comp == null) {
			_comp = new ObjectsPanel(getSession());
		}
		return _comp;
	}

	/**
	 * @see IMainPanelTab#select()
	 */
	public synchronized void refreshComponent() {
		getObjectsPanel().selected();
	}

	/**
	 * Sesssion is ending.
	 * Remove all listeners that this component has setup. Close all
	 * torn off result tab windows.
	 */
	public void sessionClosing(ISession session) {
	}

	public ObjectsPanel getObjectsPanel() {
		return (ObjectsPanel)getComponent();
	}

	void refreshTree() throws BaseSQLException {
		getObjectsPanel().refresh();
	}

	/**
	 * Return an array of <TT>IDatabaseObjectInfo</TT> objects representing all
	 * the objects selected in the objects tree.
	 *
	 * @return	array of <TT>IDatabaseObjectInfo</TT> objects.
	 */
	public IDatabaseObjectInfo[] getSelectedDatabaseObjects() {
		return getObjectsPanel().getSelectedDatabaseObjects();
	}

}
