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
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.BaseSheet;

class ScriptsSheet extends BaseSheet
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(ScriptsSheet.class);

	/** Singleton instance of this class. */
	private static ScriptsSheet s_instance;

	/** Plugin. */
	private SessionScriptPlugin _plugin;

	/** Application API. */
	private IApplication _app;

	/** Main panel. */
	private ViewSessionScriptsPanel _mainPnl;

	private ScriptsSheet(SessionScriptPlugin plugin, IApplication app)
	{
		super("Startup Scripts", true, true, true, true);
		_plugin = plugin;
		_app = app;

		createUserInterface();
	}

	public void dispose()
	{
		synchronized (getClass())
		{
			s_instance = null;
		}
		super.dispose();
	}

	public static synchronized void showSheet(SessionScriptPlugin plugin,
													IApplication app)
	{
		if (s_instance == null)
		{
			s_instance = new ScriptsSheet(plugin, app);
			app.getMainFrame().addInternalFrame(s_instance, true, null);
		}
		s_instance.setVisible(true);
	}

	/**
	 * Create this sheets user interface.
	 */
	private void createUserInterface()
	{
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//		Icon icon = app.getResources().getIcon(getClass(), "frameIcon"); //i18n
//		if (icon != null)
//		{
//			setFrameIcon(icon);
//		}

		GUIUtils.makeToolWindow(this, true);
		
		_mainPnl = new ViewSessionScriptsPanel(_plugin, _app);

		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		content.add(_mainPnl, BorderLayout.CENTER);

		setPreferredSize(new Dimension(600, 400));
		pack();
	}

}
