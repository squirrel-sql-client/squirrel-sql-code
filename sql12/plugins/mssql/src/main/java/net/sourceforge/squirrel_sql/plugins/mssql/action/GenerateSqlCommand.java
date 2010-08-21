package net.sourceforge.squirrel_sql.plugins.mssql.action;

/*
 * Copyright (C) 2004 Ryan Walberg <generalpf@yahoo.com>
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

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JFileChooser;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverProperty;
import net.sourceforge.squirrel_sql.fw.sql.WrappedSQLException;
import net.sourceforge.squirrel_sql.fw.util.*;

import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;
import net.sourceforge.squirrel_sql.plugins.mssql.gui.GenerateSqlDialog;
import net.sourceforge.squirrel_sql.fw.util.ExtensionFilter;
import net.sourceforge.squirrel_sql.plugins.mssql.util.MssqlIntrospector;

public class GenerateSqlCommand implements ICommand {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GenerateSqlCommand.class);

	private ISession _session;
	private final MssqlPlugin _plugin;

	private final IDatabaseObjectInfo[] _dbObjs;

	public GenerateSqlCommand(ISession session, MssqlPlugin plugin, IDatabaseObjectInfo[] dbObjs) {
		super();
		if (session == null)
			throw new IllegalArgumentException("ISession == null");
		if (plugin == null)
			throw new IllegalArgumentException("MssqlPlugin == null");
		if (dbObjs == null)
			throw new IllegalArgumentException("IDatabaseObjectInfo[] is null");

		_session = session;
		_plugin = plugin;
		_dbObjs = dbObjs;
	}

	public void execute() throws BaseException {
		try {
            /* because of the cross-catalog problem, let's not invoke this if the current catalog isn't equal
             * to the catalog specified in the URL.
             */
            
            SQLDriverProperty[] props = _session.getSQLConnection().getConnectionProperties().getDriverProperties();
            for (int i = 0; i < props.length; i++) {
                if (props[i].getName().equals("DBNAME")) {
                    // there's a DBNAME specified, so make sure it matches the current catalog.
                    if (!props[i].getValue().equals(_session.getSQLConnection().getCatalog())) {

							   String[] params = {props[i].getValue(), _session.getSQLConnection().getCatalog()};
							   // i18n[mmsql.catalogErr=The DBNAME of the session's URL is set to '{0}', but the session's current catalog is set to '{1}'.\n\nSQL Server doesn't support this in most cases.  This is a current issue.]
								_session.getApplication().showErrorDialog(s_stringMgr.getString("mmsql.catalogErr", params));
                        return;
                    }
                }
            }
            
			GenerateSqlDialog dlog = new GenerateSqlDialog(_session, _plugin, _dbObjs);
            dlog.preselectObjects(_dbObjs);
			dlog.pack();
			GUIUtils.centerWithinParent(dlog);
			if (!dlog.showGeneralSqlDialog())
                return;
            
            JFileChooser fc = new JFileChooser();
            if (dlog.getOneFile()) {
                ExtensionFilter ef = new ExtensionFilter();
					 // i18n[mmsql.sqlScripts=SQL Scripts]
					 ef.addExtension(s_stringMgr.getString("mmsql.sqlScripts"),"sql");
					 // i18n[mmsql.textFiles=Text Files]
					 ef.addExtension(s_stringMgr.getString("mmsql.textFiles"),"txt");
                fc.setFileFilter(ef);
            }
            else
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            
            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                
                FileWriter fw = null;
                
                if (dlog.getOneFile()) {
                    fw = new FileWriter(fc.getSelectedFile(),false);
                    
                    if (dlog.getScriptDatabase())
                        fw.write(MssqlIntrospector.generateCreateDatabaseScript(_session.getSQLConnection().getCatalog(),_session.getSQLConnection()));
                    
                    if (dlog.getScriptUsersAndRoles())
                        fw.write(MssqlIntrospector.generateUsersAndRolesScript(_session.getSQLConnection().getCatalog(),_session.getSQLConnection()));
                }
                
                ArrayList<IDatabaseObjectInfo> objs = dlog.getSelectedItems();
                for (int i = 0; i < objs.size(); i++) {
                    IDatabaseObjectInfo oi = objs.get(i);
                    
                    if (!dlog.getOneFile())
                        fw = new FileWriter(fc.getSelectedFile() + java.io.File.separator + MssqlIntrospector.getFixedVersionedObjectName(oi.getSimpleName()) + ".txt",false);
                    
                    if (dlog.getGenerateDrop())
                        fw.write(MssqlIntrospector.generateDropScript(oi));
                    
                    if (dlog.getGenerateCreate()) {
                        String script = MssqlIntrospector.generateCreateScript(oi, _session.getSQLConnection(),dlog.getScriptConstraints());
                        fw.write(script);
                    }
                    
                    if (dlog.getScriptIndexes()) {
                        String script = MssqlIntrospector.generateCreateIndexesScript(oi, _session.getSQLConnection());
                        fw.write(script);
                    }
                    
                    if (dlog.getScriptTriggers()) {
                        String script = MssqlIntrospector.generateCreateTriggersScript(oi, _session.getSQLConnection());
                        fw.write(script);
                    }
                    
                    if (dlog.getScriptPermissions()) {
                        String script = MssqlIntrospector.generatePermissionsScript(oi, _session.getSQLConnection());
                        fw.write(script);
                    }
                    
                    if (!dlog.getOneFile())
                        fw.close();
                }
                if (dlog.getOneFile())
                    fw.close();
            }
		}
		catch (SQLException ex) {
            ex.printStackTrace();
			throw new WrappedSQLException(ex);
		}
        catch (IOException ex) {
            ex.printStackTrace();
			_session.getApplication().showErrorDialog(ex.getMessage());
        }
	}
    
}
