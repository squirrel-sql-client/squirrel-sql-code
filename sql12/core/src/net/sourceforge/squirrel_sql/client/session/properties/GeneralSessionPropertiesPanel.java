package net.sourceforge.squirrel_sql.client.session.properties;
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
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JScrollPane;
import java.awt.Component;

public class GeneralSessionPropertiesPanel implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GeneralSessionPropertiesPanel.class);


	private SessionProperties _props;

	private GeneralSessionPropertiesUIPanel _panel = new GeneralSessionPropertiesUIPanel();
	private JScrollPane _scrolledMyPanel = new JScrollPane(_panel);

	public GeneralSessionPropertiesPanel()
	{
		super();
	}

	public void initialize(IApplication app) throws IllegalArgumentException
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_props = app.getSquirrelPreferences().getSessionProperties();

		_panel.loadData(_props);
	}

	public void initialize(IApplication app, ISession session)
		throws IllegalArgumentException
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}

		_props = session.getProperties();

		_panel.loadData(_props);
	}

	public Component getPanelComponent()
	{
		return _scrolledMyPanel;
	}

	public String getTitle()
	{
		return GeneralSessionPropertiesPanelI18n.TITLE;
	}

	public String getHint()
	{
		return GeneralSessionPropertiesPanelI18n.HINT;
	}

	public void applyChanges()
	{
		_panel.applyChanges(_props);
	}



}
