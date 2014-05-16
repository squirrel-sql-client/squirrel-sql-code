package org.squirrelsql.table;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;

import java.util.List;

public interface CellValueReader
{
   public ObservableValue<Object> getCellValue(TableColumn.CellDataFeatures<List<SimpleObjectProperty>, Object> row, int columnIndex);
}
