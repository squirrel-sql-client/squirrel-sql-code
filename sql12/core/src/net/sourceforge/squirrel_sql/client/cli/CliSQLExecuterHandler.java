package net.sourceforge.squirrel_sql.client.cli;

import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetException;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetDataSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ResultSetWrapper;
import net.sourceforge.squirrel_sql.fw.datasetviewer.textdataset.ResultAsText;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

public class CliSQLExecuterHandler extends CliSQLExecuterHandlerAdapter
{
   private CliSession _cliSession;

   private String _outputFile;
   private PrintWriter _printWriter;
   private FileWriter _fileWriter;
   private BufferedWriter _bufferedWriter;
   private long _counter;

   public CliSQLExecuterHandler(CliSession cliSession, String outputFile)
   {
      try
      {
         _cliSession = cliSession;

         if(null != outputFile)
         {
            tryInitFile(outputFile);
         }
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void tryInitFile(String outputFile) throws IOException
   {
      _outputFile = outputFile;
      File file = new File(_outputFile);

      if(null != file.getParentFile())
      {
         file.getParentFile().mkdirs();
      }

      file.createNewFile();

      if(false == file.exists())
      {
         throw new IllegalArgumentException("Failed to create file: " + file.getPath());
      }


      _fileWriter = new FileWriter(outputFile, true);
      _bufferedWriter = new BufferedWriter(_fileWriter);
      _printWriter = new PrintWriter(_bufferedWriter);
   }

   @Override
   public String sqlExecutionException(Throwable th, String postErrorString)
   {
      String retMessage = postErrorString;

      if (null != postErrorString)
      {
         CliMessageUtil.showMessage(CliMessageType.ERROR, postErrorString);
      }

      if(null != th)
      {
         if (th instanceof SQLException)
         {
            retMessage = Utilities.getSqlExecutionErrorMessage((SQLException) th);
            CliMessageUtil.showMessage(CliMessageType.ERROR, retMessage);
         }
         else
         {
            throw CliMessageUtil.wrapRuntime(th);
         }
      }

      return retMessage;
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
   public void sqlDataUpdated(int updateCount)
   {
      System.out.println(updateCount + " rows updated");
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
      resultAsText.close();
   }

   @Override
   public void sqlExecutionComplete(SQLExecutionInfo info, int processedStatementCount, int statementCount)
   {
      //System.out.println("Execution took " + info.getTotalElapsedMillis() + " Millis");
   }

   private void onAddLine(String line)
   {
      if(null != _printWriter)
      {
         _printWriter.print(line);

         if(++_counter % 10000L == 0)
         {
            System.out.println(_counter + " lines written to " + _outputFile);
         }
      }
      else
      {
         System.out.print(line);
      }
   }

   @Override
   public void sqlCloseExecutionHandler(ArrayList<String> sqlExecErrorMsgs, String lastExecutedStatement)
   {
      closeOutputFile();

//      if (0 < sqlExecErrorMsgs.size())
//      {
//         System.out.println(sqlExecErrorMsgs.get(0));
//      }
   }

   private void closeOutputFile()
   {
      try
      {
         if(null != _printWriter)
         {
            _printWriter.flush();
            _printWriter.close();
         }
         if(null != _bufferedWriter)
         {
            _bufferedWriter.flush();
            _bufferedWriter.close();
         }
      }
      catch (IOException e)
      {
         //
      }
      finally
      {
         if(null != _fileWriter)
         {
            try
            {
               _fileWriter.flush();
            }
            catch (Exception e)
            {
               //
            }
            finally
            {
               try
               {
                  _fileWriter.close();
               }
               catch (Exception e)
               {
                  //
               }
            }
         }
      }

//      if(null != _outputFile)
//      {
//         System.out.println("Finshed writing file " + _outputFile);
//      }
   }
}


