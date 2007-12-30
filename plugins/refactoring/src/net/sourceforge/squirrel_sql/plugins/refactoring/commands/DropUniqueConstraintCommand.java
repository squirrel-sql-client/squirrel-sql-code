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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IndexInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.DefaultDropDialog;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.DefaultListDialog;

public class DropUniqueConstraintCommand extends AbstractRefactoringCommand {
    /**
     * Logger for this class.
     */
    private final ILogger s_log = LoggerController.createLogger(DropUniqueConstraintCommand.class);
    /**
     * Internationalized strings for this class
     */
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DropUniqueConstraintCommand.class);

    static interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("DropUniqueConstraintCommand.sqlDialogTitle");
    }

    protected DefaultDropDialog customDialog;
    private IndexInfo[] _dropIndexInfo;
    private DefaultListDialog listDialog;


    public DropUniqueConstraintCommand(ISession session, IDatabaseObjectInfo[] dbInfo) {
        super(session, dbInfo);
    }


    @Override
    protected void onExecute() throws SQLException {
        ITableInfo ti = (ITableInfo) _info[0];
        List<IndexInfo> indexes = _session.getSQLConnection().getSQLMetaData().getIndexInfo(ti);
        List<IndexInfo> uniqueIndexes = new ArrayList<IndexInfo>();
        for (IndexInfo index : indexes) {
            if (!index.isNonUnique()) {
                uniqueIndexes.add(index);
            }
        }
        if (uniqueIndexes.size() == 0) {
            _session.showErrorMessage(s_stringMgr.getString("DropUniqueConstraintCommand.noUniqueConstraintonTable",
                    ti.getSimpleName()));
        } else if (uniqueIndexes.size() == 1) {
            _dropIndexInfo = new IndexInfo[]{uniqueIndexes.get(0)};
            showCustomDialog();
        } else {
            listDialog = new DefaultListDialog(indexes.toArray(new IndexInfo[]{}), ti.getSimpleName(), DefaultListDialog.DIALOG_TYPE_UNIQUE_CONSTRAINTS);
            listDialog.addColumnSelectionListener(new ColumnListSelectionActionListener());
            listDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
            listDialog.setVisible(true);
        }
    }


    protected void showCustomDialog() {
        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        customDialog = new DefaultDropDialog(_dropIndexInfo, DefaultDropDialog.DIALOG_TYPE_UNIQUE_CONSTRAINT_KEY);
                        customDialog.setCascadeVisible(false);

                        customDialog.addExecuteListener(new ExecuteListener());
                        customDialog.addEditSQLListener(new EditSQLListener(customDialog));
                        customDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, customDialog));
                        customDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
                        customDialog.setVisible(true);
                    }
                });
            }
        });
    }


    @Override
    protected String[] generateSQLStatements() throws UserCancelledOperationException {
        ArrayList<String> result = new ArrayList<String>();

        for (IndexInfo iInfo : _dropIndexInfo) {
            DatabaseObjectQualifier qualifier = new DatabaseObjectQualifier(iInfo.getCatalogName(), iInfo.getSchemaName());
            result.add(_dialect.getDropConstraintSQL(iInfo.getTableName(), iInfo.getSimpleName(), qualifier, _sqlPrefs));
        }

        return result.toArray(new String[]{});
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
		return dialectExt.supportsDropConstraint();
	}


	private class ColumnListSelectionActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (listDialog == null) return;

            listDialog.setVisible(false);

            _dropIndexInfo = listDialog.getSelectedItems().toArray(new IndexInfo[]{});
            showCustomDialog();
        }
    }
}
