package org.squirrelsql.services;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import org.squirrelsql.RightMouseMenuHandlerListener;

public class RightMouseMenuHandler
{
   private ContextMenu _contextMenu;

   public RightMouseMenuHandler(Node node)
   {
      createRightMouseMenu(node);
   }

   private void createRightMouseMenu(Node node)
   {
      _contextMenu = new ContextMenu();

      _contextMenu.setAutoHide(true);

      node.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
      {
         @Override
         public void handle(MouseEvent e)
         {
            onShowRightMouseMenu(e, _contextMenu, node);
         }
      });
   }

   private void onShowRightMouseMenu(MouseEvent e, ContextMenu cm, Node node)
   {
      if (e.getButton() == MouseButton.SECONDARY)
      {
         cm.show(node, e.getScreenX(), e.getScreenY());
      }
      else
      {
         cm.hide();
      }
   }

   public MenuItem addMenu(String menuTitle, RightMouseMenuHandlerListener rightMouseMenuHandlerListener)
   {
      return addMenu(menuTitle, null, rightMouseMenuHandlerListener);
   }

   public MenuItem addMenu(String menuTitle, KeyCodeCombination keyCodeCombination, RightMouseMenuHandlerListener rightMouseMenuHandlerListener)
   {
      MenuItem menuItem = new MenuItem(menuTitle);
      menuItem.setAccelerator(keyCodeCombination);

      menuItem.setOnAction(e -> rightMouseMenuHandlerListener.menuSelected());

      _contextMenu.getItems().add(menuItem);

      return menuItem;
   }
   
   public void addSeparator(){
	   _contextMenu.getItems().add(new SeparatorMenuItem());
   }   
}