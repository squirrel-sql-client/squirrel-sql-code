package net.sourceforge.squirrel_sql.plugins.dbcopy.actions;
/*
 * Copyright (C) 2005 Rob Manning
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
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.DBCopyPlugin;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;
import net.sourceforge.squirrel_sql.plugins.dbcopy.commands.PasteTableCommand;


public class PasteTableAction extends SquirrelAction
                                     implements ISessionAction {

	/** Current plugin. */
	private final SessionInfoProvider sessionInfoProv;

    /** The IApplication that we can use to display error dialogs */
    private IApplication app = null;
    
    /** Logger for this class. */
    private final static ILogger log = 
                         LoggerController.createLogger(PasteTableAction.class);    
    
    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(PasteTableAction.class);    
    
    /**
     * Creates a new SQuirreL action that gets fired whenever the user chooses
     * the paste operation.
     * 
     * @param app
     * @param rsrc
     * @param plugin
     */
    public PasteTableAction(IApplication app, Resources rsrc,
    									DBCopyPlugin plugin) {
        super(app, rsrc);
        this.app = app;
        sessionInfoProv = plugin;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent evt) {
        ISession destSession = sessionInfoProv.getCopyDestSession();
        IObjectTreeAPI api = 
            destSession.getObjectTreeAPIOfActiveSessionWindow();
        if (api == null) {
            return;
        }
        IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
        if (dbObjs.length > 1) {
            sessionInfoProv.setDestSelectedDatabaseObject(null);
            //i18n[PasteTableAction.error.multischemapaste=The paste 
            //operation may only be applied to one schema at a time]
            String msg =
            	s_stringMgr.getString("PasteTableAction.error.multischemapaste");
            app.showErrorDialog(msg);
            		            
            return;
        } else {
        	// When the user pastes on a TABLE label which is located under a 
        	// schema/catalog, build the schema DatabaseObjectInfo.
        	if (DatabaseObjectType.TABLE_TYPE_DBO.equals(dbObjs[0].getDatabaseObjectType())) {
        		IDatabaseObjectInfo tableLabelInfo = dbObjs[0];
        		ISQLConnection destCon = destSession.getSQLConnection();
        		SQLDatabaseMetaData md = null;
        		if (destCon != null) {
        			md = destCon.getSQLMetaData();
        		}
        		IDatabaseObjectInfo schema = 
        			new DatabaseObjectInfo(null, 
        								   tableLabelInfo.getSchemaName(),
        								   tableLabelInfo.getSchemaName(),
        								   DatabaseObjectType.SCHEMA,
        								   md);
        		sessionInfoProv.setDestSelectedDatabaseObject(schema);
        	} else {
        		sessionInfoProv.setDestSelectedDatabaseObject(dbObjs[0]);
        	}
            
        }
        
        try {
            IDatabaseObjectInfo info
                            = sessionInfoProv.getDestSelectedDatabaseObject();
            if (info == null || destSession == null) {
                return;
            }
            if (!checkSession(destSession, info)) {
                return;
            }
        } catch (UserCancelledOperationException e) {
            return;
        }
        if (sessionInfoProv.getCopySourceSession() == null) {
            return;
        }        
        if (!sourceDestSchemasDiffer()) {
            // TODO: tell the user that the selected destination schema is 
            // the same as the source schema.
            //monitor.showMessageDialog(...)            
            return;
        }
        new PasteTableCommand(sessionInfoProv).execute();
    }

	/**
	 * Set the current session.
	 * 
	 * @param	session		The current session.
	 */
    public void setSession(ISession session) {
        sessionInfoProv.setDestCopySession(session);        
    }
    
    /**
     * This a work-around for the fact that some databases in SQuirreL show 
     * "schemas" as catalogs (MySQL) while most others show them as "schemas".
     * If we restrict the Paste menu-item to schemas, then it won't appear in
     * the context menu in the MySQL object tree.  However, if add catalogs to
     * the list of database objects that the paste menu item appears in, then 
     * we must be careful not to attempt the copy operation on databases where
     * schema != catalog.(Otherwise the copy operation will fail as the qualified
     * name will be [catalog].[tablename] instead of [schema].[tablename]
     * 
     * @param session
     * @param dbObjs
     * 
     * @return true if it is ok to proceed with the copy operation; false otherwise.
     */
    private boolean checkSession(ISession session, IDatabaseObjectInfo dbObj) 
        throws UserCancelledOperationException 
    {
        if (session == null || dbObj == null) {
            return true;
        }
        String typeName = dbObj.getDatabaseObjectType().getName();
        
        log.debug("PasteTableAction.checkSession: dbObj type="+typeName+
                  " name="+dbObj.getSimpleName());

        HibernateDialect d = 
            DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                      session.getApplication().getMainFrame(), 
                                      session.getMetaData());
        if (!d.canPasteTo(dbObj)) {
            //i18n[PasteTableAction.error.destdbobj=The destination database 
            //doesn't support copying tables into '{0}' objects.\n Please 
            //select a schema to paste into.]
            String errmsg = 
                s_stringMgr.getString("PasteTableAction.error.destdbobj",
                                      new Object[] { typeName });
            app.showErrorDialog(errmsg);
            return false;
        }
        return true;
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
