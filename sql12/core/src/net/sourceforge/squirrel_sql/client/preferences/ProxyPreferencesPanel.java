package net.sourceforge.squirrel_sql.client.preferences;
/*
 * Copyright (C) 2002-2003 Colin Bell
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
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.awt.Component;

class ProxyPreferencesPanel implements IGlobalPreferencesPanel
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ProxyPreferencesPanel.class);

	/** Panel to be displayed in preferences dialog. */
	private ProxyPreferenceTabComponent _myPanel;

	/** Application API. */
	private IApplication _app;

	/**
	 * Default ctor.
	 */
	public ProxyPreferencesPanel()
	{
		super();
	}

	public void initialize(IApplication app)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;

		((ProxyPreferenceTabComponent)getPanelComponent()).loadData(_app, _app.getSquirrelPreferences());
	}

   public void uninitialize(IApplication app)
   {
      
   }

   public synchronized Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new ProxyPreferenceTabComponent();
		}
		return _myPanel;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_app.getSquirrelPreferences());
	}

	public String getTitle()
	{
		return s_stringMgr.getString("ProxyPreferencesPanel.title");
	}

	public String getHint()
	{
		return s_stringMgr.getString("ProxyPreferencesPanel.hint");
	}

}
