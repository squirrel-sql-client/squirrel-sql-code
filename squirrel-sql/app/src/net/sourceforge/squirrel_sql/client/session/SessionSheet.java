package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.ToolBar;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.Logger;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.session.action.CommitAction;
import net.sourceforge.squirrel_sql.client.session.action.ExecuteSqlAction;
import net.sourceforge.squirrel_sql.client.session.action.RefreshTreeAction;
import net.sourceforge.squirrel_sql.client.session.action.RollbackAction;
import net.sourceforge.squirrel_sql.client.session.action.SessionPropertiesAction;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;

public class SessionSheet extends JInternalFrame {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String SQL_TAB_TITLE = "SQL";
        String SQL_TAB_DESC = "Execute SQL statements";
        String OBJ_TAB_TITLE = "Objects";
        String OBJ_TAB_DESC = "Show database objects";
    }

    /**
     * IDs of tabs in the main tabbed pane.
     */
    public interface IMainTabIndexes {
        int OBJECT_TREE_TAB = 0;
        int SQL_TAB = 1;
    }

    private ISession _session;

    private MyPropertiesListener _propsListener = new MyPropertiesListener();

    private JTabbedPane _tabPane = new JTabbedPane();
    private SQLPanel _sqlPnl;
    private ObjectsPanel _objectsPnl;
    private JSplitPane _msgSplit;

    private boolean _hasBeenVisible = false;

    public SessionSheet(ISession session) throws BaseSQLException {
        super(createTitle(session), true, true, true, true);
        _session = session;
        setVisible(false);
        createUserInterface();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        propertiesHaveChanged(null);

        session.getProperties().addPropertyChangeListener(_propsListener);

        _session.getApplication().getPluginManager().sessionStarted(session);
    }

    /**
     * Close this window.
     */
    public void dispose() {
        _session.getApplication().getPluginManager().sessionEnding(_session);
        closeConnection();
        super.dispose();
    }

    public boolean hasConnection() {
        return _session.getSQLConnection() != null;
    }
    /*
        public void commit() {
            _sqlPnl.commit();
        }
    
        public void rollback() {
            _sqlPnl.rollback();
        }
    */
    public ISession getSession() {
        return _session;
    }

    public void refreshTree() throws BaseSQLException {
        _objectsPnl.refreshTree();
    }

    public void updateState() {
        ActionCollection actions = _session.getApplication().getActionCollection();
        final String tabTitle = _tabPane.getTitleAt(_tabPane.getSelectedIndex());
        if (tabTitle.equals(i18n.SQL_TAB_TITLE)) {
            actions.get(ExecuteSqlAction.class).setEnabled(true);
            boolean isAutoCommit = _session.getProperties().getAutoCommit();
            actions.get(CommitAction.class).setEnabled(!isAutoCommit);
            actions.get(RollbackAction.class).setEnabled(!isAutoCommit);
            actions.get(RefreshTreeAction.class).setEnabled(false);
        } else {
            actions.get(ExecuteSqlAction.class).setEnabled(false);
            actions.get(CommitAction.class).setEnabled(false);
            actions.get(RollbackAction.class).setEnabled(false);
            actions.get(RefreshTreeAction.class).setEnabled(true);
        }
    }

    ObjectsPanel getObjectPanel() {
        return _objectsPnl;
    }

    void closeConnection() {
        try {
            _session.closeSQLConnection();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    public void setVisible(boolean value) {
        super.setVisible(value);
        // Required under JDK1.2. Without it the divider location is reset to 0.
        if (!_hasBeenVisible && value == true) {
            _objectsPnl.fixDividerLocation();
            _msgSplit.setDividerLocation(0.9d);
            _hasBeenVisible = true;
        }
    }

    /**
     * Select a tab in the main tabbed pane.
     * 
     * @param	tabIndex	The tab to select. @see #IMainTabIndexes
     * 
     * @throws	IllegalArgumentException
     * 			Thrown if an invalid <TT>tabIndex</TT> passed.
     */
    public void selectMainTab(int tabIndex) throws IllegalArgumentException {
        if (tabIndex >= _tabPane.getTabCount()) {
            throw new IllegalArgumentException(
                "" + tabIndex + " is not a valid index into the main tabbed pane.");
        }

        if (_tabPane.getSelectedIndex() != tabIndex) {
            _tabPane.setSelectedIndex(tabIndex);
        }
    }

    String getSQLScript() {
        return _sqlPnl.getSQLScript();
    }

    void setSQLScript(String sqlScript) {
        _sqlPnl.setSQLScript(sqlScript);
    }

    SQLPanel getSQLPanel() {
        return _sqlPnl;
    }

    void executeCurrentSQL() {
        _sqlPnl.executeCurrentSQL();
    }

    private static String createTitle(ISession session) {
        StringBuffer title = new StringBuffer(session.getAlias().getName());
        String user = null;
        try {
            user = session.getSQLConnection().getUserName();
        } catch (BaseSQLException ex) {
            Logger logger = session.getApplication().getLogger();
            logger.showMessage(
                Logger.ILogTypes.ERROR,
                "Error occured retrieving user name from Connection");
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
        }
        if (user != null && user.length() > 0) {
            title.append(" as ").append(user); // i18n
        }
        return title.toString();
    }

    private void showError(Exception ex) {
        new ErrorDialog(MainFrame.getInstance(), ex).show();
    }

    private void propertiesHaveChanged(String propertyName) {
        SessionProperties props = _session.getProperties();
        if (propertyName == null
            || propertyName.equals(
                SessionProperties.IPropertyNames.COMMIT_ON_CLOSING_CONNECTION)) {
            _session.getSQLConnection().setCommitOnClose(
                props.getCommitOnClosingConnection());
        }
        updateState();
    }

    private void createUserInterface() {
        setVisible(false);
        Icon icon =
            _session.getApplication().getResources().getIcon(getClass(), "frameIcon");
        //i18n
        if (icon != null) {
            setFrameIcon(icon);
        }

        _sqlPnl = new SQLPanel(_session);
        _objectsPnl = new ObjectsPanel(_session);

        _tabPane.addTab(i18n.OBJ_TAB_TITLE, null, _objectsPnl, i18n.OBJ_TAB_DESC);
        _tabPane.addTab(i18n.SQL_TAB_TITLE, null, _sqlPnl, i18n.SQL_TAB_DESC);

        _tabPane.addChangeListener(new MyTabsListener());

        Container content = getContentPane();
        content.setLayout(new BorderLayout());
        content.add(new MyToolBar(this), BorderLayout.NORTH);

        MessagePanel msgPnl = new MessagePanel(_session.getApplication());
        _session.setMessageHandler(msgPnl);
        msgPnl.setEditable(false);
        msgPnl.setRows(4);

        _msgSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        _msgSplit.setOneTouchExpandable(true);
        _msgSplit.add(_tabPane, JSplitPane.LEFT);
        _msgSplit.add(new JScrollPane(msgPnl), JSplitPane.RIGHT);
        content.add(_msgSplit, BorderLayout.CENTER);

        // This is to fix a problem with the JDK (up to version 1.3)
        // where focus events were not generated correctly. The sympton
        // is being unable to key into the text entry field unless you click
        // elsewhere after focus is gained by the internal frame.
        // See bug ID 4309079 on the JavaSoft bug parade (plus others).
        addInternalFrameListener(new InternalFrameAdapter() {
            public void internalFrameActivated(InternalFrameEvent evt) {
                Window window = SwingUtilities.windowForComponent(SessionSheet.this._sqlPnl);
                Component focusOwner = (window != null) ? window.getFocusOwner() : null;
                if (focusOwner != null) {
                    FocusEvent lost = new FocusEvent(focusOwner, FocusEvent.FOCUS_LOST);
                    FocusEvent gained = new FocusEvent(focusOwner, FocusEvent.FOCUS_GAINED);
                    window.dispatchEvent(lost);
                    window.dispatchEvent(gained);
                    window.dispatchEvent(lost);
                    focusOwner.requestFocus();
                }
            }
        });

        validate();
    }

    private class MyToolBar extends ToolBar {
        MyToolBar(SessionSheet frame) {
            super();
            ActionCollection actions = _session.getApplication().getActionCollection();
            setUseRolloverButtons(true);
            setFloatable(false);
            add(actions.get(SessionPropertiesAction.class));
            add(actions.get(RefreshTreeAction.class));
            addSeparator();
            add(actions.get(ExecuteSqlAction.class));
            addSeparator();
            add(actions.get(CommitAction.class));
            add(actions.get(RollbackAction.class));

            actions.get(ExecuteSqlAction.class).setEnabled(false);
            actions.get(CommitAction.class).setEnabled(false);
            actions.get(RollbackAction.class).setEnabled(false);
        }
    }

    private class MyPropertiesListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            SessionSheet.this.propertiesHaveChanged(evt.getPropertyName());
        }
    }

    private class MyTabsListener implements ChangeListener {
        public void stateChanged(ChangeEvent evt) {
            updateState();
        }
    }

    private static MouseListener s_dummyMouseAdapter = new MouseAdapter() {
        private void cancelEvent(MouseEvent evt) {
            Component pane = (Component) evt.getSource();
            pane.setVisible(true);
            pane.requestFocus();
        }
        public void mouseClicked(MouseEvent evt) {
            cancelEvent(evt);
        }
        public void mousePressed(MouseEvent evt) {
            cancelEvent(evt);
        }
        public void mouseReleased(MouseEvent evt) {
            cancelEvent(evt);
        }
    };
}
