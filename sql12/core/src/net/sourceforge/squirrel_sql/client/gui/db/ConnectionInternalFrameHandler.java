package net.sourceforge.squirrel_sql.client.gui.db;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.gui.db.encryption.AliasPasswordHandler;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToAliasCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.openconnection.OpenConnectionCommand;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverPropertyCollection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;

/**
 * Handler used for connection internal frame actions.
 */
class ConnectionInternalFrameHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ConnectionInternalFrameHandler.class);

   private static final ILogger s_log = LoggerController.createLogger(ConnectToAliasCommand.class);


   /**
    * The connection internal frame.
    */
   private ConnectionInternalFrame _connectionInternalFrame;

   /**
    * Application API.
    */
   private IApplication _app;

   /**
    * <TT>SQLAlias</TT> to connect to.
    */
   private SQLAlias _alias;

   /**
    * If <TT>true</TT> a session is to be created as well as connecting to database.
    */
   private boolean _createSession;

   /**
    * User name to use to connect to alias.
    */
   private String _user;

   /**
    * Password to use to connect to alias.
    */
   private String _password;

   /**
    * Connection properties.
    */
   private SQLDriverPropertyCollection _props;

   /**
    * If <TT>true</TT> user has requested cancellation of the connection attempt.
    */
   private volatile boolean _connectWasCanceled;

   /**
    * Callback to notify client on the progress of this command.
    */
   private ConnectCompletionCallback _callback;

   /**
    * Ctor.
    *
    * @param   alias         Database alias to connect to.
    * @param   createSession   If <TT>true</TT> a session should be created.
    * @param   cmd            Command executing this handler.
    * @throws IllegalArgumentException Thrown if <TT>null</TT>IApplication</TT>, <TT>SQLAlias</TT>,
    * or <TT>ICompletionCallback</TT> passed.
    */
   ConnectionInternalFrameHandler(SQLAlias alias, boolean createSession, ConnectCompletionCallback callback)
   {
      if(alias == null)
      {
         throw new IllegalArgumentException("SQLAlias == null");
      }
      if(callback == null)
      {
         throw new IllegalArgumentException("ICompletionCallback == null");
      }
      _app = Main.getApplication();
      _alias = alias;
      _createSession = createSession;
      _callback = callback;
   }

   /**
    * User has clicked the OK button to connect to the alias. Run the connection
    * attempt in a separate thread.
    *
    * @param   connSheet   Connection internal frame.
    * @param   user      The user name entered.
    * @param   password   The password entered.
    * @param   props      Connection properties.
    */
   public void performConnect(ConnectionInternalFrame connSheet, String user, String password, SQLDriverPropertyCollection props)
   {
      _connectWasCanceled = false;
      _connectionInternalFrame = connSheet;
      _user = user;
      _password = password;
      _props = props;
      doConnect();
   }

   /**
    * User has clicked the Cancel button to cancel this connection attempt.
    *
    * @param   connSheet   Connection internal frame.
    */
   public void performCancelConnect(ConnectionInternalFrame connSheet)
   {
      _connectWasCanceled = true;
   }

   /**
    * Execute task. Connect to the alias with the information entered
    * in the connection internal frame.
    */
   public void doConnect()
   {
      final IIdentifier driverID = _alias.getDriverIdentifier();
      final ISQLDriver sqlDriver = _app.getAliasesAndDriversManager().getDriver(driverID);

      try
      {
         final OpenConnectionCommand cmd = new OpenConnectionCommand(_alias, _user, _password, _props);

         cmd.executeConnectAndWaitForResultInBackground(t -> afterExecuteFinished(sqlDriver, cmd, t));

      }
      catch(Throwable ex)
      {
         _connectionInternalFrame.finishedCreatingConnection(false);
         _callback.errorOccurred(ex, _connectWasCanceled);
      }
   }

   private void afterExecuteFinished(ISQLDriver sqlDriver, OpenConnectionCommand cmd, Throwable t)
   {
      try
      {
         if(null != t)
         {
            if(_connectWasCanceled)
            {
               String aliasName = "<failedToReadAliasName>";
               try
               {
                  aliasName = _alias.getName();
               }
               catch(Throwable e)
               {
                  // Ignore, name was just needed for logging
               }

               s_log.warn("Connecting to Alias \"" + aliasName + "\" failed after it was already canceled", t);
               return;
            }
            else
            {
               throw t;
            }
         }


         if(_alias.isAutoLogon())
         {
            // If the user checked Auto Logon but gave wrong username/password
            // in the Alias definition. He will be asked to enter username/password again in an extra dialog.
            // Here for convenience we transfer these data back into the Alias.
            // Note: Don't do this when Auto Logon is false.
            _alias.setUserName(_user);
            AliasPasswordHandler.setPassword(_alias, _password);
         }


         SQLConnection conn = cmd.getSQLConnection();
         if(_connectWasCanceled)
         {
            closeCanceledConnection(conn);
         }
         else
         {
            // After this it can't be stopped anymore!
            _callback.connected(conn);
            if(_createSession)
            {
               createSession(sqlDriver, conn);
            }
            else
            {
               _connectionInternalFrame.finishedCreatingConnection(true);
            }
         }
      }
      catch(Throwable th)
      {
         _connectionInternalFrame.finishedCreatingConnection(false);
         _callback.errorOccurred(th, _connectWasCanceled);
      }
   }

   private void closeCanceledConnection(ISQLConnection conn)
   {
      if(conn != null)
      {
         try
         {
            conn.close();
         }
         catch(Exception ex)
         {
            s_log.warn("Error occurred closing already canceled connection", ex);
         }
      }
   }

   private ISession createSession(ISQLDriver sqlDriver, SQLConnection conn)
   {
      SessionManager sm = _app.getSessionManager();
      final ISession session = sm.createSession(_app, sqlDriver, _alias, conn, _user, _password);
      _callback.sessionCreated(session);
      SwingUtilities.invokeLater(() -> createSessionFrame(session));
      return session;
   }

   private void createSessionFrame(ISession session)
   {
      try
      {
         Main.getApplication().getPluginManager().sessionCreated(session);
         SessionInternalFrame sessionInternalFrame = Main.getApplication().getWindowManager().createInternalFrame(session);

         // Close the connecting dialog. Before bug #1529 was called after calling the callback
         _connectionInternalFrame.finishedCreatingConnection(true);

         _callback.sessionInternalFrameCreated(sessionInternalFrame);

      }
      catch(Throwable th)
      {
         Main.getApplication().showErrorDialog(s_stringMgr.getString("ConnectionInternalFrameHandler.error.opensession"), th);
      }
   }
}
