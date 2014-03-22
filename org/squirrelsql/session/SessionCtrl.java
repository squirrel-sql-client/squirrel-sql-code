package org.squirrelsql.session;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.AppState;
import org.squirrelsql.Props;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;
import org.squirrelsql.session.objecttree.*;
import org.squirrelsql.workaround.SessionTabSelectionRepaintWA;
import org.squirrelsql.workaround.SplitDividerWA;


public class SessionCtrl
{
   private static final String PREF_OBJECT_TREE_SPLIT_LOC = "objecttree.split.loc";

   private static final String PREF_PRE_SELECT_SQL_TAB = "preselect.sql";


   private final Session _session;
   private final TabPane _objectTreeAndSqlTabPane;


   private I18n _i18n = new I18n(getClass());

   private Pref _pref = new Pref(getClass());
   private SplitPane _objectTabSplitPane = new SplitPane();
   private SqlTabCtrl _sqlTabCtrl;
   private final BorderPane _sessionPane;

   public SessionCtrl(DbConnectorResult dbConnectorResult)
   {
      _session = new Session(dbConnectorResult);

      _sessionPane = new BorderPane();

      _sessionPane.setTop(createTopPane());

      _objectTreeAndSqlTabPane = createObjectTreeAndSqlTabPane();

      _sessionPane.setCenter(_objectTreeAndSqlTabPane);

      AppState.get().addApplicationCloseListener(this::onClose);
   }

   private ToolBar createTopPane()
   {
      Props props = new Props(this.getClass());

      ToolBar toolBar = new ToolBar();

      Button btnRun = new Button();
      btnRun.setGraphic(props.getImageView("run.png"));

      toolBar.getItems().add(btnRun);
      return toolBar;
   }

   private TabPane createObjectTreeAndSqlTabPane()
   {
      TabPane ret = new TabPane();

      ret.getTabs().add(createObjectsTab());

      _sqlTabCtrl = new SqlTabCtrl(_session);
      ret.getTabs().add(_sqlTabCtrl.getSqlTab());

      if(_pref.getBoolean(PREF_PRE_SELECT_SQL_TAB, false))
      {
         ret.getSelectionModel().select(_sqlTabCtrl.getSqlTab());
         _sqlTabCtrl.requestFocus();
      }


      SessionTabSelectionRepaintWA.forceTabContentRepaintOnSelection(ret);

      ret.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onTabChanged(newValue));

      return ret;
   }

   private void onTabChanged(Tab newSelectedTab)
   {
      if(_sqlTabCtrl.getSqlTab() == newSelectedTab)
      {
         _sqlTabCtrl.requestFocus();
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
      return _sessionPane;
   }

   public void setSessionTab(Tab sessionTab)
   {
      sessionTab.setOnClosed(e -> onClose());
   }

   private void onClose()
   {
      _pref.set(PREF_OBJECT_TREE_SPLIT_LOC, _objectTabSplitPane.getDividerPositions()[0]);
      _pref.set(PREF_PRE_SELECT_SQL_TAB, _objectTreeAndSqlTabPane.getSelectionModel().getSelectedItem() == _sqlTabCtrl.getSqlTab());

      _sqlTabCtrl.close();
      _session.close();
   }
}
