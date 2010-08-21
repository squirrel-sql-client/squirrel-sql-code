package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2003-2004 Jason Height
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

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This <CODE>Action</CODE> allows the user to close all the SQL
 * result windows for the current session.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class CloseAllSQLResultWindowsAction extends SquirrelAction
{
   /**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 */
	public CloseAllSQLResultWindowsAction(IApplication app)
	{
		super(app);
	}

	/**
	 * Perform this action. Uses the <TT>CloseAllSQLResultWindowsCommand</TT>.
	 *
	 * @param	evt	The current event.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		IApplication app = getApplication();
		CursorChanger cursorChg = new CursorChanger(app.getMainFrame());
		cursorChg.show();
		try
		{
         ISession activeSession = getApplication().getSessionManager().getActiveSession();
         if(   activeSession.getActiveSessionWindow() instanceof SessionInternalFrame
            || activeSession.getActiveSessionWindow() instanceof SQLInternalFrame)
         {
            // Can't work with ISessionAction because if a result window is on top
            // the session in a ISessionAction is null.
			   new CloseAllSQLResultWindowsCommand(activeSession.getSQLPanelAPIOfActiveSessionWindow()).execute();
         }
		}
		finally
		{
			cursorChg.restore();
		}
	}
}
