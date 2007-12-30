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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.TreeSet;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.MergeColumnDialog;


public class MergeColumnCommand extends AbstractRefactoringCommand {
    /**
     * Logger for this class.
     */
    private final static ILogger s_log = LoggerController.createLogger(MergeColumnCommand.class);

    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MergeColumnCommand.class);

    static interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("MergeColumnCommand.sqlDialogTitle");
    }

    protected MergeColumnDialog customDialog;


    public MergeColumnCommand(ISession session, IDatabaseObjectInfo[] info) {
        super(session, info);
    }


    @Override
    protected void onExecute() throws SQLException {
        if (!(_info[0] instanceof ITableInfo)) return;

        showCustomDialog();
    }


    private void showCustomDialog() throws SQLException {
        ITableInfo selectedTable = (ITableInfo) _info[0];
        TableColumnInfo[] tableColumnInfos = _session.getMetaData().getColumnInfo(selectedTable);
        TreeSet<String> localColumns = new TreeSet<String>();
        for (TableColumnInfo columns : tableColumnInfos) {
            localColumns.add(columns.getColumnName());
        }

        customDialog = new MergeColumnDialog(selectedTable.getSimpleName(), localColumns.toArray(new String[]{}));
        customDialog.addExecuteListener(new ExecuteListener());
        customDialog.addEditSQLListener(new EditSQLListener(customDialog));
        customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
        customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
        customDialog.setVisible(true);
    }


    @Override
    protected String[] generateSQLStatements() throws UserCancelledOperationException {
        ArrayList<String> results = new ArrayList<String>();
        ArrayList<String> updateResults = new ArrayList<String>();

        ISQLDatabaseMetaData md = _session.getMetaData();

        String joinString = customDialog.getJoinString();
        String column1 = customDialog.getFirstColumn();
        String column2 = customDialog.getSecondColumn();
        String catalog = _info[0].getCatalogName();
        String schema = _info[0].getSchemaName();
        DatabaseObjectQualifier qualifier = new DatabaseObjectQualifier(catalog, schema);
        String table = _info[0].getSimpleName();
        ArrayList<String[]> data = new ArrayList<String[]>();
        String mergeInColumn = "";
        int typeLength = 0;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // Selects the values from the existing table
            String dataQuery = "SELECT  " + "\"" + column1 + "\", \"" + column2 + 
            						 "\" FROM \"" + schema + "\".\"" + table + "\"";
            stmt = _session.getSQLConnection().createStatement();
            rs = stmt.executeQuery(dataQuery);
            while (rs.next()) {
                data.add(new String[]{rs.getString(1), rs.getString(2)});
            }

            if (customDialog.isNewColumn()) {
                // merge columns in a new column
                mergeInColumn = customDialog.getNewColumnName();


            } else if (customDialog.isMergeInExistingColumn()) {
                // merge columns in an existing column
                mergeInColumn = customDialog.getMergeInExistingColumn();

            }

            for (String[] dataColumn : data) {
                StringBuilder mergeColumnBuilder = new StringBuilder();
                String mergeColumnData;
                if (dataColumn[0] == null && dataColumn[1] == null && joinString.equals("")) {
                    mergeColumnData = null;
                    typeLength = 1;
                } else {
                    mergeColumnBuilder.append("'");
                    mergeColumnBuilder.append(dataColumn[0] == null ? "" : dataColumn[0]);
                    mergeColumnBuilder.append(joinString);
                    mergeColumnBuilder.append(dataColumn[1] == null ? "" : dataColumn[1]);
                    mergeColumnBuilder.append("'");
                    mergeColumnData = mergeColumnBuilder.toString();

                    if (typeLength < mergeColumnBuilder.length() - 2) typeLength = mergeColumnBuilder.length() - 2;

                    if (dataColumn[0] != null) dataColumn[0] = "'" + dataColumn[0] + "'";
                    if (dataColumn[1] != null) dataColumn[1] = "'" + dataColumn[1] + "'";
                }

                updateResults.add(_dialect.getUpdateSQL(table,
                        new String[]{mergeInColumn}, new String[]{mergeColumnData}, null,
                        new String[]{column1, column2}, new String[]{dataColumn[0], dataColumn[1]},
                        qualifier, _sqlPrefs));
            }

            // add column if neeeded
            if (customDialog.isNewColumn()) {
                TableColumnInfo newColumn = new TableColumnInfo(
                        catalog, schema, table, mergeInColumn,
                        Types.VARCHAR, JDBCTypeMapper.getJdbcTypeName(Types.VARCHAR), typeLength,
                        0, 0, 1, null, null, 0, 1, "YES", md);
                results.add(_dialect.getAddColumnSQL(newColumn, qualifier, _sqlPrefs));
            }
            results.addAll(updateResults);
        } catch (SQLException e) {
      	  s_log.error("generateSQLStatements: unexpected exception: "+e.getMessage(), e);
            _session.showErrorMessage(e);
        } finally {
      	  SQLUtilities.closeResultSet(rs);
      	  SQLUtilities.closeStatement(stmt);
        }

        return results.toArray(new String[]{});
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
		boolean result = true;
		// This refactoring depends on the following dialect API methods:
		// getUpdateSQL
		result = result && dialectExt.supportsUpdate();

		// TODO: Are there databases that don't support adding columns to tables?
		// getColumnAddSQL - no API method to check this

		return result;
	}
}
