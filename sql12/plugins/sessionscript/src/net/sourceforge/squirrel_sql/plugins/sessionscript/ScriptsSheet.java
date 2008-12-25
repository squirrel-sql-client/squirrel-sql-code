package net.sourceforge.squirrel_sql.plugins.sessionscript;
/*
 * Copyright (C) 2002 Colin Bell
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.awt.*;

class ScriptsSheet extends DialogWidget
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ScriptsSheet.class);
	

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
		// i18n[sessionscript.startupScripts=Startup Scripts]
		super(s_stringMgr.getString("sessionscript.startupScripts"), true, true, true, true, app);
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
			app.getMainFrame().addWidget(s_instance);
		}
		s_instance.setVisible(true);
	}

	/**
	 * Create this sheets user interface.
	 */
	private void createUserInterface()
	{
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//		Icon icon = app.getResources().getIcon(getClass(), "frameIcon"); //i18n
//		if (icon != null)
//		{
//			setFrameIcon(icon);
//		}

		makeToolWindow(true);

		_mainPnl = new ViewSessionScriptsPanel(_plugin, _app);

		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		content.add(_mainPnl, BorderLayout.CENTER);

		setPreferredSize(new Dimension(600, 400));
		pack();
	}

}
