/*
 * Copyright (C) 2008 Michael Romankiewicz
 * mirommail(at)web.de
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
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.pref;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.IFirebirdManagerSessionPreferencesBean;

/**
 * Manager for loading and saving preferences
 * @author Michael Romankiewicz
 */
public class PreferencesManager {
    // Logger for this class
    private final static ILogger log = LoggerController.createLogger(PreferencesManager.class);
    
    public static final int PREFERENCES_BEAN_GLOBAL = 0;
    public static final int PREFERENCES_BEAN_BEACKUP_AND_RESTORE = 1;
    public static final int PREFERENCES_BEAN_CREATE_DATABASE = 2;
    public static final int PREFERENCES_BEAN_USERS = 3;
    public static final int PREFERENCES_BEAN_GRANT_AND_REVOKE = 4;
    
    // Name of the preferences files
    private static final String PREFERENCES_FILE_NAME_GLOBAL = "prefsGlobal.xml";    
    private static final String PREFERENCES_FILE_NAME_SESSION_BACKUP_RESTORE = "prefsBckRes.xml";    
    private static final String PREFERENCES_FILE_NAME_SESSION_CREATE_DB = "prefsCreateDb.xml";    
    private static final String PREFERENCES_FILE_NAME_SESSION_USERS = "prefsUsers.xml";    
    private static final String PREFERENCES_FILE_NAME_SESSION_GRANT_REVOKE = "prefsGrantRevoke.xml";    
    
    // Folder to store the user settings in
    private static File userSettingsFolder;
    
    // Bean for the global preferences
    private static FirebirdManagerPreferenceBean firebirdManagerPrefs = null;
    // the plugin
    private static IPlugin firebirdManagerPlugin = null;
    
    /**
     * Initialization method to load the global plugin preferences
     * @param plugin plugin
     * @throws PluginException plugin exception
     */
    public static void initialize(IPlugin plugin) throws PluginException {
        firebirdManagerPlugin = plugin;
        try {
            userSettingsFolder = firebirdManagerPlugin.getPluginUserSettingsFolder();
        } catch (IOException ex) {
            throw new PluginException(ex);
        }        
        firebirdManagerPrefs = (FirebirdManagerPreferenceBean)loadPreferences(PREFERENCES_BEAN_GLOBAL);
    }
    
    /**
     * Get the global preferences bean object
     * @return global preferences bean object
     */
    public static FirebirdManagerPreferenceBean getGlobalPreferences() {
        return firebirdManagerPrefs;
    }
    
    /**
     * Unload method to save the global preferences
     */
    public static void unload() {
        savePreferences(firebirdManagerPrefs, PREFERENCES_BEAN_GLOBAL);
    }
    
    /**
     * Save the session preferences   
     * @param sessionPreferencesBean session preferences bean 
     */
    public static void savePreferences(IFirebirdManagerSessionPreferencesBean sessionPreferencesBean, int beanType) {
    	String filename = getSessionPreferencesFilename(beanType);
        try {
            XMLBeanWriter writer = new XMLBeanWriter(sessionPreferencesBean);
            writer.save(new File(userSettingsFolder, filename));
        } catch (Exception e) {
            log.error("Cannot write the firebird manager preferences to file: "
                    + filename, e);
        }
    }

    /**
     * Load the session preferences 
     * @return session preferences bean 
     */
    public static IFirebirdManagerSessionPreferencesBean loadPreferences(int beanType) {
    	IFirebirdManagerSessionPreferencesBean prefBean = null;
    	String filename = getSessionPreferencesFilename(beanType);
    	if (filename.length() == 0) {
    		return getEmptyPreferencesBean(beanType);
    	}
        try {
            XMLBeanReader reader = new XMLBeanReader();
            File file = new File(userSettingsFolder, filename);
            reader.load(file, getSessionPreferencesClassloader(beanType));
            
            Iterator<Object> it = reader.iterator();
            if (it.hasNext()) {
            	prefBean = (IFirebirdManagerSessionPreferencesBean)it.next();
            }
        } catch (FileNotFoundException eNotFound) {
            log.info(filename + " not found. It will be created!");
        } catch (Exception e) {
            log.error("Cannot read from the firebird manager preferences file: "
                    + filename, e);
        }
        if (prefBean == null) {
    		return getEmptyPreferencesBean(beanType);
        }
        
        return prefBean;
    }    
    private static String getSessionPreferencesFilename(int beanType) {
    	switch (beanType) {
		case PREFERENCES_BEAN_GLOBAL:
			return PREFERENCES_FILE_NAME_GLOBAL;
		case PREFERENCES_BEAN_BEACKUP_AND_RESTORE:
			return PREFERENCES_FILE_NAME_SESSION_BACKUP_RESTORE;
		case PREFERENCES_BEAN_CREATE_DATABASE:
			return PREFERENCES_FILE_NAME_SESSION_CREATE_DB;
		case PREFERENCES_BEAN_USERS:
			return PREFERENCES_FILE_NAME_SESSION_USERS;
		case PREFERENCES_BEAN_GRANT_AND_REVOKE:
			return PREFERENCES_FILE_NAME_SESSION_GRANT_REVOKE;
		default:
			return "";
		}
    }
    private static IFirebirdManagerSessionPreferencesBean getEmptyPreferencesBean(int beanType) {
    	switch (beanType) {
		case PREFERENCES_BEAN_GLOBAL:
			return new FirebirdManagerPreferenceBean();
		case PREFERENCES_BEAN_BEACKUP_AND_RESTORE:
			return new FirebirdManagerBackupAndRestorePreferenceBean();
		case PREFERENCES_BEAN_CREATE_DATABASE:
			return new FirebirdManagerCreateDatabasePreferenceBean();
		case PREFERENCES_BEAN_USERS:
			return new FirebirdManagerUsersPreferenceBean();
//		case SESSION_BEAN_GRANT_AND_REVOKE:
//			return new FirebirdManagerGrantPreferenceBean();
		default:
			return null;
		}
    }
    private static ClassLoader getSessionPreferencesClassloader(int beanType) {
        switch (beanType) {
		case PREFERENCES_BEAN_GLOBAL:
            return FirebirdManagerPreferenceBean.class.getClassLoader();
		case PREFERENCES_BEAN_BEACKUP_AND_RESTORE:
            return FirebirdManagerBackupAndRestorePreferenceBean.class.getClassLoader();
		case PREFERENCES_BEAN_CREATE_DATABASE:
            return FirebirdManagerCreateDatabasePreferenceBean.class.getClassLoader();
		case PREFERENCES_BEAN_USERS:
            return FirebirdManagerUsersPreferenceBean.class.getClassLoader();
//		case SESSION_BEAN_GRANT_AND_REVOKE:
//            return FirebirdManagerGrantPreferenceBean.class.getClassLoader();
		default:
			return null;
		}
        
    }
}
