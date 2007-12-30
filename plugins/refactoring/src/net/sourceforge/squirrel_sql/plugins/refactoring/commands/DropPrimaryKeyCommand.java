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

import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Implements showing a list of primay key columns for a selected table to the
 * user, allowing the user to drop the primary key or view the SQL that will do
 * this.
 *
 * @author manningr
 */
public class DropPrimaryKeyCommand extends AbstractRefactoringCommand {
    /**
     * Logger for this class.
     */
    private final static ILogger s_log = LoggerController.createLogger(RemoveColumnCommand.class);

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DropPrimaryKeyCommand.class);

    static interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("DropPrimaryKeyCommand.sqlDialogTitle");
    }

    protected ColumnListDialog customDialog;


    public DropPrimaryKeyCommand(ISession session, IDatabaseObjectInfo[] info) {
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
    protected void onExecute() throws SQLException {
        if (!(_info[0] instanceof ITableInfo)) {
            return;
        }
        ITableInfo ti = (ITableInfo) _info[0];
        PrimaryKeyCommandUtility pkcUtil = new PrimaryKeyCommandUtility(super._session, _info);
        if (!pkcUtil.tableHasPrimaryKey()) {
            _session.showErrorMessage(s_stringMgr.getString("DropPrimaryKeyCommand.noKeyToDrop",
                    ti.getSimpleName()));
        } else {
            showCustomDialog();
        }
    }


    protected void showCustomDialog() throws SQLException {
        ITableInfo ti = (ITableInfo) _info[0];
        TableColumnInfo[] columns = getPkTableColumns(ti);

        //Show the user a dialog with a list of columns and ask them to select
        customDialog = new ColumnListDialog(columns, ColumnListDialog.DROP_PRIMARY_KEY_MODE);
        customDialog.addColumnSelectionListener(new ExecuteListener());
        customDialog.addEditSQLListener(new EditSQLListener(customDialog));
        customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
        customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
        customDialog.setMultiSelection();
        customDialog.setTableName(ti.getQualifiedName());

        SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
        PrimaryKeyInfo[] infos = md.getPrimaryKey(ti);
        String pkName = infos[0].getSimpleName();
        customDialog.setPrimaryKeyName(pkName);
        customDialog.setVisible(true);
    }


    private TableColumnInfo[] getPkTableColumns(ITableInfo ti) throws SQLException {
        ArrayList<TableColumnInfo> result = new ArrayList<TableColumnInfo>();
        PrimaryKeyInfo[] pkCols = _session.getMetaData().getPrimaryKey(ti);
        TableColumnInfo[] colInfos = _session.getMetaData().getColumnInfo(ti);

        for (PrimaryKeyInfo pkInfo : pkCols) {
            String pkColName = pkInfo.getQualifiedColumnName();
            for (TableColumnInfo colInfo : colInfos) {
                if (colInfo.getSimpleName().equals(pkColName)) {
                    result.add(colInfo);
                }
            }
        }
        return result.toArray(new TableColumnInfo[result.size()]);
    }


    @Override
    protected String[] generateSQLStatements() throws UserCancelledOperationException {
        String result = null;

        try {
            result = _dialect.getDropPrimaryKeySQL(customDialog.getPrimaryKeyName(), customDialog.getTableName());
        } catch (UnsupportedOperationException e2) {
            _session.showMessage(s_stringMgr.getString("DropPrimaryKeyCommand.unsupportedOperationMsg",
                    _dialect.getDisplayName()));
        }
        return new String[]{result};
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
	 * @param dialectExt the HibernateDialect to check
	 * @return true if this refactoring is supported; false otherwise.
	 */
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialectExt)
	{
		// implemented in all originally supported dialects		
		return true;
	}
}