package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2003-2004 Maury Hammel
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

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This <CODE>ICommand</CODE> displays a dialog box that allows the user to
 * enter a 'where' clause or an 'order by' clause used when getting data via
 * the 'Contents' tab.
 *
 * @author <A HREF="mailto:mjhammel@users.sourceforge.net">Maury Hammel</A>
 *
 * TODO: Change name to ContentsTabFilterCommand
 */
public class SQLFilterCommand implements ICommand
{
	/** The object tree containing the object tto be filtered. */
	private final IObjectTreeAPI _objectTree;

	/** The object we are filtering. */
	private final IDatabaseObjectInfo _objectInfo;

	/**
	 * Creates a new instance of SQLFilterCommand.
	 *
	 * @param	objectTree	The object tree containing the table we are
	 *						filtering
	 * @param	objectInfo	The table to be filtered.
	 */
	public SQLFilterCommand(IObjectTreeAPI objectTree,
								IDatabaseObjectInfo objectInfo)
	{
		super();
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("Null IDatabaseObjectInfo passed");
		}
		if (objectTree == null)
		{
			throw new IllegalArgumentException("Null IObjectTreeAPI passed");
		}
		_objectTree = objectTree;
		_objectInfo = objectInfo;
	}

	/**
	 * Display the SQL Filter dialog.
	 */
	public void execute()
	{
		if (_objectTree != null)
		{
			final ISession session = _objectTree.getSession();
			final IApplication app = session.getApplication();
			final WindowManager winMgr = app.getWindowManager();
			winMgr.showSQLFilterDialog( _objectTree, _objectInfo);
		}
	}
}
