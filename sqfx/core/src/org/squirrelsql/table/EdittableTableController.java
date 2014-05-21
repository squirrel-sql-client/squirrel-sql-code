package org.squirrelsql.table;

import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class EdittableTableController
{
   private final TableLoader _tableLoader;
   private final TableView _tv;

   public EdittableTableController(TableLoader tableLoader, TableView tv)
   {
      _tableLoader = tableLoader;
      _tv = tv;
      _tableLoader.load(tv);

      initTableEditiListener();
   }

   private void initTableEditiListener()
   {
      for (ColumnHandle columnHandle : _tableLoader.getColumnHandles())
      {
         columnHandle.getTableColumn().setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>()
         {
            @Override
            public void handle(TableColumn.CellEditEvent event)
            {
               Object newValue = event.getNewValue();
               System.out.println("Edit: New=" + newValue + ", Old=" + event.getOldValue());
               _tableLoader.writeValue(event.getNewValue(), event.getTablePosition());
            }
         });
      }
   }

   public void setEditable(boolean b)
   {
      _tv.setEditable(b);

      if (b)
      {
         _tableLoader.getColumnHandles().forEach(ch -> ch.installEditableCellFactory(createEditableCellFactory()));
      }
      else
      {
         _tableLoader.getColumnHandles().forEach(ColumnHandle::uninstallEditableCellFactory);
      }
   }

   private Callback<TableColumn, TableCell> createEditableCellFactory()
   {
      return new Callback<TableColumn, TableCell>()
            {
               @Override
               public TableCell call(TableColumn param)
               {
                  return new TextFieldTableCell(new StringConverter()
                  {
                     @Override
                     public String toString(Object object)
                     {
                        return "" + object;
                     }

                     @Override
                     public Object fromString(String string)
                     {
                        return string;
                     }
                  });
               }
            };
   }
}
