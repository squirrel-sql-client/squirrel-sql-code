/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.oracle.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class DBUtil {
    
    /** Logger for this class. */
    private static final ILogger s_log =
        LoggerController.createLogger(DBUtil.class);
    
    public static final String TABLE_SCRIPT_CREATE_CLASS = 
        "net.sourceforge.squirrel_sql.plugins.sqlscript.table_script.CreateTableScriptCommand";
    
    public static final String SCRIPT_PLUGIN_CLASS = 
        "net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin";
    
    
    public static String getTableSource(ISession session, 
                                        IDatabaseObjectInfo dboInfo) 
    {
        String result = null;

        try {
            Class CreateTableScriptCommandClass = 
                Class.forName(TABLE_SCRIPT_CREATE_CLASS);
            Class SQLScriptPluginClass = Class.forName(SCRIPT_PLUGIN_CLASS);
            Class[] args = new Class[] { ISession.class, SQLScriptPluginClass};
            Method createTableScriptStringMethod = 
                CreateTableScriptCommandClass.getDeclaredMethod("createTableScriptString", IDatabaseObjectInfo.class);
            Constructor con = CreateTableScriptCommandClass.getConstructor(args);
            Object CreateTableScriptCommandObj = con.newInstance(session, null);
            Object script = 
                createTableScriptStringMethod.invoke(CreateTableScriptCommandObj, dboInfo);
            result = (String)script;       
        } catch (Throwable e) {
            if (s_log.isInfoEnabled()) {
                s_log.info("Encountered exception while trying to execute " +
                           "CreateTableScriptCommand.createTableScript: "+
                           e.getMessage(), e);
            }
            return "Requires the SQLScript plugin to be installed/loaded";
        }
        
        return result;
    }
}
