package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.WhereTreeNodeStructure;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.OrderStructureXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.PrintXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.SelectStructureXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ZoomerXmlBean;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;

public class GraphPanelController
{
   private GraphDesktopController _graphDesktopController;
   private JPanel _graphPanel;
   private ModeManager _modeManager;
   private JSplitPane _split;
   private JPanel _bottomPanelContainer;
   private int _standardDividerSize;

   public GraphPanelController(TableFramesModel tableFramesModel, GraphDesktopChannel graphDesktopChannel, ISession session, GraphPlugin plugin, boolean showDndDesktopImageAtStartup)
   {
      GraphControllerFacade graphControllerFacade = new GraphControllerFacade()
      {
         @Override
         public void showDock(JPanel panel, int lastHeight)
         {
            onShow(panel, lastHeight);
         }

         @Override
         public void hideDock()
         {
            onHide();
         }

         @Override
         public void showPopupAbove(Point loc, GraphControllerPopupListener graphControllerPopupListener)
         {
            _graphDesktopController.showPopupAbove(loc, graphControllerPopupListener);
         }

         @Override
         public void hidePopup()
         {
            _graphDesktopController.hidePopup();
         }

         @Override
         public void repaint()
         {
            GraphPanelController.this.repaint();
         }
      };

      _modeManager = new ModeManager(tableFramesModel, session, plugin, graphControllerFacade);
      _graphDesktopController = new GraphDesktopController(graphDesktopChannel, session, plugin, _modeManager, showDndDesktopImageAtStartup);

      JScrollPane scrollPane = new JScrollPane(_graphDesktopController.getDesktopPane());

      _split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      _split.setTopComponent(scrollPane);
      _standardDividerSize = _split.getDividerSize();
      onHide();


      _graphPanel = new JPanel(new BorderLayout());
      _graphPanel.add(_split, BorderLayout.CENTER);
      _bottomPanelContainer = new JPanel(new GridLayout(1,1));
      _graphPanel.add(_bottomPanelContainer, BorderLayout.SOUTH);
   }

   private void onHide()
   {
      _split.setDividerSize(0);
      _split.setDividerLocation(Integer.MAX_VALUE);
   }

   private void onShow(JPanel panel, int lastHeight)
   {
      _split.setBottomComponent(panel);
      _split.setDividerLocation(_split.getHeight() - lastHeight - _standardDividerSize - 1);
      _split.setDividerSize(_standardDividerSize);
   }

   public GraphDesktopController getDesktopController()
   {
      return _graphDesktopController;
   }

   public void repaint()
   {
      _graphPanel.repaint();
      _graphDesktopController.repaint();
   }

   public JPanel getGraphPanel()
   {
      return _graphPanel;
   }

   public void initMode(Mode mode, ZoomerXmlBean zoomerXmlBean, PrintXmlBean printXmlBean, boolean queryHideNoJoins, SelectStructureXmlBean selectStructure, WhereTreeNodeStructure whereTreeNodeStructure, OrderStructureXmlBean orderStructure)
   {
      EdgesListener edgesListener = _graphDesktopController.createEdgesListener();
      GraphDesktopPane desktopPane = _graphDesktopController.getDesktopPane();

      _modeManager.initMode(mode, zoomerXmlBean, printXmlBean, queryHideNoJoins, selectStructure, whereTreeNodeStructure, orderStructure, edgesListener, desktopPane);

      onModeChanged();
      _modeManager.addModeManagerListener(new ModeManagerListener()
      {
         @Override
         public void modeChanged(Mode newMode)
         {
            onModeChanged();
         }
      });

      //_graphPanel.add(_modeManager.getBottomPanel(), BorderLayout.SOUTH);
   }

   private void onModeChanged()
   {
      _bottomPanelContainer.removeAll();
      _bottomPanelContainer.add(_modeManager.getBottomPanel());
      _bottomPanelContainer.revalidate();
      _bottomPanelContainer.repaint();

      if (null != _split.getBottomComponent())
      {
         _split.remove(_split.getBottomComponent());
      }
      onHide();
   }

   public ModeManager getModeManager()
   {
      return _modeManager;
   }

   public void sessionEnding()
   {
      _graphDesktopController.sessionEnding();
   }

   public void removeGraph()
   {
      _graphDesktopController.removeGraph();
   }


   public void changedFromLinkToLocalCopy()
   {
      _graphDesktopController.changedFromLinkToLocalCopy();
   }
}
