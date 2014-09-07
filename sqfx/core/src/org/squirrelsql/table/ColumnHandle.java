package org.squirrelsql.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.util.Callback;

import java.util.List;

public class ColumnHandle
{
   private final Callback _originalCellFactory;
   private CellValueReader _cellValueReader;
   private String _header;
   private int _columnIndex;
   private TableColumn _tableColumn;
   private List _selectableValues;

   public ColumnHandle(String header, int columnIndex, CellValueReader cellValueReader, List selectableValues)
   {
      _header = header;
      _columnIndex = columnIndex;
      _cellValueReader = cellValueReader;
      _selectableValues = selectableValues;

      _tableColumn = new TableColumn(_header);
      _tableColumn.setId("" + columnIndex);


      _originalCellFactory = _tableColumn.getCellFactory();


      if(0 < selectableValues.size())
      {
         _tableColumn.setCellFactory(new Callback<TableColumn, TableCell>()
         {
            @Override
            public TableCell call(TableColumn param)
            {
               return new ComboBoxTableCell(selectableValues.toArray());
            }
         });

         _tableColumn.setEditable(true);
      }


      _tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<List<SimpleObjectProperty>, Object>, ObservableValue<Object>>()
      {
         public ObservableValue<Object> call(TableColumn.CellDataFeatures<List<SimpleObjectProperty>, Object> row)
         {
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

   public void installEditableCellFactory(Callback<TableColumn, TableCell> cellFactory)
   {
         _tableColumn.setCellFactory(cellFactory);
         _tableColumn.setEditable(true);
   }


   public void uninstallEditableCellFactory()
   {
      _tableColumn.setCellFactory(_originalCellFactory);
      _tableColumn.setEditable(false);
   }

   public List getSelectableValues()
   {
      return _selectableValues;
   }


   public static int extractColumnIndex(TableColumn selectedColumn)
   {
      return Integer.parseInt(selectedColumn.getId());
   }


}
