package org.squirrelsql.session;

import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.SplitPositionSaver;
import org.squirrelsql.session.objecttree.*;

import java.util.ArrayList;
import java.util.List;

public class ObjectTreeTabCtrl
{
   private I18n _i18n = new I18n(getClass());

   private SplitPositionSaver _objecttreeSplitPosSaver = new SplitPositionSaver(getClass(), "objecttree.split.loc");

   private SplitPane _objectTabSplitPane = new SplitPane();

   private SessionTabContext _sessionTabContext;
   private final Tab _objectsTab;

   public ObjectTreeTabCtrl(SessionTabContext sessionTabContext)
   {
      _sessionTabContext = sessionTabContext;

      Tab objectsTab = new Tab(_i18n.t("session.tab.objects"));
      objectsTab.setClosable(false);

      _objectTabSplitPane.setOrientation(Orientation.HORIZONTAL);

      reloadObjectTabSplitPane();

      _objecttreeSplitPosSaver.apply(_objectTabSplitPane);


      objectsTab.setContent(_objectTabSplitPane);

      _objectsTab = objectsTab;

      _sessionTabContext.getSession().getSchemaCacheValue().addListener((observable, oldValue, newValue) -> reloadObjectTabSplitPane());
   }


   private void reloadObjectTabSplitPane()
   {
      TreeItem<ObjectTreeNode> formerSelectedTreeItem = null;

      if(0 < _objectTabSplitPane.getItems().size())
      {
         TreeView<ObjectTreeNode> objectsTree = (TreeView<ObjectTreeNode>) _objectTabSplitPane.getItems().get(0);

         formerSelectedTreeItem = objectsTree.getSelectionModel().getSelectedItem();

         _objectTabSplitPane.getItems().clear();
      }


      final TreeView<ObjectTreeNode> objectsTree = new TreeView<>();

      objectsTree.setCellFactory(cf -> new ObjectsTreeCell());

      AliasCatalogsSchemasAndTypesCreator.createNodes(objectsTree, _sessionTabContext.getSession());

      TablesProceduresAndUDTsCreator.createNodes(objectsTree, _sessionTabContext.getSession());

      removeEmptySchemasIfRequested(objectsTree, _sessionTabContext.getSession());




      _objectTabSplitPane.getItems().add(objectsTree);
      _objectTabSplitPane.getItems().add(new TreeDetailsController(objectsTree, _sessionTabContext.getSession()).getComponent());


      final TreeItem<ObjectTreeNode> treeItemToSelect = ObjectTreeUtil.findTreeItem(objectsTree, formerSelectedTreeItem);

      if(null == treeItemToSelect)
      {
         TreeItem<ObjectTreeNode> aliasItem = ObjectTreeUtil.findSingleTreeItem(objectsTree, ObjectTreeNodeTypeKey.ALIAS_TYPE_KEY);
         aliasItem.setExpanded(true);
         objectsTree.getSelectionModel().select(aliasItem);
      }
      else
      {
         objectsTree.getSelectionModel().select(treeItemToSelect);
         int row = objectsTree.getRow(treeItemToSelect);
         objectsTree.scrollTo(row);
      }
   }

   private void removeEmptySchemasIfRequested(TreeView<ObjectTreeNode> objectsTree, Session session)
   {
      if(false == session.getSchemaCacheValue().get().getAliasPropertiesDecorator().isHideEmptySchemasInObjectTree())
      {
         return;
      }

      List<TreeItem<ObjectTreeNode>> schemas = ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.SCHEMA_TYPE_KEY);
      removeEmptyNodes(schemas);

      List<TreeItem<ObjectTreeNode>> catalogs = ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.CATALOG_TYPE_KEY);
      removeEmptyNodes(catalogs);

   }


   private void removeEmptyNodes(List<TreeItem<ObjectTreeNode>> nodes)
   {
      List<TreeItem<ObjectTreeNode>> toRemove = new ArrayList<>();

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


   public Tab getObjectsTab()
   {
      return _objectsTab;
   }

   public void close()
   {
      _objecttreeSplitPosSaver.save(_objectTabSplitPane);
   }
}
