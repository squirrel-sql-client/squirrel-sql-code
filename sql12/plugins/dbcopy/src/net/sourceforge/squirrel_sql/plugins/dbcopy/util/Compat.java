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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.Action;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A utility class which seeks to provide "version-aware" implementations of 
 * SQuirreL API methods.  The idea here is to limit the number of different
 * compatibility versions that the DBCopy plugin needs to support it's use in 
 * most versions of SQuirreL. Currently, this allows there to be just two 
 * versions of the DBCopy plugin that are compatible for use with 1.2beta6 and
 * 2.x.  The only reason that there must be a version for 1.2beta6 at this point
 * is due to the plugin interface change which makes implementatios of the 
 * ISessionPlugin (which is how SQuirreL's plugin manager finds plugins) 
 * incompatible for use with all versions of SQuirreL.  Someday I'd like to 
 * either get everyone off of 1.2beta6 (fat chance), or figure out a way to 
 * get both versions PluginManager's to detect a single version of this plugin.  
 */
public class Compat {

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(Compat.class);
    
    private static final String PRE_2_3_SCHEMAINFO_LOCATION = 
    	"net.sourceforge.squirrel_sql.client.session.SchemaInfo";
    
    private static final String POST_2_3_SCHEMAINFO_LOCATION = 
    	"net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo";
    
    /** Once we find the class, we don't need to search for it up again. */
    private static Class schemaInfoClass = null;
    
    /** Once we find the method, we don't need to search for it up again. */
    private static Method isKeywordMethod = null;
    
    /** ProgressCallBack interface didn't exist prior to version 2.2 */
    private static final Class[] PRE_2_2_GET_TABLES_METHOD_ARG_TYPES =   
    										new Class[] { String.class, 
        												  String.class, 
        												  String.class,
        												  String[].class };
    
    /** The null gets set to ProgressCallBack interface later if it's available. */
    private static final Class[] POST_2_2_GET_TABLES_METHOD_ARG_TYPES =
    										new Class[] { String.class, 
        												  String.class, 
        												  String.class,
        												  String[].class,
        												  null };
    
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
    public static ITableInfo[] getTables(ISession session, 
                                          String catalog, 
                                          String schema, 
                                          String tableName) 
    {
        ITableInfo[] result = new ITableInfo[0];
        
        Object[] args = null;
        Method m = null;

        Object schemaInfo = null;
        Class schemaInfoClass = 
        	safeForName("net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo");
        if (schemaInfoClass != null) {
        	Class[] params = 
        		new Class[] {String.class, String.class, String.class };
        	m = safeGetMethod(schemaInfoClass, "getITableInfos", params);
        	// The following is quivalent to:
        	// SchemaInfo schemaInfo = session.getSchemaInfo()
        	//
        	schemaInfo = safeGetObject(session, "getSchemaInfo", null);
        } 
        if (m != null) {
	        try {
	        	// The following is quivalent to: 
	        	// ITableInfo[] result = schemaInfo.getITableInfos(catalog, schema, tableName)
	        	//
	        	args = new Object[] { catalog, schema, tableName };
	            result = (ITableInfo[])m.invoke(schemaInfo, args);
	        } catch (Exception e) {
	            s_log.error("Encountered unexpected exception when attempting to "+
	                        "call SQLDatabaseMetaData.getTables with args = "+
                  arrayToString(args));
	        }
        } 
        if (result == null || result.length == 0) {
        	// Fallback to the old method, going directly to the database instead
        	// of using SchemaInfo, since SchemaInfo didn't have it.
        	SQLDatabaseMetaData d = session.getSQLConnection().getSQLMetaData();
        	result = getTables(d, catalog, schema, tableName);
        }
        
        return result;
    }

   private static String arrayToString(Object[] args)
   {
      if(null == args)
      {
         return "" + null;
      }

      String ret = "";

      for (int i = 0; i < args.length; i++)
      {
         if(0 < ret.length())
         {
            ret += ",";
         }

         ret += "[" + args[i] + "]";

      }

      return ret;
   }


   /**
     * Older, pre-2.3 SQuirreL method of getting ITableInfos for the specified 
     * parameters.
     * 
     * @param data
     * @param catalog
     * @param schema
     * @param tableName
     * @return
     */
    private static ITableInfo[] getTables(SQLDatabaseMetaData data,
            							  String catalog, 
            							  String schema, 
            							  String tableName) 
    { 
        ITableInfo[] result = new ITableInfo[0];
        Class progressCallBackClass = 
            safeForName("net.sourceforge.squirrel_sql.fw.sql.ProgressCallBack");
        
        Object[] args = null;
        Method m = null;
        Class[] argTypes = null;
        
        if (progressCallBackClass != null)  
        {
        	POST_2_2_GET_TABLES_METHOD_ARG_TYPES[4] = progressCallBackClass;
        	argTypes = POST_2_2_GET_TABLES_METHOD_ARG_TYPES;
            args = new Object[]{catalog, schema, tableName, null, null };
            s_log.debug("Compat.getTables found ProgressCallBack interface");
        } else {
            argTypes = PRE_2_2_GET_TABLES_METHOD_ARG_TYPES;
            args = new Object[]{catalog, schema, tableName, null };
            s_log.debug("Compat.getTables didn't find ProgressCallBack interface");
        }

        try {
            Class c = data.getClass();        	
            m = c.getDeclaredMethod("getTables", argTypes);
            result = (ITableInfo[])m.invoke(data, args);
        } catch (Exception e) {
            s_log.error("Encountered unexpected exception when attempting to "+
                        "call SQLDatabaseMetaData.getTables with args = "+
               arrayToString(args));
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
        	Method fireSchemaInfoUpdateMethod = null;
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
        		if (methodName.equals("fireSchemaInfoUpdate")) {
        			fireSchemaInfoUpdateMethod = method;
        		}
        	}
        	if (fireSchemaInfoUpdateMethod != null) {
	        	try {
	        		fireSchemaInfoUpdateMethod.invoke(schemaInfoObj, 
	        										  (Object[]) null);
	        	} catch (Exception e) {
	                s_log.error("Encountered reflection exception when " +
	                        "attempting to invoke method fireSchemaInfoUpdate "+
	                        " of SchemaInfo", e);        				
				}
        	}
        }
    	
    }
    
    /**
     * Whether the specified session's database considers the specified string
     * to be a keyword.
     * 
     * @param session
     * @param data
     * @return
     */
    public static boolean isKeyword(ISession session, String data) {
        return session.getSchemaInfo().isKeyword(data);
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
    
    public static boolean isTableTypeDBO(Object o) {
    	Object dboObj = safeGetField(DatabaseObjectType.class, "TABLE_TYPE_DBO");
    	if (dboObj != null && dboObj.equals(o)) {
    		return true;
    	}
    	return false;
    }
        
    public static void addToPopupForTableFolder(IObjectTreeAPI api, 
    										    Action action) 
    {
    	Class dboClass = DatabaseObjectType.class;
    	Object dboObj = safeGetField(dboClass, "TABLE_TYPE_DBO");
    	if (dboObj != null) {
    		Class apiClass = api.getClass();
    		Class[] params = 
    			new Class[] {DatabaseObjectType.class, Action.class};
    		Method m = null;
    		try {
				m = apiClass.getMethod("addToPopup", params);
			} catch (SecurityException e) {
				s_log.debug(
					"Unable to get addToPopup method from IObjectTreeAPI " +
					"class: "+e.getMessage());
			} catch (NoSuchMethodException e) {
				s_log.debug(
					"Unable to get addToPopup method from IObjectTreeAPI " +
					"class: "+e.getMessage());
			}

			try {
                if (m != null) {
                    m.invoke(api, new Object[] { dboObj, action });
                }
			} catch (IllegalArgumentException e) {
				s_log.debug(
					"Unable to invoke addToPopup method from IObjectTreeAPI " +
					"class: "+e.getMessage());
			} catch (IllegalAccessException e) {
				s_log.debug(
					"Unable to invoke addToPopup method from IObjectTreeAPI " +
					"class: "+e.getMessage());
			} catch (InvocationTargetException e) {
				s_log.debug(
					"Unable to invoke addToPopup method from IObjectTreeAPI " +
					"class: "+e.getMessage());
			}
    	}
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
            if (s_log.isDebugEnabled()) {
            	s_log.debug("Compat.safeForName: found class "+className);
            }
        } catch (ClassNotFoundException e) {
            // Class doesn't exist in this release
        	if (s_log.isDebugEnabled()) {
        		s_log.debug(
        			"Compat.safeForName: class "+className+
        			" could not be located");
        	}
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
    
    private static Object safeGetObject(Object object, 
    									String method, 
    									Class[] parameterTypes) 
    {
    	Object result = null;
    	try {
    		Method m = object.getClass().getMethod(method, parameterTypes);
    		m.setAccessible(true);
    		result = m.invoke(object, parameterTypes);
    	} catch (Exception e) {
    		s_log.debug("Compat.safeGetObject: unable to execute method " +
    				"called "+method+" on class "+
    				object.getClass().getName());
    	}
    	return result;
    }

    private static Method safeGetMethod(Class c, 
    									String methodName, 
    									Class[] parameterTypes) {
    	Method result = null;
    	try {
    		result = c.getMethod(methodName, parameterTypes);
    	} catch (Exception e) {
    		String className = c.getName();
    		s_log.debug("Compat.safeGetMethod: No method called "+
    				methodName+" with parameters ("+
             arrayToString(parameterTypes) +") " +
    				"was located in class "+className);
    	}
    	return result;
    }
    
    private static Object safeGetField(Class fieldClass, String aFieldName) {
    	Object result = null;
    	Field[] fields = fieldClass.getDeclaredFields();
    	for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();
			if (fieldName.equals(aFieldName)) {
				try {
					result = field.get(null);
				} catch (IllegalAccessException e) {
					s_log.debug(
						"Unable to get the "+aFieldName+" field of class " +
						fieldClass.getName()+": "+e.getMessage());
				}
			}
		}    	
    	return result;
    }

    private static void initSchemaInfoClass() {
    	if (schemaInfoClass == null) {
	    	schemaInfoClass = safeForName(PRE_2_3_SCHEMAINFO_LOCATION);
	    	if (schemaInfoClass == null) {
	    		schemaInfoClass = safeForName(POST_2_3_SCHEMAINFO_LOCATION);
	    	}
    	}
    }
        
    private static void initIsKeywordMethod() {
    	if (isKeywordMethod == null) {
            try {
    			isKeywordMethod = 
    				schemaInfoClass.getDeclaredMethod("isKeyword", 
											 new Class[] { String.class });
    		} catch (NoSuchMethodException e) {
    			s_log.error(
    				"initIsKeywordMethod: Unexpected exception: "+
    				e.getMessage(), e);
    			
    		}
    	}
    }
}
