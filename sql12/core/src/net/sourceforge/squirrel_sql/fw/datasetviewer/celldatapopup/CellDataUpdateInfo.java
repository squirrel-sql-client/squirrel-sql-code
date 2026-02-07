package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import javax.swing.JTable;

public class CellDataUpdateInfo
{
   private CellDataDialogState _cellDataDialogState;
   private CellDataDialog _parentCellDataDialog;

   public CellDataUpdateInfo(CellDataDialogState cellDataDialogState, CellDataDialog parentCellDataDialog)
   {
      _cellDataDialogState = cellDataDialogState;
      _parentCellDataDialog = parentCellDataDialog;
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
      if(null == _parentCellDataDialog)
      {
         return;
      }

      _parentCellDataDialog.setVisible(false);
      _parentCellDataDialog.dispose();
   }

   public void cleanUp()
   {
      _parentCellDataDialog = null;
      _cellDataDialogState = null;
   }
}
