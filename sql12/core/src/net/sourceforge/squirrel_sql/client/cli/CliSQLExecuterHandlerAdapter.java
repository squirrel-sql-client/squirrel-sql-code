package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.client.session.ISQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetWrapper;

import java.sql.SQLWarning;
import java.util.ArrayList;

public class CliSQLExecuterHandlerAdapter implements ISQLExecuterHandler
{
   @Override
   public void sqlToBeExecuted(String sql)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void sqlExecutionCancelled()
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void sqlDataUpdated(int updateCount)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void sqlResultSetAvailable(ResultSetWrapper rst, SQLExecutionInfo info, IDataSetUpdateableTableModel model) throws DataSetException
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void sqlExecutionComplete(SQLExecutionInfo info, int processedStatementCount, int statementCount)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public String sqlExecutionException(Throwable th, String postErrorString)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void sqlExecutionWarning(SQLWarning warn)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void sqlStatementCount(int statementCount)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }

   @Override
   public void sqlCloseExecutionHandler(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement)
   {
      throw new UnsupportedOperationException("Must be implemented in derived class");
   }
}
