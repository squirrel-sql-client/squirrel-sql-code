package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup.CellDataColumnDataPopupPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup.CellDataUpdateInfo;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.table.TableColumn;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;

public class ResultDataSetAndCellDetailDisplayHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultDataSetAndCellDetailDisplayHandler.class);

   private static final String PREF_KEY_CELL_DETAIL_DIVIDER_POS = "ResultDataSetAndCellDetailDisplayHandler.cell.detail.divider.pos";
   private static final String PREF_KEY_SHOW_CELL_DETAIL = "ResultDataSetAndCellDetailDisplayHandler.show.cell.detail";
   private final JLabel _lblNoCell;

   private IDataSetViewer _dataSetViewer;
   private final ResultTableType _resultTableType;

   private JScrollPane _scrollPane;
   private JSplitPane _splitPane;
   private boolean _adjustingSplitPane = false;


   public ResultDataSetAndCellDetailDisplayHandler(IDataSetViewer dataSetViewer, ResultTableType resultTableType)
   {
      _dataSetViewer = dataSetViewer;
      _resultTableType = resultTableType;

      _scrollPane = new JScrollPane();
      _scrollPane.setBorder(BorderFactory.createEmptyBorder());
      _scrollPane.setViewportView(_dataSetViewer.getComponent());

      _splitPane = new JSplitPane();
      _splitPane.setLeftComponent(_scrollPane);
      _lblNoCell = new JLabel("No cell selected");
      GUIUtils.setPreferredWidth(_lblNoCell, 0);
      GUIUtils.setMinimumWidth(_lblNoCell, 0);
      _splitPane.setRightComponent(_lblNoCell);
      _splitPane.addComponentListener(new ComponentAdapter()
      {
         @Override
         public void componentResized(ComponentEvent e)
         {
            onSplitPaneResized();
         }
      });

      if(resultTableType == ResultTableType.SQL_QUERY_RESULT)
      {
         setCellDetailVisible(Props.getBoolean(PREF_KEY_SHOW_CELL_DETAIL, false), true);
         _splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> onDividerLocationChanged(e));

         if(isDataSetViewerTablePanel())
         {
            _dataSetViewer.addRowColSelectedCountListener((selRowCount, selColCount, selRow, selCol) -> onRowColSelectionChanged((DataSetViewerTablePanel)_dataSetViewer, selRow, selCol));
         }
      }
      else
      {
         setCellDetailVisible(false, true);
      }
   }

   private void onSplitPaneResized()
   {
      if(false == _splitPane.isEnabled())
      {
         _splitPane.setDividerLocation(Integer.MAX_VALUE);
      }
   }

   private void onRowColSelectionChanged(DataSetViewerTablePanel dataSetViewer, int selRow, int selCol)
   {
      if(false == _splitPane.isEnabled())
      {
         return;
      }

      if(   -1 == selRow
         || -1 == selCol
         || false == dataSetViewer.getTable().getColumnModel().getColumn(selCol) instanceof ExtTableColumn)
      {
         int dividerLocBuf = _splitPane.getDividerLocation();
         _splitPane.setRightComponent(_lblNoCell);
         _splitPane.setDividerLocation(dividerLocBuf);
      }
      else
      {
         Object value = dataSetViewer.getTable().getValueAt(selRow, selCol);

         ExtTableColumn column = (ExtTableColumn) dataSetViewer.getTable().getColumnModel().getColumn(selCol);
         CellDataColumnDataPopupPanel panel = new CellDataColumnDataPopupPanel(value, column.getColumnDisplayDefinition(), dataSetViewer.isTableEditable());
         panel.setCellDataUpdateInfo(new CellDataUpdateInfo(selRow, selCol, dataSetViewer.getTable(), null));
         GUIUtils.setPreferredWidth(panel, 0);
         GUIUtils.setMinimumWidth(panel, 0);
         int dividerLocBuf = _splitPane.getDividerLocation();
         _splitPane.setRightComponent(panel);
         _splitPane.setDividerLocation(dividerLocBuf);
      }
   }

   private void onDividerLocationChanged(PropertyChangeEvent e)
   {
      if(_adjustingSplitPane || false == _splitPane.isEnabled())
      {
         return;
      }

      Props.putInt(PREF_KEY_CELL_DETAIL_DIVIDER_POS, (Integer) e.getNewValue());
   }

   public IDataSetViewer getDataSetViewer()
   {
      return _dataSetViewer;
   }

   public boolean isDataSetViewerTablePanel()
   {
      return _dataSetViewer instanceof DataSetViewerTablePanel;
   }

   public DataSetViewerTablePanel getDataSetViewerTablePanel()
   {
      if(false == isDataSetViewerTablePanel())
      {
         throw new IllegalStateException("Call isDataSetViewerTablePanel() first to check if the _dataSetViewer is a DataSetViewerTablePanel");
      }

      return (DataSetViewerTablePanel) _dataSetViewer;
   }


   public IDataSetViewer replaceDataSetViewer(IDataSetViewer dataSetViewer)
   {
      IDataSetViewer previousDataSetViewer = _dataSetViewer;

      if(null != _dataSetViewer)
      {
         _dataSetViewer.disableContinueRead();
      }

      _dataSetViewer = dataSetViewer;
      _scrollPane.setViewportView(_dataSetViewer.getComponent());
      _scrollPane.setRowHeader(null);

      return previousDataSetViewer;
   }

   public CellDetailDisplayAvailableInfo getCellDetailDisplayAvailableInfo()
   {
      if( false == isDataSetViewerTablePanel()  )
      {
         return new CellDetailDisplayAvailableInfo(s_stringMgr.getString("ResultDataSetAndCellDetailDisplayHandler.display.choice.for.table.only"));
      }
      else if(_resultTableType != ResultTableType.SQL_QUERY_RESULT )
      {
         return new CellDetailDisplayAvailableInfo(s_stringMgr.getString("ResultDataSetAndCellDetailDisplayHandler.display.choice.when.query.result.selected.only"));
      }

      return CellDetailDisplayAvailableInfo.INFO_DISPLAY_AVAILABLE;
   }

   public Component getComponent()
   {
      // TODO return split with cell detail display
      return _splitPane;
   }

   public void setCellDetailVisible(boolean b)
   {
      setCellDetailVisible(b, false);
   }

   private void setCellDetailVisible(boolean visible, boolean initializing)
   {
      try
      {
         _adjustingSplitPane = true;
         if(false == visible && (_splitPane.isEnabled() || initializing ))
         {
            _splitPane.setDividerLocation(Integer.MAX_VALUE);
            _splitPane.setDividerSize(0);
            _splitPane.setEnabled(false);
         }
         else if(visible && (false == _splitPane.isEnabled() || initializing))
         {
            _splitPane.setEnabled(true);
            _splitPane.setDividerSize(new JSplitPane().getDividerSize());
            _splitPane.setDividerLocation(Props.getInt(PREF_KEY_CELL_DETAIL_DIVIDER_POS, _splitPane.getMaximumDividerLocation()/2));
         }

         if(false == initializing)
         {
            Props.putBoolean(PREF_KEY_SHOW_CELL_DETAIL, _splitPane.isEnabled());
         }
      }
      finally
      {
         _adjustingSplitPane = false;
      }
   }

   public boolean isCellDetailVisible()
   {
      return _splitPane.isEnabled();
   }

   public SelectedCellInfo getSelectedCellInfo()
   {
      if(false == _dataSetViewer instanceof DataSetViewerTablePanel)
      {
         return new SelectedCellInfo().setHasSelectedCell(false);
      }

      DataSetViewerTable table = ((DataSetViewerTablePanel) _dataSetViewer).getTable();
      int selColIx = table.getSelectedColumn();

      if(-1 == selColIx)
      {
         return new SelectedCellInfo().setHasSelectedCell(false);
      }

      TableColumn column = table.getColumnModel().getColumn(selColIx);
      if(false == column instanceof ExtTableColumn)
      {
         return new SelectedCellInfo().setExtTableColumnCellSelected(false);
      }

      return new SelectedCellInfo(((ExtTableColumn) column).getColumnDisplayDefinition());
   }
}
