package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2002 Johan Compagner
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
import java.sql.Statement;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * @version 	$Id: DropTableAction.java,v 1.2 2002-03-26 12:15:35 colbell Exp $
 * @author		Johan Compagner
 */
public class DropTableAction extends SquirrelAction implements ISessionAction
{
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(DropTableAction.class);

	private ISession _session;

	/**
	 * Constructor for DropTableAction.
	 * @param app
	 * @throws IllegalArgumentException
	 */
	public DropTableAction(IApplication app) throws IllegalArgumentException
	{
		super(app);
	}

	/*
	 * @see ActionListener#actionPerformed(ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		IDatabaseObjectInfo[] selected = _session.getSelectedDatabaseObjects();
		if(selected != null && selected.length > 0)
		{
			int option = JOptionPane.showInternalConfirmDialog(_session.getSessionSheet(),"Are you sure?","Dropping table(s)",JOptionPane.YES_NO_OPTION);
			if(option == JOptionPane.YES_OPTION)
			{
				try
				{
					SQLConnection connection = _session.getSQLConnection();
					Statement statement = connection.createStatement();
					for (int i = 0; i < selected.length; i++)
					{
						String name = selected[i].getSimpleName();
						_session.getMessageHandler().showMessage("dropping table " + name);
						statement.executeUpdate("drop table " + name);
					}
					_session.getSessionSheet().refreshTree();
				} catch(Exception ex)
				{
					_session.getMessageHandler().showMessage("dropping table(s) failed: " + ex.getMessage());
					s_log.error("Dropping table(s) failed",ex);
				}
			}
		}
	}

	/*
	 * @see ISessionAction#setSession(ISession)
	 */
	public void setSession(ISession session)
	{
		_session = session;
	}

}
