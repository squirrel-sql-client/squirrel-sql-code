package org.squirrelsql.session;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.squirrelsql.AppState;
import org.squirrelsql.Props;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.aliases.dbconnector.DbConnectorResult;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;
import org.squirrelsql.session.objecttree.*;
import org.squirrelsql.workaround.SplitDividerWA;

public class SessionCtrl
{
   private static final String PREF_OBJECT_TREE_SPLIT_LOC = "objecttree.split.loc";


   private final Session _session;

   private I18n _i18n = new I18n(getClass());

   private TabPane _sessionTabPane;
   private Pref _pref = new Pref(SessionCtrl.class);
   private SplitPane _splitPane = new SplitPane();

   public SessionCtrl(DbConnectorResult dbConnectorResult)
   {
      _session = new Session(dbConnectorResult);

      AppState.get().addApplicationCloseListener(this::onClose);

      _sessionTabPane = new TabPane();

      Tab objectsTab = createObjectsTab();
      _sessionTabPane.getTabs().add(objectsTab);




      Tab sqlTab = new Tab(_i18n.t("session.tab.sql"));
      sqlTab.setClosable(false);
      _sessionTabPane.getTabs().add(sqlTab);

   }

   private Tab createObjectsTab()
   {
      Tab objectsTab = new Tab(_i18n.t("session.tab.objects"));
      objectsTab.setClosable(false);

      TreeView<ObjectTreeNode> objectsTree = new TreeView();

      objectsTree.setCellFactory(cf -> new ObjectsTreeCell());

      AliasCatalogsSchemasAndTypesCreator.createNodes(objectsTree, _session);

      TablesProceduresAndUDTsCreator.createNodes(objectsTree, _session);


      _splitPane.setOrientation(Orientation.HORIZONTAL);
      _splitPane.getItems().add(objectsTree);
      _splitPane.getItems().add(new TreeDetailsController(objectsTree, _session).getComponent());


      TreeItem<ObjectTreeNode> aliasItem = ObjectTreeUtil.findSingleTreeItem(objectsTree, ObjectTreeNodeTypeKey.ALIAS_TYPE_KEY);
      aliasItem.setExpanded(true);
      objectsTree.getSelectionModel().select(aliasItem);


      SplitDividerWA.adjustDivider(_splitPane, 0, _pref.getDouble(PREF_OBJECT_TREE_SPLIT_LOC, 0.5d));

      objectsTab.setContent(_splitPane);

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
      _pref.set(PREF_OBJECT_TREE_SPLIT_LOC, _splitPane.getDividerPositions()[0]);
      _session.close();
   }
}
