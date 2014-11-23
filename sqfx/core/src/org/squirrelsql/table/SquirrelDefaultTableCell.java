package org.squirrelsql.table;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.input.MouseEvent;
import org.squirrelsql.services.Utils;


/**
 * First of all a copy of javafx.scene.control.TableColumn.DEFAULT_CELL_FACTORY
 */
class SquirrelDefaultTableCell extends TableCell<Object, Object>
{
   public SquirrelDefaultTableCell()
   {
      addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
      {
         @Override
         public void handle(MouseEvent event)
         {
            onDoubleClick(event);
         }
      });
   }

   private void onDoubleClick(MouseEvent event)
   {
      if (Utils.isDoubleClick(event))
      {
         Object item = getItem();

         new CellDataPopupController(item, event, getTableColumn(), getTableRow());
      }
   }

   @Override
   protected void updateItem(Object item, boolean empty)
   {
      if (item == getItem()) return;

      super.updateItem(item, empty);

      if (item == null)
      {
         super.setText(null);
         super.setGraphic(null);
      }
      else if (item instanceof Node)
      {
         super.setText(null);
         super.setGraphic((Node) item);
      }
      else
      {
         String value = item.toString();

         int nlPos = value.indexOf("\n");
         int crnlPos = value.indexOf("\r\n");

         if(-1 < nlPos || -1 < crnlPos)
         {
            int cutPos = nlPos;

            if(-1 == cutPos || cutPos < crnlPos)
            {
               cutPos = crnlPos;
            }

            value = value.substring(0, Math.max(cutPos-1, 1));

            setStyle("-fx-background-color: cyan;");
         }
         else
         {
            setStyle(null);
         }


         super.setText(value);
         super.setGraphic(null);
      }
   }
}
