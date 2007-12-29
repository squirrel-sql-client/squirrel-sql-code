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
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.AddSequenceCommand;

public class AddSequenceAction extends AbstractRefactoringAction implements IObjectTreeAction {
    public AddSequenceAction(IApplication app, Resources rsrc) {
        super(app, rsrc);
    }


    @Override
    protected ICommand getCommand(IDatabaseObjectInfo[] info) {
        return new AddSequenceCommand(_session, info);
    }


    @Override
    protected String getErrorMessage() {
        return null;
    }


    @Override
    protected boolean isMultipleObjectAction() {
        return true;
    }
}
