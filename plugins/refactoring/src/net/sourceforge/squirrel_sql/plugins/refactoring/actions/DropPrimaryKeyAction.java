package net.sourceforge.squirrel_sql.plugins.refactoring.actions;
/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
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
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.DropPrimaryKeyCommand;

public class DropPrimaryKeyAction extends AbstractRefactoringAction
                                     implements ISessionAction {

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DropPrimaryKeyAction.class);
    
    private static interface i18n {
        ///i18n[DropPrimaryKeyAction.dropPrimaryKeyPart=drop a primary key]
        String columnPart = 
            s_stringMgr.getString("DropPrimaryKeyAction.dropPrimaryKeyPart");
        //i18n[Shared.singleObjectMessage=You must have a single table selected
        //in order to {0}]
        String singleObjectMessage = 
            s_stringMgr.getString("Shared.singleObjectMessage", columnPart); 
    }
    
    public DropPrimaryKeyAction(IApplication app, 
                                Resources rsrc) 
    {
        super(app, rsrc); 
    }

    protected ICommand getCommand(IDatabaseObjectInfo info) {
        return new DropPrimaryKeyCommand(_session, info);
    }
    
    protected String getErrorMessage() {
        return i18n.singleObjectMessage;
    }    
}