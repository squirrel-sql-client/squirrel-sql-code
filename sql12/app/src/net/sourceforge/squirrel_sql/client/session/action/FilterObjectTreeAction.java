package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2003 Colin Bell
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
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeFilterDialog;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * This <CODE>Action</CODE> allows the user to filter the objects displayed
 * in the object tree.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class FilterObjectTreeAction extends SquirrelAction
									implements ISessionAction
{
	/** Logger for this class. */
	private static ILogger s_log =
		LoggerController.createLogger(FilterObjectTreeAction.class);

	/** Current session. */
	private ISession _session;

	/**
	 * Ctor.
	 *
	 * @param	app		Application API.
	 */
	public FilterObjectTreeAction(IApplication app)
	{
		super(app);
	}

	/**
	 * Set the current session.
	 *
	 * @param	session		The current session.
	 */
	public void setSession(ISession session)
	{
		_session = session;
	}

	/**
	 * Perform this action.
	 *
	 * @param	evt	The current event.
	 */
	public void actionPerformed(ActionEvent evt)
	{
		IApplication app = getApplication();
		try
		{
			new ObjectTreeFilterDialog(app, _session).show();
		}
		catch (Throwable ex)
		{
			final String msg = "Error occured filtering the object tree";
			_session.getMessageHandler().showErrorMessage(msg + ": " + ex);
			s_log.error(msg, ex);
		}
	}

}
