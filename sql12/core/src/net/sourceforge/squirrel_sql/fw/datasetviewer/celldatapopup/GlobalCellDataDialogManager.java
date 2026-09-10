package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class GlobalCellDataDialogManager
{
   private CellDataWindow _pinnedCellDataWindow;
   private WindowAdapter _pinnedCellDataDialogWindowAdapter;

   private WindowAdapter _cellDataDialogWindowAdapter;
   private List<CellDataWindow> _openCellDataWindows = new ArrayList<>();

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
      _openCellDataWindows.remove(e.getWindow());
   }

   void setPinnedCellDataDialog(CellDataWindow pinnedCellDataWindow)
   {
      clearPinnedCellDataDialog();
      _pinnedCellDataWindow = pinnedCellDataWindow;
      _pinnedCellDataWindow.getCellDataWindowAdapter().addWindowListener(_pinnedCellDataDialogWindowAdapter);
   }

   void clearPinnedCellDataDialog()
   {
      if(null != _pinnedCellDataWindow)
      {
         _pinnedCellDataWindow.switchOffPinned();
         _pinnedCellDataWindow.getCellDataWindowAdapter().removeWindowListener(_pinnedCellDataDialogWindowAdapter);
         _pinnedCellDataWindow = null;
      }
   }

   public CellDataWindow getPinnedCellDataDialog()
   {
      return _pinnedCellDataWindow;
   }

   public void registerOpenCellDataDialog(CellDataWindow cellDataWindow)
   {
      cellDataWindow.getCellDataWindowAdapter().addWindowListener(_cellDataDialogWindowAdapter);
      _openCellDataWindows.add(cellDataWindow);
   }

   public List<CellDataWindow> getOpenCellDataDialogs()
   {
      return _openCellDataWindows;
   }

   public boolean isPinned(CellDataWindow cellDataWindow)
   {
      return null != _pinnedCellDataWindow && _pinnedCellDataWindow == cellDataWindow;
   }
}
