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
import java.awt.Frame;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;

public class ViewSessionScriptsCommand implements ICommand {
	/** Parent frame. */
	private final Frame _frame;

	/** The current plugin. */
	private SessionScriptPlugin _plugin;

	/** Application API. */
	private IApplication _app;

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 * @param	frame	Parent Frame.
	 * @param	plugin	The current plugin.
	 *
	 * @throws	IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IPlugin</TT> or
	 * 			<TT>IApplication</TT> passed.
	 */
	public ViewSessionScriptsCommand(IApplication app, Frame frame,
										SessionScriptPlugin plugin) {
		super();
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (plugin == null) {
			throw new IllegalArgumentException("Null IPlugin passed");
		}

		_app = app;
		_frame = frame;
		_plugin = plugin;
	}

	/**
	 * Execute this command.
	 */
	public void execute() {
		ScriptsSheet.showSheet(_plugin, _app);
	}

}