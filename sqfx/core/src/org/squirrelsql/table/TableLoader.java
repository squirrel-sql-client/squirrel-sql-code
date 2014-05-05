package org.squirrelsql.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class TableLoader
{
   private static final SimpleObjectProperty NULL_PROPERTY = new SimpleObjectProperty("<null>");
   private List<ColumnHandle> _columns = new ArrayList<>();

   private List<List<SimpleObjectProperty>> _simpleObjectPropertyRows = new ArrayList<>();

   public ColumnHandle addColumn(String header)
   {
      ColumnHandle columnHandle = new ColumnHandle(header);
      _columns.add(columnHandle);
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
      List<TableColumn> cols = new ArrayList<>();

      for (int i = 0; i < _columns.size(); i++)
      {
         ColumnHandle columnHandle = _columns.get(i);
         TableColumn tableColumn = new TableColumn(columnHandle.getHeader());

         if(0 < columnHandle.getSelectableValues().length)
         {
            tableColumn.setCellFactory(new Callback<TableColumn, TableCell>()
            {
               @Override
               public TableCell call(TableColumn param)
               {
                  return new ComboBoxTableCell(columnHandle.getSelectableValues());
               }
            });

            tableColumn.setEditable(true);
            tv.setEditable(true);
         }


         final int finalI = i;
         tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<List<SimpleObjectProperty>, Object>, ObservableValue<Object>>()
         {
            public ObservableValue<Object> call(TableColumn.CellDataFeatures<List<SimpleObjectProperty>, Object> row)
            {
               return getCellValue(finalI, row.getValue());
            }
         });
         cols.add(tableColumn);
      }

      tv.getColumns().setAll(cols);

      ObservableList<List<SimpleObjectProperty>> items = FXCollections.observableList(_simpleObjectPropertyRows);
      tv.setItems(items);

   }

   private SimpleObjectProperty getCellValue(int ix, List<SimpleObjectProperty> row)
   {
      if (row.size() > ix)
      {
         return interpretValue(row.get(ix));
      }
      else
      {
         return NULL_PROPERTY;
      }
   }

   private SimpleObjectProperty interpretValue(SimpleObjectProperty simpleObjectProperty)
   {
      if(null == simpleObjectProperty.get())
      {
         return NULL_PROPERTY;
      }

      return simpleObjectProperty;
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

   private int getColIxByName(String columnName)
   {
      int colIx = -1;
      for (int i = 0; i < _columns.size(); i++)
      {
         if(_columns.get(i).getHeader().equalsIgnoreCase(columnName))
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
      return (int) _simpleObjectPropertyRows.get(rowIx).get(getColIxByName(columnName)).get();
   }

   public Integer getCellAsInteger(String columnName, int rowIx)
   {
      return (Integer) _simpleObjectPropertyRows.get(rowIx).get(getColIxByName(columnName)).get();
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
         for (int j = 0; j < _columns.size(); j++)
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
      return _columns.size();
   }
}
