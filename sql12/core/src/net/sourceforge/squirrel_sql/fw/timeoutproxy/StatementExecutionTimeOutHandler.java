package net.sourceforge.squirrel_sql.fw.timeoutproxy;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class StatementExecutionTimeOutHandler
{
   private static final ILogger s_log = LoggerController.createLogger(StatementExecutionTimeOutHandler.class);

   public static PreparedStatement prepareStatement(ISQLConnection con, String sql) throws SQLException
   {
      long metaDataLoadingTimeOut = TimeOutUtil.getMetaDataLoadingTimeOutOfActiveSession();

      if(0 == metaDataLoadingTimeOut)
      {
         return con.prepareStatement(sql);
      }


      final Future<Object> future = StaticTimeOutThreadPool.submit(() -> con.prepareStatement(sql));
      try
      {
         return (PreparedStatement) future.get(metaDataLoadingTimeOut, TimeUnit.MILLISECONDS);
      }
      catch (TimeoutException e)
      {
         final String msg = "Timeout as configured in menu File --> New Session Properties --> tab SQL --> section \"Meta data loading\" occured.";
         s_log.error(msg);

         throw new RuntimeException(msg, e);
      }
      catch (ExecutionException e)
      {
         if(e.getCause() instanceof SQLException)
         {
            throw (SQLException)e.getCause();
         }
         else
         {
            throw Utilities.wrapRuntime(e);
         }
      }
      catch (InterruptedException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

}
