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

   private int _beginIndex;
   private int _endIndex;

   public ExtendedTableSelection(TableCell beginCell)
   {
      _beginCellItem = beginCell.getItem();
      _beginTableRow = beginCell.getTableRow();
      _beginTableColumn = beginCell.getTableColumn();
      _beginIndex = _beginTableRow.getIndex();
   }

   public void setEndCell(TableCell endCell)
   {
      _endCellItem = endCell.getItem();
      _endTableRow = endCell.getTableRow();
      _endTableColumn = endCell.getTableColumn();
      _endIndex = _endTableRow.getIndex();
   }


   @Override
   public String toString()
   {
      return "Begin: [" + _beginCellItem + "] rowIx=" + _beginIndex + ";End: [" + _endCellItem + "] rowIx=" + _endIndex;
   }

   public int getMinRowIx()
   {
      return Math.min(_beginIndex, _endIndex);
   }

   public int getMaxRowIx()
   {
      return Math.max(_beginIndex, _endIndex);
   }
}
