package org.squirrelsql.session.objecttree;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.services.*;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.action.StdActionCfg;
import org.squirrelsql.session.completion.CompletionCtrl;
import org.squirrelsql.session.completion.TextFieldTextComponentAdapter;
import org.squirrelsql.session.graph.GraphTableDndChannel;
import org.squirrelsql.workaround.KeyMatchWA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectTreeFilterCtrl
{
   public static final String DRAGGING_TO_QUERY_BUILDER = "DRAGGING_TO_QUERY_BUILDER";


   private final FxmlHelper<FilterResultUpperView> _fxmlHelper;
   private final TreeView<ObjectTreeNode> _filterResultTree;
   private final CompletionCtrl _completionCtrl;
   private final I18n _i18n = new I18n(getClass());
   private TreeView<ObjectTreeNode> _sessionsObjectTree;
   private final Stage _dialog;
   private ObjectTreeFilterCtrlMode _mode;
   private GraphTableDndChannel _graphTableDndChannel;

   public ObjectTreeFilterCtrl(Session session, String filterText)
   {
      this(session, filterText, ObjectTreeFilterCtrlMode.OBJECT_TREE_SEARCH);
   }

   public ObjectTreeFilterCtrl(Session session, String filterText, GraphTableDndChannel graphTableDndChannel)
   {
      this(session, filterText, ObjectTreeFilterCtrlMode.ADD_TO_QUERY_BUILDER);
      _graphTableDndChannel = graphTableDndChannel;
   }

   private ObjectTreeFilterCtrl(Session session, String filterText, ObjectTreeFilterCtrlMode mode)
   {
      _mode = mode;
      _sessionsObjectTree = session.getSessionCtrl().getObjectTree();
      _filterResultTree = createObjectsTree();

      _filterResultTree.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<TreeItem<ObjectTreeNode>>()
      {
         @Override
         public void onChanged(Change<? extends TreeItem<ObjectTreeNode>> c)
         {
            onFilterResultTreeSelectionChanged(_mode);
         }
      });

      _fxmlHelper = new FxmlHelper<>(FilterResultUpperView.class);

      FilterResultUpperView view = _fxmlHelper.getView();

      view.txtFilter.setText(filterText);
      //_fxmlHelper.getView().txtFilter.setOnKeyTyped(e -> Platform.runLater(() -> applyFilterString()));
      view.txtFilter.setOnKeyPressed(e -> onHandleKeyEvent(e, false));
      view.txtFilter.setOnKeyTyped(e -> onHandleKeyEvent(e, true));
      _completionCtrl = new CompletionCtrl(session, new TextFieldTextComponentAdapter(view.txtFilter));
      _completionCtrl.setOnCompletionSelected(() -> Platform.runLater(() -> applyFilterString()));


      view.btnCollapse.setOnAction(e -> onCollapseTree());

      view.txtResultCount.setEditable(false);

      BorderPane borderPane = new BorderPane();

      borderPane.setTop(_fxmlHelper.getRegion());

      borderPane.setCenter(_filterResultTree);

      _dialog = GuiUtils.createNonModalDialog(borderPane, new Pref(getClass()), 600, 400, "objecttree.FilterResult");

      applyFilterString();

      String title;
      if(ObjectTreeFilterCtrlMode.OBJECT_TREE_SEARCH == _mode)
      {
         view.lblDescription.setText(_i18n.t("objecttreefind.filter.view.explain.objecttreesearch"));
         title = _i18n.t("objecttreefind.filter.window.title");
      }
      else // ObjectTreeFilterCtrlMode.ADD_TO_QUERY_BUILDER
      {
         view.lblDescription.setText(_i18n.t("objecttreefind.filter.view.explain.addToQueryBuilder"));
         title = _i18n.t("objecttreefind.filter.window.title.for.graph.add.table");

         _filterResultTree.setOnDragDetected(this::onDragToQueryBuilder);
         _filterResultTree.setOnDragOver(this::onDragOver);
      }

      _dialog.setTitle(title);
      _dialog.show();


      AppState.get().getSessionManager().getCurrentlyActiveOrActivatingContext().addOnSessionTabClosed(sessionTabContext -> _dialog.close());

   }

   private void onDragToQueryBuilder(MouseEvent e)
   {
      if (hasSelectedTables())
      {
         Dragboard dragBoard = _filterResultTree.startDragAndDrop(TransferMode.MOVE);
         ClipboardContent content = new ClipboardContent();
         content.put(DataFormat.PLAIN_TEXT, DRAGGING_TO_QUERY_BUILDER);
         dragBoard.setContent(content);
         _graphTableDndChannel.setLastDraggingObjectTreeFilter(this);
      }

      e.consume();
   }

   private void onDragOver(DragEvent dragEvent)
   {
      if (dragEvent.getDragboard().hasString())
      {
         if (hasSelectedTables())
         {
            dragEvent.acceptTransferModes(TransferMode.MOVE);
         }
      }
      dragEvent.consume();
   }

   private boolean hasSelectedTables()
   {
      return _filterResultTree.getSelectionModel().getSelectedItems().stream().filter(tn -> tn.getValue().isOfType(ObjectTreeNodeTypeKey.TABLE_TYPE_KEY)).findFirst().isPresent();
   }


   private void onFilterResultTreeSelectionChanged(ObjectTreeFilterCtrlMode mode)
   {
      if(ObjectTreeFilterCtrlMode.ADD_TO_QUERY_BUILDER == mode)
      {
         return;
      }

      List<ObjectTreeNode> selected = CollectionUtil.transform(_filterResultTree.getSelectionModel().getSelectedItems(), otn -> otn.getValue());

      List<TreeItem<ObjectTreeNode>> matches = ObjectTreeUtil.findTreeItemsByObjectTreeNodes(_sessionsObjectTree, selected);

      ObjectTreeUtil.selectItems(_sessionsObjectTree, matches);
   }

   private void onHandleKeyEvent(KeyEvent keyEvent, boolean consumeOnly)
   {
      if ( KeyMatchWA.matches(keyEvent, StdActionCfg.SQL_CODE_COMPLETION.getActionCfg().getKeyCodeCombination()) )
      {
         if (false == consumeOnly)
         {
            _completionCtrl.completeCode();
         }
         keyEvent.consume();
         return;
      }
      else
      {
         if (false == consumeOnly)
         {
            Platform.runLater(() -> applyFilterString());
         }
      }

   }

   private void applyFilterString()
   {
      String filterText = _fxmlHelper.getView().txtFilter.getText();

      List<TreeItem<ObjectTreeNode>> filterResult;

      if (ObjectTreeFilterCtrlMode.OBJECT_TREE_SEARCH == _mode)
      {
         filterResult = ObjectTreeUtil.findObjectsMatchingName(_sessionsObjectTree, filterText, NameMatchMode.STARTS_WITH);
      }
      else
      {
         filterResult = ObjectTreeUtil.findObjectsMatchingNameAndType(_sessionsObjectTree, filterText, NameMatchMode.STARTS_WITH, ObjectTreeNodeTypeKey.TABLE_TYPE_KEY);
      }

      _filterResultTree.setRoot(null);
      fillTreeFromFilterResult(filterResult, _filterResultTree);


      _fxmlHelper.getView().txtResultCount.setText("" + filterResult.size());

      if (null != _filterResultTree.getRoot())
      {
         _filterResultTree.getRoot().setExpanded(true);
         ObjectTreeUtil.setExpandedAll(_filterResultTree, true);
      }
   }

   private void onCollapseTree()
   {
      ObjectTreeUtil.setExpandedAll(_filterResultTree, false);
      _filterResultTree.getRoot().setExpanded(true);
   }

   private void fillTreeFromFilterResult(List<TreeItem<ObjectTreeNode>> filterResult, TreeView<ObjectTreeNode> objectsTree)
   {
      HashMap<ObjectTreeNode, TreeItem<ObjectTreeNode>> cache = new HashMap<>();

      for (TreeItem<ObjectTreeNode> item : filterResult)
      {
         List<TreeItem<ObjectTreeNode>> treePath =  getTreePath(item);


         for (int i = 0; i < treePath.size(); i++)
         {

            TreeItem<ObjectTreeNode> ti = cache.get(treePath.get(i).getValue());

            if(null == ti)
            {
               TreeItem<ObjectTreeNode> newTreeItem = new TreeItem<>(treePath.get(i).getValue());

               if(0 == i)
               {
                  objectsTree.setRoot(newTreeItem);
               }
               else
               {
                  TreeItem<ObjectTreeNode> parentTi = cache.get(treePath.get(i-1).getValue());
                  parentTi.getChildren().add(newTreeItem);
               }

               cache.put(newTreeItem.getValue(), newTreeItem);
            }
         }
      }
   }

   private TreeView<ObjectTreeNode> createObjectsTree()
   {
      TreeView<ObjectTreeNode> objectsTree = new TreeView<>();
      objectsTree.setCellFactory(cf -> new ObjectsTreeCell(otn -> onDoubleClick(otn)));

      objectsTree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
      return objectsTree;
   }

   private void onDoubleClick(ObjectTreeNode otn)
   {
      _dialog.close();
   }

   private List<TreeItem<ObjectTreeNode>> getTreePath(TreeItem<ObjectTreeNode> treeItem)
   {
      ArrayList<TreeItem<ObjectTreeNode>> ret = new ArrayList<>();

      while (null != treeItem)
      {
         ret.add(treeItem);
         treeItem = treeItem.getParent();
      }

      Collections.reverse(ret);

      return ret;
   }


   public List<ObjectTreeNode> getSelectedObjectTreeNodes()
   {
      List<TreeItem<ObjectTreeNode>> buf = CollectionUtil.filter(_filterResultTree.getSelectionModel().getSelectedItems(), ti -> ti.getValue().isOfType(ObjectTreeNodeTypeKey.TABLE_TYPE_KEY));

      return CollectionUtil.transform(buf, TreeItem::getValue);
   }
}
