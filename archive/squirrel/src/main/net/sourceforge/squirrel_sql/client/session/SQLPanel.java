package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * Modifications Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewerDestination;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.MemoryComboBox;
import net.sourceforge.squirrel_sql.fw.gui.TextPopupMenu;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.util.Logger;

/**
 * This is the panel where SQL scripts can be entered and executed.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
class SQLPanel extends JPanel {
    /** Current session. */
    private ISession _session;

    private MemoryComboBox _sqlCombo = new MemoryComboBox();
    private JTextArea _sqlEntry = new JTextArea();
    private JCheckBox _limitRowsChk = new JCheckBox("Limit rows: ");
    private IntegerField _nbrRows = new IntegerField();

    private SqlComboItemListener _sqlComboItemListener = new SqlComboItemListener();
    private MyPropertiesListener _propsListener;

    private DataSetViewer _viewer = new DataSetViewer();

    /** Popup menu for text component. */
    private TextPopupMenu _textPopupMenu = new TextPopupMenu();

    /** Each tab is a <TT>ResultTab</TT> showing the results of a query. */
    private JTabbedPane _tabbedResultsPanel = new JTabbedPane();

    /** List of <TT>ResultTab</TT> objects currently visible. */
    private List _usedTabs = new ArrayList();

    /** Pool of <TT>ResultTab</TT> objects available for use. */
    private List _availableTabs = new ArrayList();

    private boolean _hasBeenVisible = false;
    private JSplitPane _splitPane;

    private ArrayList _sqlExecutionListeners = new ArrayList();
    /**
     * Ctor.
     *
     * @param   session     Current session.
     *
     * @throws  IllegalArgumentException
     *              Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
     */
    public SQLPanel(ISession session) {
        super();
        if (session == null) {
            throw new IllegalArgumentException("Null ISession passed");
        }

        _session = session;
        createUserInterface();

        propertiesHaveChanged(null);
    }

    /**
     * Add a listener listening for SQL Execution.
     *
     * @param   lis     Listener
     *
     * @throws  IllegalArgumentException
     *              If a null <TT>ISQLExecutionListener</TT> passed.
     */
    public synchronized void addSQLExecutionListener(ISQLExecutionListener lis)
        throws IllegalArgumentException {
        if (lis == null) {
            throw new IllegalArgumentException("null ISQLExecutionListener passed");
        }
        _sqlExecutionListeners.add(lis);
    }

    /**
     * Remove an SQL execution listener.
     *
     * @param   lis     Listener
     *
     * @throws  IllegalArgumentException
     *              If a null <TT>ISQLExecutionListener</TT> passed.
     */
    public synchronized void removeSQLExecutionListener(ISQLExecutionListener lis)
        throws IllegalArgumentException {
        if (lis == null) {
            throw new IllegalArgumentException("null ISQLExecutionListener passed");
        }
        _sqlExecutionListeners.remove(lis);
    }

    /**
     * Notification to this component that it now has a parent component.
     * Create required listeners.
     */
    public void addNotify() {
        if (_propsListener == null) {
            _propsListener = new MyPropertiesListener();
            _session.getProperties().addPropertyChangeListener(_propsListener);
        }
        super.addNotify();
    }

    /**
     * Notification to this component that it no longer has a parent component.
     * Remove all listeners that this component has setup.
     */
    public void removeNotify() {
        if (_propsListener != null) {
            _session.getProperties().removePropertyChangeListener(_propsListener);
            _propsListener = null;
        }
        super.removeNotify();
    }

    /**
     * Commit the current SQL transaction.
     */
    //    public void commit() {
    //        _session.commit();
    //    }

    /**
     * Rollback the current SQL transaction.
     */
    //    public void rollback() {
    //        _session.rollBack();
    //    }

    /**
     * Execute the current SQL.
     */
    void executeCurrentSQL() {
        try {
            String sql = _sqlEntry.getSelectedText();
            if (sql == null || sql.length() == 0) {
                sql = _sqlEntry.getText();
                int iStartIndex = 0;
                int iEndIndex = sql.length();

                int iCaretPos = _sqlEntry.getCaretPosition();

                int iIndex = sql.lastIndexOf("\n\n", iCaretPos);
                if (iIndex > 0)
                    iStartIndex = iIndex;
                iIndex = sql.indexOf("\n\n", iCaretPos);
                if (iIndex > 0)
                    iEndIndex = iIndex;

                sql = sql.substring(iStartIndex, iEndIndex).trim();

                /*              if(sql == null || sql.trim().equals(""))
                                {
                                    appendStatus(getLabel("message.nothingtoexecute"));
                                    return;
                                }
                */
            }

            final long start = System.currentTimeMillis();
            SessionProperties props = _session.getProperties();
            final Statement stmt = _session.getSQLConnection().createStatement();
            try {
                if (props.getSqlLimitRows()) {
                    stmt.setMaxRows(props.getSqlNbrRowsToShow());
                }

                if (props.getSqlReuseOutputTabs()) {
                    _availableTabs.addAll(_usedTabs);
                    _usedTabs.clear();
                    _tabbedResultsPanel.removeAll();
                }

                QueryTokenizer qt =
                    new QueryTokenizer(sql, props.getSqlStatementSeparatorChar());
                while (qt.hasQuery()) {
                    String origQrySql = qt.nextQuery();

                    // Allow plugins the opportunity to modify this
                    // SQL statement prior to it being executed.
                    String qrySqlToExecute = modifyIndividualScript(origQrySql);

                    if (qrySqlToExecute != null) {

                        _sqlComboItemListener.stopListening();
                        try {
                            _sqlCombo.addItem(new SqlComboItem(origQrySql));
                        } finally {
                            _sqlComboItemListener.startListening();
                        }

                        if (stmt.execute(qrySqlToExecute)) {
                            ResultSet rs = stmt.getResultSet();
                            if (rs != null) {
                                try {
                                    ResultTab tab = null;
                                    if (_availableTabs.size() > 0) {
                                        tab = (ResultTab) _availableTabs.remove(0);
                                    }
                                    if (tab == null) {
                                        tab = new ResultTab(_session, this);
                                        _usedTabs.add(tab);
                                    }
                                    origQrySql = Utilities.cleanString(origQrySql);
                                    tab.show(new ResultSetDataSet(rs), origQrySql);
                                    String sTitle = origQrySql;
                                    if (sTitle.length() > 10) {
                                        sTitle = sTitle.substring(0, 15);
                                    }
                                    if (_tabbedResultsPanel.indexOfComponent(tab) == -1) {
                                        _tabbedResultsPanel.addTab(sTitle, null, tab, origQrySql);
                                    } else {
                                        tab.setName(sTitle);
                                    }
                                    _tabbedResultsPanel.setSelectedComponent(tab);
                                } finally {
                                    rs.close();
                                }
                            }
                        } else {
                            _session.getMessageHandler().showMessage(
                                stmt.getUpdateCount() + " Rows Updated");
                        }
                    }
                }
                final long finish = System.currentTimeMillis();
                _session.getMessageHandler().showMessage(
                    "Elapsed time for query (milliseconds): " + (finish - start));
                //  i18n
            } finally {
                stmt.close();
            }
        } catch (Throwable th) {
            _session.getMessageHandler().showMessage(th);
        }
    }

    /**
     * Close the passed <TT>ResultTab</TT>. This is done by clearing
     * all data from the tab, removing it from the tabbed panel
     * and adding it to the list of available tabs.
     *
     * @throws  IllegalArgumentException
     *              Thrown if a <TT>null</TT> <TT>ResultTab</TT> passed.
     */
    public void closeTab(ResultTab tab) throws IllegalArgumentException {
        if (tab == null) {
            throw new IllegalArgumentException("Null ResultTab passed");
        }
        tab.clear();
        _tabbedResultsPanel.remove(tab);
        _availableTabs.add(tab);
    }

    /**
     * Create an internal frame for the specified tab and
     * display the tab in the internal frame after removing
     * it from the tabbed pane.
     *
     * @param   tab     <TT>Resulttab</TT> to be displayed in
     *                  an internal frame..
     *
     * @throws  IllegalArgumentException
     *              Thrown if a <TT>null</TT> <TT>ResultTab</TT> passed.
     */
    public void createWindow(ResultTab tab) throws IllegalArgumentException {
        if (tab == null) {
            throw new IllegalArgumentException("Null ResultTab passed");
        }
        _tabbedResultsPanel.remove(tab);
        ResultFrame frame = new ResultFrame(tab);
        frame.setDefaultCloseOperation(ResultFrame.DISPOSE_ON_CLOSE);
        net
            .sourceforge
            .squirrel_sql
            .client
            .mainframe
            .MainFrame
            .getInstance()
            .addInternalFrame(frame);
        frame.setVisible(true);
        frame.pack();
        frame.toFront();
        frame.requestFocus();
    }

    public void setVisible(boolean value) {
        super.setVisible(value);
        if (!_hasBeenVisible && value == true) {
            _splitPane.setDividerLocation(0.2d);
            _hasBeenVisible = true;
        }

    }

    public String getSQLScript() {
        return _sqlEntry.getText();
    }

    public void setSQLScript(String sqlScript) {
        _sqlEntry.setText(sqlScript);
    }

    private String modifyIndividualScript(String sql) {
        List list = null;
        synchronized (this) {
            list = (ArrayList) _sqlExecutionListeners.clone();
        }

        for (int i = 0; i < list.size(); ++i) {
            sql = ((ISQLExecutionListener) list.get(i)).statementExecuting(sql);
            if (sql == null) {
                break;
            }
        }

        return sql;
    }

    private void propertiesHaveChanged(String propertyName) {
        final SessionProperties props = _session.getProperties();
        /*
                if (propertyName == null || propertyName.equals(
                        SessionProperties.IPropertyNames.SQL_OUTPUT_CLASS_NAME)) {
                    final IDataSetViewerDestination previous = _output;
                    try {
                        Class destClass = Class.forName(props.getSqlOutputClassName());
                        if (IDataSetViewerDestination.class.isAssignableFrom(destClass) &&
                                Component.class.isAssignableFrom(destClass)) {
                            _output = (IDataSetViewerDestination)destClass.newInstance();
                        }
        
                    } catch (Exception ex) {
                        _session.getApplication().getLogger().showMessage(Logger.ILogTypes.ERROR, ex.getMessage());
                    }
                    if (_output == null) {
                        _output = new DataSetViewerTextPanel();
                    }
                    _viewer.setDestination(_output);
                    _outputSp.setRowHeader(null);
                    _outputSp.setViewportView((Component)_output);
                }
        */
        //      if (propertyName == null || propertyName.equals(
        //              SessionProperties.IPropertyNames.SQL_REUSE_OUTPUT_TABS)) {
        //          if (props.getSqlReuseOutputTabs()) {
        //              for (int i = _tabbedResultsPanel.getTabCount() - 1;
        //                      i > 0; --i) {
        //                  _tabbedResultsPanel.remove(i);
        //              }
        //              _availableTabs.clear();
        //              if (_usedTabs.size() > 0) {
        //                  Object tab = _usedTabs.get(0);
        //                  _usedTabs.clear();
        //                  _usedTabs.add(tab);
        //              }
        //          }
        //      }

        if (propertyName == null
            || propertyName.equals(SessionProperties.IPropertyNames.AUTO_COMMIT)) {
            final SQLConnection conn = _session.getSQLConnection();
            if (conn != null) {
                boolean auto = true;
                try {
                    auto = conn.getAutoCommit();
                } catch (BaseSQLException ignore) {
                    // SQL engine doesn't support transaction control. Handle
                    // this properly ??
                }
                try {
                    conn.setAutoCommit(props.getAutoCommit());
                } catch (BaseSQLException ex) {
                    props.setAutoCommit(auto);
                    _session.getMessageHandler().showMessage(ex);
                }
            }
        }

        if (propertyName == null
            || propertyName.equals(SessionProperties.IPropertyNames.SQL_LIMIT_ROWS)) {
            _limitRowsChk.setSelected(props.getSqlLimitRows());
        }

        if (propertyName == null
            || propertyName.equals(SessionProperties.IPropertyNames.SQL_NBR_ROWS_TO_SHOW)) {
            _nbrRows.setInt(props.getSqlNbrRowsToShow());
        }
    }

    private void createUserInterface() {
        setLayout(new BorderLayout());

        _nbrRows.setColumns(8);

        _sqlCombo.setEditable(false);
        {
            JPanel pnl = new JPanel();
            pnl.setLayout(new BorderLayout());
            pnl.add(_sqlCombo, BorderLayout.CENTER);
            Box box = Box.createHorizontalBox();
            box.add(Box.createHorizontalStrut(10));
            box.add(_limitRowsChk, BorderLayout.EAST);
            box.add(Box.createHorizontalStrut(5));
            box.add(_nbrRows, BorderLayout.EAST);
            pnl.add(box, BorderLayout.EAST);
            add(pnl, BorderLayout.NORTH);
        }

        _splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        _splitPane.setOneTouchExpandable(true);
        JPanel mid = new JPanel(new BorderLayout());
        mid.add(_splitPane, BorderLayout.CENTER);

        JScrollPane inSp = new JScrollPane();
        _sqlEntry.setRows(3);
        _sqlEntry.setTabSize(4);
        inSp.setViewportView(_sqlEntry);
        _splitPane.add(inSp, JSplitPane.LEFT);
        _splitPane.add(_tabbedResultsPanel, JSplitPane.RIGHT);

        add(_splitPane, BorderLayout.CENTER);

        propertiesHaveChanged(null);

        _sqlCombo.addActionListener(_sqlComboItemListener);
        _limitRowsChk.addChangeListener(new LimitRowsCheckBoxListener());
        _nbrRows.getDocument().addDocumentListener(new LimitRowsTextBoxListener());

        // Add mouse listener for displaying popup menu.
        _nbrRows.addMouseListener(new MyMouseListener());
        _sqlEntry.addMouseListener(new MyMouseListener());
    }

    private final class MyMouseListener extends MouseAdapter {
        public void mousePressed(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                displayPopupMenu(evt);
            }
        }
        public void mouseReleased(MouseEvent evt) {
            if (evt.isPopupTrigger()) {
                displayPopupMenu(evt);
            }
        }
        private void displayPopupMenu(MouseEvent evt) {
            Object src = evt.getSource();
            if (src instanceof JTextComponent) {
                _textPopupMenu.setTextComponent((JTextComponent) src);
                _textPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
            }
        }
    }

    private class MyPropertiesListener implements PropertyChangeListener {
        private boolean _listening = true;

        void stopListening() {
            _listening = false;
        }

        void startListening() {
            _listening = true;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            if (_listening) {
                propertiesHaveChanged(evt.getPropertyName());
            }
        }
    }

    private class SqlComboItemListener implements ActionListener {
        private boolean _listening = true;

        void stopListening() {
            _listening = false;
        }

        void startListening() {
            _listening = true;
        }

        public void actionPerformed(ActionEvent evt) {
            if (_listening) {
                SqlComboItem item = (SqlComboItem) _sqlCombo.getSelectedItem();
                if (item != null) {
                    //                  _sqlEntry.setText(item.getText());
                    _sqlEntry.append("\n\n" + item.getText());
                    _sqlEntry.setCaretPosition(_sqlEntry.getText().length() - 1);
                }
            }
        }
    }

    private static class SqlComboItem {
        private String _sql;
        private String _firstLine;

        SqlComboItem(String sql) {
            super();
            _sql = sql.trim();
            _firstLine = getFirstLine(sql);
        }

        public boolean equals(Object rhs) {
            boolean rc = false;
            if (this == rhs) {
                rc = true;
            } else if (rhs != null && rhs.getClass().equals(getClass())) {
                rc = ((SqlComboItem) rhs).getText().equals(getText());
            }
            return rc;
        }

        public String toString() {
            return _firstLine;
        }

        String getText() {
            return _sql;
        }

        private String getFirstLine(String sql) {
            int idx1 = sql.indexOf('\n');
            int idx2 = sql.indexOf('\r');
            if (idx1 == -1) {
                idx1 = idx2;
            }
            if (idx2 != -1 && idx2 < idx1) {
                idx1 = idx2;
            }
            sql = idx1 == -1 ? sql : sql.substring(0, idx1);
            return sql.replace('\t', ' ');
        }
    }

    private class LimitRowsCheckBoxListener implements ChangeListener {
        public void stateChanged(ChangeEvent evt) {
            if (_propsListener != null) {
                _propsListener.stopListening();
            }
            try {
                final boolean limitRows = ((JCheckBox) evt.getSource()).isSelected();
                _nbrRows.setEnabled(limitRows);
                _session.getProperties().setSqlLimitRows(limitRows);
            } finally {
                if (_propsListener != null) {
                    _propsListener.startListening();
                }
            }
        }
    }

    private class LimitRowsTextBoxListener implements DocumentListener {
        public void insertUpdate(DocumentEvent evt) {
            updateProperties(evt);
        }

        public void changedUpdate(DocumentEvent evt) {
            updateProperties(evt);
        }

        public void removeUpdate(DocumentEvent evt) {
            updateProperties(evt);
        }

        private void updateProperties(DocumentEvent evt) {
            if (_propsListener != null) {
                _propsListener.stopListening();
            }
            try {
                _session.getProperties().setSqlNbrRowsToShow(_nbrRows.getInt());
            } finally {
                if (_propsListener != null) {
                    _propsListener.startListening();
                }
            }
        }
    }
}
