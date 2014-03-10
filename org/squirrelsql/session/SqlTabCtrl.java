package org.squirrelsql.session;

import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;
import org.squirrelsql.services.Pref;
import org.squirrelsql.session.completion.CompletionCtrl;
import org.squirrelsql.table.TableLoaderFactory;
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

   public SqlTabCtrl(Session session)
   {
      _session = session;
      _sqlTextAreaServices = new SQLTextAreaServices();

      _completionCtrl = new CompletionCtrl(session, _sqlTextAreaServices);

      _sqlTab = createSqlTab();

   }

   public Tab getSqlTab()
   {
      return _sqlTab;
   }

   public void requestFocus()
   {
      _sqlTextAreaServices.requestFocus();
   }


   private Tab createSqlTab()
   {
      Tab sqlTab = new Tab(_i18n.t("session.tab.sql"));
      sqlTab.setClosable(false);


      _sqlTabSplitPane.setOrientation(Orientation.VERTICAL);

      TabPane sqlOutputTabPane = new TabPane();

      _sqlTabSplitPane.getItems().add(_sqlTextAreaServices.getTextArea());
      _sqlTabSplitPane.getItems().add(sqlOutputTabPane);


      EventHandler<KeyEvent> keyEventHandler =
            new EventHandler<KeyEvent>()
            {
               public void handle(final KeyEvent keyEvent)
               {
                  // if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.ENTER) doesn't work
                  if (keyEvent.isControlDown() && ("\r".equals(keyEvent.getCharacter()) || "\n".equals(keyEvent.getCharacter())))
                  {
                     onExecuteSql(_sqlTextAreaServices, sqlOutputTabPane);
                     keyEvent.consume();
                  } else if (keyEvent.isControlDown() && " ".equals(keyEvent.getCharacter()))
                  {
                     _completionCtrl.completeCode();
                     keyEvent.consume();
                  }
               }
            };

      _sqlTextAreaServices.setOnKeyTyped(keyEventHandler);

      sqlTab.setContent(_sqlTabSplitPane);
      SplitDividerWA.adjustDivider(_sqlTabSplitPane, 0, _pref.getDouble(PREF_SQL_SPLIT_LOC, 0.5d));


      return sqlTab;
   }

   private void onExecuteSql(SQLTextAreaServices sqlTextAreaServices, TabPane sqlOutputTabPane)
   {

      String sql = sqlTextAreaServices.getCurrentSql();


      SQLResult sqlResult = TableLoaderFactory.loadDataFromSQL(_session.getDbConnectorResult(), sql, 100);

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

         sqlOutputTabPane.getTabs().add(errorTab);

         sqlOutputTabPane.getSelectionModel().select(errorTab);

      } else
      {
         String s = sql.replaceAll("\n", " ");
         Tab outputTab = new Tab(s);

         TableView tv = new TableView();

         sqlResult.getTableLoader().load(tv);

         outputTab.setContent(tv);

         sqlOutputTabPane.getTabs().add(outputTab);

         sqlOutputTabPane.getSelectionModel().select(outputTab);
      }


   }


   public void close()
   {
      _pref.set(PREF_SQL_SPLIT_LOC, _sqlTabSplitPane.getDividerPositions()[0]);
   }
}

