package org.squirrelsql.session;

import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.squirrelsql.services.*;
import org.squirrelsql.session.completion.CompletionCtrl;
import org.squirrelsql.table.SQLExecutor;
import org.squirrelsql.workaround.SplitDividerWA;

public class SqlTabCtrl
{
   private static final String PREF_SQL_SPLIT_LOC = "sql.split.loc";

   private MessageHandler _mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);
   private I18n _i18n = new I18n(getClass());

   private Pref _pref = new Pref(getClass());


   private SQLTextAreaServices _sqlTextAreaServices;
   private CompletionCtrl _completionCtrl;

   private SplitPane _sqlTabSplitPane = new SplitPane();

   private final Tab _sqlTab;
   private Session _session;
   private final TabPane _sqlOutputTabPane;

   public SqlTabCtrl(Session session)
   {
      _session = session;
      _sqlTextAreaServices = new SQLTextAreaServices();

      _completionCtrl = new CompletionCtrl(session, _sqlTextAreaServices);

      _sqlTab = new Tab(_i18n.t("session.tab.sql"));
      _sqlTab.setClosable(false);


      _sqlTabSplitPane.setOrientation(Orientation.VERTICAL);

      _sqlOutputTabPane = new TabPane();

      _sqlTabSplitPane.getItems().add(_sqlTextAreaServices.getTextArea());
      _sqlTabSplitPane.getItems().add(_sqlOutputTabPane);


      EventHandler<KeyEvent> keyEventHandler =
            new EventHandler<KeyEvent>()
            {
               public void handle(final KeyEvent keyEvent)
               {
                  // if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.ENTER) doesn't work
                  if (keyEvent.isControlDown() && ("\r".equals(keyEvent.getCharacter()) || "\n".equals(keyEvent.getCharacter())))
                  {
                     onExecuteSql(_sqlTextAreaServices, _sqlOutputTabPane);
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

      _sqlTab.setContent(_sqlTabSplitPane);
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


   private void onExecuteSql(SQLTextAreaServices sqlTextAreaServices, TabPane sqlOutputTabPane)
   {

      String sql = sqlTextAreaServices.getCurrentSql();

      StatementChannel statementChannel = new StatementChannel();

      SQLCancelTabCtrl sqlCancelTabCtrl = new SQLCancelTabCtrl(sql, statementChannel);

      Tab cancelTab = new Tab(_i18n.t("session.tab.sql.executing.tab.title"));
      cancelTab.setContent(sqlCancelTabCtrl.getNode());

      _sqlOutputTabPane.getTabs().add(cancelTab);
      _sqlOutputTabPane.getSelectionModel().select(cancelTab);


      ProgressTask<SQLResult> pt = new ProgressTask<SQLResult>()
      {
         @Override
         public SQLResult  call()
         {
            return SQLExecutor.loadDataFromSQL(_session.getDbConnectorResult(), sql, 100000, statementChannel);
         }

         @Override
         public void goOn(SQLResult  sqlResult)
         {
            onGoOn(sqlResult, sql, sqlCancelTabCtrl);
         }
      };

      ProgressUtil.start(pt);
   }

   private void onGoOn(SQLResult sqlResult, String sql, SQLCancelTabCtrl sqlCancelTabCtrl)
   {
      if (null != sqlResult.getSqlException())
      {
         String errMsg = _mh.errorSQLNoStack(sqlResult.getSqlException());

         Tab errorTab = new Tab();

         Label errorTabLabel = new Label("Error");
         errorTabLabel.setTextFill(Color.RED);
         errorTab.setGraphic(errorTabLabel);

         Label errorLabel = new Label(errMsg);

         errorLabel.setFont(Font.font("Courier", FontWeight.EXTRA_BOLD, 15));
         errorLabel.setTextFill(Color.RED);
         errorTab.setContent(errorLabel);

         _sqlOutputTabPane.getTabs().add(errorTab);

         _sqlOutputTabPane.getSelectionModel().select(errorTab);

      }
      else
      {
         String s = sql.replaceAll("\n", " ");
         Tab outputTab = new Tab(s);

         TableView tv = new TableView();

         sqlResult.getTableLoader().load(tv);

         outputTab.setContent(tv);

         _sqlOutputTabPane.getTabs().add(outputTab);

         _sqlOutputTabPane.getSelectionModel().select(outputTab);

         _mh.info(_i18n.t("session.tab.sql.executing.times", tv.getItems().size(), sqlResult.getCompleteTime(), sqlResult.getExecutionTime(), sqlResult.getBuildingOutputTime()));
      }
   }


   public void close()
   {
      _pref.set(PREF_SQL_SPLIT_LOC, _sqlTabSplitPane.getDividerPositions()[0]);
   }
}

