package org.squirrelsql.session.objecttree;

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

   public void addColumn(String header)
   {
      _columns.add(header);
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

      for (int i = 0; i < _rows.get(0).size(); i++)
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
}
