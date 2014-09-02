package org.squirrelsql.table.tableselection;

import javafx.scene.control.TableColumn;

public class IndexedTableColumn
{
   private final int _index;
   private final TableColumn _tableColumn;

   public IndexedTableColumn(int index, TableColumn tableColumn)
   {

      _index = index;
      _tableColumn = tableColumn;
   }

   public int getIndex()
   {
      return _index;
   }

   public TableColumn getTableColumn()
   {
      return _tableColumn;
   }
}
