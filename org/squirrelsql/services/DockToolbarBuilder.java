package org.squirrelsql.services;

import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class DockToolbarBuilder
{

   private final BorderPane _borderPane;
   private final ToolBar _toolbarLeft;
   private final ToolBar _toolbarRight;

   public DockToolbarBuilder()
   {
      _borderPane = new BorderPane();

      _toolbarLeft = new ToolBar();
      _toolbarRight = new ToolBar();

      _borderPane.setCenter(_toolbarLeft);
      _borderPane.setRight(_toolbarRight);
   }

   public Button addButtonLeft(ImageView icon, String tooltip)
   {
      return addButton(icon, tooltip, _toolbarLeft);
   }

   public Button addButtonRight(ImageView imageView, String tooltip)
   {
      return addButton(imageView, tooltip, _toolbarRight);
   }

   public ToggleButton addToggleButtonLeft(ImageView imageView, String tooltip)
   {
      return addToggleButton(imageView, tooltip, _toolbarLeft);
   }


   private Button addButton(ImageView icon, String tooltip, ToolBar toolBar)
   {
      Button btn = new Button();
      btn.setGraphic(icon);
      btn.setTooltip(new Tooltip(tooltip));
      toolBar.getItems().add(btn);
      return btn;
   }


   private ToggleButton addToggleButton(ImageView imageView, String tooltip, ToolBar toolBar)
   {
      ToggleButton btn = new ToggleButton ();
      btn.setGraphic(imageView);
      btn.setTooltip(new Tooltip(tooltip));
      toolBar.getItems().add(btn);

      return btn;
   }

   public BorderPane getToolbarPane()
   {
      return _borderPane;
   }
}
