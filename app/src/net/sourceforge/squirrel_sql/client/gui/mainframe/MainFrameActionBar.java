package net.sourceforge.squirrel_sql.client.gui.mainframe;
/*
 * Copyright (C) 2003-2004 Jason Height
 * jmheight@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.session.action.NewObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.NewSQLWorksheetAction;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
/**
 * Action Bar  for <CODE>MainFrame</CODE>.
 *
 * @author  <A HREF="mailto:jmheight@users.sourceforge.net">Jason Height</A>
 */
class MainFrameActionBar extends ToolBar
{
	/** Application API. */
	private IApplication _app;

	/**
	 * ctor.
	 *
	 * @param	app		Application API
	 * @param	frame	Application main frame
	 *
	 * @throws	IllegalArgumentException
	 *			<TT>null</TT> <TT>IApplication</TT> or <TT>MainFrame</TT>
	 *			passed.
	 */
	MainFrameActionBar(IApplication app, MainFrame frame)
	{
		super(VERTICAL);
		if (app == null)
		{
			throw new IllegalArgumentException("null IApplication passed.");
		}
		if (frame == null)
		{
			throw new IllegalArgumentException("null MainFrame passed.");
		}
		_app = app;
		_app.getSessionManager().addSessionListener(new ActionBarActionEnabler());
		setUseRolloverButtons(true);
		setFloatable(true);

		ActionCollection actions = _app.getActionCollection();
		add(actions.get(NewSQLWorksheetAction.class));
		actions.get(NewSQLWorksheetAction.class).setEnabled(false);
		add(actions.get(NewObjectTreeAction.class));
		actions.get(NewObjectTreeAction.class).setEnabled(false);
	}

	/**
	 * This class listens to the session to enable/disable the standard actions
	 * associated with the action bar.
	 */
	private class ActionBarActionEnabler extends SessionAdapter
	{
		public void sessionClosing(SessionEvent evt)
		{
			ActionCollection actions = _app.getActionCollection();
			actions.get(NewSQLWorksheetAction.class).setEnabled(false);
			actions.get(NewObjectTreeAction.class).setEnabled(false);
		}

		public void sessionActivated(SessionEvent evt)
		{
			ActionCollection actions = _app.getActionCollection();
			actions.get(NewSQLWorksheetAction.class).setEnabled(true);
			actions.get(NewObjectTreeAction.class).setEnabled(true);
		}
	}
}