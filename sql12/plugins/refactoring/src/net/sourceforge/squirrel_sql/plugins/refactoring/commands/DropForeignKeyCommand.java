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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.DefaultDropDialog;
import net.sourceforge.squirrel_sql.plugins.refactoring.gui.DefaultListDialog;

public class DropForeignKeyCommand extends AbstractRefactoringCommand
{
	/**
	 * Logger for this class.
	 */
	@SuppressWarnings("unused")
	private final static ILogger s_log = LoggerController.createLogger(DropForeignKeyCommand.class);

	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DropForeignKeyCommand.class);

	static interface i18n
	{
		String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("DropForeignKeyCommand.sqlDialogTitle");
	}

	protected DefaultDropDialog customDialog;

	private DefaultListDialog _listDialog;

	private ForeignKeyInfo[] _foreignKeyInfo = null;

	public DropForeignKeyCommand(ISession session, IDatabaseObjectInfo[] info)
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
			return;

		ITableInfo ti = (ITableInfo) _info[0];

		ForeignKeyInfo[] fkInfo = _session.getMetaData().getImportedKeysInfo(ti);

		// Don't show foreignKeys dialog if only one index exists to be modified
		if (fkInfo.length == 1)
		{
			_foreignKeyInfo = fkInfo;
			showCustomDialog();
		} else if (fkInfo.length == 0)
		{
			_session.showErrorMessage(s_stringMgr.getString("DropForeignKeyCommand.noKeyToDrop",
				_info[0].getSimpleName()));
		} else
		{
			_listDialog =
				new DefaultListDialog(fkInfo, ti.getSimpleName(), DefaultListDialog.DIALOG_TYPE_FOREIGN_KEY);
			_listDialog.addColumnSelectionListener(new ColumnListSelectionActionListener());
			_listDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
			_listDialog.setVisible(true);
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.refactoring.commands.AbstractRefactoringCommand#generateSQLStatements()
	 */
	@Override
	protected String[] generateSQLStatements()
	{
		ArrayList<String> result = new ArrayList<String>();

		for (ForeignKeyInfo fgInfo : _foreignKeyInfo)
		{
			StringBuilder sql = new StringBuilder();
			sql.append(_dialect.getDropForeignKeySQL(fgInfo.getForeignKeyName(), _info[0].getQualifiedName())); // only
																																					// gives
																																					// the
																																					// SQL
																																					// without
																																					// the
																																					// Cascade/Restrict
																																					// Constraint
			if (customDialog.isCascadeSelected())
			{
				sql.append(" CASCADE");
			} else
			{
				sql.append(" RESTRICT");
			}
			sql.append(_session.getQueryTokenizer().getSQLStatementSeparator());
			result.add(sql.toString());
		}

		return result.toArray(new String[] {});
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

		_session.getApplication().getThreadPool().addTask(new Runnable()
		{
			public void run()
			{
				GUIUtils.processOnSwingEventThread(new Runnable()
				{
					public void run()
					{
						customDialog.setVisible(false);
						for (IDatabaseObjectInfo dbinfo : _info)
						{
							_session.getSchemaInfo().reload(dbinfo);
						}
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
						customDialog =
							new DefaultDropDialog(_foreignKeyInfo, DefaultDropDialog.DIALOG_TYPE_FOREIGN_KEY);
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

	private class ColumnListSelectionActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			if (_listDialog == null)
				return;

			_listDialog.setVisible(false);
			_foreignKeyInfo = _listDialog.getSelectedItems().toArray(new ForeignKeyInfo[] {});

			showCustomDialog();
		}
	}
}
