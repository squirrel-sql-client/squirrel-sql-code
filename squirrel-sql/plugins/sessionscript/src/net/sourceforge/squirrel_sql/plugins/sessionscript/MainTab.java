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
import java.awt.Component;

import net.sourceforge.squirrel_sql.client.session.mainpanel.BaseMainPanelTab;

class MainTab extends BaseMainPanelTab {
	private SessionScriptPlugin _plugin;

	/** Panel for main tabbed pane. */
	private MainTabPanel _mainTabPnl;

	MainTab(SessionScriptPlugin plugin) {
		super();
		_plugin = plugin;
	}

	/**
	 * @see BaseMainPanelTab#refreshComponent()
	 */
	protected void refreshComponent() {
		((MainTabPanel)getComponent()).createUserInterface();
	}

	/**
	 * @see IMainPanelTab#getTitle()
	 */
	public String getTitle() {
		return "Startup";
	}

	/**
	 * @see IMainPanelTab#getHint()
	 */
	public String getHint() {
		return "Scripts to run at session startup";
	}

	/**
	 * @see IMainPanelTab#getComponent()
	 */
	public synchronized Component getComponent() {
		if (_mainTabPnl == null) {
			_mainTabPnl = new MainTabPanel(_plugin);
		}
		return _mainTabPnl;
	}

}

