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
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.hibernate.IHibernateDialectExtension;

import java.sql.SQLException;

/**
 * Implements showing a list of columns for a selected table to the
 * user and making the ones that are selected become the primary key for
 * the table
 *
 * @author rmmannin
 */
public class AddPrimaryKeyCommand extends AbstractRefactoringCommand {
    /**
     * Logger for this class.
     */
    private final static ILogger s_log = LoggerController.createLogger(RemoveColumnCommand.class);

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(AddPrimaryKeyCommand.class);

    static interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("AddPrimaryKeyCommand.sqlDialogTitle");
    }

    protected ColumnListDialog customDialog;


    public AddPrimaryKeyCommand(ISession session, IDatabaseObjectInfo[] info) {
        super(session, info);
    }


    //TODO: Remove when IHibernateDialectExtension is merged into HibernateDialect.
    private HibernateDialect _dialect;


    //TODO: Remove when IHibernateDialectExtension is merged into HibernateDialect.
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


    protected void onExecute() throws SQLException {
        if (!(_info[0] instanceof ITableInfo)) {
            return;
        }

        ITableInfo ti = (ITableInfo) _info[0];
        PrimaryKeyCommandUtility pkcUtil = new PrimaryKeyCommandUtility(_session, _info);
        if (pkcUtil.tableHasPrimaryKey()) {
            _session.showErrorMessage(s_stringMgr.getString("AddPrimaryKeyCommand.primaryKeyExists",
                    ti.getSimpleName()));
        } else {
            showCustomDialog();
        }
    }


    public void showCustomDialog() throws SQLException {
        ITableInfo ti = (ITableInfo) _info[0];
        TableColumnInfo[] columns = _session.getMetaData().getColumnInfo(ti);
        if(columns == null || columns.length == 0) {
            _session.showErrorMessage(s_stringMgr.getString("AddPrimaryKeyCommand.noColumns",
                    ti.getSimpleName()));
            return;
        }

        //Show the user a dialog with a list of columns and ask them to select one or more columns to drop
        customDialog = new ColumnListDialog(columns, ColumnListDialog.ADD_PRIMARY_KEY_MODE);
        customDialog.setTableName(ti.getQualifiedName());
        // Set a default primary key name based on the name of the table
        customDialog.setPrimaryKeyName("PK_" + columns[0].getTableName().toUpperCase());

        customDialog.addColumnSelectionListener(new ExecuteListener());
        customDialog.addEditSQLListener(new EditSQLListener(customDialog));
        customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
        customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
        customDialog.setMultiSelection();
        customDialog.setVisible(true);
    }


    @Override
    protected String[] generateSQLStatements() throws UserCancelledOperationException {
        return _dialect.getAddPrimaryKeySQL(customDialog.getPrimaryKeyName(),
                customDialog.getSelectedColumnList(), (ITableInfo) _info[0]);
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
	 * @param dialectExt the IHibernateDialectExtension to check
	 * @return true if this refactoring is supported; false otherwise.
	 */
	@Override
	protected boolean isRefactoringSupportedForDialect(IHibernateDialectExtension dialectExt)
	{
		// implemented in all originally supported dialects		
		return true;
	}
}