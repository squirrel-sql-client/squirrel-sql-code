package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultDataSetAndCellDetailDisplayHandler;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultTableType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class DataSetViewerFindHandler
{
   private ResultDataSetAndCellDetailDisplayHandler _resultDisplayHandler;
   private ISession _session;
   private Window _parent;
   private final JSplitPane _split;
   private boolean _findPanelOpen;

   private DataSetFindPanelController _dataSetFindPanelController;


   public DataSetViewerFindHandler(IDataSetViewer dataSetViewer, ISession session, ResultTableType resultTableType)
   {
      this(dataSetViewer, session, resultTableType, null);
   }

   public DataSetViewerFindHandler(IDataSetViewer dataSetViewer, ISession session, ResultTableType resultTableType, Window parent)
   {
      _resultDisplayHandler = new ResultDataSetAndCellDetailDisplayHandler(dataSetViewer, resultTableType);
      _session = session;
      _parent = parent;

      _split = new JSplitPane();
      _split.setDividerSize(0);
      _split.setOrientation(JSplitPane.VERTICAL_SPLIT);
      _split.setDividerLocation(0);
      _split.setEnabled(false); // Avoids unwanted display of thin empty bar above the SQL result table.

      _dataSetFindPanelController = new DataSetFindPanelController(() -> toggleShowFindPanel(), session);

      if (_resultDisplayHandler.isDataSetViewerTablePanel())
      {
         _dataSetFindPanelController.setDataSetViewerTablePanel(_resultDisplayHandler.getDataSetViewerTablePanel());
      }

      _split.setLeftComponent(new NullPanel());

      _split.addComponentListener(new ComponentAdapter()
      {
         @Override
         public void componentResized(ComponentEvent e)
         {
            if(_split.getLeftComponent() instanceof NullPanel)
            {
               _split.setDividerLocation(0);
            }
         }
      });


      _split.setRightComponent(_resultDisplayHandler.getComponent());

   }

   public Component getComponent()
   {
      return _split;
   }

   public boolean toggleShowFindPanel()
   {
      if (false == _resultDisplayHandler.isDataSetViewerTablePanel())
      {
         return false;
      }


      _findPanelOpen = !_findPanelOpen;
      if (_findPanelOpen)
      {
         _split.setLeftComponent(_dataSetFindPanelController.getPanel());
         _split.setDividerLocation(_dataSetFindPanelController.getPanel().getPreferredSize().height);
         _dataSetFindPanelController.focusTextField();
      }
      else
      {
         _split.setLeftComponent(new NullPanel());
         _split.setDividerLocation(0);
         _dataSetFindPanelController.wasHidden();

         if (null == _parent)
         {
            ISQLPanelAPI sqlPanelAPI = _session.getSQLPanelAPIOfActiveSessionWindow(true);

            if(null != sqlPanelAPI)
            {
               sqlPanelAPI.getSQLEntryPanel().requestFocus();
            }
         }
         else
         {
            _parent.requestFocus();
         }
      }

      return true;
   }

   /**
    * @return The replaced {@link IDataSetViewer}
    */
   public IDataSetViewer replaceDataSetViewer(IDataSetViewer dataSetViewer)
   {
      IDataSetViewer previousDataSetViewer = _resultDisplayHandler.replaceDataSetViewer(dataSetViewer);

      if (_resultDisplayHandler.isDataSetViewerTablePanel())
      {
         _dataSetFindPanelController.setDataSetViewerTablePanel(_resultDisplayHandler.getDataSetViewerTablePanel());
      }
      else
      {
         _dataSetFindPanelController.setDataSetViewerTablePanel(null);
      }

      return previousDataSetViewer;
   }

   public void resetFind()
   {
      _dataSetFindPanelController.reset();
   }

   public IDataSetViewer getDataSetViewer()
   {
      return _resultDisplayHandler.getDataSetViewer();
   }

   public void setParentWindow(Window parent)
   {
      _parent = parent;
   }

   public void clearParentWindow()
   {
      _parent = null;
   }

   public ResultDataSetAndCellDetailDisplayHandler getResultDisplayHandler()
   {
      return _resultDisplayHandler;
   }

   private static class NullPanel extends JPanel
   {
      private NullPanel()
      {
         setPreferredSize(new Dimension(0,0));
         setSize(new Dimension(0, 0));
         setMaximumSize(new Dimension(0, 0));
      }
   }

}
