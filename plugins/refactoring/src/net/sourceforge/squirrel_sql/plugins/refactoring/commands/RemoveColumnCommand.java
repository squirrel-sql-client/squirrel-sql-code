package net.sourceforge.squirrel_sql.plugins.refactoring.commands;
/*
 * Copyright (C) 2006 Rob Manning
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

public class RemoveColumnCommand extends AbstractRefactoringCommand {
    /**
     * Logger for this class.
     */
    private final static ILogger s_log = LoggerController.createLogger(RemoveColumnCommand.class);

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(RemoveColumnCommand.class);

    static interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("RemoveColumnCommand.sqlDialogTitle");
    }

    protected ColumnListDialog customDialog;


    public RemoveColumnCommand(ISession session, IDatabaseObjectInfo[] info) {
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


    @Override
    protected void onExecute() throws SQLException {
        if (!(_info[0] instanceof ITableInfo)) {
            return;
        }

        ITableInfo ti = (ITableInfo) _info[0];
        TableColumnInfo[] columns = _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);

        if (columns.length < 2) {
            _session.showErrorMessage(s_stringMgr.getString("RemoveColumnAction.singleColumnMessage"));
        } else if (!_dialect.supportsDropColumn()) {
            _session.showErrorMessage(s_stringMgr.getString("RemoveColumnAction.removeColumnNotSupported",
                    _dialect.getDisplayName()));
        } else {
            showCustomDialog();
        }
    }


    private void showCustomDialog() throws SQLException {
        ITableInfo ti = (ITableInfo) _info[0];
        TableColumnInfo[] columns = _session.getMetaData().getColumnInfo(ti);

        //Show the user a dialog with a list of columns and ask them to select
        customDialog = new ColumnListDialog(columns, ColumnListDialog.DROP_COLUMN_MODE);
        customDialog.setMultiSelection();
        customDialog.setTableName(ti.getQualifiedName());

        customDialog.addColumnSelectionListener(new ExecuteListener());
        customDialog.addEditSQLListener(new EditSQLListener(customDialog));
        customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
        customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
        customDialog.setVisible(true);
    }


    @Override
    protected String[] generateSQLStatements() throws UserCancelledOperationException {
        TableColumnInfo[] columns = customDialog.getSelectedColumnList();

        String[] result = new String[columns.length];
        try {
            // TODO: add configuration for whether or not to qualify names. (SqlGenerationPreferences & DatabaseObjectQualifier can be used, see DialectExtension)
            String tableName = _info[0].getQualifiedName();
            for (int i = 0; i < columns.length; i++) {
                TableColumnInfo info = columns[i];
                String columnName = info.getColumnName();
                result[i] = _dialect.getColumnDropSQL(tableName, columnName);
            }
        } catch (UnsupportedOperationException e2) {
            _session.showMessage(s_stringMgr.getString("RemoveColumnCommand.unsupportedOperationMsg",
                    _dialect.getDisplayName()));
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