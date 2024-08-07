package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;

/*
 * Copyright (C) 2001-2004 Colin Bell and Johan Compagner
 * colbell@users.sourceforge.net
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
/**
 * This <CODE>ICommand</CODE> allows the user to clone current connecction/tab
 *
 * @author jarmolow
 */
public class NewAliasConnectionCommand
{
	private final ConnectToAliasCommand connectToAliasCommand;

	public NewAliasConnectionCommand(SQLAlias sqlAlias)
	{
		connectToAliasCommand = new ConnectToAliasCommand(sqlAlias, true, null);
	}

	public void executeConnect()
	{
		connectToAliasCommand.executeConnect();
	}
}
