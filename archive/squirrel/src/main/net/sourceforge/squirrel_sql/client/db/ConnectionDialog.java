package net.sourceforge.squirrel_sql.client.db;
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
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanel;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanelEvent;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanelListener;
import net.sourceforge.squirrel_sql.fw.gui.PropertyPanel;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;

public class ConnectionDialog extends JDialog {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String ALIAS = "Alias:";
        String CONNECT = "Connect";
        String DRIVER = "Driver:";
        String PASSWORD = "Password:";
        String URL = "URL:";
        String USER = "User:";
    }

    public interface IOkHandler {
        public boolean execute(ConnectionDialog dlog, DialogResult result);
    }

    private IApplication _app;
    private ISQLAlias _alias;
    private ISQLDriver _sqlDriver;

    private IOkHandler _okHandler;

    private JLabel _aliasName = new JLabel();
    private JLabel _driverName = new JLabel();
    private JLabel _url = new JLabel();
    private JTextField _user = new JTextField();
    private JTextField _password = new JPasswordField();

    public ConnectionDialog(IApplication app, Frame owner, ISQLAlias alias,
                                IOkHandler okHandler) {
        super(owner, i18n.CONNECT, true);
        _app = app;
        _alias = alias;
        _okHandler = okHandler;
        _sqlDriver = _app.getDataCache().getDriver(_alias.getDriverIdentifier());
        createUserInterface();
        loadData();
    }

    public void setVisible(boolean value) {
        super.setVisible(value);
    }

    private void loadData() {
        final String userName = _alias.getUserName();
        _aliasName.setText(_alias.getName());
        _driverName.setText(_sqlDriver.getName());
        _url.setText(_alias.getUrl());
        _user.setText(userName);
        _password.setText("");
    }

    /**
     * Cancel button pressed.
     */
    private void performCancel() {
        dispose();
    }

    /**
     * OK button pressed.
     */
    private void performOk() {
        if (_okHandler != null) {
            DialogResult result = new DialogResult(true, _user.getText(),
                                                    _password.getText());
            boolean ok = false;
            CursorChanger cursorChg = new CursorChanger(this);
            cursorChg.show();
            try {
                ok = _okHandler.execute(this, result);
            } finally {
                cursorChg.restore();
            }
            if (ok) {
                dispose();
            }
        } else {
            dispose();
        }
    }

    private void createUserInterface() {
        PropertyPanel dataEntryPnl = new PropertyPanel();

        JLabel lbl = new JLabel(i18n.ALIAS, SwingConstants.RIGHT);
        dataEntryPnl.add(lbl, _aliasName);

        lbl = new JLabel(i18n.DRIVER, SwingConstants.RIGHT);
        dataEntryPnl.add(lbl, _driverName);

        lbl = new JLabel(i18n.URL, SwingConstants.RIGHT);
        dataEntryPnl.add(lbl, _url);

        lbl = new JLabel(i18n.USER, SwingConstants.RIGHT);
        _user.setColumns(25);
        dataEntryPnl.add(lbl, _user);

        lbl = new JLabel(i18n.PASSWORD, SwingConstants.RIGHT);
        dataEntryPnl.add(lbl, _password);

        // Ok and cancel buttons at bottom of dialog.
        OkCancelPanel btnsPnl = new OkCancelPanel();
        btnsPnl.addListener(new MyOkCancelPanelListener());

        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(dataEntryPnl, BorderLayout.CENTER);
        contentPane.add(btnsPnl, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnsPnl.getOkButton());
        pack();
        GUIUtils.centerWithinParent(this);
        setResizable(false);

        addWindowListener(new MyWindowListener());
    }

    public static class DialogResult {
        private DialogResult(boolean okPressed, String user, String password) {
            super();
            _okPressed = okPressed;
            _user = user;
            _password = password;
        }
        public boolean _okPressed;
        public String _user;
        public String _password;
    }

    private final class MyOkCancelPanelListener implements OkCancelPanelListener {
        public void okPressed(OkCancelPanelEvent evt) {
            performOk();
        }


        public void cancelPressed(OkCancelPanelEvent evt) {
            performCancel();
        }
    }

    private final class MyWindowListener extends WindowAdapter {
        private boolean _doneOnce = false;
        public void windowActivated(WindowEvent evt) {
            if (!_doneOnce) {
                _doneOnce = true;
                final String userName = _user.getText();
                if (userName != null && userName.length() > 0) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            _password.requestFocus();
                        }
                    });
                }
            }
        }
    }

}
