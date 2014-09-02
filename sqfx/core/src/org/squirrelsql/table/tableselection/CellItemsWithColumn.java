package org.squirrelsql.table.tableselection;

import javafx.scene.control.TableColumn;

import java.util.ArrayList;

public class CellItemsWithColumn
{
   private ArrayList _items = new ArrayList();
   private IndexedTableColumn _column;

   public CellItemsWithColumn(IndexedTableColumn column)
   {
      _column = column;
   }

   public void addItem(Object item)
   {
      _items.add(item);
   }

   public ArrayList getItems()
   {
      return _items;
   }

   public TableColumn getColumn()
   {
      return _column.getTableColumn();
   }
}
