package net.sourceforge.squirrel_sql.client.session.mainpanel;
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
import java.awt.Component;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class SQLTab extends BaseMainPanelTab
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String TAB_TITLE = "SQL";
		String TAB_DESC = "Execute SQL statements";
	}
	/** Logger for this class. */
	private final static ILogger s_log =
						LoggerController.createLogger(SQLTab.class);

	/** Component to be displayed. */
	private SQLPanel _comp;

	public SQLTab(ISession session)
	{
		super();
		setSession(session);
	}

	/**
	 * @see IMainPanelTab#getTitle()
	 */
	public String getTitle()
	{
		return i18n.TAB_TITLE;
	}

	/**
	 * @see IMainPanelTab#getHint()
	 */
	public String getHint()
	{
		return i18n.TAB_DESC;
	}

	/**
	 * Return the component to be displayed in the panel.
	 *
	 * @return	The component to be displayed in the panel.
	 */
	public synchronized Component getComponent()
	{
		if (_comp == null)
		{
			_comp = new SQLPanel(getSession());
		}
		return _comp;
	}

	/**
	 * @see IMainPanelTab#setSession(ISession)
	 */
	public void setSession(ISession session)
	{
		super.setSession(session);
		getSQLPanel().setSession(session);
	}

	/**
	 * @see IMainPanelTab#select()
	 */
	public synchronized void refreshComponent()
	{
		getSQLPanel().selected();
	}

	/**
	 * Sesssion is ending.
	 */
	public void sessionClosing(ISession session)
	{
		if (_comp != null)
		{
			_comp.sessionClosing();
		}
	}

	public SQLPanel getSQLPanel()
	{
		return (SQLPanel)getComponent();
	}
}
