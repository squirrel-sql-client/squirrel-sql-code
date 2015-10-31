package org.squirrelsql.session;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;
import org.squirrelsql.services.SplitPositionSaver;
import org.squirrelsql.session.action.ActionCfg;
import org.squirrelsql.session.action.ActionScope;
import org.squirrelsql.session.action.ActionUtil;
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
   private TreeView<ObjectTreeNode> _objectsTree = new TreeView<>();

   public ObjectTreeTabCtrl(SessionTabContext sessionTabContext)
   {
      _sessionTabContext = sessionTabContext;

      Tab objectsTab = new Tab(_i18n.t("session.tab.objects"));
      objectsTab.setClosable(false);

      _objectTabSplitPane.setOrientation(Orientation.HORIZONTAL);

      loadObjectTabSplitPane();

      objectsTab.setContent(_objectTabSplitPane);

      _objectsTab = objectsTab;

      _sessionTabContext.getSession().getSchemaCacheValue().addListener(() -> reloadObjectTabSplitPane());

      _objectTabSplitPane.setOnKeyPressed(this::onHandleKeyEvent);

   }

   private void reloadObjectTabSplitPane()
   {
      _objecttreeSplitPosSaver.save(_objectTabSplitPane);

      loadObjectTabSplitPane();
   }

   private void onHandleKeyEvent(KeyEvent keyEvent)
   {
      for (ActionCfg actionCfg : ActionUtil.getAllActionCfgs())
      {
         if( actionCfg.getActionScope() == ActionScope.OBJECT_TREE || actionCfg.getActionScope() == ActionScope.UNSCOPED)
         {
            if (actionCfg.matchesKeyEvent(keyEvent))
            {
               actionCfg.fire();
               keyEvent.consume();
               return;
            }
         }
      }
   }


   private void loadObjectTabSplitPane()
   {
      TreeItem<ObjectTreeNode> formerSelectedTreeItem = null;

      if(0 < _objectTabSplitPane.getItems().size())
      {
         TreeView<ObjectTreeNode> objectsTree = (TreeView<ObjectTreeNode>) _objectTabSplitPane.getItems().get(0);

         formerSelectedTreeItem = objectsTree.getSelectionModel().getSelectedItem();

         _objectTabSplitPane.getItems().clear();
      }


      _objectsTree.setCellFactory(cf -> new ObjectsTreeCell());

      AliasCatalogsSchemasAndTypesCreator.createNodes(_objectsTree, _sessionTabContext.getSession());

      TablesProceduresAndUDTsCreator.createNodes(_objectsTree, _sessionTabContext.getSession());

      removeEmptySchemasIfRequested(_objectsTree, _sessionTabContext.getSession());

      doEmptyCheck(_objectsTree);



      _objectTabSplitPane.getItems().add(_objectsTree);
      _objectTabSplitPane.getItems().add(new TreeDetailsController(_objectsTree, _sessionTabContext.getSession()).getComponent());


      final TreeItem<ObjectTreeNode> treeItemToSelect = ObjectTreeUtil.findTreeItem(_objectsTree, formerSelectedTreeItem);

      if(null == treeItemToSelect)
      {
         TreeItem<ObjectTreeNode> aliasItem = ObjectTreeUtil.findSingleTreeItem(_objectsTree, ObjectTreeNodeTypeKey.ALIAS_TYPE_KEY);
         aliasItem.setExpanded(true);
         _objectsTree.getSelectionModel().select(aliasItem);
      }
      else
      {
         _objectsTree.getSelectionModel().select(treeItemToSelect);
         int row = _objectsTree.getRow(treeItemToSelect);
         _objectsTree.scrollTo(row);
      }

      _objecttreeSplitPosSaver.apply(_objectTabSplitPane);
   }

   private void doEmptyCheck(TreeView<ObjectTreeNode> objectsTree)
   {
      if(
            ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.SCHEMA_TYPE_KEY).isEmpty()
         && ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.CATALOG_TYPE_KEY).isEmpty()
         && ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.TABLE_TYPE_KEY).isEmpty()
         && ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.UDT_TYPE_KEY).isEmpty()
         && ObjectTreeUtil.findTreeItems(objectsTree, ObjectTreeNodeTypeKey.PROCEDURE_TYPE_KEY).isEmpty()
        )
      {
         new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL).warning(new I18n(getClass()).t("empty.object.tree.erroneous.alias.properties.warning"));
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

   public boolean selectObjectInTree(QualifiedObjectName objName)
   {
      List<TreeItem<ObjectTreeNode>> treeItems= ObjectTreeUtil.findTreeItemsByName(_objectsTree, objName);

      if(0 == treeItems.size())
      {
         return false;
      }

      treeItems.get(0).setExpanded(true);

      _objectsTree.getSelectionModel().select(treeItems.get(0));

      _objectsTree.scrollTo(_objectsTree.getSelectionModel().getSelectedIndex());

      _objectsTab.getTabPane().getSelectionModel().select(_objectsTab);

      return true;

   }
}
