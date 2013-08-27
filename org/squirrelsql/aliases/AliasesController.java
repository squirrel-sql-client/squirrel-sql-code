package org.squirrelsql.aliases;

import com.google.common.base.Strings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.DockPaneChanel;
import org.squirrelsql.Props;
import org.squirrelsql.services.*;

import java.util.ArrayList;

public class AliasesController
{
   private static final String PREF_ALIASES_PINED = "aliases.pinned";
   private final AliasTreeNodeChannel _aliasTreeNodeChannel = new AliasTreeNodeChannel();

   private AliasCutCopyState _aliasCutCopyState = new AliasCutCopyState(_aliasTreeNodeChannel);
   private Props _props = new Props(this.getClass());
   private Pref _prefs = new Pref(this.getClass());
   private I18n _i18n = new I18n(this.getClass());

   private final TreeView<AliasTreeNode> _treeView = new TreeView<>();
   private DockPaneChanel _dockPaneChanel;
   private final BorderPane _borderPane = new BorderPane();
   private ToggleButton _btnPinned;


   public AliasesController(DockPaneChanel dockPaneChanel)
   {
      _dockPaneChanel = dockPaneChanel;

      _borderPane.setTop(createToolBar());
      _borderPane.setCenter(_treeView);
      _treeView.setShowRoot(false);
      _treeView.setRoot(new TreeItem<AliasTreeNode>(new AliasFolder("This folder is root and should not be visible")));

      _treeView.setCellFactory(cf -> new AliasCell(_aliasTreeNodeChannel, _aliasCutCopyState));

      _borderPane.setOnKeyPressed(this::uncutOnEscape);
      _treeView.setOnKeyPressed(this::uncutOnEscape);

      _btnPinned.setSelected(_prefs.getBoolean(PREF_ALIASES_PINED, false));
      onPinnedChanged();


      AliasTreeStructureNode structRoot = Dao.loadAliasTree();
      ArrayList<Alias> aliases = Dao.loadAliases();

      ArrayList<Alias>  unappliedAliases = structRoot.apply(_treeView.getRoot(), aliases);

      //////////////////////////////////////////////////////////////////////////////////////////////////////
      // unappliedAliases should always be empty. But because people are so unhappy when aliases get lost
      // we do this for precautions only.
      for (Alias unappliedAlias : unappliedAliases)
      {
         _treeView.getRoot().getChildren().add(new TreeItem<AliasTreeNode>(unappliedAlias));
      }
      //
      //////////////////////////////////////////////////////////////////////////////////////////////////////

      AppState.get().addApplicationCloseListener(this::onApplicationClosing);

   }

   private void onApplicationClosing()
   {
      ObservableList<TreeItem<AliasTreeNode>> items = _treeView.getRoot().getChildren();

      ArrayList<Alias> aliases = new ArrayList<>();

      AliasTreeStructureNode structRoot = new AliasTreeStructureNode();
      structRoot.addAll(items, aliases);

      Dao.writeAliases(aliases, structRoot);
   }

   private void uncutOnEscape(KeyEvent ke)
   {
      if (ke.getCode() == KeyCode.ESCAPE)
      {
         _aliasCutCopyState.setTreeItemBeingCut(null);
      }
   }

   private BorderPane createToolBar()
   {
      DockToolbarBuilder dockToolbarBuilder = new DockToolbarBuilder();

      dockToolbarBuilder.addButtonLeft(_props.getImageView("database_connect.png"), _i18n.t("tooltip.connect")).setOnAction(e -> onConnect());
      dockToolbarBuilder.addSeparatorLeft();
      dockToolbarBuilder.addButtonLeft(_props.getImageView("add.png"), _i18n.t("tooltip.add")).setOnAction(e -> onAdd());
      dockToolbarBuilder.addButtonLeft(_props.getImageView("copy.png"), _i18n.t("tooltip.copy")).setOnAction(e -> onCopy());
      dockToolbarBuilder.addButtonLeft(_props.getImageView("edit.png"), _i18n.t("tooltip.edit")).setOnAction(e -> onEdit());
      dockToolbarBuilder.addButtonLeft(_props.getImageView("remove.png"), _i18n.t("tooltip.remove")).setOnAction(e -> onRemove());
      dockToolbarBuilder.addSeparatorLeft();
      dockToolbarBuilder.addButtonLeft(_props.getImageView("sort-ascend.png"), _i18n.t("tooltip.sort")).setOnAction(e -> onSort());
      dockToolbarBuilder.addSeparatorLeft();
      dockToolbarBuilder.addButtonLeft(_props.getImageView("folder_new.png"), _i18n.t("tooltip.new.alias.folder")).setOnAction(e -> onNewFolder());
      dockToolbarBuilder.addButtonLeft(_props.getImageView("copy_to_clip.png"), _i18n.t("tooltip.copy.alias.to.clip")).setOnAction(e -> onCopyToClip());
      dockToolbarBuilder.addButtonLeft(_props.getImageView("cut.png"), _i18n.t("tooltip.cut.alias")).setOnAction(e -> onCut());
      dockToolbarBuilder.addButtonLeft(_props.getImageView("paste.png"), _i18n.t("tooltip.paste.alias")).setOnAction(e -> onPaste());

      _btnPinned = dockToolbarBuilder.addToggleButtonRight(_props.getImageView("dock_win_unpinned.png"), _i18n.t("tooltip.pinned"));
      _btnPinned.setOnAction(e -> onPinnedChanged());

      dockToolbarBuilder.addButtonRight(_props.getImageView("dock_win_close.png"), _i18n.t("tooltip.close")).setOnAction(e -> _dockPaneChanel.closeAliases());

      return dockToolbarBuilder.getToolbarPane();
   }

   private void onPaste()
   {
      if(null == _aliasCutCopyState.getTreeItemBeingCut() && null == _aliasCutCopyState.getTreeItemBeingCopied())
      {
         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), _i18n.t("aliases.nothing.to.paste"));
         return;
      }

      if(null != _aliasCutCopyState.getTreeItemBeingCut())
      {
         TreeItem<AliasTreeNode> selectedItem = _treeView.getSelectionModel().getSelectedItem();

         TreeItem<AliasTreeNode> beingCut = _aliasCutCopyState.getTreeItemBeingCut();

         if (isEqualsOrAbove(beingCut, selectedItem))
         {
            FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), _i18n.t("aliases.cannot.cutnpaste.to.itself.ordesc"));
            return;
         }

         TreeItem<AliasTreeNode> oldParent = beingCut.getParent();

         oldParent.getChildren().remove(beingCut);


         addToTree(selectedItem, beingCut);

         _aliasCutCopyState.setTreeItemBeingCut(null);
      }
      else if(null != _aliasCutCopyState.getTreeItemBeingCopied())
      {
         TreeItem<AliasTreeNode> selectedItem = _treeView.getSelectionModel().getSelectedItem();

         TreeItem<AliasTreeNode> beingCopied = _aliasCutCopyState.getTreeItemBeingCopied();

         if(null == selectedItem)
         {
            selectedItem = _treeView.getRoot();
         }


         TreeItem<AliasTreeNode> aliasNodePathCopy = AliasTreeUtil.deepCopy(beingCopied);

         addToTree(selectedItem, aliasNodePathCopy);

         _aliasCutCopyState.setTreeItemBeingCopied(null);

      }
   }


   private void addToTree(TreeItem<AliasTreeNode> toAddTo, TreeItem<AliasTreeNode> toAdd)
   {
      if(null == toAddTo)
      {
         _treeView.getRoot().getChildren().add(toAdd);

      }
      else
      {
         toAddTo.getChildren().add(toAdd);
         toAddTo.setExpanded(true);
      }
   }

   private boolean isEqualsOrAbove(TreeItem<AliasTreeNode> item, TreeItem<AliasTreeNode> toCheck)
   {
      TreeItem<AliasTreeNode> p = toCheck;

      while(null != p)
      {
         if(p == item)
         {
            return true;
         }

         p = p.getParent();
      }

      return false;
   }

   private void onCut()
   {
      TreeItem<AliasTreeNode> selectedItem = _treeView.getSelectionModel().getSelectedItem();

      if(null == selectedItem)
      {
         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), _i18n.t("aliases.select.node.to.cut"));
         return;
      }

      _aliasCutCopyState.setTreeItemBeingCut(selectedItem);
   }

   private void onCopyToClip()
   {
      TreeItem<AliasTreeNode> selectedItem = _treeView.getSelectionModel().getSelectedItem();

      if(null == selectedItem)
      {
         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), _i18n.t("aliases.select.node.to.copy"));
         return;
      }

      _aliasCutCopyState.setTreeItemBeingCopied(selectedItem);
   }

   private void onNewFolder()
   {
      TreeItem<AliasTreeNode> selectedItem = _treeView.getSelectionModel().getSelectedItem();

      EditFolderNameCtrl editFolderNameCtrl = new EditFolderNameCtrl(null != selectedItem, null != selectedItem && selectedItem.getValue() instanceof AliasFolder);

      String newFolderName = editFolderNameCtrl.getNewFolderName();

      if(Strings.isNullOrEmpty(newFolderName))
      {
         return;
      }

      TreeItem<AliasTreeNode> newTreeItem = AliasTreeUtil.createFolderNode(newFolderName);

      positionNewItem(editFolderNameCtrl.getTreePositionCtrl(), selectedItem, newTreeItem);
   }

   private void positionNewItem(TreePositionCtrl treePositionCtrl, TreeItem<AliasTreeNode> selectedItem, TreeItem<AliasTreeNode> newTreeItem)
   {
      if(treePositionCtrl.isAddToRoot())
      {
         _treeView.getRoot().getChildren().add(newTreeItem);
      }
      else if(treePositionCtrl.isAddToSelectedAsChild())
      {
         selectedItem.getChildren().add(newTreeItem);
         selectedItem.setExpanded(true);
      }
      else if(treePositionCtrl.isAddToSelectedAsAncestor())
      {
         TreeItem<AliasTreeNode> parent = selectedItem.getParent();

         int ixOfSelected = parent.getChildren().indexOf(selectedItem);
         parent.getChildren().add(ixOfSelected, newTreeItem);

      }
      else if(treePositionCtrl.isAddToSelectedAsSuccessor())
      {
         addAsSuccessor(selectedItem, newTreeItem);
      }
   }

   private void addAsSuccessor(TreeItem<AliasTreeNode> selectedItem, TreeItem<AliasTreeNode> newTreeItem)
   {
      TreeItem<AliasTreeNode> parent = selectedItem.getParent();

      int ixOfSelected = parent.getChildren().indexOf(selectedItem);
      parent.getChildren().add(ixOfSelected + 1, newTreeItem);
   }

   private void onSort()
   {
      //To change body of created methods use File | Settings | File Templates.
   }

   private void onEdit()
   {
      TreeItem<AliasTreeNode> selectedItem = _treeView.getSelectionModel().getSelectedItem();

      if(null == selectedItem)
      {
         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), _i18n.t("aliases.select.node.to.edit"));
         return;
      }

      if(selectedItem.getValue() instanceof AliasFolder)
      {
         AliasFolder af = (AliasFolder) selectedItem.getValue();
         EditFolderNameCtrl editFolderNameCtrl = new EditFolderNameCtrl(af.getName());

         String changedFolderName = editFolderNameCtrl.getNewFolderName();

         if(Strings.isNullOrEmpty(changedFolderName))
         {
            return;
         }

         af.setName(changedFolderName);
         _aliasTreeNodeChannel.fireChanged(selectedItem);
      }
      else
      {
         Alias alias = (Alias) selectedItem.getValue();

         AliasEditController aliasEditController = new AliasEditController(alias, AliasEditController.ConstructorState.EDIT);

         if (aliasEditController.isOk())
         {
            selectedItem.setValue(aliasEditController.getAlias());
            _aliasTreeNodeChannel.fireChanged(selectedItem);
         }
      }
   }

   private void onRemove()
   {
      TreeItem<AliasTreeNode> selectedItem = _treeView.getSelectionModel().getSelectedItem();

      if(null == selectedItem)
      {
         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), _i18n.t("aliases.select.node.to.remove"));
         return;
      }

      if (selectedItem.getValue() instanceof Alias)
      {
         String msg = _i18n.t("alias.confirm.remove", selectedItem.getValue().getName());
         if(false == FXMessageBox.YES.equals(FXMessageBox.showYesNo(AppState.get().getPrimaryStage(), msg)))
         {
            return;
         }
      }
      else
      {
         String msg = _i18n.t("aliasfolder.confirm.remove", selectedItem.getValue().getName());
         if(false == FXMessageBox.YES.equals(FXMessageBox.showYesNo(AppState.get().getPrimaryStage(), msg)))
         {
            return;
         }
      }

      selectedItem.getParent().getChildren().remove(selectedItem);
   }

   private void onCopy()
   {
      TreeItem<AliasTreeNode> selectedItem = _treeView.getSelectionModel().getSelectedItem();

      if(null == selectedItem)
      {
         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), _i18n.t("aliases.select.alias.to.copy"));
         return;
      }

      if(selectedItem.getValue() instanceof AliasFolder)
      {
         Stage stage = AppState.get().getPrimaryStage();
         String msg = _i18n.t("aliases.cannot.copy.folder");
         if(FXMessageBox.YES.equals(FXMessageBox.showYesNo(stage, msg, _props.getImageView("copy_to_clip.png"))))
         {
            onCopyToClip();
         }
         return;
      }

      Alias alias = (Alias) selectedItem.getValue();

      alias = AliasTreeUtil.cloneAlias(alias);

      AliasEditController aliasEditController = new AliasEditController(alias, AliasEditController.ConstructorState.COPY);

      if (aliasEditController.isOk())
      {
         alias = aliasEditController.getAlias();
         TreeItem<AliasTreeNode> newTreeItem = AliasTreeUtil.createAliasNode(alias);
         addAsSuccessor(selectedItem, newTreeItem);
      }

   }

   private void onAdd()
   {
      TreeItem<AliasTreeNode> selectedItem = _treeView.getSelectionModel().getSelectedItem();


      AliasEditController aliasEditController = new AliasEditController(null != selectedItem, null != selectedItem && selectedItem.getValue() instanceof AliasFolder);

      if(aliasEditController.isOk())
      {
         Alias alias = aliasEditController.getAlias();
         TreeItem<AliasTreeNode> newTreeItem = AliasTreeUtil.createAliasNode(alias);
         positionNewItem(aliasEditController.getTreePositionCtrl(), selectedItem, newTreeItem);
      }
   }

   private void onPinnedChanged()
   {
      if (_btnPinned.isSelected())
      {
         _btnPinned.setGraphic(_props.getImageView("dock_win_pinned.png"));
      }
      else
      {
         _btnPinned.setGraphic(_props.getImageView("dock_win_unpinned.png"));
      }

      _prefs.set(PREF_ALIASES_PINED, _btnPinned.isSelected());
   }

   private void onConnect()
   {
      //To change body of created methods use File | Settings | File Templates.
   }


   public Node getNode()
   {
      return _borderPane;
   }
}
