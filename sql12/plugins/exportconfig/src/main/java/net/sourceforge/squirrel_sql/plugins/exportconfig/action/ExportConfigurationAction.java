package net.sourceforge.squirrel_sql.plugins.exportconfig.action;
/*
 * Copyright (C) 2003 Colin Bell
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
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPlugin;
/**
 * This <TT>Action</TT> will allow the user to export configuration information
 * to the file system.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ExportConfigurationAction extends SquirrelAction 
{
	private static final long serialVersionUID = 1L;

	/** Application API. */
	@SuppressWarnings("unused")
	private final IApplication _app;

	/** Current plugin. */
	private final ExportConfigPlugin _plugin;

	/**
	 * Ctor.
	 *
	 * @param	app			Application API.
	 * @param	rsrc		Plugins resources.
	 * @param	plugin		This plugin.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>IApplication</TT>,
	 * 			<TT>Resources</TT> or <TT>ExportConfigPlugin</TT> passed.
	 */
	public ExportConfigurationAction(IApplication app, Resources rsrc,
							ExportConfigPlugin plugin)
	{
		super(app, rsrc);
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (rsrc == null)
		{
			throw new IllegalArgumentException("Resources == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("ExportConfigPlugin == null");
		}

		_app = app;
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
			new ExportConfigurationCommand(_plugin).execute();
	}
}
