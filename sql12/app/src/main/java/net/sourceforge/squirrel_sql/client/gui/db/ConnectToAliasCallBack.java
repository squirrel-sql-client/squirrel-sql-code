package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.WrappedSQLException;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import java.sql.SQLException;

public class ConnectToAliasCallBack implements ICompletionCallback
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(ConnectToAliasCallBack.class);

   private static final ILogger s_log =
      LoggerController.createLogger(ConnectToAliasCallBack.class);


   private final IApplication _app;
   private final SQLAlias _sqlAlias;

   public ConnectToAliasCallBack(IApplication app, SQLAlias alias)
   {
      super();
      if (app == null)
      {
         throw new IllegalArgumentException("IApplication == null");
      }
      if (alias == null)
      {
         throw new IllegalArgumentException("ISQLAlias == null");
      }
      _app = app;
      _sqlAlias = alias;
   }

   /**
    * @see CompletionCallback#connected(net.sourceforge.squirrel_sql.fw.sql.SQLConnection)
    */
   public void connected(ISQLConnection conn)
   {
      // Empty.
   }

   /**
    * @see CompletionCallback#sessionCreated(net.sourceforge.squirrel_sql.client.session.ISession)
    */
   public void sessionCreated(ISession session)
   {
      // Empty.
   }

   /**
    * @see CompletionCallback#errorOccured(Throwable)
    */
   public void errorOccured(Throwable th, boolean connectingHasBeenCanceledByUser)
   {
      if (th instanceof WrappedSQLException)
      {
         th = ((WrappedSQLException)th).getSQLExeption();
      }

      if (th instanceof SQLException)
      {
         String msg = th.getMessage();
         if (msg == null || msg.length() == 0)
         {
            msg = s_stringMgr.getString("ConnectToAliasCommand.error.cantopen");
         }
         msg = _sqlAlias.getName() + ": " + msg;
         if (false == connectingHasBeenCanceledByUser)
         {
            showErrorDialog(msg, th);
         }
         else
         {
            s_log.error(msg, th);
         }
      }
      else if (th instanceof ClassNotFoundException)
      {
         String msg = s_stringMgr.getString("ConnectToAliasCommand.error.driver", _sqlAlias.getName());
         if (false == connectingHasBeenCanceledByUser)
         {
            showErrorDialog(msg, th);
         }
      }
      else if (th instanceof NoClassDefFoundError)
      {
         String msg = s_stringMgr.getString("ConnectToAliasCommand.error.driver", _sqlAlias.getName());
         s_log.error(msg, th);
         if (false == connectingHasBeenCanceledByUser)
         {
            showErrorDialog(msg, th);
         }
      }
      else
      {
         String msg = s_stringMgr.getString("ConnectToAliasCommand.error.unexpected", _sqlAlias.getName());
         s_log.debug(th.getClass().getName());
         s_log.error(msg, th);
         if (false == connectingHasBeenCanceledByUser)
         {
            showErrorDialog(msg, th);
         }
      }
   }

   protected IApplication getApplication()
   {
      return _app;
   }

   protected void showErrorDialog(final String msg, final Throwable th)
   {
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            new ErrorDialog(_app.getMainFrame(), msg, th).setVisible(true);
         }
      });
   }


   public SQLAlias getAlias()
   {
      return _sqlAlias;
   }

}
