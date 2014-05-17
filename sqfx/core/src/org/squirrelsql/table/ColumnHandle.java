package org.squirrelsql.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;

import java.util.List;

public class ColumnHandle
{
   private final Callback _originalCellFactory;
   private CellValueReader _cellValueReader;
   private String _header;
   private int _columnIndex;
   private TableColumn _tableColumn;

   public ColumnHandle(String header, int columnIndex, CellValueReader cellValueReader, List selectableValues)
   {
      _header = header;
      _columnIndex = columnIndex;
      _cellValueReader = cellValueReader;

      TableColumn tableColumn = new TableColumn(_header);
      _tableColumn = tableColumn;

      _originalCellFactory = tableColumn.getCellFactory();


      if(0 < selectableValues.size())
      {
         tableColumn.setCellFactory(new Callback<TableColumn, TableCell>()
         {
            @Override
            public TableCell call(TableColumn param)
            {
               return new ComboBoxTableCell(selectableValues.toArray());
            }
         });

         tableColumn.setEditable(true);
      }


      tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<List<SimpleObjectProperty>, Object>, ObservableValue<Object>>()
      {
         public ObservableValue<Object> call(TableColumn.CellDataFeatures<List<SimpleObjectProperty>, Object> row)
         {
            //return getCellValue(row, columnHandle1.getColumnIndex());
            return _cellValueReader.getCellValue(row, _columnIndex);
         }
      });
   }

   public String getHeader()
   {
      return _header;
   }

   public TableColumn getTableColumn()
   {
      return _tableColumn;
   }

   public void makeEditable(boolean b)
   {
      if (b)
      {
         _tableColumn.setCellFactory(new Callback<TableColumn, TableCell>()
         {
            @Override
            public TableCell call(TableColumn param)
            {
               TextFieldTableCell textFieldTableCell = new TextFieldTableCell();
               return textFieldTableCell;
            }
         });
         _tableColumn.setEditable(true);
      }
      else
      {
         _tableColumn.setCellFactory(_originalCellFactory);
         _tableColumn.setEditable(false);
      }
   }

}
