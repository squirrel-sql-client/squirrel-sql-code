package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2003-2004 Maury Hammel
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
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
/**
 * This <CODE>ICommand</CODE> displays a dialog box that allows the user to
 * enter a 'where' clause or an 'order by' clause used when getting data via
 * the 'Contents' tab.
 *
 * Adapted from EditWhereColsCommand.java by Maury Hammel
 *
 * @author <A HREF="mailto:mjhammel@users.sourceforge.net">Maury Hammel</A>
 */
public class EditWhereColsCommand implements ICommand
{
	/** Application API. */
	final IApplication _app;

	/** The object treeidentifying the table for us to limit the columns. */
	private final IObjectTreeAPI _tree;
	
	/**
	 * A variable to contain a reference to the list of database objects and
	 * information about them.
	 */
	private final IDatabaseObjectInfo _objectInfo;

	public EditWhereColsCommand(IApplication app, IObjectTreeAPI tree,
								IDatabaseObjectInfo objectInfo)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}
		if (tree == null)
		{
			throw new IllegalArgumentException("IObjectTreeAPI == null");
		}
		if (objectInfo == null)
		{
			throw new IllegalArgumentException("IDatabaseObjectInfo == null");
		}
		_app = app;
		_tree = tree;
		_objectInfo = objectInfo;
	}

	/**
	 * Display thedialog.
	 */
	public void execute()
	{
		if (_tree != null)
		{
			final WindowManager winMgr = _app.getWindowManager();
			winMgr.showEditWhereColsDialog(_tree, _objectInfo);
		}
	}
}
