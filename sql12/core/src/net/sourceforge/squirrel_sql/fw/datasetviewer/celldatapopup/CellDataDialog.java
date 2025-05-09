package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.CellDisplayPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.CellDisplayPanelContent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.DisplayMode;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultImageDisplayPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.GlobalFindRemoteControl;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.GridLayout;

public class CellDataDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDataDialog.class);

   private CellDisplayPanel _cellDisplayPanel;

   public CellDataDialog(JTable parentTable,
                         String columnName,
                         int rowIx,
                         int colIx,
                         ColumnDisplayDefinition colDef,
                         Object objectToDisplay,
                         boolean isModelEditable)
   {
      super(SwingUtilities.windowForComponent(parentTable));
      getContentPane().setLayout(new GridLayout(1,1));

      initCellDisplayPanel(parentTable, columnName, rowIx, colIx, colDef, objectToDisplay, isModelEditable,false);

      GUIUtils.enableCloseByEscape(this);
   }

   public void initCellDisplayPanel(JTable table,
                                    String columnName,
                                    int rowIx,
                                    int colIx,
                                    ColumnDisplayDefinition colDef,
                                    Object value,
                                    boolean isModelEditable,
                                    boolean pinned)
   {
      if(null != _cellDisplayPanel)
      {
         getContentPane().remove(_cellDisplayPanel);
         _cellDisplayPanel.dispose();
         _cellDisplayPanel = null;
      }

      setTitle(s_stringMgr.getString("cellDataPopup.valueofColumn", columnName));
      _cellDisplayPanel =
            new CellDisplayPanel(() -> onDisplayModeChanged(colDef, value, rowIx, colIx, isModelEditable, table),
                                 sticky -> onToggleSticky(sticky), pinned);

      _cellDisplayPanel.setCurrentColumnDisplayDefinition(colDef);
      getContentPane().add(_cellDisplayPanel);

      onDisplayModeChanged(colDef, value, rowIx, colIx, isModelEditable, table);
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

   private void onDisplayModeChanged(ColumnDisplayDefinition colDef, Object value, int row, int col, boolean isModelEditable, JTable table)
   {
      CellDisplayPanelContent pnlToDisplay;
      if(DisplayMode.IMAGE == _cellDisplayPanel.getDisplayMode())
      {
         pnlToDisplay = new ResultImageDisplayPanel(colDef,
                                                    value,
                                                    isModelEditable,
                                                    row,
                                                    col,
                                                    (DataSetViewerTable) table);
      }
      else
      {
         CellDataColumnDataPanel cellDataPanel = new CellDataColumnDataPanel(value, colDef, isModelEditable);
         cellDataPanel.setCellDataUpdateInfo(new CellDataUpdateInfo(row, col, table, this));

         pnlToDisplay = cellDataPanel;
      }

      _cellDisplayPanel.setCurrentColumnDisplayDefinition(colDef);
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
}
