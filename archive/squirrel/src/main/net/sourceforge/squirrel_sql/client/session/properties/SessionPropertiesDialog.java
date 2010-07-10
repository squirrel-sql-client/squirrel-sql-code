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
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanel;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanelEvent;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanelListener;

import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class SessionPropertiesDialog extends JDialog {

    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String TITLE = "Session Properties";
        String OUTPUT = "Output";
        String SQL = "SQL";
    }

    private ISession _session;
    private List _panels = new ArrayList();

    //private SQLPropertiesPanel _sqlPnl;
    //private OutputPropertiesPanel _outputPnl;

    public SessionPropertiesDialog(Frame frame, ISession session) {
        super(frame, i18n.TITLE);
        if (session == null) {
            throw new IllegalArgumentException("Null ISession passed");
        }
        _session = session;
        createUserInterface();
    }

    private void performCancel() {
        setVisible(false);
    }

    /**
     * OK button pressed. Edit data and if ok save to aliases model
     * and then close dialog.
     */
    private void performOk() {
        for (Iterator it = _panels.iterator(); it.hasNext();) {
            ((ISessionPropertiesPanel)it.next()).applyChanges();
        }

        dispose();
    }

    private void createUserInterface() {
        final SessionProperties props = _session.getProperties();

        _panels.add(new SQLPropertiesPanel(i18n.SQL, i18n.SQL));
        _panels.add(new OutputPropertiesPanel(i18n.OUTPUT, i18n.OUTPUT));

        // Ok and cancel buttons at bottom of dialog.
        OkCancelPanel btnsPnl = new OkCancelPanel();
        btnsPnl.addListener(new MyOkCancelPanelListener());

        getRootPane().setDefaultButton(btnsPnl.getOkButton());

        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //?? Go thru all plugins asking for panels.

        // Initialize all panels and add them to the dialog.
        JTabbedPane tabPane = new JTabbedPane();
        for (Iterator it = _panels.iterator(); it.hasNext();) {
            ISessionPropertiesPanel pnl = (ISessionPropertiesPanel)it.next();
            pnl.initialize(_session.getApplication(), props);
            String title = pnl.getTitle();
            String hint = pnl.getHint();
            tabPane.addTab(title, null, pnl.getPanelComponent(), hint);
        }

        contentPane.add(tabPane, BorderLayout.NORTH);
        contentPane.add(btnsPnl, BorderLayout.CENTER);

        setResizable(false);
        setModal(true);
        pack();

        GUIUtils.centerWithinParent(this);
    }

    private final class MyOkCancelPanelListener implements OkCancelPanelListener {
        public void okPressed(OkCancelPanelEvent evt) {
            performOk();
        }

        public void cancelPressed(OkCancelPanelEvent evt) {
            performCancel();
        }
    }
}
