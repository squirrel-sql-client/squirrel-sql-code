package org.squirrelsql.session.objecttree;

import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.Pref;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.SessionTabCloseListener;
import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.session.action.StdActionCfg;
import org.squirrelsql.session.completion.CompletionCtrl;
import org.squirrelsql.session.completion.TextFieldTextComponentAdapter;
import org.squirrelsql.workaround.KeyMatchWA;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FilterResultCtrl
{

   private final FxmlHelper<FilterResultUpperView> _fxmlHelper;
   private final Pref _pref = new Pref(getClass());
   private final TreeView<ObjectTreeNode> _filterResultTree;
   private final CompletionCtrl _completionCtrl;
   private TreeView<ObjectTreeNode> _sessionsObjectTree;

   public FilterResultCtrl(Session session, TreeView<ObjectTreeNode> sessionsObjectTree, String filterText)
   {
      _sessionsObjectTree = sessionsObjectTree;
      _filterResultTree = createObjectsTree();

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

      Stage dialog = GuiUtils.createNonModalDialog(borderPane, new Pref(getClass()), 600, 400, "objecttree.FilterResult");

      applyFilterString();

      dialog.show();


      AppState.get().getSessionManager().getCurrentlyActiveOrActivatingContext().addOnSessionTabClosed(sessionTabContext -> dialog.close());

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

      List<TreeItem<ObjectTreeNode>> filterResult = ObjectTreeUtil.findObjectsMatchingName(_sessionsObjectTree, filterText, NameMatchMode.STARTS_WITH);

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
      objectsTree.setCellFactory(cf -> new ObjectsTreeCell());
      return objectsTree;
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


}
