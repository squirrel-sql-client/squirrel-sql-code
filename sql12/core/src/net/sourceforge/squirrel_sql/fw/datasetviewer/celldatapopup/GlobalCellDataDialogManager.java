package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

public class GlobalCellDataDialogManager
{
   private CellDataDialog _pinnedCellDataDialog;
   private WindowAdapter _pinnedCellDataDialogWindowAdapter;

   private WindowAdapter _cellDataDialogWindowAdapter;
   private Set<CellDataDialog> _openCellDataDialogs = new HashSet<>();

   public GlobalCellDataDialogManager()
   {
      _pinnedCellDataDialogWindowAdapter = new WindowAdapter()
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

      _cellDataDialogWindowAdapter = new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            unregisterCellDataDialog(e);
         }

         @Override
         public void windowClosed(WindowEvent e)
         {
            unregisterCellDataDialog(e);
         }
      };
   }

   private void unregisterCellDataDialog(WindowEvent e)
   {
      e.getWindow().removeWindowListener(_cellDataDialogWindowAdapter);
      _openCellDataDialogs.remove(e.getWindow());
   }

   void setPinnedCellDataDialog(CellDataDialog pinnedCellDataDialog)
   {
      clearPinnedCellDataDialog();
      _pinnedCellDataDialog = pinnedCellDataDialog;
      _pinnedCellDataDialog.addWindowListener(_pinnedCellDataDialogWindowAdapter);
   }

   void clearPinnedCellDataDialog()
   {
      if(null != _pinnedCellDataDialog)
      {
         _pinnedCellDataDialog.switchOffPinned();
         _pinnedCellDataDialog.removeWindowListener(_pinnedCellDataDialogWindowAdapter);
         _pinnedCellDataDialog = null;
      }
   }

   CellDataDialog getPinnedCellDataDialog()
   {
      return _pinnedCellDataDialog;
   }

   public void registerOpenCellDataDialog(CellDataDialog cellDataDialog)
   {
      cellDataDialog.addWindowListener(_cellDataDialogWindowAdapter);
      _openCellDataDialogs.add(cellDataDialog);
   }

   public Set<CellDataDialog> getOpenCellDataDialogs()
   {
      return _openCellDataDialogs;
   }
}
