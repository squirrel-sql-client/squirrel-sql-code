package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.client.globalsearch.GlobalSearchType;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetViewer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultDataSetAndCellDetailDisplayHandler;
import net.sourceforge.squirrel_sql.fw.datasetviewer.columndisplaychoice.ResultTableType;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;

public class DataSetViewerFindHandler
{
   private ResultDataSetAndCellDetailDisplayHandler _resultDisplayHandler;
   private ISession _session;
   private Window _parent;
   private final JPanel _pnlFindResultCompound;
   private boolean _findPanelOpen;

   private DataSetFindPanelController _dataSetFindPanelController;


   public DataSetViewerFindHandler(IDataSetViewer dataSetViewer, ISession session, ResultTableType resultTableType)
   {
      this(dataSetViewer, session, resultTableType, null);
   }

   public DataSetViewerFindHandler(IDataSetViewer dataSetViewer, ResultTableType resultTableType, Window parent)
   {
      this(dataSetViewer, null, resultTableType, parent);
   }

   public DataSetViewerFindHandler(IDataSetViewer dataSetViewer, ISession session, ResultTableType resultTableType, Window parent)
   {
      _resultDisplayHandler = new ResultDataSetAndCellDetailDisplayHandler(dataSetViewer, resultTableType);
      _session = session;
      _parent = parent;

      _pnlFindResultCompound = new JPanel(new BorderLayout());
      //_split.setDividerSize(0);
      //_split.setOrientation(JSplitPane.VERTICAL_SPLIT);
      //_split.setDividerLocation(0);
      //_split.setEnabled(false); // Avoids unwanted display of thin empty bar above the SQL result table.

      _dataSetFindPanelController = new DataSetFindPanelController(() -> toggleShowFindPanel(), session);

      if (_resultDisplayHandler.isDataSetViewerTablePanel())
      {
         _dataSetFindPanelController.setDataSetViewerTablePanel(_resultDisplayHandler.getDataSetViewerTablePanel());
      }

      //_split.setLeftComponent(new NullPanel());

      //_split.addComponentListener(new ComponentAdapter()
      //{
      //   @Override
      //   public void componentResized(ComponentEvent e)
      //   {
      //      if(_split.getLeftComponent() instanceof NullPanel)
      //      {
      //         _split.setDividerLocation(0);
      //      }
      //   }
      //});


      _pnlFindResultCompound.add(_resultDisplayHandler.getComponent(), BorderLayout.CENTER);

   }

   public Component getComponent()
   {
      return _pnlFindResultCompound;
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
         _pnlFindResultCompound.add(_dataSetFindPanelController.getPanel(), BorderLayout.NORTH);
         //_split.setDividerLocation(_dataSetFindPanelController.getPanel().getPreferredSize().height);
         _pnlFindResultCompound.revalidate();
         _dataSetFindPanelController.focusTextField();
      }
      else
      {
         _pnlFindResultCompound.remove(_dataSetFindPanelController.getPanel());
         //_split.setDividerLocation(0);
         _dataSetFindPanelController.wasHidden();
         _pnlFindResultCompound.revalidate();

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

   public GlobalFindRemoteControl getDataSetViewerFindRemoteControlOrNull()
   {
      if (false == _resultDisplayHandler.isDataSetViewerTablePanel())
      {
         return null;
      }

      return (textToSearch, globalSearchType) -> onExecuteFindTillFirstResult(textToSearch, globalSearchType);
   }

   private FirstSearchResult onExecuteFindTillFirstResult(String textToSearch, GlobalSearchType globalSearchType)
   {
      if(false == _findPanelOpen)
      {
         toggleShowFindPanel();
      }
      return _dataSetFindPanelController.executeFindTillFirstResult(textToSearch, globalSearchType);
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
}
