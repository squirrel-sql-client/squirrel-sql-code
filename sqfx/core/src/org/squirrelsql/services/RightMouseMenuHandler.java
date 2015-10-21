package org.squirrelsql.services;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.squirrelsql.RightMouseMenuHandlerListener;

public class RightMouseMenuHandler
{
   private ContextMenu _contextMenu;

   public RightMouseMenuHandler(Control control)
   {
      createRightMouseMenu(control);
   }

   private void createRightMouseMenu(Control control)
   {
      _contextMenu = new ContextMenu();

      _contextMenu.setAutoHide(true);

      control.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
      {
         @Override
         public void handle(MouseEvent e)
         {
            onShowRightMouseMenu(e, _contextMenu, control);
         }
      });
   }

   private void onShowRightMouseMenu(MouseEvent e, ContextMenu cm, Control control)
   {
      if (e.getButton() == MouseButton.SECONDARY)
      {
         cm.show(control, e.getScreenX(), e.getScreenY());
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