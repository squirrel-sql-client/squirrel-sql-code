package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetWrapper;
import net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset.ResultAsText;
import net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset.ResultAsTextLineCallback;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

import java.util.ArrayList;

public class CliSQLExecuterHandler extends CliSQLExecuterHandlerAdapter
{

   private CliSession _cliSession;

   public CliSQLExecuterHandler(CliSession cliSession)
   {
      _cliSession = cliSession;
   }

   @Override
   public String sqlExecutionException(Throwable th, String postErrorString)
   {
      if (null != postErrorString)
      {
         System.out.println(postErrorString);
      }

      if(null != th)
      {
         throw CliExceptionUtil.wrapRunntime(th);
      }

      return postErrorString;
   }

   @Override
   public void sqlStatementCount(int statementCount)
   {
      //System.out.println("statementCount = " + statementCount);
   }

   @Override
   public void sqlToBeExecuted(String sql)
   {
      //System.out.println("sql = " + sql);
   }

   @Override
   public void sqlResultSetAvailable(ResultSetWrapper rst, SQLExecutionInfo info, IDataSetUpdateableTableModel model) throws DataSetException
   {

      ResultSetDataSet rsds = new ResultSetDataSet();
      rsds.setLimitDataRead(true);

      DialectType dialectType =
            DialectFactory.getDialectType(_cliSession.getMetaData());


      rsds.setSqlExecutionTabResultSet(rst, null, dialectType);


      ResultAsText resultAsText = new ResultAsText(rsds.getDataSetDefinition().getColumnDefinitions(), true, line -> onAddLine(line));

      for (Object[] row : rsds.getAllDataForReadOnly())
      {
         resultAsText.addRow(row);
      }
   }

   @Override
   public void sqlExecutionComplete(SQLExecutionInfo info, int processedStatementCount, int statementCount)
   {
      //System.out.println("Execution took " + info.getTotalElapsedMillis() + " Millis");
   }

   private void onAddLine(String line)
   {
      System.out.print(line);
   }

   @Override
   public void sqlCloseExecutionHandler(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement)
   {
      if (0 < sqlExecErrorMsgs.size())
      {
         System.out.println(sqlExecErrorMsgs.get(0));
      }
   }
}


