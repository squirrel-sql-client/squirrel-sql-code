package net.sourceforge.squirrel_sql.client.session;
/*
 * Copyright (C) 2003-2004 Jason Height
 * jmheight@users.sourceforge.net
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
import java.sql.ResultSet;
import java.sql.SQLWarning;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;

/**
 * This interface is the call back interface used during execution
 * of SQl via the SQLExecuter
 *
 * @author  <A HREF="mailto:jmheight@users.sourceforge.net">Jason Height</A>
 */
public interface ISQLExecuterHandler
{
	/**
	 * Called just prior to the sql being executed
	 * <p/> This event is fired once for each sql statement that is executed.
	 * It signals the start of a new sql execution.
	 * <p/>This callback is called from the thread that the SQLExecuter is running on.
	 * It may or may not be the Swing thread so card should be taken with any gui actions
	 */
	public void sqlToBeExecuted(String sql);

	/** Called if another thread cancels the sql execution
	 * <p/>This callback is called from the thread that the SQLExecuter is running on.
	 * It may or may not be the Swing thread so card should be taken with any gui actions
	 * It is called AFTER the SQLExecutor has cancelled out its execution.
	 */
	public void sqlExecutionCancelled();

	/** Called if the execution of the sql caused data to be updated.
	 * <p/>The updateCount can be 0 especially when the database structure changed
	 * or the update count is not supported by the database.
	 * <p/>This callback is called from the thread that the SQLExecuter is running on.
	 * It may or may not be the Swing thread so card should be taken with any gui actions
	 */
	public void sqlDataUpdated(int updateCount);

	/** Called when the execution of the sql caused data to be returned.
	 * <p/>This can fire multiple times if there are multiple result sets.
	 * <p/>This callback is called from the thread that the SQLExecuter is running on.
	 * It may or may not be the Swing thread so card should be taken with any gui actions 
	 */
	public void sqlResultSetAvailable(ResultSet rst, SQLExecutionInfo info,
			IDataSetUpdateableTableModel model) throws DataSetException;

	/** Called when the SQLExecutor succesfully completes execution of a sql
	 *  statement.
	 */
	public void sqlExecutionComplete(SQLExecutionInfo info, int processedStatementCount, int statementCount);

	/** Called when the SQLExecutor terminates due to an exception
	 */
	public String sqlExecutionException(Throwable th, String postErrorString);

	/** Called when a SQLWarning is received during execuion of the sql*/
	public void sqlExecutionWarning(SQLWarning warn);

   /**
    * To set the number of statements that will be executed
    */
   public void sqlStatementCount(int statementCount);

   /**
    * Tell the execution handler that we don't need it anymore
    * In SQLExecutionHandler this will close the cancel panel.
    *
    * @param sqlExecErrorMsgs null if no error occurred
    * @param lastExecutedStatement
    */
   public void sqlCloseExecutionHandler(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement);
}