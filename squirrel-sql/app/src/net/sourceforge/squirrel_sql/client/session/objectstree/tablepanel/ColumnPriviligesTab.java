package net.sourceforge.squirrel_sql.client.session.objectstree.tablepanel;
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
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.objectstree.objectpanel.ResultSetPanel;

/**
 * This tab shows the column privilige info for the currently selected table.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ColumnPriviligesTab extends BaseTablePanelTab
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String TITLE = "Column Priviliges";
		String HINT = "Show access rights for columns";
	}

	/** Component to be displayed. */
	private ResultSetPanel _comp;

	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	public String getTitle()
	{
		return i18n.TITLE;
	}

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	public String getHint()
	{
		return i18n.HINT;
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
			_comp = new ResultSetPanel();
		}
		return _comp;
	}

	/**
	 * @see BaseObjectPanelTab#clear()
	 */
	public void clear()
	{
		((ResultSetPanel) getComponent()).clear();
	}

	/**
	 * Refresh the component displaying the <TT>ITableInfo</TT> object.
	 */
	public synchronized void refreshComponent() throws IllegalStateException
	{
		ISession session = getSession();
		if (session == null)
		{
			throw new IllegalStateException("Null ISession");
		}
		ITableInfo ti = getTableInfo();
		if (ti == null)
		{
			throw new IllegalStateException("Null ITableInfo");
		}
		String destClassName =
			session.getProperties().getMetaDataOutputClassName();
		try
		{
			ResultSet rs = session.getSQLConnection().getSQLMetaData().getColumnPrivileges(ti);
			((ResultSetPanel) getComponent()).load(session, rs, null, destClassName);
		}
		catch (Exception ex)
		{
			session.getMessageHandler().showErrorMessage(ex);
		}
	}
}
