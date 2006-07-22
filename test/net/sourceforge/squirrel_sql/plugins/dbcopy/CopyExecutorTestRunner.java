/*
 * Copyright (C) 2005 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import java.util.ResourceBundle;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.MockPlugin;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.DBCopyPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;


/**
 * A description of this class goes here...
 */

public class CopyExecutorTestRunner  {

    private SessionInfoProvider prov = null;
    
    private CopyExecutor copyExecutor = null;
    
    private MockCopyTableListener listener = null;
    
    private MockUICallbacks uiCallbacks = null;
    
    private static DBCopyPreferenceBean prefs = null;
    
    /**
     * 
     *
     */
    public CopyExecutorTestRunner(SessionInfoProvider provider) {
        prov = provider;
        copyExecutor = new CopyExecutor(prov);
        listener = new MockCopyTableListener();
        //listener.setShowSqlStatements(true);
        uiCallbacks = new MockUICallbacks();
        copyExecutor.addListener(listener);
        copyExecutor.setPref(uiCallbacks);
    }
    
    public void run() {
        copyExecutor.execute();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String props = args[0];
        boolean dropOnly = getDropOnly();
        ApplicationArguments.initialize(new String[0]);
        ResourceBundle bundle = ResourceBundle.getBundle(props);
        String prefsDir = bundle.getString("prefsDir");
        System.out.println("Reading preferences from prefs.xml in "+prefsDir);
        IPlugin plugin = new MockPlugin(prefsDir);
        PreferencesManager.initialize(plugin);
        prefs = PreferencesManager.getPreferences();
        System.out.println("Copying primary keys: "+prefs.isCopyPrimaryKeys());
        
        MockSessionInfoProvider provider = new MockSessionInfoProvider(args[0], dropOnly);
        CopyExecutorTestRunner runner = new CopyExecutorTestRunner(provider);
        runner.run();
    }

    private static boolean getDropOnly() {
        boolean result = false;
        String dropOnly = System.getProperty("dropOnly");
        if (dropOnly != null) {
            if ("true".equalsIgnoreCase(dropOnly)) {
                System.out.println("Skipping copy; will only drop tables in destination database");
                result = true;
            }
        }
        return result;
    }
}
