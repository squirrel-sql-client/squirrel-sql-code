package org.squirrelsql.session.graph.whereconfig;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.dndpositionmarker.RelativeNodePosition;
import org.squirrelsql.services.rightmousemenuhandler.RightMouseMenuHandler;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.graph.GraphPersistenceWrapper;
import org.squirrelsql.session.graph.QueryChannel;
import org.squirrelsql.session.sql.SQLTextAreaServices;
import org.squirrelsql.sqlreformat.CodeReformatorFractory;

import java.util.ArrayList;

public class WhereConfigCtrl
{

   private final SplitPane _splitPane;
   private GraphPersistenceWrapper _graphPersistenceWrapper;

   TreeView<WhereConfigColTreeNode> _treeView = new TreeView<>();
   private TreeItem<WhereConfigColTreeNode> _root;
   private Button _btnAddAnd = new Button(new I18n(getClass()).t("whereconfig.add.and"));
   private Button _btnAddOr = new Button(new I18n(getClass()).t("whereconfig.add.or"));
   private final SQLTextAreaServices _txtWhereClause;

   public WhereConfigCtrl(GraphPersistenceWrapper graphPersistenceWrapper, QueryChannel queryChannel, Session session)
   {
      _graphPersistenceWrapper = graphPersistenceWrapper;
      _treeView.setCellFactory(cf -> new WhereConfigColCell(this::onDropped));

      _txtWhereClause = new SQLTextAreaServices(session, false);
      _txtWhereClause.setEditable(false);

      _splitPane = new SplitPane(createLeftPanel(), _txtWhereClause.getTextAreaNode());

      _btnAddAnd.setOnAction(e -> onAddFolder(WhereConfigColEnum.AND));
      _btnAddOr.setOnAction(e -> onAddFolder(WhereConfigColEnum.OR));

      queryChannel.addQueryChannelListener(this::updateWhereConfig);
      updateWhereConfig();


      RightMouseMenuHandler rightMouseMenuHandler = new RightMouseMenuHandler(_treeView);
      rightMouseMenuHandler.setRightMousePopupAllowedCallback(this::isShowPopupAllowed);
      rightMouseMenuHandler.addMenu(new I18n(getClass()).t("whereconfig.folder.remove"), this::onRemoveSelectedFolder);
   }

   private boolean isShowPopupAllowed()
   {
      TreeItem<WhereConfigColTreeNode> selectedItem = _treeView.getSelectionModel().getSelectedItem();

      if(null == selectedItem)
      {
         return false;
      }

      return selectedItem.getValue().isFolder() && !selectedItem.getValue().isRoot();
   }

   private void onRemoveSelectedFolder()
   {
      TreeItem<WhereConfigColTreeNode> selectedItem = _treeView.getSelectionModel().getSelectedItem();

      ArrayList<TreeItem<WhereConfigColTreeNode>> filters = new ArrayList<>();

      findFilters(selectedItem, filters);

      filters.forEach(f -> f.getParent().getChildren().remove(f));

      TreeItem<WhereConfigColTreeNode> parent = selectedItem.getParent();

      parent.getChildren().remove(selectedItem);
      parent.getChildren().addAll(filters);


      toPersistence();
      updateSqlTextField();
   }

   private void findFilters(TreeItem<WhereConfigColTreeNode> parent, ArrayList<TreeItem<WhereConfigColTreeNode>> filters)
   {
      for (TreeItem<WhereConfigColTreeNode> item : parent.getChildren())
      {
         if (item.getValue().isFilter())
         {
            filters.add(item);
         }
         else if (item.getValue().isFolder())
         {
            findFilters(item, filters);
         }
      }
   }

   private void onDropped(String idToMove, TreeItem<WhereConfigColTreeNode> targetTreeItem, RelativeNodePosition relativeNodePosition)
   {
      TreeItem<WhereConfigColTreeNode> draggedTreeItem = findById(_root, idToMove);

      if(null == draggedTreeItem)
      {
         return;
      }

      draggedTreeItem.getParent().getChildren().remove(draggedTreeItem);


      if(relativeNodePosition == RelativeNodePosition.ROOT)
      {
         _treeView.getRoot().getChildren().add(draggedTreeItem);
      }
      else if(relativeNodePosition == RelativeNodePosition.CHILD)
      {
         targetTreeItem.getChildren().add(0, draggedTreeItem);
         targetTreeItem.setExpanded(true);
      }
      else if(relativeNodePosition == RelativeNodePosition.UPPER_SIBLING)
      {
         TreeItem<WhereConfigColTreeNode> parent = targetTreeItem.getParent();

         if(null == parent)
         {
            parent = _root;
         }

         int ixOfSelected = parent.getChildren().indexOf(targetTreeItem);
         parent.getChildren().add(Math.max(ixOfSelected, 0), draggedTreeItem);

      }
      else if(relativeNodePosition == RelativeNodePosition.LOWER_SIBLING)
      {
         TreeItem<WhereConfigColTreeNode> parent = targetTreeItem.getParent();

         if(null == parent)
         {
            parent = _root;
         }

         int ixOfSelected = parent.getChildren().indexOf(targetTreeItem);
         parent.getChildren().add(ixOfSelected + 1, draggedTreeItem);
      }

      _treeView.getSelectionModel().select(draggedTreeItem);

      toPersistence();

   }

   private TreeItem<WhereConfigColTreeNode> findById(TreeItem<WhereConfigColTreeNode> parent, String idToFind)
   {
      for (TreeItem<WhereConfigColTreeNode> whereConfigColTreeNodeTreeItem : parent.getChildren())
      {
         if(whereConfigColTreeNodeTreeItem.getValue().getId().equals(idToFind))
         {
            return whereConfigColTreeNodeTreeItem;
         }
         else
         {
            TreeItem<WhereConfigColTreeNode> ret = findById(whereConfigColTreeNodeTreeItem, idToFind);

            if(null != ret)
            {
               return ret;
            }
         }
      }

      return null;
   }

   private void onAddFolder(WhereConfigColEnum whereConfigColEnum)
   {
      ObservableList<TreeItem<WhereConfigColTreeNode>> selectedItems = _treeView.getSelectionModel().getSelectedItems();

      WhereConfigColTreeNode newNode = new WhereConfigColTreeNode(whereConfigColEnum);

      if(0 == selectedItems.size())
      {
         _root.getChildren().add(new TreeItem<>(newNode));
      }
      else
      {
         TreeItem<WhereConfigColTreeNode> treeItem = selectedItems.get(0);

         if(treeItem.getValue().isFolder())
         {
            treeItem.getChildren().add(new TreeItem<>(newNode));
            treeItem.setExpanded(true);
         }
         else
         {
            TreeItem<WhereConfigColTreeNode> parent = treeItem.getParent();

            if(null == parent)
            {
               parent = _treeView.getRoot();
            }

            int index = parent.getChildren().indexOf(treeItem);
            parent.getChildren().add(index + 1, new TreeItem<>(newNode));
         }
      }

      toPersistence();

   }

   private void toPersistence()
   {
      WhereConfigPersister.toPersistence(_root, _graphPersistenceWrapper);

      updateSqlTextField();
   }

   private void updateSqlTextField()
   {
      String sqlWhereClause = WhereClauseCreator.generateWhereClause(_root);
      sqlWhereClause = CodeReformatorFractory.createCodeReformator().reformat(sqlWhereClause);
      _txtWhereClause.setText(sqlWhereClause);
   }

   private BorderPane createLeftPanel()
   {
      _root = new TreeItem<>(new WhereConfigColTreeNode());
      _treeView.setRoot(_root);
      _treeView.setShowRoot(true);

      BorderPane ret = new BorderPane(_treeView);

      HBox buttons = new HBox(_btnAddAnd, _btnAddOr);

      HBox.setMargin(_btnAddAnd, new Insets(1,1,1,1));
      HBox.setMargin(_btnAddOr, new Insets(1,0,1,1));


      ret.setTop(buttons);

      return ret;
   }

   private void updateWhereConfig()
   {
      WhereConfigPersister.toGui(_root, _graphPersistenceWrapper);
      updateSqlTextField();
   }

   public SplitPane getPane()
   {
      return _splitPane;
   }
}
