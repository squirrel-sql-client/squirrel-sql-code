package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class PinnedCellDataDialogHandler
{
   private CellDataDialog _stickyCellDataDialog;

   private WindowAdapter _windowAdapter;

   public PinnedCellDataDialogHandler()
   {
      _windowAdapter = new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            clearStickyCellDataDialog();
         }

         @Override
         public void windowClosed(WindowEvent e)
         {
            clearStickyCellDataDialog();
         }
      };
   }

   public void setStickyCellDataDialog(CellDataDialog stickyCellDataDialog)
   {
      clearStickyCellDataDialog();
      _stickyCellDataDialog = stickyCellDataDialog;
      _stickyCellDataDialog.addWindowListener(_windowAdapter);
   }

   public void clearStickyCellDataDialog()
   {
      if(null != _stickyCellDataDialog)
      {
         _stickyCellDataDialog.switchOffPinned();
         _stickyCellDataDialog.removeWindowListener(_windowAdapter);
         _stickyCellDataDialog = null;
      }
   }

   public CellDataDialog getStickyPopup()
   {
      return _stickyCellDataDialog;
   }
}
