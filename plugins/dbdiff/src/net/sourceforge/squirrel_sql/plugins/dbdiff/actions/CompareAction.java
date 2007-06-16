package net.sourceforge.squirrel_sql.plugins.dbdiff.actions;
/*
 * Copyright (C) 2007 Rob Manning
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
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider;
import net.sourceforge.squirrel_sql.plugins.dbdiff.commands.CompareCommand;


public class CompareAction extends SquirrelAction
                                     implements ISessionAction {

	/** Current plugin. */
	private final SessionInfoProvider sessionInfoProv;

    /** The IApplication that we can use to display error dialogs */
    private IApplication app = null;
    
    /** Logger for this class. */
    private final static ILogger log = 
                         LoggerController.createLogger(CompareAction.class);    
    
    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(CompareAction.class);    
    
    /**
     * Creates a new SQuirreL action that gets fired whenever the user chooses
     * the paste operation.
     * 
     * @param app
     * @param rsrc
     * @param plugin
     */
    public CompareAction(IApplication app, 
                         Resources rsrc, 
                         SessionInfoProvider prov) {
        super(app, rsrc);
        this.app = app;
        sessionInfoProv = prov;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        ISession destSession = sessionInfoProv.getDiffDestSession();
        IObjectTreeAPI api = destSession.getObjectTreeAPIOfActiveSessionWindow();
        if (api == null) {
            return;
        }
        IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
        sessionInfoProv.setDestSelectedDatabaseObjects(dbObjs);

        if (sessionInfoProv.getDiffSourceSession() == null) {
            return;
        }        
        if (!sourceDestSchemasDiffer()) {
            // TODO: tell the user that the selected destination schema is 
            // the same as the source schema.
            //monitor.showMessageDialog(...)            
            return;
        }
        new CompareCommand(sessionInfoProv).execute();
    }

	/**
	 * Set the current session.
	 * 
	 * @param	session		The current session.
	 */
    public void setSession(ISession session) {
        sessionInfoProv.setDestDiffSession(session);        
    }
        
    /**
     * Returns a boolean value indicating whether or not the source and 
     * destination sessions refer to the same schema.
     * 
     * @return
     */
    private boolean sourceDestSchemasDiffer() {
        //ISession sourceSession = sessionInfoProv.getCopySourceSession();
        //ISession destSession = sessionInfoProv.getCopyDestSession();
        
        // TODO: check to be sure that the source and destination schemas are
        // different. Abort if they are the same and inform the user.
        
        return true;
    }    
}
