package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.procedure;
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
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;

import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.IObjectTab;
/**
 * This interface defines the behaviour for a tab displayed when a stored
 * procedure is selected in the object tree.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IProcedureTab extends IObjectTab
{
	/**
	 * Set the <TT>IProcedureInfo</TT> object that specifies the table that
	 * is to have its information displayed.
	 *
	 * @param	value	<TT>IProcedureInfo</TT> object that specifies the currently
	 *					selected procedure. This can be <TT>null</TT>.
	 */
	void setProcedureInfo(IProcedureInfo value);
}

