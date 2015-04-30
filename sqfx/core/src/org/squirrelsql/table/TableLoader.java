package org.squirrelsql.table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

import org.squirrelsql.services.CollectionUtil;

public class TableLoader
{
   public static final String NULL_AS_STRING = "<null>";
   public static final NullMarker NULL_AS_MARKER = new NullMarker();
   public static final SimpleObjectProperty NULL_PROPERTY = new SimpleObjectProperty(NULL_AS_MARKER);
   private final CellValueReader _cellValueReader;
   private List<ColumnHandle> _columnHandles = new ArrayList<>();

   private List<List<SimpleObjectProperty>> _simpleObjectPropertyRows = new ArrayList<>();

   public TableLoader()
   {
      _cellValueReader = new CellValueReader()
      {
         @Override
         public ObservableValue<Object> getCellValue(TableColumn.CellDataFeatures<List<SimpleObjectProperty>, Object> row, int columnIndex)
         {
            return interpretValue(row.getValue().get(columnIndex));
         }
      };
   }

   public ColumnHandle addColumn(String header)
   {
      return addColumn(header, Collections.emptyList());
   }

   public ColumnHandle addColumn(String header, List selectableValues)
   {
      ColumnHandle columnHandle = new ColumnHandle(header, _columnHandles.size(), _cellValueReader, selectableValues);
      _columnHandles.add(columnHandle);
      return columnHandle;
   }


   public List<SimpleObjectProperty> addRow(List row)
   {
      return addRow(row.toArray(new Object[row.size()]));
   }

   public List<SimpleObjectProperty> addRow(Object... row)
   {
      List<SimpleObjectProperty> buf = TableUtil.createSimpleObjectPropertyRow(row);
      addSimpleObjectPropertyRow(buf);
      return buf;
   }

   void addSimpleObjectPropertyRow(List<SimpleObjectProperty> buf)
   {
      _simpleObjectPropertyRows.add(buf);
   }

   public void load(TableView tv)
   {
      List<TableColumn> tableColumns = CollectionUtil.transform(_columnHandles, col -> col.getTableColumn());

      tv.getColumns().setAll(tableColumns);

      ObservableList<List<SimpleObjectProperty>> items = FXCollections.observableList(_simpleObjectPropertyRows);
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

   public List<String> getCellsAsString(String colName)
   {
      return getCellsAsString(getColIxByName(colName));
   }

   public List<String> getCellsAsString(int col)
   {
      List<String> ret = new ArrayList<>(_simpleObjectPropertyRows.size());
      for (List<SimpleObjectProperty> row : _simpleObjectPropertyRows)
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

   public String getColIxByNameSave(String columnName)
   {
      return null;
   }

   public int getColIxByName(String columnName)
   {
      String availableColumns = "";

      int colIx = -1;
      for (int i = 0; i < _columnHandles.size(); i++)
      {
         availableColumns += _columnHandles.get(i).getHeader() + "\n";
         if(_columnHandles.get(i).getHeader().equalsIgnoreCase(columnName))
         {
            colIx = i;
            break;
         }
      }

      if (-1 == colIx)
      {
         throw new ColumnNotFoundException("Unknown column name: " + columnName + " available colum names are:\n" + availableColumns);
      }
      return colIx;
   }

   //TODO Need a better way of handling this, could be other objects besides BigDecimal
   public int getCellAsInt(String columnName, int rowIx)
   {
	  Object o = _simpleObjectPropertyRows.get(rowIx).get(getColIxByName(columnName)).get();
	  if(o instanceof BigDecimal){
		  return ((BigDecimal) o).intValue();
	  }
	  else {
		  return (int) o;
	  }
   }

   public Integer getCellAsInteger(String columnName, int rowIx)
   {
	  Object o = _simpleObjectPropertyRows.get(rowIx).get(getColIxByName(columnName)).get();
	  if(o instanceof BigDecimal){
		  return ((BigDecimal) o).intValue();
	  }
	  else {
		  return (int)o;
	  }
   }


   private String getCellAt(int rowIx, int colIx)
   {
      SimpleObjectProperty simpleObjectProperty = getObjectPropertyAt(rowIx, colIx);
      return getStringValue(simpleObjectProperty);
   }

   private SimpleObjectProperty getObjectPropertyAt(int rowIx, int colIx)
   {
      return interpretValue(_simpleObjectPropertyRows.get(rowIx).get(colIx));
   }

   public int size()
   {
      return _simpleObjectPropertyRows.size();
   }

   public List<List> getRows()
   {
      List<List> ret = new ArrayList<>();

      for (int i = 0; i < size(); i++)
      {
         List row = new ArrayList();
         for (int j = 0; j < _columnHandles.size(); j++)
         {
            List<SimpleObjectProperty> sopRow = _simpleObjectPropertyRows.get(i);
            if (j < sopRow.size())
            {
               SimpleObjectProperty simpleObjectProperty = sopRow.get(j);
               row.add(simpleObjectProperty.get());
            }
            else
            {
               row.add(null);
            }
         }

         ret.add(row);
      }

      return ret;
   }

   public void clearRows()
   {
      _simpleObjectPropertyRows.clear();
   }

   public int getColumnCount()
   {
      return _columnHandles.size();
   }

   public List<ColumnHandle> getColumnHandles()
   {
      return _columnHandles;
   }

   public List<List<SimpleObjectProperty>> getSimpleObjectPropertyRows()
   {
      return _simpleObjectPropertyRows;
   }

   public void writeValue(Object newValue, TablePosition tablePosition)
   {
      _simpleObjectPropertyRows.get(tablePosition.getRow()).get(tablePosition.getColumn()).set(newValue);
   }

}
