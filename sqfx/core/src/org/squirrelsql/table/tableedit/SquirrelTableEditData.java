package org.squirrelsql.table.tableedit;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;

import java.util.List;

public class SquirrelTableEditData
{
   private String _newValue;
   private Object _oldValue;
   private TablePosition _tablePosition;
   private List _rowValue;

   public SquirrelTableEditData(TableColumn.CellEditEvent cellEditEvent)
   {
      this((String) cellEditEvent.getNewValue(), cellEditEvent.getOldValue(), cellEditEvent.getTablePosition(), (List) cellEditEvent.getRowValue());
   }

   public SquirrelTableEditData(String newValue, Object oldValue, TablePosition tablePosition, List rowValue)
   {
      _newValue = newValue;
      _oldValue = oldValue;
      _tablePosition = tablePosition;
      _rowValue = rowValue;
   }

   public String getNewValue()
   {
      return _newValue;
   }

   public Object getOldValue()
   {
      return _oldValue;
   }

   public TablePosition getTablePosition()
   {
      return _tablePosition;
   }

   public List getRowValue()
   {
      return _rowValue;
   }
}
