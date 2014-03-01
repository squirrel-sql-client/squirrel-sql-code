package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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

import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
public class SQLTab extends BaseMainPanelTab
{

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SQLTab.class);  

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
        // i18n[SQLTab.title=SQL]
		return s_stringMgr.getString("SQLTab.title");
	}

	/**
	 * @see IMainPanelTab#getHint()
	 */
	public String getHint()
	{
        // i18n[SQLTab.hint=Execute SQL statements]
		return s_stringMgr.getString("SQLTab.hint");
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
			_comp = new SQLPanel(getSession(), true);
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
		// JASON: Do we need this?
//		getSQLPanel().selected();
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

	/**
	 * This tab has been selected. Set focus to the SQL entry area.
	 */
	public synchronized void select()
	{
		super.select();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_comp.getSQLEntryPanel().requestFocus();
			}
		});
	}

	public SQLPanel getSQLPanel()
	{
		return (SQLPanel)getComponent();
	}
}
