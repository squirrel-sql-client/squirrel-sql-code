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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnDetailDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DatabaseObjectQualifier;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
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

import org.hibernate.HibernateException;

public class AddColumnCommand extends AbstractRefactoringCommand
{
	/**
	 * Logger for this class.
	 */
	private final static ILogger s_log = LoggerController.createLogger(AddColumnCommand.class);

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddColumnCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("AddColumnCommand.sqlDialogTitle");

		String COLUMN_ALREADY_EXISTS_TITLE = s_stringMgr.getString("AddColumnCommand.columnAlreadyExistsTitle");
	}

	protected ColumnDetailDialog customDialog;

	public AddColumnCommand(ISession session, IDatabaseObjectInfo[] info)
	{
		super(session, info);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.commands.AbstractRefactoringCommand#onExecute()
	 */
	@Override
	protected void onExecute()
	{
		showCustomDialog();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.commands.AbstractRefactoringCommand#generateSQLStatements()
	 */
	@Override
	protected String[] generateSQLStatements() throws Exception
	{
		TableColumnInfo info = customDialog.getColumnInfo();
		String[] result = null;
		DatabaseObjectQualifier qualifier =
			new DatabaseObjectQualifier(info.getCatalogName(), info.getSchemaName());
		
		try
		{
			result =  _dialect.getAddColumnSQL(info, qualifier, _sqlPrefs);
		} catch (HibernateException e1)
		{
			String dataType = customDialog.getSelectedTypeName();
			JOptionPane.showMessageDialog(customDialog,
				s_stringMgr.getString("AbstractRefactoringCommand.unsupportedTypeMsg",
					_dialect.getDisplayName(),
					dataType),
				AbstractRefactoringCommand.i18n.UNSUPPORTED_TYPE_TITLE,
				JOptionPane.ERROR_MESSAGE);
		} catch (UnsupportedOperationException e2)
		{
			_session.showMessage(s_stringMgr.getString("AddColumnCommand.unsupportedOperationMsg",
				_dialect.getDisplayName()));
		}
		return result;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.commands.AbstractRefactoringCommand#executeScript(java.lang.String)
	 */
	@Override
	protected void executeScript(String script)
	{
		CommandExecHandler handler = new CommandExecHandler(_session);
		SQLExecuterTask executer = new SQLExecuterTask(_session, script, handler);
		executer.run(); // Execute the sql synchronously

		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				customDialog.setVisible(false);
			}
		});
	}

	/**
	 * Returns a boolean value indicating whether or not this refactoring is supported for the specified
	 * dialect.
	 * 
	 * @param dialect
	 *           the HibernateDialect to check
	 * @return true if this refactoring is supported; false otherwise.
	 */
	@Override
	protected boolean isRefactoringSupportedForDialect(HibernateDialect dialect)
	{
		// implemented in all originally supported dialects
		return true;
	}

	private void showCustomDialog()
	{
		_session.getApplication().getThreadPool().addTask(new Runnable()
		{
			public void run()
			{
				GUIUtils.processOnSwingEventThread(new Runnable()
				{
					public void run()
					{
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

	private class AddColumnExecuteListener extends ExecuteListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String columnName = customDialog.getColumnInfo().getColumnName();
			try
			{
				if (!isColumnNameUnique(columnName))
				{
					String message =
						s_stringMgr.getString("AddColumnCommand.columnAlreadyExistsMsg",
							customDialog.getTableName(),
							columnName);
					JOptionPane.showMessageDialog(customDialog,
						message,
						i18n.COLUMN_ALREADY_EXISTS_TITLE,
						JOptionPane.ERROR_MESSAGE);
				} else
				{
					super.actionPerformed(e);
				}
			} catch (SQLException ex)
			{
				_session.showErrorMessage(ex);
				s_log.error("Unexpected exception - " + ex.getMessage(), ex);
			}
		}

		/**
		 * Tests to see if the specified column name already exists.
		 * 
		 * @param columnName
		 *           the column name to check.
		 * @return true if the column name doesn't exists; false otherwise
		 * @throws java.sql.SQLException
		 *            unexpected sql exception
		 */
		private boolean isColumnNameUnique(String columnName) throws SQLException
		{
			boolean result = true;
			ISQLDatabaseMetaData md = _session.getMetaData();
			TableColumnInfo[] columnInfos = md.getColumnInfo((ITableInfo) _info[0]);
			for (TableColumnInfo columnInfo : columnInfos)
			{
				String existingColumnName = columnInfo.getColumnName();
				if (columnName.equalsIgnoreCase(existingColumnName))
				{
					result = false;
					break;
				}
			}

			return result;
		}
	}

	private class DialectListListener implements ItemListener
	{
		public void itemStateChanged(ItemEvent e)
		{
			_dialect = DialectFactory.getDialect(customDialog.getSelectedDBName());
		}
	}
}