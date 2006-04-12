package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.Statement;
import java.sql.SQLException;

public class CancelStatementThread extends Thread
{
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(CancelStatementThread.class);

   private static final ILogger s_log = LoggerController.createLogger(CancelStatementThread.class);


   private Statement _stmt;
   private IMessageHandler _messageHandler;
   private int _timeOutMillis;
   private boolean _threadFinished;
   private boolean _cancelSucceeded;

   public CancelStatementThread(Statement stmt, IMessageHandler messageHandler, int timeOutMillis)
   {
      _stmt = stmt;
      _messageHandler = messageHandler;
      _timeOutMillis = timeOutMillis;
   }

   public boolean tryCancel()
   {
      try
      {
         start();
         join(_timeOutMillis);

         if(false == _threadFinished)
         {
            // i18n[CancelStatementThread.cancelTimedOut=Failed to cancel statement. Timeout reached. See timeout settings in Global Preferences. Propably your driver/database does not support cancelling statements.]
            String msg = s_stringMgr.getString("CancelStatementThread.cancelTimedOut");
            _messageHandler.showErrorMessage(msg);
            s_log.error(msg);
            return false;
         }

         if(_cancelSucceeded)
         {
            // i18n[CancelStatementThread.cancelSucceeded=The database has been asked to cancel the statment]
            String msg = s_stringMgr.getString("CancelStatementThread.cancelSucceeded");
            _messageHandler.showMessage(msg);
            return true;
         }
         else
         {
            return false;
         }
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }


   public void run()
   {
      try
      {
         if(null != _stmt)
         {
            _stmt.cancel();
            _cancelSucceeded = true;
         }
      }
      catch (SQLException e)
      {
         // i18n[CancelStatementThread.cancelFailed=Failed to cancel statement. Propably the driver/RDDBMS does not support cancelling statements. See logs for further details ({0})]
         String msg = s_stringMgr.getString("CancelStatementThread.cancelFailed", e);
         _messageHandler.showErrorMessage(msg);
         s_log.error(msg, e);
      }
      finally
      {
         _threadFinished = true;
      }
   }
}
