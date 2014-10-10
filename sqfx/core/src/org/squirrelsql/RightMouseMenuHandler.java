package org.squirrelsql;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

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

   public void addMenu(String menuTitle, RightMouseMenuHandlerListener rightMouseMenuHandlerListener)
   {
      MenuItem menuItem = new MenuItem(menuTitle);

      menuItem.setOnAction(e -> rightMouseMenuHandlerListener.menuSelected());

      _contextMenu.getItems().add(menuItem);
   }
}