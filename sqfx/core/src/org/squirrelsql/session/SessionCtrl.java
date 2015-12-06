package org.squirrelsql.session;

import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import org.squirrelsql.AppState;
import org.squirrelsql.ApplicationCloseListener;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.*;
import org.squirrelsql.services.progress.ProgressTask;
import org.squirrelsql.services.progress.Progressable;
import org.squirrelsql.services.progress.SimpleProgressCtrl;
import org.squirrelsql.session.action.ActionUtil;
import org.squirrelsql.session.action.ActionScope;
import org.squirrelsql.session.action.StdActionCfg;
import org.squirrelsql.session.schemainfo.SchemaCache;
import org.squirrelsql.session.schemainfo.SchemaCacheConfig;
import org.squirrelsql.session.schemainfo.SchemaCacheFactory;
import org.squirrelsql.session.sql.SqlTabCtrl;
import org.squirrelsql.session.sql.ViewInObjectTreeCommand;
import org.squirrelsql.session.sql.bookmark.BookmarkEditCtrl;
import org.squirrelsql.workaround.SessionTabSelectionRepaintWA;


public class SessionCtrl
{
   private static final String PREF_PRE_SELECT_SQL_TAB = "preselect.sql";


   private I18n _i18n = new I18n(getClass());

   private Pref _pref = new Pref(getClass());
   private SqlTabCtrl _sqlTabCtrl;
   private final BorderPane _sessionPane;
   private final Tab _sessionTab;
   private SessionTabContext _sessionTabContext;
   private ObjectTreeTabCtrl _objectTreeTabCtrl;
   private ChangeListener<Tab> _tabChangeListener;

   private final TabPane _objectTreeAndSqlTabPane;
   private final ApplicationCloseListener _applicationCloseListener;
   private final TransactionManager _transactionManager;
   private final FileManager _fileManager;


   public SessionCtrl(SessionTabContext sessionTabContext)
   {
      _sessionTabContext = sessionTabContext;

      _sessionPane = new BorderPane();

      _sessionPane.setTop(ActionUtil.createStdActionToolbar());

      _objectTreeAndSqlTabPane = createObjectTreeAndSqlTabPane();

      _sessionPane.setCenter(_objectTreeAndSqlTabPane);

      _sessionTab = new Tab();
      SessionTabHeaderCtrl sessionTabHeaderCtrl = new SessionTabHeaderCtrl(_sessionTabContext);
      _sessionTab.setGraphic(sessionTabHeaderCtrl.getTabHeader());
      _sessionTab.setContent(_sessionPane);

      initStandardActions(sessionTabContext);

      _transactionManager = new TransactionManager(sessionTabContext.getSession());

      _fileManager = new FileManager(_sqlTabCtrl.getSQLTextAreaServices(), sessionTabHeaderCtrl);

      _applicationCloseListener = this::onClose;
      AppState.get().addApplicationCloseListener(_applicationCloseListener, ApplicationCloseListener.FireTime.WITHIN_SESSION_FIRE_TIME);

      _sessionTab.setOnCloseRequest(_fileManager::closeRequest);
      _sessionTab.setOnClosed(e -> onClose());
      _sessionTab.setOnSelectionChanged(this::onSelectionChanged);
   }

   private void initStandardActions(SessionTabContext sessionTabContext)
   {
      StdActionCfg.NEW_SQL_TAB.setAction(() -> AppState.get().getSessionManager().createSqlTab(sessionTabContext));
      StdActionCfg.EDIT_BOOKMARK.setAction(() -> new BookmarkEditCtrl(sessionTabContext));
      StdActionCfg.RELOAD_DB_META_DATA.setAction(() -> reloadSchemaCache());
      StdActionCfg.VIEW_IN_OBJECT_TREE.setAction(() -> new ViewInObjectTreeCommand(_sqlTabCtrl.getSQLTextAreaServices(), _objectTreeTabCtrl));


      DbConnectorResult dbConnectorResult = _sessionTabContext.getSession().getDbConnectorResult();
      Session session = sessionTabContext.getSession();

      StdActionCfg.RECONNECT.setAction(() -> session.getSQLConnection().reconnect(dbConnectorResult, _transactionManager.isAutoCommit()));

   }

   private void reloadSchemaCache()
   {
      SimpleProgressCtrl simpleProgressCtrl = new SimpleProgressCtrl(false, true);

      simpleProgressCtrl.start(new ProgressTask<SchemaCache>()
      {
         @Override
         public SchemaCache call()
         {
            return doReload(simpleProgressCtrl.getProgressable());
         }

         @Override
         public void goOn(SchemaCache schemaCache)
         {
            onGoOnReload(schemaCache);
         }
      });

   }

   private void onGoOnReload(SchemaCache schemaCache)
   {
      _sessionTabContext.getSession().getDbConnectorResult().setSchemaCache(schemaCache);
      _sessionTabContext.getSession().getDbConnectorResult().getSchemaCacheValue().fireChanged();
   }

   private SchemaCache doReload(Progressable progressable)
   {
      DbConnectorResult dbConnectorResult = _sessionTabContext.getSession().getDbConnectorResult();

      if(Dao.deleteSerializedSchemaCache(dbConnectorResult.getAliasDecorator().getAlias()))
      {
         new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL).info(_i18n.t("schema.cache.file.deleted", dbConnectorResult.getAliasDecorator().getName()));
      }

      SchemaCacheConfig schemaCacheConfig = new SchemaCacheConfig(dbConnectorResult.getAliasDecorator().getAliasPropertiesDecorator());

      SchemaCache schemaCache = SchemaCacheFactory.createSchemaCache(dbConnectorResult, dbConnectorResult.getSQLConnection(), schemaCacheConfig);

      schemaCache.load(progressable);

      return schemaCache;
   }

   private void onSelectionChanged(Event e)
   {
      if(_sessionTab.isSelected())
      {
         AppState.get().getSessionManager().setCurrentlyActiveOrActivatingContext(_sessionTabContext);
         onTabChanged(_objectTreeAndSqlTabPane.getSelectionModel().getSelectedItem());
      }
   }

   private TabPane createObjectTreeAndSqlTabPane()
   {
      _objectTreeTabCtrl = new ObjectTreeTabCtrl(_sessionTabContext);

      TabPane ret = new TabPane();

      ret.getTabs().add(_objectTreeTabCtrl.getObjectsTab());
      ActionUtil.setActionScope(ActionScope.OBJECT_TREE);


      _sqlTabCtrl = new SqlTabCtrl(_sessionTabContext);

      Tab sqlTab = _sqlTabCtrl.getSqlTab();
      ret.getTabs().add(sqlTab);

      if(_pref.getBoolean(PREF_PRE_SELECT_SQL_TAB, false))
      {
         ret.getSelectionModel().select(sqlTab);
         onSwitchedToSqlTab();
      }


      SessionTabSelectionRepaintWA.forceTabContentRepaintOnSelection(ret);

      _tabChangeListener = (observable, oldValue, newValue) -> onTabChanged(newValue);
      ret.getSelectionModel().selectedItemProperty().addListener(_tabChangeListener);

      return ret;
   }

   private void onSwitchedToSqlTab()
   {
      _sqlTabCtrl.requestFocus();
      ActionUtil.setActionScope(ActionScope.SQL_EDITOR);
   }

   private void onTabChanged(Tab newSelectedTab)
   {
      if(_sqlTabCtrl.getSqlTab() == newSelectedTab)
      {
         onSwitchedToSqlTab();
      }
      else
      {
         ActionUtil.setActionScope(ActionScope.OBJECT_TREE);
      }
   }


   private void onClose()
   {
      _objectTreeTabCtrl.close();


      _pref.set(PREF_PRE_SELECT_SQL_TAB, _objectTreeAndSqlTabPane.getSelectionModel().getSelectedItem() == _sqlTabCtrl.getSqlTab());

      _sqlTabCtrl.close();
      _sessionTabContext.getSession().close();
      
      _objectTreeAndSqlTabPane.getSelectionModel().selectedItemProperty().removeListener(_tabChangeListener);
      _sessionTab.setOnSelectionChanged(null);
      

      AppState.get().getSessionManager().sessionClose(_sessionTabContext);

      AppState.get().removeApplicationCloseListener(_applicationCloseListener);
      _sessionTab.setOnClosed(null);
   }

   public Tab getSessionTab()
   {
      return _sessionTab;
   }
}
