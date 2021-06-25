package net.sourceforge.squirrel_sql.fw.datasetviewer.coloring.markduplicates;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTableModel;

import java.awt.Color;
import java.util.HashMap;

public class DuplicateRowsHandler implements DuplicateHandler
{
   private DataSetViewerTable _dataSetViewerTable;
   private HashMap<Object, Color>  _colorByDuplicateRow;

   public DuplicateRowsHandler(DataSetViewerTable dataSetViewerTable)
   {
      _dataSetViewerTable = dataSetViewerTable;
   }

   public void markDuplicates(boolean selected)
   {
      try
      {

         if(false == selected)
         {
            _colorByDuplicateRow = null;
            return;
         }

         DataSetViewerTableModel dataSetViewerTableModel = _dataSetViewerTable.getDataSetViewerTableModel();

         ValueListReader rdr = new ValueListReader(dataSetViewerTableModel.getRowCount(), ix -> new RowWrapper(dataSetViewerTableModel, ix));
         _colorByDuplicateRow = DuplicatesColorer.getColorByDuplicateValueMap(rdr);
      }
      finally
      {
         _dataSetViewerTable.repaint();
      }
   }


   @Override
   public MarkDuplicatesMode getMode()
   {
      return MarkDuplicatesMode.DUPLICATE_ROWS;
   }

   public Color getBackgroundForCell(int row, int column, Object value)
   {
      if(null == _colorByDuplicateRow)
      {
         return null;
      }

      RowWrapper rowWrapperBuf = new RowWrapper();
      rowWrapperBuf.init(_dataSetViewerTable.getDataSetViewerTableModel(), row);

      return _colorByDuplicateRow.get(rowWrapperBuf);
   }
}
