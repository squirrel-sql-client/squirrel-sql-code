package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import javax.swing.JTable;

public class CellDataUpdateInfo
{
   private CellDataDialogState _cellDataDialogState;
   private CellDataWindow _parentCellDataWindow;

   public CellDataUpdateInfo(CellDataDialogState cellDataDialogState, CellDataWindow parentCellDataWindow)
   {
      _cellDataDialogState = cellDataDialogState;
      _parentCellDataWindow = parentCellDataWindow;
   }

   public int getRow()
   {
      return _cellDataDialogState.getEditableState().getRowIx();
   }

   public int getCol()
   {
      return _cellDataDialogState.getEditableState().getColIx();
   }

   public JTable getTable()
   {
      return _cellDataDialogState.getEditableState().getDatasetViewerTable();
   }

   public void closeParentDialog()
   {
      if(null == _parentCellDataWindow)
      {
         return;
      }

      _parentCellDataWindow.getCellDataWindowAdapter().setVisible(false);
      _parentCellDataWindow.getCellDataWindowAdapter().dispose();
   }

   public void cleanUp()
   {
      _parentCellDataWindow = null;
      _cellDataDialogState = null;
   }
}
