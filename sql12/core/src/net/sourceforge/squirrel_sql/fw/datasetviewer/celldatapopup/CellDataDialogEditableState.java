package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import javax.swing.JTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;

public class CellDataDialogEditableState
{
   private final JTable _table;
   private final int _rowIx;
   private final int _colIx;

   public CellDataDialogEditableState(JTable table, int rowIx, int colIx)
   {
      _table = table;
      _rowIx = rowIx;
      _colIx = colIx;
   }


   public int getRowIx()
   {
      return _rowIx;
   }

   public int getColIx()
   {
      return _colIx;
   }

   public DataSetViewerTable getDatasetViewerTable()
   {
      return (DataSetViewerTable) _table;
   }
}
