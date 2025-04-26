package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ExtTableColumn;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup.CellDataColumnDataPanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup.CellDataUpdateInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind.GlobalFindRemoteControl;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;

public class ResultDataSetAndCellDetailDisplayHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultDataSetAndCellDetailDisplayHandler.class);

   private static final String PREF_KEY_CELL_DETAIL_DIVIDER_POS = "ResultDataSetAndCellDetailDisplayHandler.cell.detail.divider.pos";
   private final LabelNoCellSelected _lblNoCell;
   private final CellDisplayPanel _rightCellDisplayPanel;


   private IDataSetViewer _dataSetViewer;
   private final ResultTableType _resultTableType;

   private JScrollPane _scrollPane;
   private JSplitPane _splitPane;
   private boolean _adjustingSplitPane = false;
   private CellDetailCloseListener _cellDetailCloseListener;
   private boolean _cellDetailSplitActive;

   public ResultDataSetAndCellDetailDisplayHandler(IDataSetViewer dataSetViewer, ResultTableType resultTableType)
   {
      _dataSetViewer = dataSetViewer;
      _resultTableType = resultTableType;

      _scrollPane = new JScrollPane();
      _scrollPane.setBorder(BorderFactory.createEmptyBorder());
      _scrollPane.setViewportView(_dataSetViewer.getComponent());

      _splitPane = new JSplitPane();
      _splitPane.setLeftComponent(_scrollPane);
      _lblNoCell = new LabelNoCellSelected("No cell selected");
      GUIUtils.setPreferredWidth(_lblNoCell.getContentComponent(), 0);
      GUIUtils.setMinimumWidth(_lblNoCell.getContentComponent(), 0);


      _rightCellDisplayPanel = new CellDisplayPanel(() -> onDisplayChanged(), () -> onClose());
      _splitPane.setRightComponent(_rightCellDisplayPanel);

      _splitPane.addComponentListener(new ComponentAdapter()
      {
         @Override
         public void componentResized(ComponentEvent e)
         {
            onSplitPaneResized();
         }
      });

      if(resultTableType == ResultTableType.SQL_QUERY_RESULT && isDataSetViewerTablePanel())
      {
         setCellDetailVisible(ColumnDisplayUtil.isShowCellDetail(), true);
         _splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> onDividerLocationChanged(e));

         _dataSetViewer.addRowColSelectedCountListener((selRowCount, selColCount, selRow, selCol, isAdjusting) -> onRowColSelectionChanged((DataSetViewerTablePanel)_dataSetViewer, isAdjusting));
      }
      else
      {
         setCellDetailVisible(false, true);
      }
   }

   private void onClose()
   {
      if(null != _cellDetailCloseListener)
      {
         _cellDetailCloseListener.close();
      }
   }


   private void onSplitPaneResized()
   {
      if( false == isCellDetailSplitActive() )
      {
         _splitPane.setDividerLocation(Integer.MAX_VALUE);
      }
   }

   private void onRowColSelectionChanged(DataSetViewerTablePanel dataSetViewer, boolean isAdjusting)
   {
      if( isAdjusting || false == isCellDetailSplitActive() )
      {
         return;
      }

      int rowLeadSelectionIndex = dataSetViewer.getTable().getSelectionModel().getLeadSelectionIndex();
      int colLeadSelectionIndex = dataSetViewer.getTable().getColumnModel().getSelectionModel().getLeadSelectionIndex();

      if(   -1 == rowLeadSelectionIndex
         || -1 == colLeadSelectionIndex
         || false == dataSetViewer.getTable().getColumnModel().getColumn(colLeadSelectionIndex) instanceof ExtTableColumn)
      {
         int dividerLocBuf = _splitPane.getDividerLocation();
         _rightCellDisplayPanel.setContentComponent(_lblNoCell);
         _splitPane.setDividerLocation(dividerLocBuf);
      }
      else
      {
         Object value = dataSetViewer.getTable().getValueAt(rowLeadSelectionIndex, colLeadSelectionIndex);

         ExtTableColumn column = (ExtTableColumn) dataSetViewer.getTable().getColumnModel().getColumn(colLeadSelectionIndex);
         _rightCellDisplayPanel.setCurrentColumnDisplayDefinition(column.getColumnDisplayDefinition());

         CellDisplayPanelContent pnlToDisplay;
         if(DisplayMode.IMAGE == _rightCellDisplayPanel.getDisplayMode())
         {
            pnlToDisplay = new ResultImageDisplayPanel(column.getColumnDisplayDefinition(),
                                                       value,
                                                       dataSetViewer.isTableEditable(),
                                                       rowLeadSelectionIndex,
                                                       colLeadSelectionIndex,
                                                       dataSetViewer.getTable());
         }
         else
         {
            CellDataColumnDataPanel panel = new CellDataColumnDataPanel(value, column.getColumnDisplayDefinition(), dataSetViewer.isTableEditable());
            panel.setCellDataUpdateInfo(new CellDataUpdateInfo(rowLeadSelectionIndex, colLeadSelectionIndex, dataSetViewer.getTable(), null));
            pnlToDisplay = panel;
         }

         GUIUtils.setPreferredWidth(pnlToDisplay.getContentComponent(), 0);
         GUIUtils.setMinimumWidth(pnlToDisplay.getContentComponent(), 0);
         int dividerLocBuf = _splitPane.getDividerLocation();
         _rightCellDisplayPanel.setContentComponent(pnlToDisplay);
         _splitPane.setDividerLocation(dividerLocBuf);
      }
   }

   private void onDividerLocationChanged(PropertyChangeEvent e)
   {
      if(_adjustingSplitPane || false == isCellDetailSplitActive() )
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
      _dataSetViewer.moveRowColListenersToMe(previousDataSetViewer);

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

   public JSplitPane getComponent()
   {
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
         if(false == visible && (isCellDetailSplitActive() || initializing))
         {
            _splitPane.setDividerLocation(Integer.MAX_VALUE);
            _splitPane.setDividerSize(0);
            setCellDetailSplitActive(false);
         }
         else if(visible && (false == isCellDetailSplitActive() || initializing))
         {
            setCellDetailSplitActive(true);
            _splitPane.setDividerSize(new JSplitPane().getDividerSize());
            _splitPane.setDividerLocation(Props.getInt(PREF_KEY_CELL_DETAIL_DIVIDER_POS, _splitPane.getMaximumDividerLocation()/2));

            if(_dataSetViewer instanceof DataSetViewerTablePanel)
            {
               fireCellSelectionChangedForCurrentSelectedCell();
            }
         }

         if(false == initializing)
         {
            ColumnDisplayUtil.setShowCellDetail(isCellDetailSplitActive());
         }
      }
      finally
      {
         _adjustingSplitPane = false;
      }
   }

   private void setCellDetailSplitActive(boolean enabled)
   {
      //_splitPane.setEnabled(enabled); No good --> Prevents adjusting mouse cursor
      _cellDetailSplitActive = enabled;
   }

   private boolean isCellDetailSplitActive()
   {
      //return _splitPane.isEnabled();
      return _cellDetailSplitActive;
   }

   public boolean isOpen()
   {
      return isCellDetailSplitActive();
   }


   private void fireCellSelectionChangedForCurrentSelectedCell()
   {
      onRowColSelectionChanged((DataSetViewerTablePanel)_dataSetViewer, false);
   }

   private void onDisplayChanged()
   {
      fireCellSelectionChangedForCurrentSelectedCell();
   }

   public void setCloseListener(CellDetailCloseListener cellDetailCloseListener)
   {
      _cellDetailCloseListener = cellDetailCloseListener;
   }

   public GlobalFindRemoteControl getDisplayHandlerFindRemoteControlOrNull()
   {
      if(false == isCellDetailSplitActive())
      {
         return null;
      }

      if(_rightCellDisplayPanel.getDisplayMode() != DisplayMode.DEFAULT)
      {
         return null;
      }

      if(null == _rightCellDisplayPanel.getContentComponent())
      {
         return null;
      }


      if(_rightCellDisplayPanel.getContentComponent().getContentComponent() instanceof CellDataColumnDataPanel cellDataColumnDataPanel)
      {
         return cellDataColumnDataPanel.getDisplayHandlerFindRemoteControlOrNull();
      }

      return null;
   }
}
