package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.PrintXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ZoomerXmlBean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ModeManager
{
   private ZoomPrintController _zoomPrintController;
   private QueryBuilderController _queryBuilderController;

   private ModeMenuItem _mnuMode;
   private TableFramesModel _tableFramesModel;
   private ISession _session;
   private GraphPlugin _plugin;
   private GraphDockHandleFactory _graphDockHandleFactory;

   private ArrayList<ModeManagerListener> _listeners = new ArrayList<ModeManagerListener>();

   public ModeManager(TableFramesModel tableFramesModel, ISession session, GraphPlugin plugin, GraphDockHandleFactory graphDockHandleFactory)
   {
      _tableFramesModel = tableFramesModel;
      _session = session;
      _plugin = plugin;
      _graphDockHandleFactory = graphDockHandleFactory;

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
      JPanel ret = new JPanel(new GridLayout(1,1));
      switch (_mnuMode.getMode())
      {
         case DEFAULT:
            ret.add(new JLabel("Default Bottom Panel Dummy"));
            return ret;
         case ZOOM_PRINT:
            return _zoomPrintController.getBottomPanel();
         case QUERY_BUILDER:
            //ret.add(new JLabel("Query Builder Bottom Panel Dummy"));
            //return ret;
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
            _zoomPrintController.activate(false);
            _queryBuilderController.activate(false);
            break;
         case ZOOM_PRINT:
            _zoomPrintController.activate(true);
            _queryBuilderController.activate(false);
            break;
         case QUERY_BUILDER:
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
   }

   public ModeMenuItem getModeMenuItem()
   {
      return _mnuMode;
   }

   public Mode getMode()
   {
      return _mnuMode.getMode();
   }

   public void initMode(Mode mode, ZoomerXmlBean zoomerXmlBean, PrintXmlBean printXmlBean, EdgesListener edgesListener, GraphDesktopPane desktopPane)
   {
      _zoomPrintController = new ZoomPrintController(zoomerXmlBean, printXmlBean, edgesListener, desktopPane, _session, _plugin);
      _queryBuilderController = new QueryBuilderController(_tableFramesModel, _graphDockHandleFactory, _session);

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
}
