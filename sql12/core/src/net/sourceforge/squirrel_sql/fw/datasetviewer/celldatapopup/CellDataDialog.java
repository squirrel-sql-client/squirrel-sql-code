package net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.CellDisplayPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.DisplayMode;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultImageDisplayPanel;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.GridLayout;

class CellDataDialog extends JDialog
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(CellDataDialog.class);
   private final CellDisplayPanel _cellDisplayPanel;

   public CellDataDialog(Component comp, String columnName, ColumnDisplayDefinition colDef,
                         Object value, int row, int col,
                         boolean isModelEditable, JTable table)
   {
      super(SwingUtilities.windowForComponent(comp), s_stringMgr.getString("cellDataPopup.valueofColumn", columnName));
      getContentPane().setLayout(new GridLayout(1,1));

      _cellDisplayPanel = new CellDisplayPanel(() -> onDisplayModeChanged(colDef, value, row, col, isModelEditable, table));
      _cellDisplayPanel.setCurrentColumnDisplayDefinition(colDef);
      getContentPane().add(_cellDisplayPanel);

      onDisplayModeChanged(colDef, value, row, col, isModelEditable, table);

      GUIUtils.enableCloseByEscape(this);
   }

   private void onDisplayModeChanged(ColumnDisplayDefinition colDef, Object value, int row, int col, boolean isModelEditable, JTable table)
   {
      JPanel pnlToDisplay;
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
}
