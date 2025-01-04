package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PinnedCellDataDialogHandler
{
   private CellDataDialog _pinnedCellDataDialog;

   private WindowAdapter _windowAdapter;

   public PinnedCellDataDialogHandler()
   {
      _windowAdapter = new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            clearPinnedCellDataDialog();
         }

         @Override
         public void windowClosed(WindowEvent e)
         {
            clearPinnedCellDataDialog();
         }
      };
   }

   void setPinnedCellDataDialog(CellDataDialog pinnedCellDataDialog)
   {
      clearPinnedCellDataDialog();
      _pinnedCellDataDialog = pinnedCellDataDialog;
      _pinnedCellDataDialog.addWindowListener(_windowAdapter);
   }

   void clearPinnedCellDataDialog()
   {
      if(null != _pinnedCellDataDialog)
      {
         _pinnedCellDataDialog.switchOffPinned();
         _pinnedCellDataDialog.removeWindowListener(_windowAdapter);
         _pinnedCellDataDialog = null;
      }
   }

   CellDataDialog getPinnedCellDataDialog()
   {
      return _pinnedCellDataDialog;
   }
}
