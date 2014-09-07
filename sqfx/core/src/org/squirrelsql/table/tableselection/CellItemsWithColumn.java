package org.squirrelsql.table.tableselection;

import javafx.scene.control.TableColumn;

import java.util.ArrayList;

public class CellItemsWithColumn
{
   private ArrayList _items = new ArrayList();
   private TableColumn _column;

   public CellItemsWithColumn(TableColumn column)
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
      return _column;
   }
}
