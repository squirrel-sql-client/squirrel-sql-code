package net.sourceforge.squirrel_sql.plugins.refactoring.commands;
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

import net.sourceforge.squirrel_sql.client.gui.db.ColumnDetailDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.DBUtil;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;

import org.hibernate.HibernateException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;


public class AddColumnCommand extends AbstractRefactoringCommand {
    /**
     * Logger for this class.
     */
    private final static ILogger s_log = LoggerController.createLogger(AddColumnCommand.class);

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AddColumnCommand.class);

    static interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("AddColumnCommand.sqlDialogTitle");
        String COLUMN_ALREADY_EXISTS_TITLE = s_stringMgr.getString("AddColumnCommand.columnAlreadyExistsTitle");
    }

    protected ColumnDetailDialog customDialog;


    public AddColumnCommand(ISession session, IDatabaseObjectInfo[] info) {
        super(session, info);
    }


    //TODO: Remove when HibernateDialect is merged into HibernateDialect.
    private HibernateDialect _dialect;


    //TODO: Remove when HibernateDialect is merged into HibernateDialect.
    @Override
    public void execute() {
        try {
            _dialect = DialectFactory.getDialect(DialectFactory.DEST_TYPE,
                    _session.getApplication().getMainFrame(), _session.getMetaData());
            onExecute();
        } catch (UserCancelledOperationException e) {
            _session.showErrorMessage(AbstractRefactoringCommand.i18n.DIALECT_SELECTION_CANCELLED);
        } catch (Exception e) {
            _session.showErrorMessage(e);
            s_log.error("Unexpected exception on execution: " + e.getMessage(), e);
        }
    }


    @Override
    protected void onExecute() {
        showCustomDialog();
    }


    protected void showCustomDialog() {
        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        customDialog = new ColumnDetailDialog(ColumnDetailDialog.ADD_MODE);
                        customDialog.setTableName(_info[0].getQualifiedName());
                        customDialog.setSelectedDialect(_dialect.getDisplayName());
                        customDialog.addExecuteListener(new AddColumnExecuteListener());
                        customDialog.addEditSQLListener(new EditSQLListener(customDialog));
                        customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
                        customDialog.addDialectListListener(new DialectListListener());
                        customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
                        customDialog.setVisible(true);
                    }
                });
            }
        });
    }


    @Override
    protected String[] generateSQLStatements() throws Exception {
        TableColumnInfo info = customDialog.getColumnInfo();
        String[] result = null;
        try {
            result = DBUtil.getAlterSQLForColumnAddition(info, _dialect);

            //TODO: Append the SqlStatementSeparators directly in the Dialect. (for examples, see the DialectExtensions)
            for (int i = 0; i < result.length; i++) {
                result[i] = result[i] + _sqlPrefs.getSqlStatementSeparator();
            }
        } catch (HibernateException e1) {
            String dataType = customDialog.getSelectedTypeName();
            JOptionPane.showMessageDialog(customDialog,
                    s_stringMgr.getString("AbstractRefactoringCommand.unsupportedTypeMsg",
                            _dialect.getDisplayName(), dataType),
                    AbstractRefactoringCommand.i18n.UNSUPPORTED_TYPE_TITLE, JOptionPane.ERROR_MESSAGE);
        } catch (UnsupportedOperationException e2) {
            _session.showMessage(s_stringMgr.getString("AddColumnCommand.unsupportedOperationMsg",
                    _dialect.getDisplayName()));
        }
        return result;
    }


    @Override
    protected void executeScript(String script) {
        CommandExecHandler handler = new CommandExecHandler(_session);
        SQLExecuterTask executer = new SQLExecuterTask(_session, script, handler);
        executer.run();     // Execute the sql synchronously

        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                customDialog.setVisible(false);
            }
        });
    }


	/**
	 * Returns a boolean value indicating whether or not this refactoring is supported for the specified
	 * dialect.
	 * 
	 * @param dialectExt
	 *           the HibernateDialect to check
	 * @return true if this refactoring is supported; false otherwise.
	 */
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialectExt)
	{
		// implemented in all originally supported dialects 
		return true;
	}


	private class AddColumnExecuteListener extends ExecuteListener {
        public void actionPerformed(ActionEvent e) {
            String columnName = customDialog.getColumnInfo().getColumnName();
            try {
                if (!isColumnNameUnique(columnName)) {
                    JOptionPane.showMessageDialog(customDialog,
                            s_stringMgr.getString("AddColumnCommand.columnAlreadyExistsMsg",
                                    customDialog.getTableName(), columnName),
                            i18n.COLUMN_ALREADY_EXISTS_TITLE, JOptionPane.ERROR_MESSAGE);
                } else {
                    super.actionPerformed(e);
                }
            } catch (SQLException ex) {
                _session.showErrorMessage(ex);
                s_log.error("Unexpected exception - " + ex.getMessage(), ex);
            }
        }


        /**
         * Tests to see if the specified column name already exists.
         *
         * @param columnName the column name to check.
         * @return true if the column name doesn't exists; false otherwise
         * @throws java.sql.SQLException unexpected sql exception
         */
        private boolean isColumnNameUnique(String columnName) throws SQLException {
            boolean result = true;
            ISQLDatabaseMetaData md = _session.getMetaData();
            TableColumnInfo[] columnInfos = md.getColumnInfo((ITableInfo) _info[0]);
            for (TableColumnInfo columnInfo : columnInfos) {
                String existingColumnName = columnInfo.getColumnName();
                if (columnName.equalsIgnoreCase(existingColumnName)) {
                    result = false;
                    break;
                }
            }

            return result;
        }
    }

    private class DialectListListener implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            _dialect = DialectFactory.getDialect(customDialog.getSelectedDBName());
        }
    }
}