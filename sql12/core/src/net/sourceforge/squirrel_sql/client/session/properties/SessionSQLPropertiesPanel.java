package net.sourceforge.squirrel_sql.client.session.properties;
/*
 * Copyright (C) 2001-2003 Colin Bell
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

import java.awt.Component;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This panel allows the user to tailor SQL settings for a session.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionSQLPropertiesPanel
	implements INewSessionPropertiesPanel, ISessionPropertiesPanel
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(SessionSQLPropertiesPanel.class);

	/** The actual GUI panel that allows user to do the maintenance. */
	private final SQLPropertiesPanel _myPanel;

   private final JScrollPane _scrolledMyPanel;

	/** Session properties object being maintained. */
	private SessionProperties _props;

   /**
	 * ctor specifying the Application API.
	 *
	 * @param session
    * @throws	IllegalArgumentException
	 * 			Thrown if <tt>null</tt> <tt>IApplication</tt>
	 * 			passed.
	 */
	public SessionSQLPropertiesPanel(ISession session) throws IllegalArgumentException
	{
		_myPanel = new SQLPropertiesPanel(session);
      _scrolledMyPanel = new JScrollPane(_myPanel);

		GUIUtils.forceScrollToBegin(_scrolledMyPanel);
	}


	public void initialize(IApplication app)
	{
		_props = Main.getApplication().getSquirrelPreferences().getSessionProperties();
		_myPanel.loadData(_props);
	}

	public void initialize(IApplication app, ISession session)
		throws IllegalArgumentException
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_props = session.getProperties();
		_myPanel.loadData(_props);
	}

	public Component getPanelComponent()
	{
		return _scrolledMyPanel;
	}

	public String getTitle()
	{
		return s_stringMgr.getString("SessionSQLPropertiesPanel.sql");
	}

	public String getHint()
	{
		return getTitle();
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_props);
	}

}
