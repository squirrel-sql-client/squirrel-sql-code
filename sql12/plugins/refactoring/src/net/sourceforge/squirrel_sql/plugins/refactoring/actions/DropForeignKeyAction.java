package net.sourceforge.squirrel_sql.plugins.refactoring.actions;

/*
 * Copyright (C) 2007 Daniel Regli & Yannick Winiger
 * http://sourceforge.net/projects/squirrel-sql
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.resources.Resources;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.DropForeignKeyCommand;

public class DropForeignKeyAction extends AbstractRefactoringAction
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DropForeignKeyAction.class);

	public DropForeignKeyAction(IApplication app, Resources rsrc)
	{
		super(app, rsrc);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.actions.AbstractRefactoringAction#getCommand(net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo[])
	 */
	@Override
	protected ICommand getCommand(IDatabaseObjectInfo[] info)
	{
		return new DropForeignKeyCommand(_session, info);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.actions.AbstractRefactoringAction#getErrorMessage()
	 */
	@Override
	protected String getErrorMessage()
	{
		return s_stringMgr.getString("Shared.singleObjectMessage",
											  s_stringMgr.getString("Shared.tableObject"),
											  s_stringMgr.getString("DropForeignKeyAction.actionPart"));
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.actions.AbstractRefactoringAction#isMultipleObjectAction()
	 */
	@Override
	protected boolean isMultipleObjectAction()
	{
		return false;
	}
}
