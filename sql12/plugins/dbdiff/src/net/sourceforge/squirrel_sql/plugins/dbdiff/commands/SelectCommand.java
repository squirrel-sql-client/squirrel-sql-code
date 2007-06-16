package net.sourceforge.squirrel_sql.plugins.dbdiff.commands;

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

import javax.swing.ProgressMonitor;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.DBDiffPlugin;

public class SelectCommand implements ICommand
{
    /**
     * Current session.
     */
    private ISession _session;
    
    /**
     * Current plugin.
     */
    private final DBDiffPlugin _plugin;
    
    /** Logger for this class. */
    private final static ILogger log = 
                         LoggerController.createLogger(SelectCommand.class);
    
    /** Internationalized strings for this class */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SelectCommand.class);   
    
    static interface i18n {
        
        //i18n[CopyTablesCommand.progressDialogTitle=Analyzing FKs in Tables to Copy]
        String PROGRESS_DIALOG_TITLE = 
            s_stringMgr.getString("CopyTablesCommand.progressDialogTitle");
        
        //i18n[CopyTablesCommand.loadingPrefix=Analyzing table:]
        String LOADING_PREFIX = 
            s_stringMgr.getString("CopyTablesCommand.loadingPrefix");        
        
    }
    
    
    /** When analyzing FK dependencies for the selected tables, show the
     *  user the progress since this can take a while.
     */
    ProgressMonitor pm = null;
    
    int progressCount = 0;
    
    /**
     * Ctor specifying the current session.
     */
    public SelectCommand(ISession session, DBDiffPlugin plugin)
    {
        super();
        _session = session;
        _plugin = plugin;
    }
    
    /**
     * Execute this command. Save the session and selected objects in the plugin
     * for use in paste command.
     */
    public void execute()
    {
        IObjectTreeAPI api = _session.getObjectTreeAPIOfActiveSessionWindow();
        if (api != null) {
            IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
            try {
                _plugin.setDiffSourceSession(_session);
                _plugin.setSelectedDatabaseObjects(dbObjs);
                _plugin.setCompareMenuEnabled(true);
            } catch (Exception e) {
                log.error("Unexected exception: ", e);
            }
        } 
    }
        
}