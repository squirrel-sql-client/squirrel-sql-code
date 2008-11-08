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
import javax.swing.JDialog;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPlugin;
import net.sourceforge.squirrel_sql.plugins.exportconfig.gui.ExportDialog;
/**
 * This command allow the user to save configuration information to the file
 * system.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class ExportConfigurationCommand implements ICommand
{
	/** Logger for this class. */
	@SuppressWarnings("unused")
	private final static ILogger s_log =
		LoggerController.createLogger(ExportConfigurationCommand.class);

	/** Current plugin. */
	private ExportConfigPlugin _plugin;

	/**
	 * Ctor.
	 * 
	 * @param	frame	Parent frame for dialogs etc.
	 * @param	plugin	Current plugin.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> 
	 * 			<TT>ExportConfigPlugin</TT> passed.
	 */
	public ExportConfigurationCommand(ExportConfigPlugin plugin)
	{
		super();

		if (plugin == null)
		{
			throw new IllegalArgumentException("ExportConfigPlugin == null");
		}

		_plugin = plugin;
	}

	/**
	 * Execute this command. Display dialog and process users request.
	 */
	public void execute()
	{
		final IApplication app = _plugin.getApplication();
		final JDialog dlog = new ExportDialog(app, _plugin);
		dlog.pack();
		GUIUtils.centerWithinParent(dlog);
		dlog.setVisible(true);
 	}
}
