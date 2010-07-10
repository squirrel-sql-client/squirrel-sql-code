package net.sourceforge.squirrel_sql.plugins.sessionscript;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

public class ViewSessionScriptsAction extends SquirrelAction
{
	private SessionScriptPlugin _plugin;

	/**
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IPlugin</TT> passed.
	 */
	public ViewSessionScriptsAction(IApplication app, Resources rsrc,
										SessionScriptPlugin plugin)
	{
		super(app, rsrc);
		if (plugin == null) {
			throw new IllegalArgumentException("Null IPlugin passed");
		}

		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		ViewSessionScriptsCommand cmd = new ViewSessionScriptsCommand(
							getApplication(), getParentFrame(evt), _plugin);
		cmd.execute();
	}
}


