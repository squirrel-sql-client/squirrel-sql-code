package org.squirrelsql.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class EdittableTableController
{
   private final TableLoader _tableLoader;
   private final TableView _tv;

   public EdittableTableController(TableLoader tableLoader, TableView tv)
   {
      _tableLoader = tableLoader;
      _tv = tv;
      _tableLoader.load(tv);

      for (ColumnHandle columnHandle : _tableLoader.getColumnHandles())
      {
         columnHandle.getTableColumn().setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>()
         {
            @Override
            public void handle(TableColumn.CellEditEvent event)
            {
               Object newValue = event.getNewValue();
               System.out.println("Edit: New=" + newValue + ", Old=" + event.getOldValue());
            }
         });
      }


   }

   public void setEditable(boolean b)
   {
      _tableLoader.makeEditable(b, _tv);
   }
}
