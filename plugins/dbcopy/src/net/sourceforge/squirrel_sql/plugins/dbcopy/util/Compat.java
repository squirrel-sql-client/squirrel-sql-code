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
package net.sourceforge.squirrel_sql.plugins.dbcopy.util;

import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A utility class which seeks to provide "version-aware" implementations of 
 * SQuirreL API methods.  The idea here is to limit the number of different
 * compatibility versions that the DBCopy plugin needs to support it's use in 
 * most versions of SQuirreL.
 */
public class Compat {

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(Compat.class);
    
    /**
     * This method normalizes the various versions of SQuirreL with regard to 
     * getting tables with the specified characteristics using the specified 
     * SQLDatabaseMetaData. Since there are at least three variations between
     * versions of 1.x, 2.x, and 2.3, it was necessary to combine the logic here
     * to prevent people from having to decide which version of the plugin to 
     * download.
     * 
     * @param data
     * @param catalog
     * @param schema
     * @param tableName
     * @return
     * @throws SQLException
     */
    public static ITableInfo[] getTables(SQLDatabaseMetaData data, 
                                          String catalog, 
                                          String schema, 
                                          String tableName) 
        throws SQLException 
    {
        ITableInfo[] result = new ITableInfo[0];
        Class c = data.getClass();
        Class progressCallBackClass = 
            safeForName("net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack");
        
        Object[] args = null;
        Method m = null;
        Class[] argTypes = null;
        if (progressCallBackClass != null)  
        {
            argTypes = new Class[] { String.class, 
                                     String.class, 
                                     String.class,
                                     String[].class,
                                     progressCallBackClass };
            args = new Object[]{catalog, schema, tableName, null, null };
            s_log.debug("Compat.getTables found ProgressCallBack interface");
        } else {
            argTypes = new Class[] { String.class, 
                                     String.class, 
                                     String.class,
                                     String[].class };
            
            args = new Object[]{catalog, schema, tableName, null };
            s_log.debug("Compat.getTables didn't find ProgressCallBack interface");
        }

        try {
            m = c.getDeclaredMethod("getTables", argTypes);
            result = (ITableInfo[])m.invoke(data, args);
        } catch (Exception e) {
            s_log.error("Encountered unexpected exception when attempting to "+
                        "call SQLDatabaseMetaData.getTables with args = "+args);
        }
        return result;
    }

    /**
     * Figures out which method of ISession that gets the IObjectTreeAPI is 
     * available and invokes it, returning the result.
     * 
     * @param session
     * @param plugin
     * @return
     */
    public static IObjectTreeAPI getIObjectTreeAPI(ISession session, 
                                                   IPlugin plugin) {
        IObjectTreeAPI result = null;
        Class c = getISessionInterface(session);
        if (c == null) {
            return null;
        }
        Method getObjectTreeAPIMethod = null;
        Object[] args = null;
        
        Method[] methods = c.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String methodName = method.getName();
            if (methodName.equals("getObjectTreeAPIOfActiveSessionWindow")) {
                getObjectTreeAPIMethod = method;
                args = null;
                break;
            }
            if (methodName.equals("getObjectTreeAPI")) {
                getObjectTreeAPIMethod = method;
                args = new Object[] { plugin };
                break;
            }

        }
        if (getObjectTreeAPIMethod != null) {
            try {
                result = 
                    (IObjectTreeAPI)getObjectTreeAPIMethod.invoke(session, args);
            } catch (Exception e) {
                String methodName = getObjectTreeAPIMethod.getName();
                s_log.error("Encountered reflection exception when " +
                            "attempting to invoke method " + methodName +
                            " of Session", e);
            }            
        } else {
            s_log.error(
                "Failed to find either getObjectTreeAPIOfActiveSessionWindow()" +
                " or getObjectTreeAPI(IPlugin) methods in the specified " +
                "ISession argument");
        }
        return result;
    }
    
    /**
     * Reloads the specified db object using the specified Session.
     * @param session
     * @param info
     */
    public static void reloadSchema(ISession session, 
    								IDatabaseObjectInfo info) 
    {
        Class c = getISessionInterface(session);
        if (c == null) {
            return;
        }
        Method[] methods = c.getDeclaredMethods();
        Object schemaInfoObj = null;
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            String methodName = method.getName();
            if (methodName.equals("getSchemaInfo")) {
            	try {
            		schemaInfoObj = method.invoke(session, null);
            	} catch (Exception e) {
                    s_log.error("Encountered reflection exception when " +
                            "attempting to invoke method " + methodName +
                            " of Session", e);
            	}
            	break;
            }
        }
        if (schemaInfoObj != null) {
        	methods = schemaInfoObj.getClass().getDeclaredMethods();
        	for (int i = 0; i < methods.length; i++) {
        		Method method = methods[i];
        		String methodName = method.getName();
        		if (methodName.equals("reload")) {
        			try {
        				method.invoke(schemaInfoObj, new Object[] {info});
        			} catch (Exception e) {
                        s_log.error("Encountered reflection exception when " +
                                "attempting to invoke method " + methodName +
                                " of SchemaInfo", e);        				
        			}
        		}
        	}
        }
    	
    }
    
    /**
     * Retrieves whether this session treats mixed case unquoted SQL 
     * identifiers as case insensitive and stores them in upper case.
	 *
     * @param session
     * @return true if so; false otherwise
     * @throws SQLException
     */
    public static boolean storesUpperCaseIdentifiers(ISession session) 
    {
    	boolean result = false;
    	SQLDatabaseMetaData md = session.getSQLConnection().getSQLMetaData();
    	try {
    		Method m = 
    			md.getClass().getDeclaredMethod("storesUpperCaseIdentifiers", null);
    		Boolean invResult = (Boolean)m.invoke(md, null);
    		result = invResult.booleanValue();
    	} catch (Exception e) {
    		try {
    			DatabaseMetaData dbmd = 
    				session.getSQLConnection().getConnection().getMetaData();
    			result = dbmd.storesUpperCaseIdentifiers();
    		} catch (Exception ex) {
    			s_log.debug(
    				"Unable to determine if the session "+session.getTitle()+
    				" stores uppercase identifiers: "+ex.getMessage());
    		}
    	}
    	return result;
    }
        
    /**
     * Test to see if the specified className is available using the current 
     * ClassLoader.   
     * 
     * @param className the name of the Class to seek.
     * @return the Class that represents the specified className or null if the 
     *         class isn't available.
     */
    private static Class safeForName(String className) {
        Class result = null;
        try {
            result = Class.forName(className);
        } catch (ClassNotFoundException e) {
            // Class doesn't exist in this release
        }
        return result;
    }
    
    private static Class getISessionInterface(ISession session) {
        Class result = null;
    	// Need to use the interface since the implementation passed in as 
        // ISession might not be a public class.
        Class[] classes = session.getClass().getInterfaces();
        for (int i = 0; i < classes.length; i++) {
            Class class1 = classes[i];
            if (class1.getName().endsWith("ISession")) {
                result = class1;
            }
        }    	
        if (result == null) {
            s_log.error("Failed to find the public interface ISession for " +
                        " the specified argument session");
        }
        return result;
    }
}
