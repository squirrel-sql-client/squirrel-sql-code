package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanelUtil;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowNumberTableColumn;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.List;

public class DuplicateCellsInSameRowHandler implements DuplicateHandler
{
   private DataSetViewerTable _dataSetViewerTable;

   private HashMap<Integer, HashMap<Object, Color>> _duplicateValuesByRowIndex;

   private Point _cellBuf = new Point();

   public DuplicateCellsInSameRowHandler(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;
   }

   @Override
   public void markDuplicates(boolean selected)
   {
      if (false == selected)
      {
         _duplicateValuesByRowIndex = null;
      }

      _duplicateValuesByRowIndex = new HashMap<>();

      DataSetViewerTableModel dataSetViewerTableModel = _dataSetViewerTable.getDataSetViewerTableModel();

      for (int j = 0; j < dataSetViewerTableModel.getRowCount(); ++j)
      {
         final List<ExtTableColumn> columns = DataSetViewerTablePanelUtil.getTableColumns(_dataSetViewerTable);

         int finalJ = j;
         ValueListReader rdr = new ValueListReader(columns.size(), ix -> dataSetViewerTableModel.getValueAt(finalJ, columns.get(ix).getModelIndex()));
         HashMap<Object, Color> colorByDuplicateValue = DuplicatesColorer.getColorByDuplicateValueMap(rdr);

         _duplicateValuesByRowIndex.put(j, colorByDuplicateValue);
      }
   }

   @Override
   public MarkDuplicatesMode getMode()
   {
      return MarkDuplicatesMode.DUPLICATE_CELLS_IN_ROW;
   }

   @Override
   public Color getBackgroundForCell(int row, int column, Object value)
   {
      if(null == _duplicateValuesByRowIndex)
      {
         return null;
      }

      int columnModelIndex = _dataSetViewerTable.getColumnModel().getColumn(column).getModelIndex();

      if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == columnModelIndex)
      {
         return null;
      }

      return _duplicateValuesByRowIndex.get(row).get(value);
   }
}
