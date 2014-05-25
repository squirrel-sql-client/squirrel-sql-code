package org.squirrelsql.table.tableedit;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.sql.SQLResult;
import org.squirrelsql.table.ColumnHandle;
import org.squirrelsql.table.TableLoader;

public class EdittableTableController
{
   private Session _session;
   private final SQLResult _sqlResult;
   private final TableView _tv;
   private String _tableNameFromSQL;

   public EdittableTableController(Session session, SQLResult sqlResult, TableView tv, String tableNameFromSQL)
   {
      _session = session;
      _sqlResult = sqlResult;
      _tv = tv;
      _tableNameFromSQL = tableNameFromSQL;
      _sqlResult.getResultTableLoader().load(tv);

      initTableEditiListener();
   }

   private void initTableEditiListener()
   {
      for (ColumnHandle columnHandle : _sqlResult.getResultTableLoader().getColumnHandles())
      {
         columnHandle.getTableColumn().setOnEditCommit( e-> onEditCommit((TableColumn.CellEditEvent)e));
      }
   }

   private void onEditCommit(TableColumn.CellEditEvent event)
   {
      String userEnteredString = (String) event.getNewValue();

      System.out.println("Edit: New=" + userEnteredString + ", Old=" + event.getOldValue());

      DatabaseTableUpdateResult res = DatabaseTableUpdater.updateDatabase(_session, _sqlResult, userEnteredString, event, _tableNameFromSQL);
      _sqlResult.getResultTableLoader().writeValue(res.getInterpretedNewValue(), event.getTablePosition());
   }

   public void setEditable(boolean b)
   {
      _tv.setEditable(b);

      if (b)
      {
         _sqlResult.getResultTableLoader().getColumnHandles().forEach(ch -> ch.installEditableCellFactory(createEditableCellFactory()));
      }
      else
      {
         _sqlResult.getResultTableLoader().getColumnHandles().forEach(ColumnHandle::uninstallEditableCellFactory);
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
