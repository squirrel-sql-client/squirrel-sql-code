package org.squirrelsql.session.sql;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class SQLResultRightMouseMenuHandler
{

   private ContextMenu _contextMenu;

   public SQLResultRightMouseMenuHandler(TableView tv)
   {
      createRightMouseMenu(tv);
   }

   private void createRightMouseMenu(TableView tv)
   {
      _contextMenu = new ContextMenu();

      _contextMenu.setAutoHide(true);

      tv.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
      {
         @Override
         public void handle(MouseEvent e)
         {
            onShowRightMouseMenu(e, _contextMenu, tv);
         }
      });
   }

   private void onShowRightMouseMenu(MouseEvent e, ContextMenu cm, TableView tv)
   {
      if (e.getButton() == MouseButton.SECONDARY)
      {
         cm.show(tv, e.getScreenX(), e.getScreenY());
      }
      else
      {
         cm.hide();
      }
   }

   public void addMenu(String menuTitle, SQLResultRightMouseMenuHandlerListener sqlResultRightMouseMenuHandlerListener)
   {
      MenuItem menuItem = new MenuItem(menuTitle);

      menuItem.setOnAction(e -> sqlResultRightMouseMenuHandlerListener.menuSelected());

      _contextMenu.getItems().add(menuItem);
   }
}
