package org.squirrelsql.table;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TablePosition;
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

         TablePosition tp = new TablePosition(getTableView(), getTableRow().getIndex(), getTableColumn() );

         new CellDataPopupController(item, event, getTableRow(), tp);
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
         CellProperties cellProperties = TableCellUtil.getCellProperties(item);
         super.setText(cellProperties.getValue());
         super.setStyle(cellProperties.getStyle());

         super.setGraphic(null);
      }
   }
}
