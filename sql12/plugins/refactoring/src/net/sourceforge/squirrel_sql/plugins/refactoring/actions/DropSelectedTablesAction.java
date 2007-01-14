package net.sourceforge.squirrel_sql.plugins.refactoring.actions;
/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net 
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
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.DropTablesCommand;

public class DropSelectedTablesAction extends AbstractRefactoringAction
										implements IObjectTreeAction
{
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DropSelectedTablesAction.class);   
    
	/** Title for confirmation dialog. */
	private static final String TITLE = 
        s_stringMgr.getString("DropSelectedTablesAction.title");

	/** Message for confirmation dialog. */
	private static final String MSG = 
        s_stringMgr.getString("DropSelectedTablesAction.message");


	/**
	 * @param	app	Application API.
	 */
	public DropSelectedTablesAction(IApplication app, Resources rsrc)
	{
		super(app, rsrc);
	}

    @Override
    protected ICommand getCommand(IDatabaseObjectInfo[] info) {
        return new DropTablesCommand(_session, info);
    }

    @Override
    protected String getErrorMessage() {
        return "";
    }

    @Override
    protected boolean isMultipleObjectAction() {
        return true;
    }
}
