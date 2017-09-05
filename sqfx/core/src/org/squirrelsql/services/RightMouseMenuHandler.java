package org.squirrelsql.services;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.squirrelsql.RightMouseMenuHandlerListener;

public class RightMouseMenuHandler
{
   private ContextMenu _contextMenu;
   private Node _parentNode;

   public RightMouseMenuHandler(Node parentNode)
   {
      this(parentNode, true);
   }

   public RightMouseMenuHandler(Node parentNode, boolean attachMouseListenerToParentNode)
   {
      _parentNode = parentNode;
      createRightMouseMenu(attachMouseListenerToParentNode);
   }

   private void createRightMouseMenu(boolean attachListener)
   {
      _contextMenu = new ContextMenu();

      _contextMenu.setAutoHide(true);

      if (attachListener)
      {
         _parentNode.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
         {
            @Override
            public void handle(MouseEvent e)
            {
               onShowRightMouseMenu(e, _contextMenu);
            }
         });
      }
   }

   private void onShowRightMouseMenu(MouseEvent e, ContextMenu cm)
   {
      if (isPopupTrigger(e))
      {
         cm.show(_parentNode, e.getScreenX(), e.getScreenY());
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
   
   public void addSeparator()
   {
	   _contextMenu.getItems().add(new SeparatorMenuItem());
   }

   public void show(MouseEvent e)
   {
      onShowRightMouseMenu(e, _contextMenu);
   }

   public static boolean isPopupTrigger(MouseEvent e)
   {
      return e.getButton() == MouseButton.SECONDARY;
   }
}