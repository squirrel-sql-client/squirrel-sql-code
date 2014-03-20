package org.squirrelsql.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class TableLoader
{
   private static final SimpleObjectProperty NULL_PROPERTY = new SimpleObjectProperty("<null>");
   private ArrayList<String> _columns = new ArrayList<>();

   private ArrayList<ArrayList<SimpleObjectProperty>> _rows = new ArrayList<>();

   public void addColumn(String... headers)
   {
      for (String header : headers)
      {
         _columns.add(header);
      }
   }

   public void addRow(List row)
   {
      addRow(row.toArray(new Object[row.size()]));
   }

   public void addRow(Object... row)
   {
      ArrayList<SimpleObjectProperty> buf = new ArrayList<>();

      for (Object o : row)
      {
         buf.add(new SimpleObjectProperty(o));
      }

      _rows.add(buf);
   }

   public void load(TableView tv)
   {
      ArrayList<TableColumn> cols = new ArrayList<>();

      for (int i = 0; i < _columns.size(); i++)
      {
         TableColumn tableColumn = new TableColumn(_columns.get(i));

         final int finalI = i;
         tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ArrayList<SimpleObjectProperty>, Object>, ObservableValue<Object>>()
         {
            public ObservableValue<Object> call(TableColumn.CellDataFeatures<ArrayList<SimpleObjectProperty>, Object> row)
            {
               return interpretValue(row.getValue().get(finalI));
            }
         });
         cols.add(tableColumn);
      }

      tv.getColumns().setAll(cols);

      ObservableList<ArrayList<SimpleObjectProperty>> items = FXCollections.observableArrayList(_rows);
      tv.setItems(items);

   }

   private SimpleObjectProperty interpretValue(SimpleObjectProperty simpleObjectProperty)
   {
      if(null == simpleObjectProperty.get())
      {
         return NULL_PROPERTY;
      }

      return simpleObjectProperty;
   }

   public ArrayList<String> getCellsAsString(int col)
   {
      ArrayList<String> ret = new ArrayList<>(_rows.size());
      for (ArrayList<SimpleObjectProperty> row : _rows)
      {
         SimpleObjectProperty simpleObjectProperty = interpretValue(row.get(col));
         ret.add(getStringValue(simpleObjectProperty));
      }

      return ret;
   }

   private String getStringValue(SimpleObjectProperty simpleObjectProperty)
   {
      if(simpleObjectProperty.get() instanceof String)
      {
         return (String) simpleObjectProperty.get();
      }

      return simpleObjectProperty.get().toString();
   }

   public String getCellAsString(String columnName, int rowIx)
   {
      return getCellAt(rowIx, getColIxByName(columnName));

   }

   private int getColIxByName(String columnName)
   {
      int colIx = -1;
      for (int i = 0; i < _columns.size(); i++)
      {
         if(_columns.get(i).equalsIgnoreCase(columnName))
         {
            colIx = i;
            break;
         }
      }

      if (-1 == colIx)
      {
         throw new IllegalArgumentException("Unknown column name: " + columnName);
      }
      return colIx;
   }

   public int getCellAsInt(String columnName, int rowIx)
   {
      return (int) _rows.get(rowIx).get(getColIxByName(columnName)).get();
   }

   public Integer getCellAsInteger(String columnName, int rowIx)
   {
      return (Integer) _rows.get(rowIx).get(getColIxByName(columnName)).get();
   }


   private String getCellAt(int rowIx, int colIx)
   {
      SimpleObjectProperty simpleObjectProperty = interpretValue(_rows.get(rowIx).get(colIx));
      return getStringValue(simpleObjectProperty);
   }

   public int size()
   {
      return _rows.size();
   }

}
