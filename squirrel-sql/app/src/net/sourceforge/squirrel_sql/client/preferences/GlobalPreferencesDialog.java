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
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanel;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanelEvent;
import net.sourceforge.squirrel_sql.fw.gui.OkCancelPanelListener;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.session.properties.OutputPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.properties.SQLPropertiesPanel;

public class GlobalPreferencesDialog extends JDialog {
    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String TITLE = "Global Preferences";
        String NEW_SESSION_SQL = "New Session SQL";
        String NEW_SESSION_OUTPUT = "New Session Output";
    }

    private IApplication _app;
    private List _panels = new ArrayList();

    /**
     * Default properties for new sessions.
     */
    private SessionProperties _sessionProperties;

    public GlobalPreferencesDialog(IApplication app, Frame owner)
            throws IllegalArgumentException {
        super(owner, i18n.TITLE);
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        _app = app;
        createUserInterface();
    }

    private void performCancel() {
        dispose();
    }

    /**
     * OK button pressed. Edit data and if ok save and then close dialog.
     */
    private void performOk() {
        for (Iterator it = _panels.iterator(); it.hasNext();) {
            ((IGlobalPreferencesPanel)it.next()).applyChanges();
        }

        dispose();
    }

    private void createUserInterface() {
        final SquirrelPreferences prefs = _app.getSquirrelPreferences();
        final SessionProperties props = prefs.getSessionProperties();

        // Add panels for core Squirrel functionality.
        _panels.add(new GeneralPreferencesPanel());
        _panels.add(new GlobalSQLPreferencesPanel());
        _panels.add(new SQLPropertiesPanel(i18n.NEW_SESSION_SQL, i18n.NEW_SESSION_SQL));
        _panels.add(new OutputPropertiesPanel(i18n.NEW_SESSION_OUTPUT, i18n.NEW_SESSION_OUTPUT));

        // Go thru all loaded plugins asking for panels.
        PluginInfo[] plugins = _app.getPluginManager().getPluginInformation();
        for (int plugIdx = 0; plugIdx < plugins.length; ++plugIdx) {
            PluginInfo pi = plugins[plugIdx];
            if (pi.isLoaded()) {
                IGlobalPreferencesPanel[] pnls = pi.getPlugin().getGlobalPreferencePanels();
                if (pnls != null && pnls.length > 0) {
                    for (int pnlIdx = 0; pnlIdx < pnls.length; ++pnlIdx) {
                        _panels.add(pnls[pnlIdx]);
                    }
                }
            }
        }

        // Initialize all panels and add them to the dialog.
        JTabbedPane tabPane = new JTabbedPane();
        for (Iterator it = _panels.iterator(); it.hasNext();) {
            IGlobalPreferencesPanel pnl = (IGlobalPreferencesPanel)it.next();
            pnl.initialize(_app);
            String title = pnl.getTitle();
            String hint = pnl.getHint();
            tabPane.addTab(title, null, pnl.getPanelComponent(), hint);
        }

        // Ok and cancel buttons at bottom of dialog.
        OkCancelPanel btnsPnl = new OkCancelPanel();
        btnsPnl.addListener(new MyOkCancelPanelListener());

        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(tabPane, BorderLayout.NORTH);
        contentPane.add(btnsPnl, BorderLayout.CENTER);

        getRootPane().setDefaultButton(btnsPnl.getOkButton());

        pack();
        GUIUtils.centerWithinParent(this);
        setResizable(false);
        setModal(true);
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
