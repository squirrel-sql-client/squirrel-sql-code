package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2003 Maury Hammel
 * mjhammel@users.sourceforge.net
 *
 * Adapted from SessionPropertiesCommand.java by Colin Bell.
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
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionWindowManager;
/**
 * This <CODE>ICommand</CODE> displays a dialog box that allows the user to
 * enter a 'where' clause or an 'order by' clause used when getting data via
 * the 'Contents' tab.
 *
 * @author <A HREF="mailto:mjhammel@users.sourceforge.net">Maury Hammel</A>
 */
public class SQLFilterCommand implements ICommand
{
	/** The session for which the SQL filter will be displayed/maintained. */
	private final ISession _session;
	/** A variable to contain a reference to the list of database objects and
	 * information about them.
	 */
	private final IDatabaseObjectInfo _objectInfo;

	/** Creates a new instance of SQLFilterCommand
	* @param session A variable to contain a reference to the current SQuirreL session instance.
	*
	* @param objectInfo A variable to contain a reference to the list of data objects and information
	* aobut them.
	*
	*/
	public SQLFilterCommand(ISession session, IDatabaseObjectInfo objectInfo)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("Null IDatabaseObjectInfo passed");
		}
		_session = session;
		_objectInfo = objectInfo;
	}

	/**
	 * Display the SQL Filter dialog.
	 */
	public void execute()
	{
		if (_session != null)
		{
			SessionWindowManager winMgr =
				_session.getApplication().getSessionWindowManager();
			winMgr.showSQLFilterDialog(_session, _objectInfo);
		}
	}
}
