package org.squirrelsql.table.tableselection;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

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

   private ArrayList<TableColumn> _selectedColumns;

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


   public void initSelectedColumns(TableView tableView)
   {
      _selectedColumns = findSelectedColumns(tableView);
   }

   private ArrayList<TableColumn> findSelectedColumns(TableView tableView)
   {
      ArrayList<TableColumn> ret = new ArrayList<>();

      List<TableColumn> cols = tableView.getColumns();

      boolean inBetween = false;

      for (int i = 0; i < cols.size(); i++)
      {
         TableColumn col = cols.get(i);
         if(isFirstAndLast(col))
         {
            ret.add(col);
            return ret;
         }
         else if(isFirstOrLast(col))
         {
            inBetween = !inBetween;
            ret.add(col);
         }
         else if(inBetween)
         {
            ret.add(col);
         }
      }

      return ret;
   }

   private boolean isFirstOrLast(TableColumn col)
   {
      return _beginTableColumn == col || _endTableColumn == col;
   }

   private boolean isFirstAndLast(TableColumn col)
   {
      return _beginTableColumn == col && _endTableColumn == col;
   }

   public boolean isSingleCell()
   {
      return _beginIndex == _endIndex && _beginTableColumn == _endTableColumn;
   }

   public ArrayList<TableColumn> getSelectedColumns()
   {
      return _selectedColumns;
   }
}
