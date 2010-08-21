package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;
/*
 * Copyright (C) 2001-2004 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This interface defines the behaviour for a tab in one of the object panels.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IObjectTab
{
	/**
	 * Return the title for the tab.
	 *
	 * @return	The title for the tab.
	 */
	String getTitle();

	/**
	 * Return the hint for the tab.
	 *
	 * @return	The hint for the tab.
	 */
	String getHint();

	/**
	 * Return the component to be displayed in the panel.
	 *
	 * @return	The component to be displayed in the panel.
	 */
	Component getComponent();

	/**
	 * Set the current session.
	 *
	 * @param	session		Current session.
	 */
	void setSession(ISession session);

	/**
	 * Set the <TT>IDatabaseObjectInfo</TT> object that specifies the object that
	 * is to have its information displayed.
	 *
	 * @param	value	<TT>IDatabaseObjectInfo</TT> object that specifies
	 *					the currently selected object. This can be <TT>null</TT>.
	 */
	void setDatabaseObjectInfo(IDatabaseObjectInfo value);

	/**
	 * This tab has been selected.
	 */
	void select();

	/**
	 * Clears the current view.
	 */
	void clear();

	/**
	 * Rebuild the tab. This usually means that some kind of configuration
	 * data has changed (I.E. the output type has changed from text to table).
	 */
	void rebuild();
}
