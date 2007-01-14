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
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;

public abstract class AbstractRefactoringAction extends SquirrelAction
                                                implements ISessionAction {

	/** Current session. */
    protected ISession _session;
    
    /** API for the current tree. */
    protected IObjectTreeAPI _tree;
    
        
    public AbstractRefactoringAction(IApplication app, 
                                     Resources rsrc) 
    {
        super(app, rsrc); 
    }

    public void actionPerformed(ActionEvent evt) {
        if (_session != null) {
            IObjectTreeAPI api = 
                _session.getObjectTreeAPIOfActiveSessionWindow();
            IDatabaseObjectInfo[] infos = api.getSelectedDatabaseObjects();
            if (infos.length > 1 && !isMultipleObjectAction()) {
                _session.getMessageHandler().showMessage(getErrorMessage());
            } else {
                try {
                    getCommand(infos).execute();
                } catch (Exception e) {
                    _session.getMessageHandler().showMessage(e);
                }            
            }
        }
    }

    protected abstract ICommand getCommand(IDatabaseObjectInfo[] info);
    
    protected abstract boolean isMultipleObjectAction();
    
    protected abstract String getErrorMessage();

    public void setObjectTree(IObjectTreeAPI tree)
    {
       if(null != tree)
       {
          _session = tree.getSession();
       }
       else
       {
          _session = null;
       }
       _tree = tree;
       setEnabled(null != _session);
    }    
    
    // ISessionAction implementation
    
	/**
	 * Set the current session.
	 * 
	 * @param	session		The current session.
	 */
    public void setSession(ISession session) {
        _session = session;
    }
}