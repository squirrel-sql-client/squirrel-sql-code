package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.Statement;

public class CancelStatementThread extends Thread
{
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(CancelStatementThread.class);

   private static final ILogger s_log = LoggerController.createLogger(CancelStatementThread.class);


   private Statement _stmt;
   private IMessageHandler _messageHandler;
   private boolean _threadFinished;
   private boolean _joinReturned;

   public CancelStatementThread(Statement stmt, IMessageHandler messageHandler)
   {
      _stmt = stmt;
      _messageHandler = messageHandler;
   }

   public void tryCancel()
   {
      try
      {
         start();
         join(1500);

         synchronized (this)
         {
            _joinReturned = true;
            if(false == _threadFinished)
            {
               // i18n[CancelStatementThread.cancelTimedOut=Failed to cancel statement within one second. Perhaps your driver/database does not support cancelling statements. If cancelling succeeds later you'll get a further messages.]
               String msg = s_stringMgr.getString("CancelStatementThread.cancelTimedOut");
               _messageHandler.showErrorMessage(msg);
               s_log.error(msg);
            }
         }
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }


   public void run()
   {
      String msg;

      boolean cancelSucceeded = false;
      boolean closeSucceeded = false;

      try
      {
          if (_stmt != null) {
              _stmt.cancel();
          }
         cancelSucceeded = true;
      }
      catch (Throwable t)
      {
         // i18n[CancelStatementThread.cancelFailed=Failed to cancel statement. Perhaps the driver/RDDBMS does not support cancelling statements. See logs for further details ({0})]
         msg = s_stringMgr.getString("CancelStatementThread.cancelFailed", t);
         _messageHandler.showErrorMessage(msg);
         s_log.error(msg, t);
      }


      try
      {
         // give the ResultSetReader some time to realize that the user requested
         // cancel and stop fetching results.  This allows us to stop the query
         // processing gracefully.
         Utilities.sleep(500);
         if (_stmt != null) {
             _stmt.close();
         }
         closeSucceeded = true;
      }
      catch (Throwable t)
      {
         // i18n[CancelStatementThread.closeFailed=Failed to close statement. Propably the driver/RDDBMS does not support canceling statements. See logs for further details ({0})]
         msg = s_stringMgr.getString("CancelStatementThread.closeFailed", t);
         _messageHandler.showErrorMessage(msg);
         s_log.error(msg, t);
      }


      synchronized (this)
      {

         if (cancelSucceeded && closeSucceeded)
         {
            if (_joinReturned)
            {
               // i18n[CancelStatementThread.cancelSucceededLate=Canceling statement succeeded now. But took longer than one second.]
               msg = s_stringMgr.getString("CancelStatementThread.cancelSucceededLate");
               _messageHandler.showMessage(msg);
            }
            else
            {
               // i18n[CancelStatementThread.cancelSucceeded=The database has been asked to cancel the statment.]
               msg = s_stringMgr.getString("CancelStatementThread.cancelSucceeded");
               _messageHandler.showMessage(msg);
            }
         }

         _threadFinished = true;
      }

   }
}
