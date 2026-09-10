package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.CellDisplayPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.CellDisplayPanelContent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.DisplayMode;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.DisplayPanelListener;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultImageDisplayPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.GlobalFindRemoteControl;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class CellDataWindow
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDataWindow.class);
   private final CellDataWindowAdapter _cellDataWindowAdapter;
   private final CellDataDialogState _cellDataDialogState;

   private CellDisplayPanel _cellDisplayPanel;

   public CellDataWindow(CellWindowType cellWindowType, CellDataDialogState cellDataDialogState, Window parentWindow)
   {
      _cellDataWindowAdapter = new CellDataWindowAdapter(cellWindowType);
      _cellDataDialogState = cellDataDialogState;
      _cellDataWindowAdapter.initWindow(parentWindow);
      _cellDataWindowAdapter.getContentPane().setLayout(new GridLayout(1,1));

      initCellDisplayPanel(cellDataDialogState);

      _cellDataWindowAdapter.enableCloseByEscape(window -> cleanUp());

      _cellDataWindowAdapter.addWindowListener(new WindowAdapter()
      {
         @Override
         public void windowClosing(WindowEvent e)
         {
            cleanUp();
         }

         @Override
         public void windowClosed(WindowEvent e)
         {
            cleanUp();
         }
      });
   }

   private void cleanUp()
   {
      _cellDisplayPanel.cleanUp();
   }

   public void initCellDisplayPanel(CellDataDialogState cellDataDialogState)
   {


      if(null != _cellDisplayPanel)
      {
         _cellDataWindowAdapter.getContentPane().remove(_cellDisplayPanel);
         _cellDisplayPanel.dispose();
         _cellDisplayPanel = null;
      }

      _cellDataWindowAdapter.setTitle(s_stringMgr.getString("cellDataPopup.valueofColumn", cellDataDialogState.getCellName()));

      DisplayPanelListener displayPanelListener = new DisplayPanelListener()
      {
         @Override
         public void displayModeChanged()
         {
            onDisplayModeChanged(cellDataDialogState);
         }

         @Override
         public void scaleImageToPanelSize()
         {
            onScaleImageToPanelSize();
         }
      };

      _cellDisplayPanel = new CellDisplayPanel(displayPanelListener,sticky -> onToggleSticky(sticky), cellDataDialogState.isPinned(), new CellWindowTypeChooser(this));

      _cellDisplayPanel.setCurrentColumnDisplayDefinition(cellDataDialogState.getColDispDef());
      _cellDataWindowAdapter.getContentPane().add(_cellDisplayPanel);

      onDisplayModeChanged(cellDataDialogState);
   }

   private void onScaleImageToPanelSize()
   {
      if(_cellDisplayPanel.getContentComponent() instanceof ResultImageDisplayPanel imageDisplayPanel)
      {
         imageDisplayPanel.scaleImageToPanelSize();
      }
   }


   private void onToggleSticky(boolean sticky)
   {
      if(sticky)
      {
         Main.getApplication().getGlobalCellDataDialogManager().setPinnedCellDataDialog(this);
      }
      else
      {
         Main.getApplication().getGlobalCellDataDialogManager().clearPinnedCellDataDialog();
      }
   }

   private void onDisplayModeChanged(CellDataDialogState cellDataDialogState)
   {
      CellDisplayPanelContent pnlToDisplay;
      if(DisplayMode.IMAGE == _cellDisplayPanel.getDisplayMode())
      {
         pnlToDisplay = new ResultImageDisplayPanel(cellDataDialogState,
                                                    () -> _cellDisplayPanel.getContentComponent().castToComponent().getSize()
                                                    );
      }
      else
      {
         CellDataColumnDataPanel cellDataPanel = new CellDataColumnDataPanel(cellDataDialogState.getValueToDisplay(), cellDataDialogState.getColDispDef(), cellDataDialogState.isEditable());
         cellDataPanel.setCellDataUpdateInfo(new CellDataUpdateInfo(cellDataDialogState, this));

         pnlToDisplay = cellDataPanel;
      }

      _cellDisplayPanel.setCurrentColumnDisplayDefinition(cellDataDialogState.getColDispDef());
      _cellDisplayPanel.setContentComponent(pnlToDisplay);

      _cellDisplayPanel.revalidate();
   }

   public void switchOffPinned()
   {
      _cellDisplayPanel.switchOffPinned();
   }

   public GlobalFindRemoteControl getCellDetailFindRemoteControlOrNull()
   {
      if(_cellDisplayPanel.getDisplayMode() != DisplayMode.DEFAULT)
      {
         return null;
      }

      if(null == _cellDisplayPanel.getContentComponent())
      {
         return null;
      }


      if(_cellDisplayPanel.getContentComponent().castToComponent() instanceof CellDataColumnDataPanel cellDataColumnDataPanel)
      {
         return cellDataColumnDataPanel.getCellDetailFindRemoteControlOrNull();
      }

      return null;
   }

   public CellDataWindowAdapter getCellDataWindowAdapter()
   {
      return _cellDataWindowAdapter;
   }

   public CellDataDialogState getCellDataDialogState()
   {
      return _cellDataDialogState;
   }
}
