package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTableModel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanelUtil;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.RowNumberTableColumn;

import java.awt.Color;
import java.util.HashMap;

public class DuplicateValuesInColumnsHandler implements DuplicateHandler
{
   private DataSetViewerTable _dataSetViewerTable;
   private HashMap<Integer, HashMap<Object, Color>> _duplicateValuesByColumnModelIndex;

   public DuplicateValuesInColumnsHandler(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;
   }

   public void markDuplicates(boolean selected)
   {
      try
      {

         if(false == selected)
         {
            _duplicateValuesByColumnModelIndex = null;
            return;
         }

         _duplicateValuesByColumnModelIndex = new HashMap<>();


         DataSetViewerTableModel dataSetViewerTableModel = _dataSetViewerTable.getDataSetViewerTableModel();

         for (ExtTableColumn tableColumn : DataSetViewerTablePanelUtil.getTableColumns(_dataSetViewerTable))
         {
            int columnModelIndex = tableColumn.getModelIndex();

            ValueListReader rdr = new ValueListReader(dataSetViewerTableModel.getRowCount(), ix -> dataSetViewerTableModel.getValueAt(ix, columnModelIndex));
            HashMap<Object, Color> colorByDuplicateValue = DuplicatesColorer.getColorByDuplicateValueMap(rdr);

            _duplicateValuesByColumnModelIndex.put(columnModelIndex, colorByDuplicateValue);
         }
      }
      finally
      {
         _dataSetViewerTable.repaint();
      }
   }

   @Override
   public MarkDuplicatesMode getMode()
   {
      return MarkDuplicatesMode.DUPLICATE_VALUES_IN_COLUMNS;
   }

   public Color getBackgroundForCell(int row, int column, Object value)
   {
      if(null == _duplicateValuesByColumnModelIndex)
      {
         return null;
      }


      int columnModelIndex = _dataSetViewerTable.getColumnModel().getColumn(column).getModelIndex();

      if(RowNumberTableColumn.ROW_NUMBER_MODEL_INDEX == columnModelIndex)
      {
         return null;
      }


      return _duplicateValuesByColumnModelIndex.get(columnModelIndex).get(value);
   }
}
