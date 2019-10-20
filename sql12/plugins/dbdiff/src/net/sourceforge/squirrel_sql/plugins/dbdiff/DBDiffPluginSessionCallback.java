/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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

package net.sourceforge.squirrel_sql.plugins.dbdiff;

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreePanel;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;

/**
 * @author manningr
 */
public class DBDiffPluginSessionCallback implements PluginSessionCallback
{
	DBDiffPlugin _plugin;

	public DBDiffPluginSessionCallback(DBDiffPlugin plugin)
	{
		_plugin = plugin;
	}

	public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession session)
	{
	}

	public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession session)
	{
		_plugin.addMenuItemsToContextMenu(objectTreeInternalFrame.getObjectTreeAPI());
	}

	@Override
	public void objectTreeInSQLTabOpened(ObjectTreePanel objectTreePanel)
	{
		_plugin.addMenuItemsToContextMenu(objectTreePanel);
	}

	@Override
   public void additionalSQLTabOpened(AdditionalSQLTab additionalSQLTab)
   {
   }
}
