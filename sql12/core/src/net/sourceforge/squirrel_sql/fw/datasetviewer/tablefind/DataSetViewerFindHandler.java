package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class DataSetViewerFindHandler
{
   private IDataSetViewer _dataSetViewer;
   private ISession _session;
   private final JSplitPane _split;
   private boolean _findPanelOpen;

   private JScrollPane _scrollPane;
   private DataSetFindPanelController _dataSetFindPanelController;


   public DataSetViewerFindHandler(IDataSetViewer dataSetViewer, ISession session)
   {
      _dataSetViewer = dataSetViewer;
      _session = session;

      _split = new JSplitPane();
      _split.setDividerSize(0);
      _split.setOrientation(JSplitPane.VERTICAL_SPLIT);
      _split.setDividerLocation(0);

      _dataSetFindPanelController = new DataSetFindPanelController(() -> toggleShowFindPanel(), session);

      if (_dataSetViewer instanceof DataSetViewerTablePanel)
      {
         _dataSetFindPanelController.setDataSetViewerTablePanel((DataSetViewerTablePanel) _dataSetViewer);
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


      _scrollPane = new JScrollPane();
      _scrollPane.setBorder(BorderFactory.createEmptyBorder());
      _scrollPane.setViewportView(_dataSetViewer.getComponent());
      _split.setRightComponent(_scrollPane);

   }

   public Component getComponent()
   {
      return _split;
   }

   public boolean toggleShowFindPanel()
   {
      if (false == _dataSetViewer instanceof DataSetViewerTablePanel)
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

         ISQLPanelAPI sqlPanelAPI = _session.getSQLPanelAPIOfActiveSessionWindow(true);

         if(null != sqlPanelAPI)
         {
            sqlPanelAPI.getSQLEntryPanel().requestFocus();
         }
      }

      return true;
   }

   /**
    * @return The replaced {@link IDataSetViewer}
    */
   public IDataSetViewer replaceDataSetViewer(IDataSetViewer dataSetViewer)
   {
      IDataSetViewer previousDataSetViewer = _dataSetViewer;

      if(null != _dataSetViewer)
      {
         _dataSetViewer.disableContinueRead();

      }

      _dataSetViewer = dataSetViewer;
      _scrollPane.setViewportView(dataSetViewer.getComponent());
      _scrollPane.setRowHeader(null);

      if (_dataSetViewer instanceof DataSetViewerTablePanel)
      {
         _dataSetFindPanelController.setDataSetViewerTablePanel((DataSetViewerTablePanel) _dataSetViewer);
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
      return _dataSetViewer;
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
