package org.squirrelsql.session.sql;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import org.squirrelsql.session.completion.SQLTextAreaTextComponentAdapter;
import org.squirrelsql.session.sql.bookmark.BookmarkManager;
import org.squirrelsql.session.sql.features.DuplicateLineCommand;
import org.squirrelsql.session.sql.features.EscapeDateCtrl;
import org.squirrelsql.session.sql.features.SchemaUpdater;
import org.squirrelsql.session.sql.features.SqlToTableCtrl;
import org.squirrelsql.session.sql.searchchandreplace.ReplaceCtrl;
import org.squirrelsql.session.sql.searchchandreplace.SearchCtrl;
import org.squirrelsql.table.SQLExecutor;
import org.squirrelsql.table.StatementExecution;
import org.squirrelsql.table.TableState;

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


      _completionCtrl = new CompletionCtrl(sessionTabContext.getSession(), new SQLTextAreaTextComponentAdapter(_sqlTextAreaServices));
      StdActionCfg.SQL_CODE_COMPLETION.setAction(_completionCtrl::completeCode);


      _sqlTabSplitPane.setOrientation(Orientation.VERTICAL);

      _sqlOutputTabPane = new TabPane();

      _sqlTabSplitPane.getItems().add(createTextAreaPane());
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

   private Node createTextAreaPane()
   {
      BorderPane bp = new BorderPane();

      new SearchCtrl(bp, _sqlTextAreaServices);
      new ReplaceCtrl(bp, _sqlTextAreaServices);

      bp.setCenter(_sqlTextAreaServices.getTextAreaNode());
      return bp;
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
      StdActionCfg.TOGGLE_UPPER_LOWER.setAction(() -> new ToggleUpperLowerCommand(_sqlTextAreaServices));
      StdActionCfg.TOGGLE_COMMENT_LINES.setAction(() -> new ToggleCommentLinesCommand(_sqlTextAreaServices));
      StdActionCfg.TOGGLE_QUOTE_AS_JAVA_STRING.setAction(() -> new ToggleQuote(_sqlTextAreaServices, false));
      StdActionCfg.TOGGLE_QUOTE_AS_JAVA_SB.setAction(() -> new ToggleQuote(_sqlTextAreaServices, true));
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
      _sqlTextAreaServices.requestFocus();

   }
   
   public void onReExecuteSql()
   {
	   Tab selectedTab = _sqlOutputTabPane.getSelectionModel().getSelectedItem();
	   if(null != selectedTab)
	   {
         int indexToReplace = _sqlOutputTabPane.getSelectionModel().getSelectedIndex();

         ResultTabUserData resultTabUserData = (ResultTabUserData) selectedTab.getUserData();
         String sql = resultTabUserData.getSql();

         TableState tableState = new TableState(resultTabUserData.getResultTableLoader());

		   SQLTokenizer sqlTokenizer = new SQLTokenizer(sql);
		   SchemaUpdater schemaUpdater = new SchemaUpdater(_sessionTabContext.getSession());
		   SqlExecutionFinishedListener sqlExecutionFinishedListener = new SqlExecutionFinishedListener() {
			   @Override
			   public void finished(boolean success)
			   {
				   onSqlExecutionFinished(success, sqlTokenizer, this, schemaUpdater);
			   }
		   };
		   _execSingleStatement(sqlTokenizer.getFirstSql(), sqlExecutionFinishedListener, new PredecessorTabData(indexToReplace, tableState));
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


   private void _execSingleStatement(String sql, SqlExecutionFinishedListener sqlExecutionFinishedListener, PredecessorTabData predecessorTabData)
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
            onGoOn(statementExecution, sql, sqlCancelTabCtrl, sqlExecutionFinishedListener, predecessorTabData);
         }
      };

      ProgressUtil.start(pt);
   }

   private void onGoOn(StatementExecution statExec, String sql, SQLCancelTabCtrl sqlCancelTabCtrl, SqlExecutionFinishedListener sqlExecutionFinishedListener, PredecessorTabData predecessorTabData)
   {
      removeErrorTab();

      _sqlOutputTabPane.getTabs().remove(sqlCancelTabCtrl.getTab());

      if (null != statExec.getFirstSqlException())
      {
         String errMsg = _mh.errorSQLNoStack(statExec.getFirstSqlException());

         ErrorTab errorTab = new ErrorTab();

         Label errorTabLabel = new Label(_i18n.t("sqlresult.error.tab.title"));
         errorTabLabel.setTextFill(Color.RED);
         errorTab.setGraphic(errorTabLabel);

         TextArea errorLabel = new TextArea(errMsg);

         errorLabel.setFont(Font.font("Courier", FontWeight.EXTRA_BOLD, 15));
         //errorLabel.setTextFill(Color.RED);
         errorLabel.setStyle("-fx-text-inner-color: red;");
         errorLabel.setEditable(false);
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
               resultTabController = new ResultTabController(_sessionTabContext.getSession(), sqlResult, sql, sqlCancelTabCtrl, getTableStateOrNull(predecessorTabData));
            }
            else
            {
               resultTabController = new ResultTabController(_sessionTabContext.getSession(), sqlResult, sql, null, getTableStateOrNull(predecessorTabData));
            }

            Tab outputTab = resultTabController.getTab();
            if (null != predecessorTabData)
            {
               _sqlOutputTabPane.getTabs().remove(predecessorTabData.getIndexToReplace());
               addAndSelectTabAt(outputTab, false, predecessorTabData);
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

   private TableState getTableStateOrNull(PredecessorTabData predecessorTabData)
   {
      if(null == predecessorTabData)
      {
         return null;
      }
      return predecessorTabData.getTableState();
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
      addAndSelectTabAt(outputTab, checkResultLimit, null);
   }

   private void addAndSelectTabAt(Tab outputTab, boolean checkResultLimit, PredecessorTabData predecessorTabData)
   {
      if (null == predecessorTabData)
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
         _sqlOutputTabPane.getTabs().add(predecessorTabData.getIndexToReplace(), outputTab);
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

