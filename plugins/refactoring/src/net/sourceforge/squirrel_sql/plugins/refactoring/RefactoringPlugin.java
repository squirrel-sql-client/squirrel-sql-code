package net.sourceforge.squirrel_sql.plugins.refactoring;
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

import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.AddAutoIncrementAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.AddColumnAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.AddForeignKeyAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.AddIndexAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.AddLookupTableAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.AddPrimaryKeyAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.AddSequenceAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.AddUniqueConstraintAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.AddViewAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.DropColumnAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.DropForeignKeyAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.DropIndexTableAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.DropPrimaryKeyAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.DropSelectedTablesAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.DropSequenceAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.DropUniqueConstraintAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.DropViewAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.MergeColumnAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.MergeTableAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.ModifyColumnAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.ModifySequenceAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.RenameTableAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.actions.RenameViewAction;
import net.sourceforge.squirrel_sql.plugins.refactoring.prefs.RefactoringPreferencesManager;
import net.sourceforge.squirrel_sql.plugins.refactoring.prefs.RefactoringPreferencesTab;
import net.sourceforge.squirrel_sql.plugins.refactoring.tab.SupportedRefactoringsTab;

/**
 * The Refactoring plugin class.
 */
public class RefactoringPlugin extends DefaultSessionPlugin {
    public static final String BUNDLE_BASE_NAME = "net.sourceforge.squirrel_sql.plugins.refactoring.refactoring";

    private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RefactoringPlugin.class);

	private static interface IMenuResourceKeys {
        String REFACTORING = "refactoring";
        String TABLE = s_stringMgr.getString("RefactoringPlugin.tableMenuItemLabel");
        String COLUMN = s_stringMgr.getString("RefactoringPlugin.columnMenuItemLabel");
        String INDEX = s_stringMgr.getString("RefactoringPlugin.indexMenuItemLabel");
        String DATA_QUALITY = s_stringMgr.getString("RefactoringPlugin.dataQualityMenuItemLabel");
        String REFERENTIAL_INTEGRITY = s_stringMgr.getString("RefactoringPlugin.referentialIntegrityMenuItemLabel");
    }
                      
    private PluginResources _resources;

    private JMenu _tableNodeMenu;
    private JMenu _tableObjectMenu;
    private JMenu _indexObjectMenu;
    private JMenu _viewNodeMenu;
    private JMenu _viewObjectMenu;
    private JMenu _sequenceNodeMenu;
    private JMenu _sessionNodeMenu;
    private JMenu _sequenceObjectMenu;


    /**
     * Return the internal name of this plugin.
     *
     * @return the internal name of this plugin.
     */
    public String getInternalName() {
        return "refactoring";
    }


    /**
     * Return the descriptive name of this plugin.
     *
     * @return the descriptive name of this plugin.
     */
    public String getDescriptiveName() {
        return "Refactoring Plugin";
    }


    /**
     * Returns the current version of this plugin.
     *
     * @return the current version of this plugin.
     */
    public String getVersion() {
        return "0.22";
    }


    /**
     * Returns the authors name.
     *
     * @return the authors name.
     */
    public String getAuthor() {
        return "Rob Manning";
    }


    /**
     * Returns the name of the change log for the plugin. This should
     * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
     * directory.
     *
     * @return the changelog file name or <TT>null</TT> if plugin doesn't have
     *         a change log.
     */
    public String getChangeLogFileName() {
        return "changes.txt";
    }


    /**
     * Returns the name of the Help file for the plugin. This should
     * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
     * directory.
     *
     * @return the Help file name or <TT>null</TT> if plugin doesn't have
     *         a help file.
     */
    public String getHelpFileName() {
        return "readme.html";
    }


    /**
     * Returns the name of the Licence file for the plugin. This should
     * be a text or HTML file residing in the <TT>getPluginAppSettingsFolder</TT>
     * directory.
     *
     * @return the Licence file name or <TT>null</TT> if plugin doesn't have
     *         a licence file.
     */
    public String getLicenceFileName() {
        return "licence.txt";
    }


    /**
     * @return Comma separated list of contributors.
     */
    public String getContributors() {
        return "Daniel Regli, Yannick Winiger";
    }

    /**
     * Create preferences panel for the Global Preferences dialog.
     *
     * @return Preferences panel.
     */
    public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
        RefactoringPreferencesTab tab = new RefactoringPreferencesTab();
        return new IGlobalPreferencesPanel[] {tab};
    }


    /**
     * Initialize this plugin.
     */
    public synchronized void initialize() throws PluginException {
        super.initialize();
        IApplication app = getApplication();

        _resources = new SQLPluginResources(BUNDLE_BASE_NAME, this);

        ActionCollection coll = app.getActionCollection();
        coll.add(new AddAutoIncrementAction(app, _resources));
        coll.add(new AddColumnAction(app, _resources));
        coll.add(new AddForeignKeyAction(app, _resources));
        coll.add(new AddIndexAction(app, _resources));
        coll.add(new AddLookupTableAction(app, _resources));
        coll.add(new AddPrimaryKeyAction(app, _resources));
        coll.add(new AddSequenceAction(app, _resources));
        coll.add(new AddUniqueConstraintAction(app, _resources));
        coll.add(new AddViewAction(app, _resources));
        coll.add(new DropForeignKeyAction(app, _resources));
        coll.add(new DropIndexTableAction(app, _resources));
        coll.add(new DropPrimaryKeyAction(app, _resources));
        coll.add(new DropSelectedTablesAction(app, _resources));
        coll.add(new DropSequenceAction(app, _resources));
        coll.add(new DropUniqueConstraintAction(app, _resources));
        coll.add(new DropViewAction(app, _resources));
        coll.add(new MergeColumnAction(app, _resources));
        coll.add(new MergeTableAction(app, _resources));
        coll.add(new ModifyColumnAction(app, _resources));
        coll.add(new ModifySequenceAction(app, _resources));
        coll.add(new DropColumnAction(app, _resources));
        coll.add(new RenameTableAction(app, _resources));
        coll.add(new RenameViewAction(app, _resources));
        
        RefactoringPreferencesManager.initialize(this);
    }


    public boolean allowsSessionStartedInBackground() {
        return true;
    }


    /**
     * Called when a session started. Add commands to popup menu
     * in object tree.
     *
     * @param session The session that is starting.
     * @return <TT>true</TT> to indicate that this plugin is
     *         applicable to passed session.
     */
    public PluginSessionCallback sessionStarted(final ISession session) {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                addActionsToPopup(session);
            }
        });

        return new PluginSessionCallback() {
            public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess) {
            }


            public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess) {
                addMenusToObjectTree(objectTreeInternalFrame.getObjectTreeAPI());
            }
        };
    }


    private void addActionsToPopup(ISession session)  {
        ActionCollection col = getApplication().getActionCollection();
        
        try {
	        IObjectTreeAPI _treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();
	        _treeAPI.addDetailTab(DatabaseObjectType.SESSION, new SupportedRefactoringsTab(session));
        } catch (Exception e) {
      	  e.printStackTrace();
        }
        
        // TABLE TYPE DBO
        _tableNodeMenu = _resources.createMenu(IMenuResourceKeys.REFACTORING);
        _resources.addToMenu(col.get(AddViewAction.class), _tableNodeMenu);

        // TABLE
        _tableObjectMenu = _resources.createMenu(IMenuResourceKeys.REFACTORING);
        JMenu tableMenu = new JMenu(IMenuResourceKeys.TABLE);
        _resources.addToMenu(col.get(RenameTableAction.class), tableMenu);
        _resources.addToMenu(col.get(MergeTableAction.class), tableMenu);
        _resources.addToMenu(col.get(DropSelectedTablesAction.class), tableMenu);

        JMenu columnMenu = new JMenu(IMenuResourceKeys.COLUMN);
        _resources.addToMenu(col.get(AddColumnAction.class), columnMenu);
        _resources.addToMenu(col.get(ModifyColumnAction.class), columnMenu);
        _resources.addToMenu(col.get(MergeColumnAction.class), columnMenu);
        _resources.addToMenu(col.get(DropColumnAction.class), columnMenu);        
        
        JMenu dataQualityMenu = new JMenu(IMenuResourceKeys.DATA_QUALITY);
        _resources.addToMenu(col.get(AddLookupTableAction.class), dataQualityMenu);
        _resources.addToMenu(col.get(AddAutoIncrementAction.class), dataQualityMenu);
        _resources.addToMenu(col.get(AddUniqueConstraintAction.class), dataQualityMenu);
        _resources.addToMenu(col.get(DropUniqueConstraintAction.class), dataQualityMenu);

        JMenu referentialMenu = new JMenu(IMenuResourceKeys.REFERENTIAL_INTEGRITY);
        _resources.addToMenu(col.get(AddPrimaryKeyAction.class), referentialMenu);
        _resources.addToMenu(col.get(AddForeignKeyAction.class), referentialMenu);
        _resources.addToMenu(col.get(DropPrimaryKeyAction.class), referentialMenu);
        _resources.addToMenu(col.get(DropForeignKeyAction.class), referentialMenu);

        JMenu tableIndexMenu = new JMenu(IMenuResourceKeys.INDEX);
        _resources.addToMenu(col.get(AddIndexAction.class), tableIndexMenu);
        _resources.addToMenu(col.get(DropIndexTableAction.class), tableIndexMenu);

        _tableObjectMenu.add(tableMenu);
        _tableObjectMenu.add(columnMenu);
        _tableObjectMenu.add(tableIndexMenu);
        _tableObjectMenu.add(dataQualityMenu);
        _tableObjectMenu.add(referentialMenu);

        // INDEX
        _indexObjectMenu = _resources.createMenu(IMenuResourceKeys.REFACTORING);
        _resources.addToMenu(col.get(DropIndexTableAction.class), _indexObjectMenu);
        
        // VIEW TYPE DBO (doesn't exist yet)
        _viewNodeMenu = _resources.createMenu(IMenuResourceKeys.REFACTORING);
        _resources.addToMenu(col.get(AddViewAction.class), _viewNodeMenu);

        // VIEW
        _viewObjectMenu = _resources.createMenu(IMenuResourceKeys.REFACTORING);
        _resources.addToMenu(col.get(DropViewAction.class), _viewObjectMenu);
        _resources.addToMenu(col.get(RenameViewAction.class), _viewObjectMenu);

        // SEQUENCE TYPE DBO
        _sequenceNodeMenu = _resources.createMenu(IMenuResourceKeys.REFACTORING);
        _resources.addToMenu(col.get(AddSequenceAction.class), _sequenceNodeMenu);

        // SEQUENCE
        _sequenceObjectMenu = _resources.createMenu(IMenuResourceKeys.REFACTORING);
        _resources.addToMenu(col.get(DropSequenceAction.class), _sequenceObjectMenu);
        _resources.addToMenu(col.get(ModifySequenceAction.class), _sequenceObjectMenu);
        
        // Ingres supports sequences, but there is no Ingres plugin yet to produce sequence nodes.
        // Also, since we don't have a good way to modify /delete sequences when they don't appear in the tree
        // this rules out their use in Ingres, for now.
        // TODO: Write the Ingres plugin, then rip this out.
        // 
        // Update: Since there are a number of other databases that support sequences without plugins, we will
        //         for now, just always put the add sequence in the session node's popup menu.  
        //
        //if (DialectFactory.isIngres(session.getMetaData())) {
      	  _sessionNodeMenu = _resources.createMenu(IMenuResourceKeys.REFACTORING);
      	  _resources.addToMenu(col.get(AddSequenceAction.class), _sessionNodeMenu);
        //}
        
        addMenusToObjectTree(session.getObjectTreeAPIOfActiveSessionWindow());
    }


    private void addMenusToObjectTree(IObjectTreeAPI api) {
        api.addToPopup(DatabaseObjectType.TABLE_TYPE_DBO, _tableNodeMenu);
        api.addToPopup(DatabaseObjectType.TABLE, _tableObjectMenu);
        api.addToPopup(DatabaseObjectType.INDEX, _indexObjectMenu);
        api.addToPopup(DatabaseObjectType.VIEW, _viewObjectMenu);
        api.addToPopup(DatabaseObjectType.SEQUENCE_TYPE_DBO, _sequenceNodeMenu);
        api.addToPopup(DatabaseObjectType.SEQUENCE, _sequenceObjectMenu);
        if (_sessionNodeMenu != null) {
      	  api.addToPopup(DatabaseObjectType.SESSION, _sessionNodeMenu);
        }
    }
    
}
