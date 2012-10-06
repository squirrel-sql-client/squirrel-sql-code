package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2003-2004 Colin Bell
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
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.MultipleWindowsHandler;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This action will bring the window for the <EM>next</EM> session
 * to the front.
 *
 * @author Gerd Wagner
 */
public class NextSessionAction extends SquirrelAction
								implements ISessionAction
{
	private ISession _session;

	public NextSessionAction(IApplication app)
	{
		super(app);
	}

	public void setSession(ISession session)
	{
      if (null != _session && MultipleWindowsHandler.DETACH_SESSION_WINDOW)
      {
         System.out.println("NextSessionAction.setSession  " + (null != _session.getActiveSessionWindow()?_session.getActiveSessionWindow().getTitle() : " OPENING"));
      }

      _session = session;
	}

	public void actionPerformed(ActionEvent evt)
	{
		getApplication().getWindowManager().activateNextSessionWindow();
	}
}
