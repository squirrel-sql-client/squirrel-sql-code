package org.squirrelsql.session;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import org.squirrelsql.AppState;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;
import org.squirrelsql.services.Pref;
import org.squirrelsql.session.completion.CompletionCandidate;
import org.squirrelsql.session.completion.Completor;
import org.squirrelsql.session.objecttree.*;
import org.squirrelsql.table.TableLoaderFactory;
import org.squirrelsql.workaround.SessionTabSelectionRepaintWA;
import org.squirrelsql.workaround.SplitDividerWA;


public class SessionCtrl
{
   private static final String PREF_OBJECT_TREE_SPLIT_LOC = "objecttree.split.loc";
   private static final String PREF_SQL_SPLIT_LOC = "sql.split.loc";


   private final Session _session;

   private I18n _i18n = new I18n(getClass());

   private TabPane _sessionTabPane;
   private Pref _pref = new Pref(SessionCtrl.class);
   private SplitPane _objectTabSplitPane = new SplitPane();
   private SplitPane _sqlTabSplitPane = new SplitPane();
   private MessageHandler _mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);

   public SessionCtrl(DbConnectorResult dbConnectorResult)
   {
      _session = new Session(dbConnectorResult);

      AppState.get().addApplicationCloseListener(this::onClose);

      _sessionTabPane = new TabPane();


      _sessionTabPane.getTabs().add(createObjectsTab());

      _sessionTabPane.getTabs().add(createSqlTab());

      SessionTabSelectionRepaintWA.forceTabContentRepaintOnSelection(_sessionTabPane);
   }

   private Tab createSqlTab()
   {
      Tab sqlTab = new Tab(_i18n.t("session.tab.sql"));
      sqlTab.setClosable(false);

      _sqlTabSplitPane.setOrientation(Orientation.VERTICAL);

      TabPane sqlOutputTabPane = new TabPane();

      SQLTextAreaServices sqlTextAreaServices = new SQLTextAreaServices();

      _sqlTabSplitPane.getItems().add(sqlTextAreaServices.getTextAreaComponent());
      _sqlTabSplitPane.getItems().add(sqlOutputTabPane);


      EventHandler<KeyEvent> keyEventHandler =
            new EventHandler<KeyEvent>()
            {
               public void handle(final KeyEvent keyEvent)
               {
                  // if (keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.ENTER) doesn't work
                  if (keyEvent.isControlDown() && ("\r".equals(keyEvent.getCharacter()) || "\n".equals(keyEvent.getCharacter())))
                  {
                     onExecuteSql(sqlTextAreaServices, sqlOutputTabPane);
                     keyEvent.consume();
                  }
                  else if (keyEvent.isControlDown() && " ".equals(keyEvent.getCharacter()))
                  {
                     onCompleteCode(sqlTextAreaServices);
                     keyEvent.consume();
                  }
               }
            };

      sqlTextAreaServices.setOnKeyTyped(keyEventHandler);



      sqlTab.setContent(_sqlTabSplitPane);
      SplitDividerWA.adjustDivider(_sqlTabSplitPane, 0, _pref.getDouble(PREF_SQL_SPLIT_LOC, 0.5d));


      return sqlTab;
   }

   private void onCompleteCode(SQLTextAreaServices sqlTextAreaServices)
   {
      String tokenAtCarret = sqlTextAreaServices.getTokenAtCarret();

      System.out.println("### Completing for token >" + tokenAtCarret + "<");

      Popup pp = new Popup();

      ObservableList<CompletionCandidate> completions = new Completor(_session.getSchemaCache()).getCompletions(tokenAtCarret);
      ListView<CompletionCandidate> listView = new ListView<>();
      listView.setItems(completions);

      listView.getSelectionModel().selectFirst();


      EventHandler<KeyEvent> keyEventHandler =
            new EventHandler<KeyEvent>()
            {
               public void handle(final KeyEvent keyEvent)
               {
                  // if (keyEvent.getCode() == KeyCode.ENTER) doesn't work
                  if (("\r".equals(keyEvent.getCharacter()) || "\n".equals(keyEvent.getCharacter())))
                  {
                     pp.hide();
                     CompletionCandidate selItem = listView.getSelectionModel().getSelectedItems().get(0);
                     sqlTextAreaServices.replaceTokenAtCarretBy(selItem.getReplacement());
                     keyEvent.consume();
                  }
                  else if(27 == keyEvent.getCharacter().charAt(0)) // ESCAPE Key
                  {
                     pp.hide();
                     keyEvent.consume();
                  }
               }
            };

      listView.setOnKeyTyped(keyEventHandler);

      pp.focusedProperty().addListener(new ChangeListener<Boolean>()
      {
         @Override
         public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
         {
            if(false == newValue)
            {
               pp.hide();
            }
         }
      });
 
      listView.setPrefHeight(Math.min(listView.getItems().size(), 15) * 24 + 2);

      pp.getContent().add(listView);
      Point2D cl = sqlTextAreaServices.getCarretLocationOnScreen();
      pp.show(sqlTextAreaServices.getTextAreaComponent(), cl.getX(), cl.getY() + sqlTextAreaServices.getFontHight() + 4);

   }

   private void onExecuteSql(SQLTextAreaServices sqlTextAreaServices, TabPane sqlOutputTabPane)
   {

      String sql = sqlTextAreaServices.getCurrentSql();



      SQLResult sqlResult = TableLoaderFactory.loadDataFromSQL(_session.getDbConnectorResult(), sql, 100);

      if(null != sqlResult.getSqlException())
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

      }
      else
      {
         String s =  sql.replaceAll("\n", " ");
         Tab outputTab = new Tab(s);

         TableView tv = new TableView();

         sqlResult.getTableLoader().load(tv);

         outputTab.setContent(tv);

         sqlOutputTabPane.getTabs().add(outputTab);

         sqlOutputTabPane.getSelectionModel().select(outputTab);
      }


   }


   private Tab createObjectsTab()
   {
      Tab objectsTab = new Tab(_i18n.t("session.tab.objects"));
      objectsTab.setClosable(false);

      TreeView<ObjectTreeNode> objectsTree = new TreeView();

      objectsTree.setCellFactory(cf -> new ObjectsTreeCell());

      AliasCatalogsSchemasAndTypesCreator.createNodes(objectsTree, _session);

      TablesProceduresAndUDTsCreator.createNodes(objectsTree, _session);


      _objectTabSplitPane.setOrientation(Orientation.HORIZONTAL);
      _objectTabSplitPane.getItems().add(objectsTree);
      _objectTabSplitPane.getItems().add(new TreeDetailsController(objectsTree, _session).getComponent());
      SplitDividerWA.adjustDivider(_objectTabSplitPane, 0, _pref.getDouble(PREF_OBJECT_TREE_SPLIT_LOC, 0.5d));


      TreeItem<ObjectTreeNode> aliasItem = ObjectTreeUtil.findSingleTreeItem(objectsTree, ObjectTreeNodeTypeKey.ALIAS_TYPE_KEY);
      aliasItem.setExpanded(true);
      objectsTree.getSelectionModel().select(aliasItem);



      objectsTab.setContent(_objectTabSplitPane);

      return objectsTab;
   }


   public Node getTabHeaderNode()
   {
      Alias alias = _session.getAlias();
      return new Label(_i18n.t("session.tab.header", alias.getName(), alias.getUserName()));
   }

   public Node getTabNode()
   {
      return _sessionTabPane;
   }

   public void setSessionTab(Tab sessionTab)
   {
      sessionTab.setOnClosed(e -> onClose());
   }

   private void onClose()
   {
      _pref.set(PREF_OBJECT_TREE_SPLIT_LOC, _objectTabSplitPane.getDividerPositions()[0]);
      _pref.set(PREF_SQL_SPLIT_LOC, _sqlTabSplitPane.getDividerPositions()[0]);
      _session.close();
   }
}
