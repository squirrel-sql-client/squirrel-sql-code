package net.sourceforge.squirrel_sql.plugins.exportconfig.gui;
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

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;

import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPlugin;
import net.sourceforge.squirrel_sql.plugins.exportconfig.ExportConfigPreferences;
/**
 * Dialog that allows user to export the current configuration.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ExportDialog extends JDialog
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ExportDialog.class);

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ExportDialog.class);

	public ExportDialog(IApplication app, ExportConfigPlugin plugin)
	{
		super(app.getMainFrame(), true);

		setTitle(s_stringMgr.getString("ExportDialog.title")); 
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		final ExportConfigPreferences prefs = plugin.getPreferences();
		setContentPane(new ExportPanelBuilder(app).buildPanel(prefs));
	}

}
