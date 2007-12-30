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
import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
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
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ModifyColumnCommand extends AbstractRefactoringCommand {
    /**
     * Logger for this class.
     */
    private final static ILogger s_log = LoggerController.createLogger(ModifyColumnCommand.class);

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ModifyColumnCommand.class);

    static interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("ModifyColumnCommand.sqlDialogTitle");
        String MODIFY_ONE_COL_MSG = s_stringMgr.getString("ModifyColumnCommand.modifyOneColMsg");
    }

    private ColumnListDialog listDialog = null;
    protected ColumnDetailDialog customDialog;
    private TableColumnInfo columnToModify = null;


    public ModifyColumnCommand(ISession session, IDatabaseObjectInfo[] info) {
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


    /**
     * Execute this command. Save the session and selected objects in the plugin
     * for use in paste command.
     */
    protected void onExecute() throws SQLException {
        if (!(_info[0] instanceof ITableInfo)) {
            return;
        }

        //Show the user a dialog with a list of columns and ask them to select
        // one or more columns to drop
        ITableInfo ti = (ITableInfo) _info[0];
        TableColumnInfo[] columns = _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);

        if (columns.length == 1) {
            columnToModify = columns[0];
            showCustomDialog();
        } else {
            listDialog = new ColumnListDialog(columns, ColumnListDialog.MODIFY_COLUMN_MODE);
            listDialog.setTableName(ti.getQualifiedName());
            listDialog.setSingleSelection();

            listDialog.addColumnSelectionListener(new ColumnListSelectionActionListener());
            listDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
            listDialog.setVisible(true);
        }
    }


    private void showCustomDialog() {
        customDialog = new ColumnDetailDialog(ColumnDetailDialog.MODIFY_MODE);
        customDialog.setExistingColumnInfo(columnToModify);
        customDialog.setTableName(_info[0].getQualifiedName());
        customDialog.setSelectedDialect(_dialect.getDisplayName());

        customDialog.addExecuteListener(new ExecuteListener());
        customDialog.addEditSQLListener(new EditSQLListener(customDialog));
        customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
        customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
        customDialog.setVisible(true);
    }


    @Override
    protected String[] generateSQLStatements() {
        String[] result = null;

        TableColumnInfo to = customDialog.getColumnInfo();
        String dbName = customDialog.getSelectedDBName();
        _dialect = DialectFactory.getDialect(dbName);

        try {
            result = DBUtil.getAlterSQLForColumnChange(columnToModify, to, _dialect);

            //TODO: Append the SqlStatementSeparators directly in the Dialect. (for examples, see the DialectExtensions)
            for (int i = 0; i < result.length; i++) {
                result[i] = result[i] + _sqlPrefs.getSqlStatementSeparator();
            }
        } catch (HibernateException e1) {
            JOptionPane.showMessageDialog(customDialog,
                    s_stringMgr.getString("AbstractRefactoringCommand.unsupportedTypeMsg",
                            _dialect.getDisplayName(), customDialog.getSelectedTypeName()),
                    AbstractRefactoringCommand.i18n.UNSUPPORTED_TYPE_TITLE, JOptionPane.ERROR_MESSAGE);
        } catch (UnsupportedOperationException e2) {
            _session.showErrorMessage(e2.getMessage());
        }

        return result;
    }


    @Override
    protected void executeScript(String script) {
        CommandExecHandler handler = new CommandExecHandler(_session);

        SQLExecuterTask executer = new SQLExecuterTask(_session, script, handler);
        executer.run(); // Execute the sql synchronously

        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        customDialog.setVisible(false);
                        _session.getSchemaInfo().reloadAll();
                    }
                });
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


	private class ColumnListSelectionActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (listDialog == null) return;

            listDialog.setVisible(false);
            TableColumnInfo[] colInfos = listDialog.getSelectedColumnList();
            if (colInfos == null || colInfos.length != 1) {
                _session.showMessage(i18n.MODIFY_ONE_COL_MSG);
                return;
            }
            columnToModify = colInfos[0];

            showCustomDialog();
        }
    }
}