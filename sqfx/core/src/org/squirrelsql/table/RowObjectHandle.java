package org.squirrelsql.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;

public class RowObjectHandle<T>
{

   private final List<SimpleObjectProperty> _simpleObjectProperties;
   private final T _rowObject;
   private TableLoaderRowObjectAccess<T> _rowObjectAccess;
   private int _columnCount;

   public RowObjectHandle(T rowObject, TableLoaderRowObjectAccess<T> rowObjectAccess, int columnCount)
   {
      _rowObject = rowObject;
      _rowObjectAccess = rowObjectAccess;
      _columnCount = columnCount;
      List row = new ArrayList();

      for (int i = 0; i < columnCount; i++)
      {
         row.add(rowObjectAccess.getColumn(rowObject, i));
      }

      _simpleObjectProperties = TableUtil.createSimpleObjectPropertyRow(row);

      for (int i = 0; i < _simpleObjectProperties.size(); i++)
      {
         SimpleObjectProperty simpleObjectProperty = _simpleObjectProperties.get(i);

         final int finalI = i;
         simpleObjectProperty.addListener((observable, oldValue, newValue) -> onRowObjectCellValueChanged(observable, oldValue, newValue, finalI, rowObject, rowObjectAccess));
      }
   }

   private  void onRowObjectCellValueChanged(ObservableValue observable, Object oldValue, Object newValue, int colIx, T rowObject, TableLoaderRowObjectAccess<T> cols)
   {
      cols.setColumn(rowObject, colIx, newValue);
   }


   public List<SimpleObjectProperty> getSimpleObjectProperties()
   {
      return _simpleObjectProperties;
   }

   public T getRowObject()
   {
      return _rowObject;
   }

   public void updateUI()
   {
      for (int i = 0; i < _columnCount; i++)
      {
         _simpleObjectProperties.get(i).set(_rowObjectAccess.getColumn(_rowObject, i));
      }
   }
}
