package org.squirrelsql.session.sql;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Control;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import org.squirrelsql.AppState;
import org.squirrelsql.services.*;
import org.squirrelsql.services.progress.ProgressTask;
import org.squirrelsql.services.progress.ProgressUtil;
import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.session.action.*;
import org.squirrelsql.session.completion.CompletionCtrl;
import org.squirrelsql.session.sql.bookmark.BookmarkManager;
import org.squirrelsql.session.sql.features.DuplicateLineCommand;
import org.squirrelsql.session.sql.features.EscapeDateCtrl;
import org.squirrelsql.session.sql.features.SchemaUpdater;
import org.squirrelsql.session.sql.features.SqlToTableCtrl;
import org.squirrelsql.table.SQLExecutor;
import org.squirrelsql.table.StatementExecution;

import java.util.ArrayList;
import java.util.List;

public class SqlPaneCtrl
{
   private SplitPositionSaver _sqlSplitPosSaver = new SplitPositionSaver(getClass(), "sql.split.loc");

   private final SQLEditTopPanelCtrl _sqlEditTopPanelCtrl;

   private MessageHandler _mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);
   private I18n _i18n = new I18n(getClass());

   private SQLTextAreaServices _sqlTextAreaServices;
   private CompletionCtrl _completionCtrl;

   private SplitPane _sqlTabSplitPane = new SplitPane();

   private BookmarkManager _bookmarkManager;
   private final ToolsPopupManager _toolsPopupManager;

   private SessionTabContext _sessionTabContext;
   private final TabPane _sqlOutputTabPane;
   private final BorderPane _sqlPane;


   public SqlPaneCtrl(SessionTabContext sessionTabContext)
   {
      _sessionTabContext = sessionTabContext;
      _sqlTextAreaServices = new SQLTextAreaServices(_sessionTabContext);

      _bookmarkManager = new BookmarkManager(_sqlTextAreaServices, _sessionTabContext);
      _toolsPopupManager = new ToolsPopupManager(_sqlTextAreaServices, _sessionTabContext);


      _completionCtrl = new CompletionCtrl(sessionTabContext.getSession(), _sqlTextAreaServices);

      _sqlTabSplitPane.setOrientation(Orientation.VERTICAL);

      _sqlOutputTabPane = new TabPane();

      _sqlTabSplitPane.getItems().add(_sqlTextAreaServices.getTextArea());
      _sqlTabSplitPane.getItems().add(_sqlOutputTabPane);


      initActions();

      _sqlTextAreaServices.setOnKeyPressed(e -> onHandleKeyEvent(e, false));
      _sqlTextAreaServices.setOnKeyTyped(e -> onHandleKeyEvent(e, true));
      createRightMouseMenu();


      _sqlPane = new BorderPane();

      _sqlEditTopPanelCtrl = new SQLEditTopPanelCtrl(_sqlTextAreaServices, _sessionTabContext.getSession());
      _sqlPane.setTop(_sqlEditTopPanelCtrl.getView());

      _sqlPane.setCenter(_sqlTabSplitPane);

      _sqlSplitPosSaver.apply(_sqlTabSplitPane);
   }

   private void createRightMouseMenu()
   {
      RightMouseMenuHandler textAreaContextMenu = new RightMouseMenuHandler(_sqlTextAreaServices.getTextArea());

      for (ActionCfg ac : ActionUtil.getSQLEditRightMouseActionCfgs())
      {
         textAreaContextMenu.addMenu(ac.getText(), ac.getKeyCodeCombination(), ac::fire);
      }
   }



   private void initActions()
   {
      StdActionCfg.RUN_SQL.setAction(() -> onExecuteSql(_sqlTextAreaServices));
      StdActionCfg.ESCAPE_DATE.setAction(() -> new EscapeDateCtrl(s -> _sqlTextAreaServices.insertAtCarret(s)));
      StdActionCfg.SQL_TO_TABLE.setAction(() -> new SqlToTableCtrl(_sessionTabContext.getSession(), _sqlTextAreaServices));
      StdActionCfg.DUPLICATE_LINE_OR_SELECTION.setAction(() -> new DuplicateLineCommand(_sqlTextAreaServices));
      StdActionCfg.SQL_REFORMAT.setAction(() -> new FormatSqlCommand(_sqlTextAreaServices));
      StdActionCfg.RERUN_SQL.setAction(() -> onReExecuteSql());
   }

   private void onHandleKeyEvent(KeyEvent keyEvent, boolean consumeOnly)
   {
      for (ActionCfg actionCfg : ActionUtil.getAllActionCfgs())
      {
         if( actionCfg.getActionScope() == ActionScope.SQL_EDITOR  || actionCfg.getActionScope() == ActionScope.UNSCOPED)
         {
            if (actionCfg.matchesKeyEvent(keyEvent))
            {
               if (false == consumeOnly)
               {
                  actionCfg.fire();
               }
               keyEvent.consume();
               return;
            }
         }
      }

      _bookmarkManager.execAbbreviation(keyEvent);

   }

   public BorderPane getSqlPane()
   {
      return _sqlPane;
   }

   public void requestFocus()
   {
      _sqlTextAreaServices.requestFocus();
   }


   private void onExecuteSql(SQLTextAreaServices sqlTextAreaServices)
   {

      String script = sqlTextAreaServices.getCurrentSql();

      if(0 == script.trim().length())
      {
         _mh.error(_i18n.t("session.tab.sql.no.sql"));
         return;
      }

      SQLTokenizer sqlTokenizer = new SQLTokenizer(script);

      SchemaUpdater schemaUpdater = new SchemaUpdater(_sessionTabContext.getSession());

      SqlExecutionFinishedListener sqlExecutionFinishedListener = new SqlExecutionFinishedListener()
      {
         @Override
         public void finished(boolean success)
         {
            onSqlExecutionFinished(success, sqlTokenizer, this, schemaUpdater);
         }

      };

      _execSingleStatement(sqlTokenizer.getFirstSql(), sqlExecutionFinishedListener);

   }
   
   //FIXME This is not a great way to do this, kind of a hack to set/get the SQL of the selected tab
   //TODO Move new tab to current tab spot, keep formatting of current tab in new tab
   public void onReExecuteSql()
   {
	   Tab selectedTab = _sqlOutputTabPane.getSelectionModel().getSelectedItem();
	   if(null != selectedTab)
	   {
         int indexToReplace = _sqlOutputTabPane.getSelectionModel().getSelectedIndex();

         String sql = ((ResultTabUserData)selectedTab.getUserData()).getSql();
		   SQLTokenizer sqlTokenizer = new SQLTokenizer(sql);
		   SchemaUpdater schemaUpdater = new SchemaUpdater(_sessionTabContext.getSession());
		   SqlExecutionFinishedListener sqlExecutionFinishedListener = new SqlExecutionFinishedListener() {
			   @Override
			   public void finished(boolean success)
			   {
				   onSqlExecutionFinished(success, sqlTokenizer, this, schemaUpdater);
				   _sqlOutputTabPane.getTabs().remove(selectedTab);
			   }
		   };
		   _execSingleStatement(sqlTokenizer.getFirstSql(), sqlExecutionFinishedListener, indexToReplace);
	   }
   }
   private void onSqlExecutionFinished(boolean success, SQLTokenizer sqlTokenizer, SqlExecutionFinishedListener sqlExecutionFinishedListener, SchemaUpdater schemaUpdater)
   {
      boolean hasMoreSqls = sqlTokenizer.hasMoreSqls();
      if(success)
      {
         schemaUpdater.addSql(sqlTokenizer.getCurrentSql());
      }

      if (success && hasMoreSqls)
      {
         _execSingleStatement(sqlTokenizer.nextSql(), sqlExecutionFinishedListener);
      }

      if(false == hasMoreSqls)
      {
         schemaUpdater.doUpdates(_sqlTextAreaServices);
      }
   }


   private void _execSingleStatement(final String sql, SqlExecutionFinishedListener sqlExecutionFinishedListener)
   {
      _execSingleStatement(sql, sqlExecutionFinishedListener, null);
   }


   private void _execSingleStatement(String sql, SqlExecutionFinishedListener sqlExecutionFinishedListener, Integer indexToReplace)
   {
      StatementChannel statementChannel = new StatementChannel();

      SQLCancelTabCtrl sqlCancelTabCtrl = new SQLCancelTabCtrl(sql, statementChannel);

      Tab cancelTab = sqlCancelTabCtrl.getTab();

      addAndSelectTab(cancelTab, false);


      ProgressTask<StatementExecution> pt = new ProgressTask<StatementExecution>()
      {
         @Override
         public StatementExecution  call()
         {
            return SQLExecutor.processQuery(_sessionTabContext.getSession().getDbConnectorResult(), sql, _sqlEditTopPanelCtrl.getRowLimit(), statementChannel);
         }

         @Override
         public void goOn(StatementExecution  statementExecution)
         {
            onGoOn(statementExecution, sql, sqlCancelTabCtrl, sqlExecutionFinishedListener, indexToReplace);
         }
      };

      ProgressUtil.start(pt);
   }

   private void onGoOn(StatementExecution statExec, String sql, SQLCancelTabCtrl sqlCancelTabCtrl, SqlExecutionFinishedListener sqlExecutionFinishedListener, Integer indexToReplace)
   {
      removeErrorTab();

      _sqlOutputTabPane.getTabs().remove(sqlCancelTabCtrl.getTab());

      if (null != statExec.getFirstSqlException())
      {
         String errMsg = _mh.errorSQLNoStack(statExec.getFirstSqlException());

         ErrorTab errorTab = new ErrorTab();

         Label errorTabLabel = new Label("Error");
         errorTabLabel.setTextFill(Color.RED);
         errorTab.setGraphic(errorTabLabel);

         Label errorLabel = new Label(errMsg);

         errorLabel.setFont(Font.font("Courier", FontWeight.EXTRA_BOLD, 15));
         errorLabel.setTextFill(Color.RED);
         errorTab.setContent(errorLabel);

         addAndSelectTab(errorTab, false);
         sqlExecutionFinishedListener.finished(false);

      }
      else
      {
         for (int i = 0; i < statExec.getQueryResults().size(); i++)
         {
            SQLResult sqlResult = statExec.getQueryResults().get(i);

            ResultTabController resultTabController;

            if (0 == i)
            {
               sqlCancelTabCtrl.convertToInfoTab(statExec.getCompleteTime());
               resultTabController = new ResultTabController(_sessionTabContext.getSession(), sqlResult, sql, sqlCancelTabCtrl);
            }
            else
            {
               resultTabController = new ResultTabController(_sessionTabContext.getSession(), sqlResult, sql, null);
            }

            Tab outputTab = resultTabController.getTab();
            if (null != indexToReplace)
            {
               _sqlOutputTabPane.getTabs().remove(indexToReplace);
               addAndSelectTabAt(outputTab, indexToReplace, false);
            }
            else
            {
               addAndSelectTab(outputTab, true);
            }
         }

         for (SQLResult sqlResult : statExec.getUpdateCounts())
         {
            _mh.info(_i18n.t("session.tab.sql.update.count", sqlResult.getUpdateCount()));
         }

         _mh.info(_i18n.t("session.tab.sql.executing.times", statExec.getBestQueryCount(), statExec.getCompleteTime(), statExec.getExecutionTime(), statExec.getProcessinngResultsTime()));

         _sqlEditTopPanelCtrl.addSqlToHistory(sql, _sessionTabContext.getSession().getAlias().getName());

         sqlExecutionFinishedListener.finished(true);

      }
   }

   private void removeErrorTab()
   {
      List<ErrorTab> toRemove = new ArrayList<>();
      for (Tab tab : _sqlOutputTabPane.getTabs())
      {
         if(tab instanceof ErrorTab)
         {
            toRemove.add((ErrorTab) tab);
         }
      }

      _sqlOutputTabPane.getTabs().removeAll(toRemove.toArray(new Tab[toRemove.size()]));
   }

   private void addAndSelectTab(Tab outputTab, boolean checkResultLimit)
   {
      addAndSelectTabAt(outputTab, null, checkResultLimit);
   }

   private void addAndSelectTabAt(Tab outputTab, Integer index, boolean checkResultLimit)
   {
      if (null == index)
      {
         _sqlOutputTabPane.getTabs().add(outputTab);

         int resultTabsLimit = AppState.get().getSettingsManager().getSettings().getResultTabsLimit();

         if(checkResultLimit && resultTabsLimit < _sqlOutputTabPane.getTabs().size())
         {
            _sqlOutputTabPane.getTabs().remove(0);
         }
      }
      else
      {
         _sqlOutputTabPane.getTabs().add(index, outputTab);
      }

      RightMouseMenuHandler resultTabRightMouseMenu = new RightMouseMenuHandler((Control) outputTab.getGraphic());
      resultTabRightMouseMenu.addMenu(new I18n(getClass()).t("session.tab.menu.closeall"), () -> closeTabs(outputTab, false));
      resultTabRightMouseMenu.addMenu(new I18n(getClass()).t("session.tab.menu.closeallbutthis"), () -> closeTabs(outputTab, true));
      _sqlOutputTabPane.getSelectionModel().select(outputTab);
   }

   public void closeTabs(Tab tab, Boolean keepTab){
	   if(keepTab)
	   {
		   _sqlOutputTabPane.getTabs().removeIf(p -> !p.equals(tab));
	   }
	   else
	   {
		   _sqlOutputTabPane.getTabs().clear();		   
	   }
   }   

   public void close()
   {
      _sqlTextAreaServices.close();
      _sqlSplitPosSaver.save(_sqlTabSplitPane);
      _sqlEditTopPanelCtrl.close();
   }

   public SQLTextAreaServices getSQLTextAreaServices()
   {
      return _sqlTextAreaServices;
   }
}

