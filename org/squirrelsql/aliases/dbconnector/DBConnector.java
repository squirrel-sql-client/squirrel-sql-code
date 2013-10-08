package org.squirrelsql.aliases.dbconnector;

import javafx.stage.Window;
import org.squirrelsql.AppState;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.services.CancelableProgressTask;

import java.sql.SQLException;

public class DBConnector
{
   private Alias _alias;
   private Window _owner;

   public DBConnector(Alias alias, Window owner)
   {
      _alias = alias;

      _owner = owner;
      if (null == _owner)
      {
         _owner = AppState.get().getPrimaryStage();
      }
   }


   public void tryConnect(DbConnectorListener dbConnectorListener)
   {
      _tryConnect(dbConnectorListener, false);
   }

   private void _tryConnect(final DbConnectorListener dbConnectorListener, boolean forceLogin)
   {
      if(false == _alias.isAutoLogon() || forceLogin)
      {
         LoginController loginController = new LoginController(_alias, _owner);

         if(false == loginController.isOk())
         {
            DbConnectorResult dbConnectorResult = new DbConnectorResult(_alias);
            dbConnectorListener.finished(dbConnectorResult);
            return;
         }
      }


      final ConnectingController connectingController = new ConnectingController(_owner, _alias);

      CancelableProgressTask<DbConnectorResult> pt = new CancelableProgressTask<DbConnectorResult>()
      {
         @Override
         public DbConnectorResult call()
         {
            return doTryConnect();
         }

         @Override
         public void goOn(DbConnectorResult dbConnectorResult)
         {
            onGoOn(dbConnectorResult, dbConnectorListener, connectingController);
         }

         @Override
         public void cancel()
         {
            onCanceled(dbConnectorListener, connectingController);
         }
      };

      connectingController.startConnecting(pt);





   }

   private void onCanceled(DbConnectorListener dbConnectorListener, ConnectingController connectingController)
   {
      DbConnectorResult dbConnectorResult = new DbConnectorResult(_alias);
      dbConnectorResult.setCanceled(true);
      onGoOn(dbConnectorResult, dbConnectorListener, connectingController);
   }

   private DbConnectorResult doTryConnect()
   {
      try
      {
         Thread.sleep(5000);
         DbConnectorResult dbConnectorResult = new DbConnectorResult(_alias);
         dbConnectorResult.setConnectException(new SQLException("Nix Connection"));
         return dbConnectorResult;
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onGoOn(DbConnectorResult dbConnectorResult, DbConnectorListener dbConnectorListener, ConnectingController connectingController)
   {
      if(dbConnectorResult.isCanceled())
      {
         connectingController.close();
         dbConnectorListener.finished(dbConnectorResult);
      }
      else if(false == dbConnectorResult.isConnected())
      {
         connectingController.displayAndDecideOnConnectFailure(dbConnectorResult, d -> onConnectFailureDecision(d, dbConnectorResult, dbConnectorListener));
      }
      else
      {
         connectingController.close();
         dbConnectorListener.finished(dbConnectorResult);
      }



   }

   private void onConnectFailureDecision(ConnectFailureDecisionListener.Decision decision, DbConnectorResult dbConnectorResult, DbConnectorListener dbConnectorListener)
   {
      if(ConnectFailureDecisionListener.Decision.RELOGIN_REQUESTED == decision)
      {
         _tryConnect(dbConnectorListener, true);
      }
      else
      {
         dbConnectorResult.setEditAliasRequested(true);
         dbConnectorListener.finished(dbConnectorResult);
      }
   }
}
