package net.sourceforge.squirrel_sql.plugins.mssql;

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

import java.awt.event.ActionListener;
import java.lang.StringBuffer;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.plugins.mssql.action.GenerateSqlAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.IndexDefragAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.ScriptProcedureAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.ShowStatisticsAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.ShrinkDatabaseAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.TruncateLogAction;
import net.sourceforge.squirrel_sql.plugins.mssql.action.UpdateStatisticsAction;
import net.sourceforge.squirrel_sql.plugins.mssql.event.IndexIterationListener;

public class MssqlPlugin extends net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin {
	private final static ILogger s_log = LoggerController.createLogger(MssqlPlugin.class);
	private PluginResources _resources;
	private IObjectTreeAPI _treeAPI;
	private JMenu _mssqlMenu;
    private ISession _session;
    
    public MssqlPlugin() {
        super();
    }
    
    public String getChangeLogFileName() {
        return "changes.txt";
    }
    
    public String getContributors() {
        String retValue;
        
        retValue = super.getContributors();
        return retValue;
    }
    
    public net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
        net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel[] retValue;
        
        retValue = super.getGlobalPreferencePanels();
        return retValue;
    }
    
    public String getHelpFileName() {
        return "readme.html";
    }
    
    public String getLicenceFileName() {
        return "licence.txt";
    }
    
    public net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel[] getNewSessionPropertiesPanels() {
        net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel[] retValue;
        
        retValue = super.getNewSessionPropertiesPanels();
        return retValue;
    }
    
    public net.sourceforge.squirrel_sql.client.plugin.IPluginDatabaseObjectType[] getObjectTypes(net.sourceforge.squirrel_sql.client.session.ISession iSession) {
        net.sourceforge.squirrel_sql.client.plugin.IPluginDatabaseObjectType[] retValue;
        
        retValue = super.getObjectTypes(iSession);
        return retValue;
    }
    
    public java.io.File getPluginAppSettingsFolder() throws java.io.IOException {
        java.io.File retValue;
        
        retValue = super.getPluginAppSettingsFolder();
        return retValue;
    }
    
    public java.io.File getPluginUserSettingsFolder() throws java.io.IOException {
        java.io.File retValue;
        
        retValue = super.getPluginUserSettingsFolder();
        return retValue;
    }
    
    public net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel[] getSessionPropertiesPanels(net.sourceforge.squirrel_sql.client.session.ISession iSession) {
        net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel[] retValue;
        
        retValue = super.getSessionPropertiesPanels(iSession);
        return retValue;
    }
    
    public String getWebSite() {
        String retValue;
        
        retValue = super.getWebSite();
        return retValue;
    }
    
    public void initialize() throws net.sourceforge.squirrel_sql.client.plugin.PluginException {
        super.initialize();

		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();
        
        coll.add(new GenerateSqlAction(app, _resources, this));
        coll.add(new ScriptProcedureAction(app, _resources, this));
        coll.add(new ShrinkDatabaseAction(app, _resources, this));
        coll.add(new TruncateLogAction(app, _resources, this));
        coll.add(new UpdateStatisticsAction(app, _resources, this));

		_mssqlMenu = createFullMssqlMenu();
		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, _mssqlMenu);
    }
    
    public void load(net.sourceforge.squirrel_sql.client.IApplication iApplication) throws net.sourceforge.squirrel_sql.client.plugin.PluginException {
        super.load(iApplication);
        
        _resources = new MssqlResources(getClass().getName(), this);
    }
    
    public void sessionCreated(net.sourceforge.squirrel_sql.client.session.ISession iSession) {
        super.sessionCreated(iSession);
    }
    
    public void sessionEnding(net.sourceforge.squirrel_sql.client.session.ISession iSession) {
        super.sessionEnding(iSession);
    }
    
    public boolean sessionStarted(net.sourceforge.squirrel_sql.client.session.ISession iSession) {
        boolean isMssql = false;
        String productName;
        try {
            productName = iSession.getSQLConnection().getSQLMetaData().getDatabaseProductName();
        }
        catch (java.sql.SQLException ex) {
            productName = "";
        }
        isMssql = productName.equals("Microsoft SQL Server");
        
		if (super.sessionStarted(iSession) && isMssql) {
            _treeAPI = iSession.getObjectTreeAPI(this);
            final ActionCollection coll = getApplication().getActionCollection();

            _treeAPI.addToPopup(DatabaseObjectType.CATALOG, addToMssqlCatalogMenu(null));
            _treeAPI.addToPopup(DatabaseObjectType.TABLE, addToMssqlTableMenu(null));
            _treeAPI.addToPopup(DatabaseObjectType.PROCEDURE, addToMssqlProcedureMenu(null));

            _session = iSession;
            
            return true;
		}

        return false;
    }
    
    public void unload() {
        super.unload();
    }
    
    public String getAuthor() {
        return new String("Ryan Walberg");
    }
    
    public String getDescriptiveName() {
        return new String("Microsoft SQL Server Assistant");
    }
    
    public String getInternalName() {
        return new String("mssql");
    }
    
    public String getVersion() {
        return new String("0.1");
    }
    
    private void removeActionsOfType(ActionCollection coll,java.lang.Class classType) {
        java.lang.Object obj;
        java.util.Iterator iter = coll.actions();
        while (iter.hasNext()) {
            obj = iter.next();
            if (obj.getClass() == classType)
                iter.remove();
        }
    }
                
    private JMenu addToMssqlTableMenu(JMenu menu) {
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();
        final MssqlPlugin plugin = this;

		final JMenu mssqlMenu;
        if (menu == null)
            mssqlMenu = _resources.createMenu(MssqlResources.IMenuResourceKeys.MSSQL);
        else
            mssqlMenu = menu;
        
        _resources.addToMenu(coll.get(UpdateStatisticsAction.class), mssqlMenu);

        final JMenu showStatisticsMenu = _resources.createMenu(MssqlResources.IMenuResourceKeys.SHOW_STATISTICS);
        showStatisticsMenu.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                final JMenu menu = (JMenu) e.getSource();
                menu.removeAll();
                removeActionsOfType(coll,ShowStatisticsAction.class);
                iterateIndexes(new IndexIterationListener() {
                    public void indexSpotted(final ITableInfo tableInfo, final String indexName) {
                        final ShowStatisticsAction showStatisticsAction = new ShowStatisticsAction(app,_resources,plugin,tableInfo,indexName);
                        showStatisticsAction.setSession(_session);
                        coll.add(showStatisticsAction);
                        _resources.addToMenu(showStatisticsAction,menu);
                    }
                });
            }
            public void menuDeselected(MenuEvent e) { }
            public void menuCanceled(MenuEvent e) { }
        }
        );
        
        final JMenu indexDefragMenu = _resources.createMenu(MssqlResources.IMenuResourceKeys.INDEXDEFRAG);
        indexDefragMenu.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                final JMenu menu = (JMenu) e.getSource();
                menu.removeAll();
                removeActionsOfType(coll,IndexDefragAction.class);
                iterateIndexes(new IndexIterationListener() {
                    public void indexSpotted(final ITableInfo tableInfo, final String indexName) {
                        final IndexDefragAction indexDefragAction = new IndexDefragAction(app,_resources,plugin,tableInfo,indexName);
                        indexDefragAction.setSession(_session);
                        coll.add(indexDefragAction);
                        _resources.addToMenu(indexDefragAction,menu);
                    }
                });
            }
            public void menuDeselected(MenuEvent e) { }
            public void menuCanceled(MenuEvent e) { }
        }
        );
		
        mssqlMenu.add(showStatisticsMenu);
        mssqlMenu.add(indexDefragMenu);

		return mssqlMenu;
	}
    
    private void iterateIndexes(IndexIterationListener listener) {
        /* this should just bring back one table, i hope. */
		final IDatabaseObjectInfo[] dbObjs = _treeAPI.getSelectedDatabaseObjects();

		// Get the names of all the selected tables in a comma separated list,
		if (dbObjs.length != 1) {
            System.err.println("iterateIndexes: dbObjs.length != 1");
            return;
        }
        if (dbObjs[0].getDatabaseObjectType() != DatabaseObjectType.TABLE) {
            System.err.println("iterateIndexes: selected item isn't a table");
            return;
        }
        
        ITableInfo tableInfo = (ITableInfo) dbObjs[0];
        
        SQLConnection conn = _session.getSQLConnection();
        SQLDatabaseMetaData metaData = conn.getSQLMetaData();
        
        try {
            ResultSet indexInfo = metaData.getIndexInfo(tableInfo);
            String indexName = "";
            while (indexInfo.next()) {
                String thisIndexName = indexInfo.getString(6);
                /* for some reason, the first value is always NULL. */
                if (thisIndexName != null) {
                    if (!indexName.equals(thisIndexName)) {
                        listener.indexSpotted(tableInfo,thisIndexName);
                        indexName = thisIndexName;
                    }
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private JMenu createFullMssqlMenu() {
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		final JMenu mssqlMenu = _resources.createMenu(MssqlResources.IMenuResourceKeys.MSSQL);
        
        _resources.addToMenu(coll.get(GenerateSqlAction.class),mssqlMenu);
        
        addToMssqlCatalogMenu(mssqlMenu);
        addToMssqlTableMenu(mssqlMenu);
        addToMssqlProcedureMenu(mssqlMenu);
        
		return mssqlMenu;
	}
    
    private JMenu addToMssqlCatalogMenu(JMenu menu) {
        final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

        final JMenu mssqlMenu;
        if (menu == null)
            mssqlMenu = _resources.createMenu(MssqlResources.IMenuResourceKeys.MSSQL);
        else
            mssqlMenu = menu;
        
        _resources.addToMenu(coll.get(ShrinkDatabaseAction.class),mssqlMenu);
        _resources.addToMenu(coll.get(TruncateLogAction.class),mssqlMenu);

		return mssqlMenu;
    }
    
    private JMenu addToMssqlProcedureMenu(JMenu menu) {
        final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

        final JMenu mssqlMenu;
        if (menu == null)
            mssqlMenu = _resources.createMenu(MssqlResources.IMenuResourceKeys.MSSQL);
        else
            mssqlMenu = menu;
        
        _resources.addToMenu(coll.get(ScriptProcedureAction.class),mssqlMenu);

		return mssqlMenu;
    }
}
