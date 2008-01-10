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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnDetailDialog;
import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.DBUtil;

import org.hibernate.HibernateException;

/**
 * The command implementation that allows a user to modify a single column in a table. If the table has more
 * than one column, this command will first present the user with a column list dialog to ask them which
 * column to modify.
 * 
 * @author manningr
 */
public class ModifyColumnCommand extends AbstractRefactoringCommand
{
	/**
	 * Logger for this class.
	 */
	private final static ILogger s_log = LoggerController.createLogger(ModifyColumnCommand.class);

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ModifyColumnCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("ModifyColumnCommand.sqlDialogTitle");

		String MODIFY_ONE_COL_MSG = s_stringMgr.getString("ModifyColumnCommand.modifyOneColMsg");
	}

	/**
	 * Dialog that is displayed to user when this command is executed and there is more than one column in the
	 * table that was selected.
	 */
	private ColumnListDialog listDialog = null;

	/**
	 * Dialog that is displayed to user when this command is executed and there is only one column in the table,
	 * or the column list dialog as presented and the user chose a single column to modify.
	 */
	protected ColumnDetailDialog customDialog;

	/**
	 * The column that we will be modifying in this command, as it exists presently in the table, before
	 * modification.
	 */
	private TableColumnInfo columnToModify = null;

	
	/**
	 * Creates an instance of ModifyColumnCommand.
	 * 
	 * @param session the current ISession
	 * @param info the list of IDatabaseObjectInfo(s) that the user selected
	 */
	public ModifyColumnCommand(ISession session, IDatabaseObjectInfo[] info)
	{
		super(session, info);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.commands.AbstractRefactoringCommand#onExecute()
	 */
	@Override
	protected void onExecute() throws SQLException
	{
		if (!(_info[0] instanceof ITableInfo))
		{
			s_log.error("onExecute: _info[0] isn't an instance of ITableInfo: class="
				+ _info[0].getClass().getName());
			return;
		}

		ITableInfo ti = (ITableInfo) _info[0];
		TableColumnInfo[] columns = _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);

		// If there is only one column in the table,
		if (columns.length == 1)
		{
			if (s_log.isDebugEnabled()) {
				s_log.debug("Table has exactly one column, so presenting the modify column dialog");
			}
			columnToModify = columns[0];
			showCustomDialog();
		} else
		{
			if (s_log.isDebugEnabled()) {
				s_log.debug("Table has exactly " + columns.length
					+ " columns, so presenting the column list selection dialog");
			}
			listDialog = new ColumnListDialog(columns, ColumnListDialog.MODIFY_COLUMN_MODE);
			listDialog.setTableName(ti.getQualifiedName());
			listDialog.setSingleSelection();
			listDialog.addColumnSelectionListener(new ColumnListSelectionActionListener());
			listDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
			listDialog.setVisible(true);
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.commands.AbstractRefactoringCommand#generateSQLStatements()
	 */
	@Override
	protected String[] generateSQLStatements()
	{
		String[] result = null;

		TableColumnInfo to = customDialog.getColumnInfo();
		String dbName = customDialog.getSelectedDBName();
		_dialect = DialectFactory.getDialect(dbName);

		try
		{
			result = DBUtil.getAlterSQLForColumnChange(columnToModify, to, _dialect);

			for (int i = 0; i < result.length; i++)
			{
				result[i] = result[i] + _sqlPrefs.getSqlStatementSeparator();
			}
		} catch (HibernateException e1)
		{
			JOptionPane.showMessageDialog(customDialog,
				s_stringMgr.getString("AbstractRefactoringCommand.unsupportedTypeMsg",
					_dialect.getDisplayName(),
					customDialog.getSelectedTypeName()),
				AbstractRefactoringCommand.i18n.UNSUPPORTED_TYPE_TITLE,
				JOptionPane.ERROR_MESSAGE);
		} catch (UnsupportedOperationException e2)
		{
			_session.showErrorMessage(e2.getMessage());
		}

		return result;
	}

	@Override
	protected void executeScript(String script)
	{
		CommandExecHandler handler = new CommandExecHandler(_session);

		SQLExecuterTask executer = new SQLExecuterTask(_session, script, handler);
		executer.run(); // Execute the sql synchronously

		_session.getApplication().getThreadPool().addTask(new Runnable()
		{
			public void run()
			{
				GUIUtils.processOnSwingEventThread(new Runnable()
				{
					public void run()
					{
						customDialog.setVisible(false);
						_session.getSchemaInfo().reload(_info[0]);
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

	private void showCustomDialog()
	{
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

	private class ColumnListSelectionActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (listDialog == null)
				return;

			listDialog.setVisible(false);
			TableColumnInfo[] colInfos = listDialog.getSelectedColumnList();
			if (colInfos == null || colInfos.length != 1)
			{
				_session.showMessage(i18n.MODIFY_ONE_COL_MSG);
				return;
			}
			columnToModify = colInfos[0];

			showCustomDialog();
		}
	}
}