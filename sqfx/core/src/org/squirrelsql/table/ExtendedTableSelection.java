package org.squirrelsql.table;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

public class ExtendedTableSelection
{
   private Object _beginCellItem;
   private TableRow _beginTableRow;
   private TableColumn _beginTableColumn;

   private Object _endCellItem;
   private TableRow _endTableRow;
   private TableColumn _endTableColumn;

   public ExtendedTableSelection(TableCell beginCell)
   {
      _beginCellItem = beginCell.getItem();
      _beginTableRow = beginCell.getTableRow();
      _beginTableColumn = beginCell.getTableColumn();
   }

   public void setEndCell(TableCell endCell)
   {
      _endCellItem = endCell.getItem();
      _endTableRow = endCell.getTableRow();
      _endTableColumn = endCell.getTableColumn();
   }


   @Override
   public String toString()
   {
      return "Begin: [" + _beginCellItem + "] End: [" + _endCellItem + "]";
   }
}
