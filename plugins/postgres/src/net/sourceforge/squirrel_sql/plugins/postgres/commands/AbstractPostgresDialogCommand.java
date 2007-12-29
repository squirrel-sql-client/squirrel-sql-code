package net.sourceforge.squirrel_sql.plugins.postgres.commands;
/*
* Copyright (C) 2007 Daniel Regli & Yannick Winiger
* http://sourceforge.net/projects/squirrel-sql
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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public abstract class AbstractPostgresDialogCommand extends AbstractPostgresCommand {
    public AbstractPostgresDialogCommand(ISession session) {
        super(session);
    }


    /**
     * The subclass should implement this so that the ExecuteListener can delegate the execution
     * of the sql script to the subclass.
     *
     * @param script the sql script that should be executed
     */
    protected abstract void executeScript(String script);


    /**
     * An ActionListener for the Show SQL button that opens the sql statement in a dialog.
     * The new dialog will have the title and parent window as specified in the constructors parameters.
     */
    protected class ShowSQLListener implements ActionListener, SQLResultListener {
        private final String _dialogTitle;
        private final JDialog _parentDialog;


        public ShowSQLListener(String dialogTitle, JDialog parentDialog) {
            _dialogTitle = dialogTitle;
            _parentDialog = parentDialog;
        }


        public void actionPerformed(ActionEvent e) {
            getSQLStatements(this);
        }


        public void finished(final String[] stmts) {
            if (stmts == null || stmts.length == 0) {
                _session.showMessage("No changes have been done.");
                return;
            }
            StringBuilder script = new StringBuilder();
            for (String stmt : stmts) {
                script.append(stmt).append("\n\n");
            }

            ErrorDialog sqldialog = new ErrorDialog(_parentDialog, script.substring(0, script.length() - 2));
            sqldialog.setTitle(_dialogTitle);
            sqldialog.setVisible(true);
        }
    }

    /**
     * An ActionListener for the Edit SQL button that appends the sql statement to the sql panel,
     * hides the specified dialog and switches to the sql panel.
     */
    protected class EditSQLListener implements ActionListener, SQLResultListener {
        private final JDialog _parentDialog;


        public EditSQLListener(JDialog parentDialog) {
            _parentDialog = parentDialog;
        }


        public void actionPerformed(ActionEvent e) {
            getSQLStatements(this);
        }


        public void finished(final String[] stmts) {
            if (stmts == null || stmts.length == 0) {
                _session.showMessage("No changes have been done.");
                return;
            }
            final StringBuilder script = new StringBuilder();
            for (String stmt : stmts) {
                script.append(stmt).append("\n\n");
            }

            GUIUtils.processOnSwingEventThread(new Runnable() {
                public void run() {
                    _parentDialog.setVisible(false);
                    _session.getSQLPanelAPIOfActiveSessionWindow().appendSQLScript(script.substring(0, script.length() - 2), true);
                    _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
                }
            });
        }
    }

    /**
     * An ActionListener for the Execute button that delegates the execution of the sql statement to the subclass.
     * The actual execution is specified in the subclass's implementation of the executeScript method.
     */
    protected class ExecuteListener implements ActionListener, SQLResultListener {
        public void actionPerformed(ActionEvent e) {
            getSQLStatements(this);
        }


        public void finished(String[] stmts) {
            if (stmts == null || stmts.length == 0) {
                _session.showMessage("No changes have been done.");
                return;
            }
            final StringBuilder script = new StringBuilder();
            for (String stmt : stmts) {
                script.append(stmt).append("\n");
            }

            executeScript(script.toString());
        }
    }
}
