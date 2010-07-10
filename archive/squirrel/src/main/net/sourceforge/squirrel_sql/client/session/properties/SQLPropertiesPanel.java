package net.sourceforge.squirrel_sql.client.session.properties;
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

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.gui.CharField;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class SQLPropertiesPanel
        implements IGlobalPreferencesPanel, ISessionPropertiesPanel {
    private boolean _initialized = false;
    private String _title;
    private String _hint;
    private IApplication _app;
    private SessionProperties _props;

    private MyPanel _myPanel = new MyPanel();

    public SQLPropertiesPanel(String title, String hint) {
        super();

        _title = title != null ? title : MyPanel.i18n.SQL;
        _hint = hint != null ? hint : MyPanel.i18n.SQL;
    }

    public void initialize(IApplication app)
            throws IllegalArgumentException {
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }

        initialize(app, app.getSquirrelPreferences().getSessionProperties());
    }

    public void initialize(IApplication app, SessionProperties props)
            throws IllegalArgumentException {
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        if (props == null) {
            throw new IllegalArgumentException("Null SessionProperties passed");
        }
        _app = app;
        _props = props;

        if (!_initialized) {
            _initialized = true;
            _myPanel.createUserInterface(app);
        }
        _myPanel.loadData(_props);
    }

    public Component getPanelComponent() {
        return _myPanel;
    }

    public String getTitle() {
        return _title;
    }

    public String getHint() {
        return _hint;
    }

    public void applyChanges() {
        _myPanel.applyChanges(_props);
    }

    private static final class MyPanel extends JPanel {
        /**
         * This interface defines locale specific strings. This should be
         * replaced with a property file.
         */
        interface i18n {
            String AUTO_COMMIT = "Auto Commit SQL:";
            String COMMIT_ON_CLOSE = "Commit On Closing Session:";
            String NBR_ROWS_CONTENTS = "Contents - Number of rows:";
            String NBR_ROWS_SQL = "SQL - Number of rows:";
            String LIMIT_ROWS_CONTENTS = "Contents - Limit rows:";
            String LIMIT_ROWS_SQL = "SQL - Limit rows:";
            String MULTIPLE_TABS_SQL = "SQL - Reuse Output Tabs:";
            String SHOW_ROW_COUNT = "Show Row Count for Tables:";
            String TABLE = "Table";
            String TEXT = "Text";
            String STATEMENT_SEPARATOR = "Statement Separator:";
            String PERF_WARNING = "Note: Settings marked with an hourglass will have performance implications.";
            String SQL = "SQL";
        }

        private JCheckBox _autoCommitChk = new JCheckBox();
        private JCheckBox _commitOnClose = new JCheckBox();
        private IntegerField _contentsNbrRowsToShowField = new IntegerField();
        private JCheckBox _contentsLimitRowsChk = new JCheckBox();
        private JCheckBox _showRowCount = new JCheckBox();
        private IntegerField _sqlNbrRowsToShowField = new IntegerField();
        private JCheckBox _sqlLimitRows = new JCheckBox();
        private JCheckBox _sqlMultipleTabs = new JCheckBox();
        private CharField _stmtSepChar = new CharField();

        MyPanel() {
            super();
        }

        void loadData(SessionProperties props) {
            _autoCommitChk.setSelected(props.getAutoCommit());
            _commitOnClose.setSelected(props.getCommitOnClosingConnection());
            _contentsNbrRowsToShowField.setInt(props.getContentsNbrRowsToShow());
            _contentsLimitRowsChk.setSelected(props.getContentsLimitRows());
            _sqlNbrRowsToShowField.setInt(props.getSqlNbrRowsToShow());
            _sqlLimitRows.setSelected(props.getSqlLimitRows());
            _sqlMultipleTabs.setSelected(props.getSqlReuseOutputTabs());
            _showRowCount.setSelected(props.getShowRowCount());
            _stmtSepChar.setChar(props.getSqlStatementSeparatorChar());
        }

        void applyChanges(SessionProperties props) {
            props.setAutoCommit(_autoCommitChk.isSelected());
            props.setCommitOnClosingConnection(_commitOnClose.isSelected());
            props.setContentsNbrRowsToShow(_contentsNbrRowsToShowField.getInt());
            props.setContentsLimitRows(_contentsLimitRowsChk.isSelected());
            props.setSqlNbrRowsToShow(_sqlNbrRowsToShowField.getInt());
            props.setSqlLimitRows(_sqlLimitRows.isSelected());
            props.setSqlReuseOutputTabs(_sqlMultipleTabs.isSelected());
            props.setShowRowCount(_showRowCount.isSelected());
            props.setSqlStatementSeparatorChar(_stmtSepChar.getChar());
        }

        private void createUserInterface(IApplication app) {
            setLayout(new BorderLayout());

            final Icon warnIcon = app.getResources().getIcon(SquirrelResources.ImageNames.PERFORMANCE_WARNING);

            // Centre panel is the properties panel.
            PropertyPanel pnl = new PropertyPanel();
            JLabel lbl = new JLabel(i18n.AUTO_COMMIT, SwingConstants.RIGHT);
            pnl.add(lbl, _autoCommitChk);
            _autoCommitChk.addChangeListener(new AutoCommitCheckBoxListener());

            lbl = new JLabel(i18n.COMMIT_ON_CLOSE, SwingConstants.RIGHT);
            pnl.add(lbl, _commitOnClose);

            lbl = new JLabel(i18n.SHOW_ROW_COUNT, SwingConstants.RIGHT);
            pnl.add(lbl, _showRowCount, new JLabel(warnIcon));

            lbl = new JLabel(i18n.LIMIT_ROWS_CONTENTS, SwingConstants.RIGHT);
            pnl.add(lbl, _contentsLimitRowsChk, new JLabel(warnIcon));
            lbl = new JLabel(i18n.NBR_ROWS_CONTENTS, SwingConstants.RIGHT);
            pnl.add(lbl, _contentsNbrRowsToShowField, new JLabel(warnIcon));
            _contentsLimitRowsChk.addChangeListener(new LimitRowsCheckBoxListener(_contentsNbrRowsToShowField));

            lbl = new JLabel(i18n.LIMIT_ROWS_SQL, SwingConstants.RIGHT);
            pnl.add(lbl, _sqlLimitRows, new JLabel(warnIcon));
            lbl = new JLabel(i18n.NBR_ROWS_SQL, SwingConstants.RIGHT);
            pnl.add(lbl, _sqlNbrRowsToShowField, new JLabel(warnIcon));
            _sqlLimitRows.addChangeListener(new LimitRowsCheckBoxListener(_sqlNbrRowsToShowField));

            lbl = new JLabel(i18n.MULTIPLE_TABS_SQL, SwingConstants.RIGHT);
            pnl.add(lbl, _sqlMultipleTabs);

            lbl = new JLabel(i18n.STATEMENT_SEPARATOR, SwingConstants.RIGHT);
            pnl.add(lbl, _stmtSepChar);

            add(pnl, BorderLayout.CENTER);

            // Warning message in bottom panel.
            JTextArea ta = new JTextArea(i18n.PERF_WARNING);
            ta.setBackground(getBackground());
            ta.setEditable(false);
            ta.setFont(lbl.getFont());
            add(ta, BorderLayout.SOUTH);
        }

        private class LimitRowsCheckBoxListener implements ChangeListener {
            private IntegerField _field;
            LimitRowsCheckBoxListener(IntegerField field) {
                super();
                _field = field;
            }
            public void stateChanged(ChangeEvent evt) {
                _field.setEnabled(((JCheckBox)evt.getSource()).isSelected());
            }
        }

        private class AutoCommitCheckBoxListener implements ChangeListener {
            public void stateChanged(ChangeEvent evt) {
                _commitOnClose.setEnabled(!((JCheckBox)evt.getSource()).isSelected());
            }
        }

        private final static class OutputType {
            private final String _name;
            private final String _className;

            OutputType(String name, String className) {
                super();
                _name = name;
                _className = className;
            }

            public String toString() {
                return _name;
            }

            String getPanelClassName() {
                return _className;
            }
        }

    }

}
