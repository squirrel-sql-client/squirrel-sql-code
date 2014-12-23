package org.squirrelsql.table.tableedit;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.sql.SQLResult;
import org.squirrelsql.table.ColumnHandle;
import org.squirrelsql.table.NullMarker;

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
         columnHandle.getTableColumn().setOnEditCommit( e-> onEditCommit(new SquirrelTableEditData((TableColumn.CellEditEvent)e)));
      }
   }

   private DatabaseTableUpdateResult onEditCommit(SquirrelTableEditData tableEditData)
   {
      String userEnteredString = tableEditData.getNewValue();

      //System.out.println("Edit: New=" + userEnteredString + ", Old=" + tableEditData.getOldValue());

      DatabaseTableUpdateResult res = DatabaseTableUpdater.updateDatabase(_session, _sqlResult, userEnteredString, tableEditData, _tableNameFromSQL);
      if (res.success())
      {
         _sqlResult.getResultTableLoader().writeValue(res.getInterpretedNewValue(), tableEditData.getTablePosition());
      }
      else
      {
         Object oldValue = tableEditData.getOldValue();

         ////////////////////////////////////////////////////////////////////////////////////////
         // This way definitely trigger the UI update back to the old value
         _sqlResult.getResultTableLoader().writeValue(null, tableEditData.getTablePosition());
         _sqlResult.getResultTableLoader().writeValue(new NullMarker(), tableEditData.getTablePosition());
         _sqlResult.getResultTableLoader().writeValue(oldValue, tableEditData.getTablePosition());
         //
         ////////////////////////////////////////////////////////////////////////////////////////
      }

      return res;
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
                  return new SquirrelTextFieldTableCell(squirrelTableEditData -> onEditCommit(squirrelTableEditData));
               }
            };
   }
}
