package net.sourceforge.squirrel_sql.plugins.postgres.commands.handler;
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

import net.sourceforge.squirrel_sql.client.gui.ProgessCallBackDialog;
import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.sql.SQLExecutionException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLWarning;

public abstract class ProgressSQLExecuterHandler implements ISQLExecuterHandler
{
	protected ISession _session;

	protected ProgessCallBackDialog _pdialog;

	protected String _commandPrefix;

	/**
	 * Logger for this class.
	 */
	private final static ILogger s_log = LoggerController.createLogger(ProgressSQLExecuterHandler.class);

	public ProgressSQLExecuterHandler(ISession session, JDialog owner, String progressDialogTitle,
		String commandPrefix)
	{
		_session = session;
		_pdialog = new ProgessCallBackDialog(owner, progressDialogTitle, 0);
		_commandPrefix = commandPrefix;
	}

	public void sqlToBeExecuted(String sql)
	{
		if (s_log.isDebugEnabled())
		{
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				if (s_log.isDebugEnabled())
				{
					s_log.debug(e);
				}
			}
		}

		_pdialog.setLoadingPrefix(_commandPrefix);
		_pdialog.currentlyLoading(getSuffix(sql));
	}

	protected abstract String getSuffix(String sql);

	public void sqlExecutionComplete(SQLExecutionInfo info, int processedStatementCount, int statementCount)
	{
	}

	public void sqlExecutionWarning(SQLWarning warn)
	{
		_session.showMessage(warn);
	}

	public void sqlStatementCount(int statementCount)
	{
		_pdialog.setTotalItems(statementCount + 1);
	}

	public void sqlCloseExecutionHandler()
	{
	}

	public void sqlExecutionCancelled()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_pdialog.dispose();
			}
		});
	}

	public void sqlDataUpdated(int updateCount)
	{
	}

	public void sqlResultSetAvailable(ResultSet rst, SQLExecutionInfo info, IDataSetUpdateableTableModel model)
	{
	}

	public void sqlExecutionException(Throwable th, String postErrorString)
	{
		String message = _session.formatException(new SQLExecutionException(th, postErrorString));
		_session.showErrorMessage(message);

		if (_session.getProperties().getWriteSQLErrorsToLog())
		{
			s_log.info(message);
		}
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_pdialog.dispose();
			}
		});
	}
}
