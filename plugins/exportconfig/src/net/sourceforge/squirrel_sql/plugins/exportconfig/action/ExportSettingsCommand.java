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
import java.awt.Frame;
import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPlugin;
/**
 * This command allow the user to save the application settings to the file system.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class ExportSettingsCommand extends AbstractSaveCommand
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ExportSettingsCommand.class);


	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ExportSettingsCommand.class);

	/** Parent frame. */
	private final Frame _frame;

	/** Current plugin. */
	private ExportConfigPlugin _plugin;

	/**
	 * Ctor.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a�<TT>null</TT> <TT>ISession</TT>,
	 * 			<TT>Resources</TT> or <TT>MysqlPlugin</TT> passed.
	 */
	public ExportSettingsCommand(Frame frame, ExportConfigPlugin plugin)
	{
		super(frame, plugin);
		_frame = frame;
		_plugin = plugin;
	}

	/**
	 * Save database drivers to <TT>file</TT>.
	 *
	 * @param	file	The <TT>File</TT> to be written to.
	 *
	 * @throws	IOException		Thrown if an IO error occurs.
	 */
	protected void writeToFile(File file) throws IOException, XMLException
	{
		final SquirrelPreferences prefs = _plugin.getApplication().getSquirrelPreferences();
		new XMLBeanWriter(prefs).save(file);
	}

	/**
	 * Retrieve the default file name for the save.
	 *
	 * @return	The default file name.
	 */
	protected String getDefaultFilename()
	{
		return new ApplicationFiles().getUserPreferencesFile().getName();
	}

	/**
	 * Retrieve the description of the objects being saved.
	 *
	 * @return	description of the objects being saved.
	 */
	protected String getSaveDescription()
	{
		// i18n[exportconfig.applicationSettings=Application Settings]
		return s_stringMgr.getString("exportconfig.applicationSettings");
	}

}
