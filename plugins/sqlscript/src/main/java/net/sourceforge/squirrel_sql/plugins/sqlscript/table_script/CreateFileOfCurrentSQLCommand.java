/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;

import java.awt.Frame;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.gui.IAbortEventHandler;
import net.sourceforge.squirrel_sql.client.gui.ProgressAbortDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.action.ResultSetExportCommand;
import net.sourceforge.squirrel_sql.fw.gui.action.TableExportCsvDlg;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

/**
 * Command to export the result of the current SQL into a File.
 * With this command is the user able to export the result of the current SQL into a file using the {@link TableExportCsvDlg}.
 * The command will run on a separate thread and a separate connection to the database. It is monitored with a {@link ProgressAbortDialog} and can be canceled.
 * @see ResultSetExportCommand
 * @see ProgressAbortCallback
 * @author Stefan Willinger
 */
public class CreateFileOfCurrentSQLCommand extends AbstractDataScriptCommand {
	private static final StringManager s_stringMgr = StringManagerFactory
			.getStringManager(CreateFileOfCurrentSQLCommand.class);

	/**
	 * Command for exporting the data.
	 */
	private ResultSetExportCommand resultSetExportCommand;
	
	private Statement stmt = null;

	/**
	 * Progress dialog which supports the ability to cancel the task.
	 */
	private ProgressAbortCallback progressDialog;
	
	/**
	 * The current SQL in the SQL editor pane.
	 */
	private String currentSQL = null;

	/**
	 * Ctor specifying the current session.
	 */
	public CreateFileOfCurrentSQLCommand(ISession session, SQLScriptPlugin plugin) {
		super(session, plugin);
	}

	
	/**
	 * Does the job.
	 * @see net.sourceforge.squirrel_sql.fw.util.ICommand#execute()
	 */
	@Override
	public void execute() {
		
		this.currentSQL = getSelectedSelectStatement();
		
		showProgressMonitor();
		getSession().getApplication().getThreadPool().addTask(new Runnable() {
			public void run() {
				doCreateFileOfCurrentSQL();
			}
		});
		 
	}



	/**
	 * Do the work.
	 */
	private void doCreateFileOfCurrentSQL() {
		try {
		
			ISQLConnection unmanagedConnection = null;
			try {
				unmanagedConnection = createUnmanagedConnection();
				
				// TODO maybe, we should use a SQLExecutorTask for taking advantage of some ExecutionListeners like the parameter replacement. But how to get the right Listeners?
				if(unmanagedConnection != null){
					stmt = unmanagedConnection.createStatement();
				}else{
					stmt = getSession().getSQLConnection().createStatement();
				}
				
				DialectType dialectType =
			            DialectFactory.getDialectType(getSession().getMetaData());
				resultSetExportCommand = new ResultSetExportCommand(stmt, currentSQL, dialectType, progressDialog);
				resultSetExportCommand.execute();
				
				if (isAborted()) {
					return;
				}
			} finally {
				SQLUtilities.closeStatement(stmt);
				if(unmanagedConnection != null){
					unmanagedConnection.close();
				}
			}
		} catch (Exception e) {
			getSession().showErrorMessage(e);
			e.printStackTrace();
		} finally {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					hideProgressMonitor();
				}
			});
		}
	}

	/**
	 * Create a new unmanaged connection, , which is not associated with the current session.
	 * @return a new unmanaged connection or null, if no connection can be created.
	 * @throws SQLException 
	 */
	private ISQLConnection createUnmanagedConnection() throws SQLException {
		ISQLConnection unmanagedConnection = getSession().createUnmanagedConnection();
		
		if(unmanagedConnection == null){
			int option = JOptionPane.showConfirmDialog(null, "Unable to open a new connection. The current connection will be used instead.", "Unable to open a new Connection", JOptionPane.OK_CANCEL_OPTION);
			if(option == JOptionPane.CANCEL_OPTION){
				return null;
			}
		}else{
			// we didn't want a autocommit
			unmanagedConnection.setAutoCommit(false);
		}
		return unmanagedConnection;
	}


	/**
	 * Create and show a new  progress monitor with the ability to cancel the task.
	 */
	protected void showProgressMonitor() {
		
		// i18n[CreateFileOfCurrentSQLCommand.progress.description=Exporting the SQL {0} into a file.]
		String description = s_stringMgr.getString("CreateFileOfCurrentSQLCommand.progress.description", currentSQL);
		
        // i18n[CreateFileOfCurrentSQLCommand.progress.title=Exporting to a file.]
		String title = s_stringMgr.getString("CreateFileOfCurrentSQLCommand.progress.title");
		progressDialog = new ProgressAbortDialog((Frame)null, title, description, 0, true, new IAbortEventHandler() {
			@Override
			public void cancel() {
				if(stmt != null){
					try {
						stmt.cancel();
					} catch (SQLException e1) {
						// nothing todo
					}
				}
				
				if(resultSetExportCommand != null){
					resultSetExportCommand.cancel();
				}				
			}
		});
		
	}

	/**
	 * Hide the progress monitor.
	 * The progress monitor will not be destroyed.
	 */
	protected void hideProgressMonitor() {
		if(progressDialog != null){
			progressDialog.setVisible(false);
			progressDialog.dispose();
		}
	}
	
	/**
	 * Check, if the user has canceled the task.
	 * @return true, if the user has canceled the task, otherwise false.
	 */
	protected boolean isAborted() {
		if(progressDialog != null && progressDialog.isStop()){
			return true;
		}
		return false;
		
	}
}
