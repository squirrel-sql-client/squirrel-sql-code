package org.squirrelsql.session.graph;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.squirrelsql.services.I18n;

public class WhereConfigCtrl
{

   private final SplitPane _splitPane;
   private GraphPersistenceWrapper _graphPersistenceWrapper;

   TreeView<WhereConfigColTreeNode> _treeView = new TreeView<>();
   private TreeItem<WhereConfigColTreeNode> _root;
   private Button _btnAddAnd = new Button(new I18n(getClass()).t("whereconfig.add.and"));
   private Button _btnAddOr = new Button(new I18n(getClass()).t("whereconfig.add.or"));

   public WhereConfigCtrl(GraphPersistenceWrapper graphPersistenceWrapper, QueryChannel queryChannel)
   {
      _graphPersistenceWrapper = graphPersistenceWrapper;
      //treeView.setCellFactory(cf -> new WhereConfigCell());

      _splitPane = new SplitPane(createLeftPanel(), new TextArea());


      _btnAddAnd.setOnAction(e -> onAddFolder(WhereConfigColEnum.AND));
      _btnAddOr.setOnAction(e -> onAddFolder(WhereConfigColEnum.OR));

      queryChannel.addQueryChannelListener(this::updateWhereConfig);
      updateWhereConfig();
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
            int index = parent.getChildren().indexOf(treeItem);
            parent.getChildren().add(index + 1, new TreeItem<>(newNode));
         }
      }
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
      _root.getChildren().clear();

      for (GraphTablePersistence graphTablePersistence : _graphPersistenceWrapper.getDelegate().getGraphTablePersistences())
      {
         for (ColumnPersistence columnPersistence : graphTablePersistence.getColumnPersistences())
         {
            FilterPersistence filterPersistence = columnPersistence.getColumnConfigurationPersistence().getFilterPersistence();

            if (false == FilterPersistenceUtil.isEmpty(filterPersistence))
            {
               TreeItem<WhereConfigColTreeNode> treeItem = new TreeItem<>(new WhereConfigColTreeNode(columnPersistence));

               _root.getChildren().add(treeItem);
            }

         }
      }

      _root.setExpanded(true);
   }

   public SplitPane getPane()
   {
      return _splitPane;
   }
}
