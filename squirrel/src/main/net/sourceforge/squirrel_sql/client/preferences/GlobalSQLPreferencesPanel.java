package net.sourceforge.squirrel_sql.client.preferences;
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
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.properties.SQLPropertiesPanel;
import net.sourceforge.squirrel_sql.client.preferences.*;


class GlobalSQLPreferencesPanel /*extends JPanel*/ implements IGlobalPreferencesPanel {

    private IApplication _app;

    private MyPanel _myPanel = new MyPanel();

//  private JCheckBox _debugJdbc = new JCheckBox();
//  private IntegerField _loginTimeout = new IntegerField();

    //public GlobalSQLPreferencesPanel() {
    //  this(null);
    //}

    public GlobalSQLPreferencesPanel(/*SquirrelPreferences prefs*/) {
        super();
//      if (prefs == null) {
//          throw new IllegalArgumentException("Null SquirrelPreferences passed");
//      }
//      _prefs = prefs;
//      createUserInterface();
//      loadData();
    }

    public void initialize(IApplication app)
            throws IllegalArgumentException {
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }

        _app = app;

//      createUserInterface();
        _myPanel.loadData(_app.getSquirrelPreferences());
    }

//  public void setPreferences(SquirrelPreferences prefs) {
//      _prefs = prefs;
//  }

    public void applyChanges() {
        _myPanel.applyChanges(_app.getSquirrelPreferences());
/*
        _prefs.setDebugJdbc(_debugJdbc.isSelected());
        _prefs.setLoginTimeout(_loginTimeout.getInt());
*/
    }

    public String getTitle() {
        return MyPanel.i18n.TAB_TITLE;
    }

    public String getHint() {
        return MyPanel.i18n.TAB_HINT;
    }

    public Component getPanelComponent() {
        return _myPanel;
    }

/*
    private void loadData() {
        _debugJdbc.setSelected(_prefs.getDebugJdbc());
        _loginTimeout.setInt(_prefs.getLoginTimeout());
    }
*/

/*
    private void createUserInterface() {
        final Icon warnIcon = _prefs.getApplication().getResources().getIcon(SquirrelResources.ImageNames.PERFORMANCE_WARNING);

        setLayout(new BorderLayout());

        // Centre panel is the properties panel.
        PropertyPanel pnl = new PropertyPanel();
        JLabel lbl = new JLabel(i18n.DEBUG_JDBC, SwingConstants.RIGHT);
        pnl.add(lbl, _debugJdbc, new JLabel(warnIcon));
        lbl = new JLabel(i18n.LOGIN_TIMEOUT, SwingConstants.RIGHT);
        pnl.add(lbl, _loginTimeout);
        add(pnl, BorderLayout.CENTER);

        // Warning message in bottom panel.
        JTextArea ta = new JTextArea(i18n.PERF_WARNING);
        ta.setBackground(getBackground());
        ta.setEditable(false);
        ta.setFont(lbl.getFont());
        add(ta, BorderLayout.SOUTH);
    }
*/

    private static final class MyPanel extends JPanel {
        /**
         * This interface defines locale specific strings. This should be
         * replaced with a property file.
         */
        private interface i18n {
            String NBR_ROWS = "SQL - Number of rows:";
            String LIMIT_ROWS = "SQL - Limit rows:";
            String DEBUG_JDBC = "JDBC Debug Info to Standard Output:";
            String LOGIN_TIMEOUT = "Login Timeout (Seconds)";
            String TAB_HINT = "Global SQL";
            String TAB_TITLE = "Global SQL";
            String PERF_WARNING = "Note: Settings marked with an hourglass will have performance implications.";
        }

        private boolean _initialized = false;

        private JCheckBox _debugJdbc = new JCheckBox();
        private IntegerField _loginTimeout = new IntegerField();

        MyPanel() {
            super();
        }

        void loadData(SquirrelPreferences prefs) {
            _debugJdbc.setSelected(prefs.getDebugJdbc());
            _loginTimeout.setInt(prefs.getLoginTimeout());
            if (!_initialized) {
                createUserInterface(prefs);
                _initialized = true;
            }
        }

        void applyChanges(SquirrelPreferences prefs) {
            prefs.setDebugJdbc(_debugJdbc.isSelected());
            prefs.setLoginTimeout(_loginTimeout.getInt());
        }

        private void createUserInterface(SquirrelPreferences prefs) {
            final Icon warnIcon = prefs.getApplication().getResources().getIcon(SquirrelResources.ImageNames.PERFORMANCE_WARNING);

            setLayout(new BorderLayout());

            // Centre panel is the properties panel.
            PropertyPanel pnl = new PropertyPanel();
            JLabel lbl = new JLabel(i18n.DEBUG_JDBC, SwingConstants.RIGHT);
            pnl.add(lbl, _debugJdbc, new JLabel(warnIcon));
            lbl = new JLabel(i18n.LOGIN_TIMEOUT, SwingConstants.RIGHT);
            pnl.add(lbl, _loginTimeout);
            add(pnl, BorderLayout.CENTER);

            // Warning message in bottom panel.
            JTextArea ta = new JTextArea(i18n.PERF_WARNING);
            ta.setBackground(getBackground());
            ta.setEditable(false);
            ta.setFont(lbl.getFont());
            add(ta, BorderLayout.SOUTH);
        }
    }
}
