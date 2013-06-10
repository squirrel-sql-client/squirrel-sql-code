package org.squirrelsql.aliases;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.DockPaneChanel;
import org.squirrelsql.Props;
import org.squirrelsql.services.DockToolbarBuilder;
import org.squirrelsql.services.I18n;

public class AliasesController
{
   private Props _props = new Props(this.getClass());
   private I18n _i18n = new I18n(this.getClass());

   private DockPaneChanel _dockPaneChanel;
   private final BorderPane _borderPane = new BorderPane();
   private ToggleButton _btnPinned;


   public AliasesController(DockPaneChanel dockPaneChanel)
   {
      _dockPaneChanel = dockPaneChanel;

      _borderPane.setTop(createToolBar());
      _borderPane.setCenter(new TreeView<SQLAlias>());

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
      dockToolbarBuilder.addButtonLeft(_props.getImageView("folder_new.png"), _i18n.t("tooltip.new.alias.folder")).setOnAction(e -> onSort());
      dockToolbarBuilder.addButtonLeft(_props.getImageView("copy_to_klip.png"), _i18n.t("tooltip.copy.alias.to.clip")).setOnAction(e -> onSort());
      dockToolbarBuilder.addButtonLeft(_props.getImageView("cut.png"), _i18n.t("tooltip.cut.alias")).setOnAction(e -> onSort());
      dockToolbarBuilder.addButtonLeft(_props.getImageView("paste.png"), _i18n.t("tooltip.paste.alias")).setOnAction(e -> onSort());

      _btnPinned = dockToolbarBuilder.addToggleButtonRight(_props.getImageView("dock_win_unpinned.png"), _i18n.t("tooltip.pinned"));
      _btnPinned.setOnAction(e -> onPinnedChanged());

      dockToolbarBuilder.addButtonRight(_props.getImageView("dock_win_close.png"), _i18n.t("tooltip.close")).setOnAction(e -> _dockPaneChanel.closeAliases());

      return dockToolbarBuilder.getToolbarPane();
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
