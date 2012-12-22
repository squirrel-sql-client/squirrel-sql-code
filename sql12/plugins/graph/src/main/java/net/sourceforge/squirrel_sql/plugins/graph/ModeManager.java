package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.QueryBuilderController;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.WhereTreeNodeStructure;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.OrderStructureXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.PrintXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.SelectStructureXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ZoomerXmlBean;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ModeManager
{
   private DefaultController _defaultController;
   private ZoomPrintController _zoomPrintController;
   private QueryBuilderController _queryBuilderController;

   private ModeMenuItem _mnuMode;
   private TableFramesModel _tableFramesModel;
   private ISession _session;
   private GraphPlugin _plugin;
   private GraphControllerFacade _graphControllerFacade;

   private ArrayList<ModeManagerListener> _listeners = new ArrayList<ModeManagerListener>();

   public ModeManager(TableFramesModel tableFramesModel, ISession session, GraphPlugin plugin, GraphControllerFacade graphControllerFacade)
   {
      _tableFramesModel = tableFramesModel;
      _session = session;
      _plugin = plugin;
      _graphControllerFacade = graphControllerFacade;

      _mnuMode = new ModeMenuItem(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onModeChanged();
         }
      });
   }

   public ZoomPrintController getZoomPrintController()
   {
      return _zoomPrintController;
   }

   public JPanel getBottomPanel()
   {
      switch (_mnuMode.getMode())
      {
         case DEFAULT:
            return _defaultController.getBottomPanel();
         case ZOOM_PRINT:
            return _zoomPrintController.getBottomPanel();
         case QUERY_BUILDER:
            return _queryBuilderController.getBottomPanel();
         default:
            throw new IllegalStateException("Unknown mode " + _mnuMode.getMode());
      }
   }

   public Zoomer getZoomer()
   {
      return _zoomPrintController.getZoomer();
   }

   public void sessionEnding()
   {
      _zoomPrintController.sessionEnding();
   }

   private void onModeChanged()
   {

      switch (_mnuMode.getMode())
      {
         case DEFAULT:
            _defaultController.activate(true);
            _zoomPrintController.activate(false);
            _queryBuilderController.activate(false);
            break;
         case ZOOM_PRINT:
            _defaultController.activate(false);
            _zoomPrintController.activate(true);
            _queryBuilderController.activate(false);
            break;
         case QUERY_BUILDER:
            _defaultController.activate(false);
            _zoomPrintController.activate(false);
            _queryBuilderController.activate(true);
            break;
         default:
            throw new IllegalStateException("Unknown mode " + _mnuMode.getMode());
      }

      ModeManagerListener[] listeners = _listeners.toArray(new ModeManagerListener[_listeners.size()]);
      for (ModeManagerListener listener : listeners)
      {
         listener.modeChanged(_mnuMode.getMode());
      }

      SwingUtilities.invokeLater(
      new Runnable()
      {
         public void run()
         {
            _tableFramesModel.recalculateAllConnections();
         }
      });
   }

   public ModeMenuItem getModeMenuItem()
   {
      return _mnuMode;
   }

   public Mode getMode()
   {
      return _mnuMode.getMode();
   }

   public void initMode(Mode mode, ZoomerXmlBean zoomerXmlBean, PrintXmlBean printXmlBean, boolean queryHideNoJoins, SelectStructureXmlBean selectStructure, WhereTreeNodeStructure whereTreeNodeStructure, OrderStructureXmlBean orderStructure, EdgesListener edgesListener, GraphDesktopPane desktopPane)
   {
      StartButtonHandler startButtonHandler;
      GraphPluginResources rsrc = new GraphPluginResources(_plugin);

      startButtonHandler = new StartButtonHandler(_graphControllerFacade, rsrc);
      _zoomPrintController = new ZoomPrintController(zoomerXmlBean, printXmlBean, edgesListener, desktopPane, _session, _plugin, startButtonHandler);

      startButtonHandler = new StartButtonHandler(_graphControllerFacade, rsrc);
      _queryBuilderController = new QueryBuilderController(_tableFramesModel, _graphControllerFacade, queryHideNoJoins, selectStructure, whereTreeNodeStructure, orderStructure, _session, _plugin, startButtonHandler);

      startButtonHandler = new StartButtonHandler(_graphControllerFacade, rsrc);
      _defaultController = new DefaultController(startButtonHandler);

      _mnuMode.setMode(mode);
   }

   public void addModeManagerListener(ModeManagerListener modeManagerListener)
   {
      _listeners.remove(modeManagerListener);
      _listeners.add(modeManagerListener);
   }

   public void removeModeManagerListener(ModeManagerListener modeManagerListener)
   {
      _listeners.remove(modeManagerListener);
   }

   public void setMode(Mode mode)
   {
      _mnuMode.setMode(mode);
      onModeChanged();
   }

   public boolean isQueryHideNoJoins()
   {
      return _queryBuilderController.isHideNoJoins();
   }

   public TableFramesModel getTableFramesModel()
   {
      return _tableFramesModel;
   }

   public WhereTreeNodeStructure getWhereTreeNodeStructure()
   {
      return _queryBuilderController.getWhereTreeNodeStructure();
   }

   public OrderStructureXmlBean getOrderStructure()
   {
      return _queryBuilderController.getOrderStructure();
   }

   public SelectStructureXmlBean getSelectStructure()
   {
      return _queryBuilderController.getSelectStructure();
   }

   public void graphClosed()
   {
      _queryBuilderController.graphClosed();
   }
}
