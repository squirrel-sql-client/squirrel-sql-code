package org.squirrelsql.session.sql;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import org.squirrelsql.services.*;
import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.session.action.*;
import org.squirrelsql.session.completion.CompletionCtrl;
import org.squirrelsql.session.sql.bookmark.BookmarkManager;
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

   private SessionTabContext _sessionTabContext;
   private final TabPane _sqlOutputTabPane;
   private final BorderPane _sqlPane;


   public SqlPaneCtrl(SessionTabContext sessionTabContext)
   {
      _sessionTabContext = sessionTabContext;
      _sqlTextAreaServices = new SQLTextAreaServices(_sessionTabContext);

      _bookmarkManager = new BookmarkManager(_sqlTextAreaServices, _sessionTabContext);


      _completionCtrl = new CompletionCtrl(sessionTabContext.getSession(), _sqlTextAreaServices);

      _sqlTabSplitPane.setOrientation(Orientation.VERTICAL);

      _sqlOutputTabPane = new TabPane();

      _sqlTabSplitPane.getItems().add(_sqlTextAreaServices.getTextArea());
      _sqlTabSplitPane.getItems().add(_sqlOutputTabPane);


      initStandardActions();

      _sqlTextAreaServices.setOnKeyPressed(this::onHandleKeyEvent);
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
         textAreaContextMenu.addMenu(ac.getText(), ac::fire);
      }
   }



   private void initStandardActions()
   {
      StdActionCfg.RUN_SQL.setAction(() -> onExecuteSql(_sqlTextAreaServices));
      StdActionCfg.SQL_CODE_COMPLETION.setAction(_completionCtrl::completeCode);
      StdActionCfg.EXEC_BOOKMARK.setAction(_bookmarkManager::showBookmarkPopup);
      StdActionCfg.ESCAPE_DATE.setAction(() -> new EscapeDateCtrl(s -> _sqlTextAreaServices.insertAtCarret(s)));
   }

   private void onHandleKeyEvent(KeyEvent keyEvent)
   {
      for (ActionCfg actionCfg : ActionUtil.getAllActionCfgs())
      {
         if( actionCfg.getActionScope() == ActionScope.SQL_EDITOR  || actionCfg.getActionScope() == ActionScope.UNSCOPED)
         {
            if (actionCfg.matchesKeyEvent(keyEvent))
            {
               actionCfg.fire();
               return;
            }
         }
      }

      _bookmarkManager.execAbreviation();

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

      String sql = sqlTextAreaServices.getCurrentSql();

      if(0 == sql.trim().length())
      {
         _mh.error(_i18n.t("session.tab.sql.no.sql"));
         return;
      }

      StatementChannel statementChannel = new StatementChannel();

      SQLCancelTabCtrl sqlCancelTabCtrl = new SQLCancelTabCtrl(sql, statementChannel);

      Tab cancelTab = sqlCancelTabCtrl.getTab();

      addAndSelectTab(cancelTab);


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
            onGoOn(statementExecution, sql, sqlCancelTabCtrl);
         }
      };

      ProgressUtil.start(pt);
   }

   private void onGoOn(StatementExecution statExec, String sql, SQLCancelTabCtrl sqlCancelTabCtrl)
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

         addAndSelectTab(errorTab);

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
            addAndSelectTab(outputTab);
         }

         for (SQLResult sqlResult : statExec.getUpdateCounts())
         {
            _mh.info(_i18n.t("session.tab.sql.update.count", sqlResult.getUpdateCount()));
         }

         _mh.info(_i18n.t("session.tab.sql.executing.times", statExec.getBestQueryCount(), statExec.getCompleteTime(), statExec.getExecutionTime(), statExec.getProcessinngResultsTime()));

         _sqlEditTopPanelCtrl.addSqlToHistory(sql);
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

   private void addAndSelectTab(Tab outputTab)
   {
      _sqlOutputTabPane.getTabs().add(outputTab);
      _sqlOutputTabPane.getSelectionModel().select(outputTab);
   }


   public void close()
   {
      _sqlTextAreaServices.close();
      _sqlSplitPosSaver.save(_sqlTabSplitPane);
      _sqlEditTopPanelCtrl.close();
   }
}

