package org.squirrelsql.aliases;

import com.google.common.base.Strings;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.AppState;
import org.squirrelsql.DockPaneChanel;
import org.squirrelsql.Props;
import org.squirrelsql.services.DockToolbarBuilder;
import org.squirrelsql.services.FXMessageBox;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;

public class AliasesController
{
   private static final String PREF_ALIASES_PINED = "aliases.pinned";
   private final AliasCell _aliasCell;


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

      _aliasCell = new AliasCell();


      if (false)
      {
         // Hier sollten ausgeschnittene Konten grau gemalt werden --> Funktioniert noch nicht.
         _treeView.setCellFactory(cf -> _aliasCell);
      }


      _btnPinned.setSelected(_prefs.getBoolean(PREF_ALIASES_PINED, false));
      onPinnedChanged();

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
      dockToolbarBuilder.addButtonLeft(_props.getImageView("copy_to_klip.png"), _i18n.t("tooltip.copy.alias.to.clip")).setOnAction(e -> onCopyToClip());
      dockToolbarBuilder.addButtonLeft(_props.getImageView("cut.png"), _i18n.t("tooltip.cut.alias")).setOnAction(e -> onCut());
      dockToolbarBuilder.addButtonLeft(_props.getImageView("paste.png"), _i18n.t("tooltip.paste.alias")).setOnAction(e -> onPaste());

      _btnPinned = dockToolbarBuilder.addToggleButtonRight(_props.getImageView("dock_win_unpinned.png"), _i18n.t("tooltip.pinned"));
      _btnPinned.setOnAction(e -> onPinnedChanged());

      dockToolbarBuilder.addButtonRight(_props.getImageView("dock_win_close.png"), _i18n.t("tooltip.close")).setOnAction(e -> _dockPaneChanel.closeAliases());

      return dockToolbarBuilder.getToolbarPane();
   }

   private void onPaste()
   {
      if(null == _aliasCell.getTreeItemBeingCut() && null == _aliasCell.getTreeItemBeingCopied())
      {
         FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), _i18n.t("aliases.nothing.to.paste"));
         return;
      }

      if(null != _aliasCell.getTreeItemBeingCut())
      {
         TreeItem<AliasTreeNode> selectedItem = _treeView.getSelectionModel().getSelectedItem();

         TreeItem<AliasTreeNode> beingCut = _aliasCell.getTreeItemBeingCut();

         if (isEqualsOrAbove(beingCut, selectedItem))
         {
            FXMessageBox.showInfoOk(AppState.get().getPrimaryStage(), _i18n.t("aliases.cannot.cutnpaste.to.itself.ordesc"));
            return;
         }

         TreeItem<AliasTreeNode> oldParent = beingCut.getParent();

         oldParent.getChildren().remove(beingCut);


         if(null == selectedItem)
         {
            _treeView.getRoot().getChildren().add(beingCut);

         }
         else
         {
            selectedItem.getChildren().add(beingCut);
            selectedItem.setExpanded(true);
         }

         _aliasCell.setTreeItemBeingCut(null);



      }
      else if(null != _aliasCell.getTreeItemBeingCopied())
      {

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

      _aliasCell.setTreeItemBeingCut(selectedItem);
   }

   private void onCopyToClip()
   {
      //To change body of created methods use File | Settings | File Templates.
   }

   private void onNewFolder()
   {
      TreeItem<AliasTreeNode> selectedItem = _treeView.getSelectionModel().getSelectedItem();

      EditFolderNameCtrl editFolderNameCtrl = new EditFolderNameCtrl(null != selectedItem);

      String newFolderName = editFolderNameCtrl.getNewFolderName();

      if(Strings.isNullOrEmpty(newFolderName))
      {
         return;
      }

      TreeItem<AliasTreeNode> newTreeItem = new TreeItem<AliasTreeNode>(new AliasFolder(newFolderName), _props.getImageView("folder.png"));

      if(editFolderNameCtrl.isAddToRoot())
      {
         _treeView.getRoot().getChildren().add(newTreeItem);
      }
      else if(editFolderNameCtrl.isAddToSelectedAsChild())
      {
         selectedItem.getChildren().add(newTreeItem);
         selectedItem.setExpanded(true);
      }
      else if(editFolderNameCtrl.isAddToSelectedAsAncestor())
      {
         TreeItem<AliasTreeNode> parent = selectedItem.getParent();

         int ixOfSelected = parent.getChildren().indexOf(selectedItem);
         parent.getChildren().add(ixOfSelected, newTreeItem);

      }
      else if(editFolderNameCtrl.isAddToSelectedAsSuccessor())
      {
         TreeItem<AliasTreeNode> parent = selectedItem.getParent();

         int ixOfSelected = parent.getChildren().indexOf(selectedItem);
         parent.getChildren().add(ixOfSelected + 1, newTreeItem);
      }


   }

   private void onSort()
   {
      //To change body of created methods use File | Settings | File Templates.
   }

   private void onEdit()
   {
      //To change body of created methods use File | Settings | File Templates.
   }

   private void onRemove()
   {
      //To change body of created methods use File | Settings | File Templates.
   }

   private void onCopy()
   {
      //To change body of created methods use File | Settings | File Templates.
   }

   private void onAdd()
   {
      //To change body of created methods use File | Settings | File Templates.
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
