package org.squirrelsql.aliases.dbconnector;

import javafx.stage.Window;
import org.squirrelsql.AppState;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.AliasUtil;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.JDBCUtil;
import org.squirrelsql.services.progress.Progressable;
import org.squirrelsql.services.progress.SimpleProgressCtrl;
import org.squirrelsql.services.sqlwrap.SQLConnection;
import org.squirrelsql.services.CancelableProgressTask;
import org.squirrelsql.session.schemainfo.SchemaCache;
import org.squirrelsql.session.schemainfo.SchemaCacheConfig;
import org.squirrelsql.session.schemainfo.SchemaCacheFactory;

import java.sql.Connection;

public class DBConnector
{
   private Alias _alias;
   private Window _owner;
   private SchemaCacheConfig _schemaCacheConfig;

   public DBConnector(Alias alias, Window owner, SchemaCacheConfig schemaCacheConfig)
   {
      _alias = alias;

      _owner = owner;
      _schemaCacheConfig = schemaCacheConfig;
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

      final String user[] = new String[1];
      final String password[] = new String[1];


      if(false == _alias.isAutoLogon() || forceLogin)
      {
         LoginController loginController = new LoginController(_alias, _owner);

         if(false == loginController.isOk())
         {
            DbConnectorResult dbConnectorResult = new DbConnectorResult(_alias, null, null);
            dbConnectorResult.setLoginCanceled(true);
            dbConnectorListener.finished(dbConnectorResult);
            return;
         }

         user[0] = loginController.getUserName();
         password[0] = loginController.getPassword();
      }
      else
      {
         user[0] = AliasUtil.getUserNameRespectAliasConfig(_alias);
         password[0] = AliasUtil.getPasswordRespectAliasConfig(_alias);
      }


      String title = new I18n(getClass()).t("connectingctrl.alias.display", _alias.getName(), _alias.getUrl(), _alias.getUserName());

      SimpleProgressCtrl simpleProgressCtrl = new SimpleProgressCtrl(true, false, title);


      CancelableProgressTask<DbConnectorResult> pt = new CancelableProgressTask<DbConnectorResult>()
      {
         @Override
         public DbConnectorResult call()
         {
            return doTryConnect(user[0], password[0], simpleProgressCtrl.getProgressable());
         }

         @Override
         public void goOn(DbConnectorResult dbConnectorResult)
         {
            onGoOn(dbConnectorResult, dbConnectorListener, simpleProgressCtrl);
         }

         @Override
         public void cancel()
         {
            onCanceled(dbConnectorListener, user[0], simpleProgressCtrl);
         }
      };



      simpleProgressCtrl.start(pt);

   }

   private void onCanceled(DbConnectorListener dbConnectorListener, String user, SimpleProgressCtrl simpleProgressCtrl)
   {
      DbConnectorResult dbConnectorResult = new DbConnectorResult(_alias, user, null);
      dbConnectorResult.setCanceled(true);
      onGoOn(dbConnectorResult, dbConnectorListener, simpleProgressCtrl);
   }

   private DbConnectorResult doTryConnect(String user, String password, Progressable progressable)
   {
      DbConnectorResult dbConnectorResult = new DbConnectorResult(_alias, user, password);

      try
      {
         Connection jdbcConn = JDBCUtil.createJDBCConnection(_alias, user, password);

         SQLConnection sqlConnection = new SQLConnection(jdbcConn);
         dbConnectorResult.setSQLConnection(sqlConnection);

         SchemaCache schemaCache = SchemaCacheFactory.createSchemaCache(dbConnectorResult, sqlConnection, _schemaCacheConfig);
         schemaCache.load(progressable);
         dbConnectorResult.setSchemaCache(schemaCache);

         return dbConnectorResult;
      }
      catch (Throwable e)
      {
         dbConnectorResult.setConnectException(e);
         return dbConnectorResult;
      }
   }

   private void onGoOn(DbConnectorResult dbConnectorResult, DbConnectorListener dbConnectorListener, SimpleProgressCtrl simpleProgressCtrl)
   {
      simpleProgressCtrl.close();
      if(dbConnectorResult.isCanceled())
      {
         dbConnectorListener.finished(dbConnectorResult);
      }
      else if(false == dbConnectorResult.isConnected() || null != dbConnectorResult.getConnectException())
      {
         new ConnectFailedController(_alias, dbConnectorResult, d -> onConnectFailureDecision(d, dbConnectorResult, dbConnectorListener));
      }
      else
      {
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
