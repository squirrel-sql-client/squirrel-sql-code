package net.sourceforge.squirrel_sql.plugins.refactoring.actions;
/*
 * Copyright (C) 20056 Rob Manning
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
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.RemoveColumnCommand;

public class RemoveColumnAction extends SquirrelAction
                                     implements ISessionAction {

	/** Current session. */
    private ISession _session;

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(RemoveColumnAction.class);
    
    
    public RemoveColumnAction(IApplication app, 
                               Resources rsrc) 
    {
        super(app, rsrc);
    }

    public void actionPerformed(ActionEvent evt) {
        
        if (_session != null) {
            IObjectTreeAPI api = _session.getObjectTreeAPIOfActiveSessionWindow();
            if (api != null) {
                IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();

                if (dbObjs.length != 1) {
                    //i18n[RemoveColumnAction.singleObjectMessage=You must have 
                    //a single table selected to drop a column]
                    String msg = 
                        s_stringMgr.getString("RemoveColumnAction.singleObjectMessage");
                    _session.getMessageHandler().showErrorMessage(msg);
                } else {
                    new RemoveColumnCommand(_session, dbObjs[0]).execute();
                }           
            }
        }
    }

	/**
	 * Set the current session.
	 * 
	 * @param	session		The current session.
	 */
    public void setSession(ISession session) {
        _session = session;
    }
    
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
       setEnabled(null != _session);
    }    
    
}