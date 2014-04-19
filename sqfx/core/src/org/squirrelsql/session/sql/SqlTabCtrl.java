package org.squirrelsql.session.sql;

import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.squirrelsql.services.*;
import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.session.action.ActionHandle;
import org.squirrelsql.session.action.ActionManager;
import org.squirrelsql.session.action.StandardActionConfiguration;
import org.squirrelsql.session.completion.CompletionCtrl;
import org.squirrelsql.table.SQLExecutor;
import org.squirrelsql.table.StatementExecution;
import org.squirrelsql.workaround.SplitDividerWA;

import java.util.ArrayList;

public class SqlTabCtrl
{
   private static final String PREF_SQL_SPLIT_LOC = "sql.split.loc";
   private final SQLEditTopPanelCtrl _sqlEditTopPanelCtrl = new SQLEditTopPanelCtrl();

   private MessageHandler _mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);
   private I18n _i18n = new I18n(getClass());

   private Pref _pref = new Pref(getClass());


   private SQLTextAreaServices _sqlTextAreaServices;
   private CompletionCtrl _completionCtrl;

   private SplitPane _sqlTabSplitPane = new SplitPane();

   private final Tab _sqlTab;
   private SessionTabContext _sessionTabContext;
   private final TabPane _sqlOutputTabPane;

   public SqlTabCtrl(SessionTabContext sessionTabContext)
   {
      _sessionTabContext = sessionTabContext;
      _sqlTextAreaServices = new SQLTextAreaServices();

      _completionCtrl = new CompletionCtrl(sessionTabContext.getSession(), _sqlTextAreaServices);

      _sqlTab = new Tab(_i18n.t("session.tab.sql"));
      _sqlTab.setClosable(false);


      _sqlTabSplitPane.setOrientation(Orientation.VERTICAL);

      _sqlOutputTabPane = new TabPane();

      _sqlTabSplitPane.getItems().add(_sqlTextAreaServices.getTextArea());
      _sqlTabSplitPane.getItems().add(_sqlOutputTabPane);


      ActionHandle actionHandle = new ActionManager().getActionHandleForActiveOrActivatingSessionContext(StandardActionConfiguration.RUN_SQL);

      actionHandle.setOnAction(() -> onExecuteSql(_sqlTextAreaServices));

      EventHandler<KeyEvent> keyEventHandler =
            new EventHandler<KeyEvent>()
            {
               public void handle(final KeyEvent keyEvent)
               {
                  // if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.ENTER) doesn't work
                  if (keyEvent.isControlDown() && ("\r".equals(keyEvent.getCharacter()) || "\n".equals(keyEvent.getCharacter())))
                  {
                     onExecuteSql(_sqlTextAreaServices);
                     keyEvent.consume();
                  }
                  else if (keyEvent.isControlDown() && " ".equals(keyEvent.getCharacter()))
                  {
                     _completionCtrl.completeCode();
                     keyEvent.consume();
                  }
               }
            };

      _sqlTextAreaServices.setOnKeyTyped(keyEventHandler);

      BorderPane bp = new BorderPane();
      bp.setTop(_sqlEditTopPanelCtrl.getView());
      bp.setCenter(_sqlTabSplitPane);

      _sqlTab.setContent(bp);
      SplitDividerWA.adjustDivider(_sqlTabSplitPane, 0, _pref.getDouble(PREF_SQL_SPLIT_LOC, 0.5d));
   }

   public Tab getSqlTab()
   {
      return _sqlTab;
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
               resultTabController = new ResultTabController(sqlResult, sql, sqlCancelTabCtrl);
            }
            else
            {
               resultTabController = new ResultTabController(sqlResult, sql, null);
            }

            Tab outputTab = resultTabController.getTab();
            addAndSelectTab(outputTab);
         }

         for (SQLResult sqlResult : statExec.getUpdateCounts())
         {
            _mh.info(_i18n.t("session.tab.sql.update.count", sqlResult.getUpdateCount()));
         }

         _mh.info(_i18n.t("session.tab.sql.executing.times", statExec.getBestQueryCount(), statExec.getCompleteTime(), statExec.getExecutionTime(), statExec.getProcessinngResultsTime()));



      }
   }

   private void removeErrorTab()
   {
      ArrayList<ErrorTab> toRemove = new ArrayList<>();
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
      _pref.set(PREF_SQL_SPLIT_LOC, _sqlTabSplitPane.getDividerPositions()[0]);
      _sqlEditTopPanelCtrl.close();
   }
}

