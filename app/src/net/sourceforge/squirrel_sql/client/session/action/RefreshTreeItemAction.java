package net.sourceforge.squirrel_sql.client.session.action;
/*
 * TODO: Delete me
 * Copyright (C) 2002-2003 Johan Compagner
 * jcompagner@j-com.nl
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

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * @version 	$Id: RefreshTreeItemAction.java,v 1.5 2003-12-01 12:03:32 colbell Exp $
 * @author		Johan Compagner
 */
public class RefreshTreeItemAction extends SquirrelAction
										implements ISessionAction
{
	/** Current session. */
	private ISession _session;

	/**
	 * Constructor for DropTableAction.
	 *
	 * @param	app		Application API.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if <tt>null</tt> <tt>IApplication</tt> passed.
	 */
	public RefreshTreeItemAction(IApplication app)
		throws IllegalArgumentException
	{
		super(app);
	}

	/*
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
//		if (_session != null)
//		{
//			CursorChanger cursorChg = new CursorChanger(_session.getApplication().getMainFrame());
//			cursorChg.show();
//			try
//			{
//				_session.getSessionSheet().refreshSelectedDatabaseObjects();
//			}
//			catch (BaseSQLException ex)
//			{
//				final String msg = "Error occured refreshing the objects tree";
//				s_log.error(msg, ex);
//				_session.getMessageHandler().showMessage(msg);
//				_session.getMessageHandler().showMessage(ex);
//			}
//			finally
//			{
//				cursorChg.restore();
//			}
//		}
		Dialogs.showNotYetImplemented(_session.getSessionSheet());
	}

	/*
	 * @see IClientSessionAction#setSession(ISession)
	 */
	public void setSession(ISession session)
	{
		_session = session;
	}

}
