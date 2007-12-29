package net.sourceforge.squirrel_sql.plugins.refactoring.commands;
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

import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.DialectFactoryExtension;
import net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.IHibernateDialectExtension;
import net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.SqlGenerationPreferences;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class AbstractRefactoringCommand implements ICommand {
    /**
     * Logger for this class.
     */
    private final static ILogger s_log = LoggerController.createLogger(AbstractRefactoringCommand.class);

    /**
     * Internationalized strings for this class
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AbstractRefactoringCommand.class);

    static interface i18n {
        String NO_CHANGES = s_stringMgr.getString("AbstractRefactoringCommand.noChanges");
        String DIALECT_SELECTION_CANCELLED = s_stringMgr.getString("AbstractRefactoringCommand.dialectSelectionCancelled");
        String UNSUPPORTED_TYPE_TITLE = s_stringMgr.getString("AbstractRefactoringCommand.unsupportedTypeTitle");
    }

    /**
     * Current session
     */
    protected final ISession _session;

    /**
     * Selected database object(s)
     */
    protected final IDatabaseObjectInfo[] _info;

    /**
     * HibernateDialect to use for this refactoring.
     * TODO: Replace IHibernateDialectExtension with HibernateDialect as soon
     * TODO: as all the extensional code is merged into the original HibernateDialect classes.
     */
    protected IHibernateDialectExtension _dialect;

    /**
     * User preferences regarding the generation of SQL scripts.
     */
    protected final SqlGenerationPreferences _sqlPrefs;


    public AbstractRefactoringCommand(ISession session, IDatabaseObjectInfo[] info) {
        if (session == null) throw new IllegalArgumentException("ISession cannot be null");
        if (info == null) throw new IllegalArgumentException("IDatabaseObjectInfo[] cannot be null");

        _session = session;
        _info = info;

        //TODO: Get the actual user-defined preferences.
        _sqlPrefs = new SqlGenerationPreferences();
        _sqlPrefs.setSqlStatementSeparator(_session.getQueryTokenizer().getSQLStatementSeparator());
    }


    /**
     * Does general execution work that every refactoring command needs (e.g. HibernateDialect)
     * and then calls the onExecute method for the subclass's specific execution implementation.
     */
    public void execute() {
        try {
      	  ISQLDatabaseMetaData md = _session.getMetaData();
            _dialect = DialectFactoryExtension.getDialect(DialectFactory.DEST_TYPE,
                    _session.getApplication().getMainFrame(), md);
            if (isRefactoringSupportedForDialect(_dialect)) {
            	onExecute();
            } else {
            	String dialectName = DialectFactory.getDialectType(md).name();
            	String msg = 
            		s_stringMgr.getString("AbstractRefactoringCommand.unsupportedRefactoringMsg", dialectName);
            	_session.showErrorMessage(msg);
            }
        } catch (UserCancelledOperationException e) {
            _session.showErrorMessage(AbstractRefactoringCommand.i18n.DIALECT_SELECTION_CANCELLED);
        } catch (Exception e) {
            _session.showErrorMessage(e);
            s_log.error("Unexpected exception on execution: " + e.getMessage(), e);
        }
    }
    
 	/**
	  * Returns a boolean value indicating whether or not this refactoring is supported for the specified 
	  * dialect. 
	  * 
	  * @param dialectExt the IHibernateDialectExtension to check
	  * @return true if this refactoring is supported; false otherwise.
	  */
	protected abstract boolean isRefactoringSupportedForDialect(IHibernateDialectExtension dialectExt);
    
    /**
     * The subclass should implement this method with it's refactoring specific execution code.
     *
     * @throws Exception if something goes wrong while executing the command
     */
    protected abstract void onExecute() throws Exception;


    /**
     * The subclass should implement this so that getSQLStatements can generate the actual statements.
     *
     * @return the sql statements
     * @throws Exception if something goes wrong while generating the sql statements
     */
    protected abstract String[] generateSQLStatements() throws Exception;


    /**
     * Adds a new task to the ThreadPool that generates the SQL statements and
     * notifies the listener when the statements were successfully generated.
     *
     * @param listener the listener to notify when the statments are ready
     */
    protected void getSQLStatements(final SQLResultListener listener) {
        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                try {
                    listener.finished(generateSQLStatements());
                } catch (UserCancelledOperationException ucoe) {
                    _session.showErrorMessage(i18n.DIALECT_SELECTION_CANCELLED);
                } catch (Exception e) {
                    _session.showErrorMessage(e);
                    s_log.error("Unexpected exception on sql generation: " + e.getMessage(), e);                    
                }
            }
        });
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
     * <p/>
     * If the specified parent JDialog is null, the new dialog's parent will be the application's mainframe.
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
                _session.showMessage(i18n.NO_CHANGES);
                return;
            }

            final StringBuilder script = new StringBuilder();
            for (String stmt : stmts) {
                script.append(stmt).append("\n\n");
            }
            script.setLength(script.length() - 2);

            GUIUtils.processOnSwingEventThread(new Runnable() {
                public void run() {
                    final ErrorDialog showSQLDialog;
                    if (_parentDialog != null) showSQLDialog = new ErrorDialog(_parentDialog, script.toString());
                    else showSQLDialog = new ErrorDialog(_session.getApplication().getMainFrame(), script.toString());
                    showSQLDialog.setTitle(_dialogTitle);
                    showSQLDialog.setVisible(true);
                }
            });
        }
    }


    /**
     * An ActionListener for the Edit SQL button that appends the sql statement to the sql panel,
     * disposes the specified dialog and switches to the sql panel.
     * <p/>
     * The specified dialog can be null if no JDialog needs to be disposed.
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
                _session.showMessage(i18n.NO_CHANGES);
                return;
            }
            final StringBuilder script = new StringBuilder();
            for (String stmt : stmts) {
                script.append(stmt).append("\n\n");
            }
            script.setLength(script.length() - 2);

            GUIUtils.processOnSwingEventThread(new Runnable() {
                public void run() {
                    if (_parentDialog != null) {
                        _parentDialog.dispose();
                    }
                    _session.getSQLPanelAPIOfActiveSessionWindow().appendSQLScript(script.toString(), true);
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
                _session.showMessage(i18n.NO_CHANGES);
                return;
            }
            final StringBuilder script = new StringBuilder();
            for (String stmt : stmts) {
                script.append(stmt).append("\n");
            }
            script.setLength(script.length() - 1);

            executeScript(script.toString());
        }
    }

    protected class CommandExecHandler extends DefaultSQLExecuterHandler {
        protected boolean exceptionEncountered = false;


        public CommandExecHandler(ISession session) {
            super(session);
        }


        public void sqlExecutionException(Throwable th, String postErrorString) {
            super.sqlExecutionException(th, postErrorString);
            exceptionEncountered = true;
        }


        public boolean exceptionEncountered() {
            return exceptionEncountered;
        }
    }
}
