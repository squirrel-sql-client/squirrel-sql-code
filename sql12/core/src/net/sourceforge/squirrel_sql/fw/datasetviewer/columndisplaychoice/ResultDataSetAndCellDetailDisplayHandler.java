package net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class ResultDataSetAndCellDetailDisplayHandler
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultDataSetAndCellDetailDisplayHandler.class);

   private IDataSetViewer _dataSetViewer;
   private final ResultTableType _resultTableType;

   private JScrollPane _scrollPane;


   public ResultDataSetAndCellDetailDisplayHandler(IDataSetViewer dataSetViewer, ResultTableType resultTableType)
   {
      _dataSetViewer = dataSetViewer;
      _resultTableType = resultTableType;

      _scrollPane = new JScrollPane();
      _scrollPane.setBorder(BorderFactory.createEmptyBorder());
      _scrollPane.setViewportView(_dataSetViewer.getComponent());
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
      return _scrollPane;
   }

   public void showCellDetail()
   {
      // TODO
   }
}
