package org.squirrelsql.session;

import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.AppState;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;
import org.squirrelsql.session.action.ActionManager;
import org.squirrelsql.session.action.ActionScope;
import org.squirrelsql.session.objecttree.*;
import org.squirrelsql.session.sql.NewSqlTabHelper;
import org.squirrelsql.session.sql.SqlTabCtrl;
import org.squirrelsql.workaround.SessionTabSelectionRepaintWA;
import org.squirrelsql.workaround.SplitDividerWA;

import java.util.ArrayList;


public class SessionCtrl
{
   private static final String PREF_OBJECT_TREE_SPLIT_LOC = "objecttree.split.loc";

   private static final String PREF_PRE_SELECT_SQL_TAB = "preselect.sql";


   private final TabPane _objectTreeAndSqlTabPane;


   private I18n _i18n = new I18n(getClass());

   private Pref _pref = new Pref(getClass());
   private SplitPane _objectTabSplitPane = new SplitPane();
   private SqlTabCtrl _sqlTabCtrl;
   private final BorderPane _sessionPane;
   private final Tab _sessionTab;
   private SessionTabContext _sessionTabContext;

   public SessionCtrl(SessionTabContext sessionTabContext)
   {
      _sessionTabContext = sessionTabContext;

      _sessionPane = new BorderPane();

      _sessionPane.setTop(new ActionManager().createToolbar());

      _objectTreeAndSqlTabPane = createObjectTreeAndSqlTabPane();

      _sessionPane.setCenter(_objectTreeAndSqlTabPane);

      _sessionTab = new Tab();
      _sessionTab.setGraphic(getTabHeaderNode());
      _sessionTab.setContent(_sessionPane);


      _sessionTab.setOnClosed(e -> onClose());
      _sessionTab.setOnSelectionChanged(this::onSelectionChanged);

      NewSqlTabHelper.registerSessionTabListener(sessionTabContext, _sessionTab);
      AppState.get().addApplicationCloseListener(this::onClose);
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
      TabPane ret = new TabPane();

      Tab objectsTab = createObjectsTab();
      ret.getTabs().add(objectsTab);
      new ActionManager().setCurrentActionScope(ActionScope.OBJECT_TREE);


      _sqlTabCtrl = new SqlTabCtrl(_sessionTabContext);

      Tab sqlTab = _sqlTabCtrl.getSqlTab();
      ret.getTabs().add(sqlTab);

      if(_pref.getBoolean(PREF_PRE_SELECT_SQL_TAB, false))
      {
         ret.getSelectionModel().select(sqlTab);
         onSwitchedToSqlTab();
      }


      SessionTabSelectionRepaintWA.forceTabContentRepaintOnSelection(ret);

      ret.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onTabChanged(newValue));

      return ret;
   }

   private void onSwitchedToSqlTab()
   {
      _sqlTabCtrl.requestFocus();
      new ActionManager().setCurrentActionScope(ActionScope.SQL_EDITOR);
   }

   private void onTabChanged(Tab newSelectedTab)
   {
      if(_sqlTabCtrl.getSqlTab() == newSelectedTab)
      {
         onSwitchedToSqlTab();
      }
      else
      {
         new ActionManager().setCurrentActionScope(ActionScope.OBJECT_TREE);
      }
   }

   private Tab createObjectsTab()
   {
      Tab objectsTab = new Tab(_i18n.t("session.tab.objects"));
      objectsTab.setClosable(false);

      TreeView<ObjectTreeNode> objectsTree = new TreeView();

      objectsTree.setCellFactory(cf -> new ObjectsTreeCell());

      AliasCatalogsSchemasAndTypesCreator.createNodes(objectsTree, _sessionTabContext.getSession());

      TablesProceduresAndUDTsCreator.createNodes(objectsTree, _sessionTabContext.getSession());

      removeEmptySchemasIfRequested(objectsTree, _sessionTabContext.getSession());


      _objectTabSplitPane.setOrientation(Orientation.HORIZONTAL);
      _objectTabSplitPane.getItems().add(objectsTree);
      _objectTabSplitPane.getItems().add(new TreeDetailsController(objectsTree, _sessionTabContext.getSession()).getComponent());
      SplitDividerWA.adjustDivider(_objectTabSplitPane, 0, _pref.getDouble(PREF_OBJECT_TREE_SPLIT_LOC, 0.5d));


      TreeItem<ObjectTreeNode> aliasItem = ObjectTreeUtil.findSingleTreeItem(objectsTree, ObjectTreeNodeTypeKey.ALIAS_TYPE_KEY);
      aliasItem.setExpanded(true);
      objectsTree.getSelectionModel().select(aliasItem);



      objectsTab.setContent(_objectTabSplitPane);

      return objectsTab;
   }


   private void removeEmptySchemasIfRequested(TreeView<ObjectTreeNode> objectsTree, Session session)
   {
      if(false == session.getSchemaCache().getAliasPropertiesDecorator().isHideEmptySchemasInObjectTree())
      {
         return;
      }

      ArrayList<TreeItem<ObjectTreeNode>> schemas = ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.SCHEMA_TYPE_KEY);
      removeEmptyNodes(schemas);

      ArrayList<TreeItem<ObjectTreeNode>> catalogs = ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.CATALOG_TYPE_KEY);
      removeEmptyNodes(catalogs);

   }

   private void removeEmptyNodes(ArrayList<TreeItem<ObjectTreeNode>> nodes)
   {
      ArrayList<TreeItem<ObjectTreeNode>> toRemove = new ArrayList<>();

      for (TreeItem<ObjectTreeNode> schema : nodes)
      {
         if(0 == schema.getChildren().size())
         {
            toRemove.add(schema);
         }
      }

      for (TreeItem<ObjectTreeNode> del : toRemove)
      {
         del.getParent().getChildren().remove(del);
      }
   }


   public Node getTabHeaderNode()
   {
      Alias alias = _sessionTabContext.getSession().getAlias();
      return new Label(_i18n.t("session.tab.header", alias.getName(), alias.getUserName()));
   }

   private void onClose()
   {
      _pref.set(PREF_OBJECT_TREE_SPLIT_LOC, _objectTabSplitPane.getDividerPositions()[0]);
      _pref.set(PREF_PRE_SELECT_SQL_TAB, _objectTreeAndSqlTabPane.getSelectionModel().getSelectedItem() == _sqlTabCtrl.getSqlTab());

      _sqlTabCtrl.close();
      _sessionTabContext.getSession().close();

      AppState.get().getSessionManager().sessionClose(_sessionTabContext);

   }

   public Tab getSessionTab()
   {
      return _sessionTab;
   }
}
