package org.squirrelsql.table;

import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class RowObjectTableLoader<T>
{
   private TableLoader _tableLoader = new TableLoader();


   private List<RowObjectHandle<T>> _rowObjectHandles = new ArrayList<>();

   public ColumnHandle addColumn(String header)
   {
      return _tableLoader.addColumn(header);
   }


   public void load(TableView tv)
   {
      _tableLoader.load(tv);
   }

   public int size()
   {
      return _rowObjectHandles.size();
   }


   public  void addRowObjects(List<T> rowObjects, TableLoaderRowObjectAccess<T> cols)
   {
      for (T rowObject : rowObjects)
      {
         addRowObject(rowObject, cols);
      }
   }

   public void addRowObject(T rowObject, TableLoaderRowObjectAccess<T> rowObjectAccess)
   {
      RowObjectHandle h = new RowObjectHandle<T>(rowObject, rowObjectAccess, _tableLoader.getColumnCount());
      _tableLoader.addSimpleObjectPropertyRow(h.getSimpleObjectProperties());

      _rowObjectHandles.add(h);
   }

   public List<RowObjectHandle<T>> getRowObjectHandles()
   {
      return _rowObjectHandles;
   }

   public List<T> getRowObjects()
   {
      List<T> ret = new ArrayList<>();

      for (RowObjectHandle<T> rowObjectHandle : _rowObjectHandles)
      {
         ret.add(rowObjectHandle.getRowObject());
      }

      return ret;
   }

   public void updateUI()
   {
      _rowObjectHandles.forEach(h -> h.updateUI());
   }

   public void clearRows()
   {
      _tableLoader.clearRows();
      _rowObjectHandles.clear();
   }
}
