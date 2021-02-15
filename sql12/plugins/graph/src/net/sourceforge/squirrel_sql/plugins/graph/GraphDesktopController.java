package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.findcolums.FindColumnsCtrl;
import net.sourceforge.squirrel_sql.client.session.action.findcolums.FindColumnsScope;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeDndTransfer;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.RectangleSelectionHandler;
import net.sourceforge.squirrel_sql.fw.gui.RectangleSelectionListener;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.Color;
import java.awt.Point;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.Vector;
import java.util.stream.Collectors;


public class GraphDesktopController
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GraphDesktopController.class);

   private GraphDesktopPane _desktopPane;
   private ConstraintView _lastPressedConstraintView;

   private JPopupMenu _popUp;

   ////////////////////////////////////
   // Saving Graph
   private JMenuItem _mnuSaveGraph;
   private JMenuItem _mnuRenameGraph;
   private JMenuItem _mnuRemoveGraph;
   //
   ////////////////////////////////////

   ////////////////////////////////////
   // Saving link
   private JMenuItem _mnuSaveLinkAsLocalCopy;
   private JMenuItem _mnuSaveLinkedGraph;
   private JMenuItem _mnuRemoveLink;
   private JMenuItem _mnuShowLinkDetails;
   //
   ////////////////////////////////////

   private JMenuItem _mnuCopyGraph;


   private JMenuItem _mnuRefreshAllTables;
   private JMenuItem _mnuScriptAllTables;
   private JMenuItem _mnuFindColumns;
   private JMenuItem _mnuSelectAllTables;
   private JMenuItem _mnuSelectTablesByName;
   private JCheckBoxMenuItem _mnuShowConstraintNames;
   private JCheckBoxMenuItem _mnuShowQualifiedTableNames;
   private JMenuItem _mnuToggleWindowTab;
   private GraphDesktopChannel _channel;
   private ISession _session;
   private GraphPlugin _plugin;
   private ModeManager _modeManager;

   private JMenuItem _mnuAllTablesDbOrder;
   private JMenuItem _mnuAllTablesByNameOrder;
   private JMenuItem _mnuAllTablesPkConstOrder;
   private JMenuItem _mnuAllFilteredSelectedOrder;
   private GraphPluginResources _graphPluginResources;
   private GraphControllerPopupListener _currentGraphControllerPopupListener;
   private final RectangleSelectionHandler _rectangleSelectionHandler = new RectangleSelectionHandler();


   public GraphDesktopController(GraphDesktopChannel channel, ISession session, GraphPlugin plugin, ModeManager modeManager, boolean showDndDesktopImageAtStartup)
   {
      _channel = channel;
      _session = session;
      _plugin = plugin;
      _graphPluginResources = new GraphPluginResources(_plugin);

      ImageIcon startUpImage = null;

      if (showDndDesktopImageAtStartup)
      {
         startUpImage = _graphPluginResources.getIcon(GraphPluginResources.IKeys.DND);
      }

      _desktopPane = new GraphDesktopPane(_session.getApplication(), startUpImage, _rectangleSelectionHandler);
      _rectangleSelectionHandler.setComponent(_desktopPane);

      _rectangleSelectionHandler.setRectangleSelectionListener(new RectangleSelectionListener(){
         @Override
         public void rectSelected(Point dragBeginPoint, Point dragEndPoint)
         {
            onRectSelected(dragBeginPoint, dragEndPoint);
         }
      });

      _desktopPane.setBackground(Color.white);

      _modeManager = modeManager;


      DropTarget dt = new DropTarget();

      try
      {
         dt.addDropTargetListener(new DropTargetAdapter()
         {
            public void drop(DropTargetDropEvent dtde)
            {
               onTablesDroped(dtde);
            }
         });
      }
      catch (TooManyListenersException e)
      {
         throw new RuntimeException(e);
      }

      _desktopPane.setDropTarget(dt);

      _desktopPane.addMouseListener(new MouseAdapter()
      {
         public void mouseClicked(MouseEvent e)
         {
            onMouseClicked(e);
         }

         public void mousePressed(MouseEvent e)
         {
            onMousePressed(e);
         }

         public void mouseReleased(MouseEvent e)
         {
            onMouseReleased(e);
         }
      });

      _desktopPane.addMouseMotionListener(new MouseMotionAdapter()
      {
         public void mouseDragged(MouseEvent e)
         {
            onMouseDragged(e);
         }
      });

      createPopUp();

   }

   private void onTablesDroped(DropTargetDropEvent dtde)
   {
      try
      {
         Object transferData = dtde.getTransferable().getTransferData(dtde.getTransferable().getTransferDataFlavors()[0]);

         if(transferData instanceof ObjectTreeDndTransfer)
         {
            ObjectTreeDndTransfer objectTreeDndTransfer = (ObjectTreeDndTransfer) transferData;

            if(false == objectTreeDndTransfer.getSessionIdentifier().equals(_session.getIdentifier()))
            {
               JOptionPane.showMessageDialog(SwingUtilities.windowForComponent(_desktopPane), s_stringMgr.getString("GraphDesktopController.tableDropedFormOtherSession"));
               return;
            }




            _channel.tablesDropped(objectTreeDndTransfer.getSelectedTables(), dtde.getLocation());
         }

      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }


   EdgesListener createEdgesListener()
   {
      return new EdgesListener()
      {
         public void edgesGraphComponentChanged(EdgesGraphComponent edgesGraphComponent, boolean put)
         {
            onEdgesGraphComponentChanged(edgesGraphComponent, put);
         }
      };
   }

   private void onEdgesGraphComponentChanged(EdgesGraphComponent edgesGraphComponent, boolean put)
   {
      if(put)
      {
         _desktopPane.putGraphComponents(new GraphComponent[]{edgesGraphComponent});
      }
      else
      {
         _desktopPane.removeGraphComponents(new GraphComponent[]{edgesGraphComponent});
      }
      _desktopPane.repaint();
   }

   private void createPopUp()
   {
      _popUp = new JPopupMenu();

      if (_channel.isLink())
      {
         createLinkSavingMenus();
      }
      else
      {
         createGraphSavingMenus();
      }

      _mnuCopyGraph= new JMenuItem(s_stringMgr.getString("graph.copyGraph"));
      _mnuCopyGraph.setIcon(_graphPluginResources.getIcon(GraphPluginResources.IKeys.COPY_GRAPH));
      _mnuCopyGraph.addActionListener(e -> onCopyGraph());


		_mnuRefreshAllTables = new JMenuItem(s_stringMgr.getString("graph.refreshAllTables"));
      _mnuRefreshAllTables.addActionListener(e -> onRefreshAllTables());

		_mnuScriptAllTables = new JMenuItem(s_stringMgr.getString("graph.scriptAllTables"));
      _mnuScriptAllTables.addActionListener(e -> onScriptAllTables());

		_mnuFindColumns = new JMenuItem(s_stringMgr.getString("graph.FindColumns"));
      _mnuFindColumns.setIcon(Main.getApplication().getResources().getIcon(SquirrelResources.IImageNames.FIND_COLUMN));
      _mnuFindColumns.addActionListener(e -> onFindColumns());

      /////////////////////////////////////////////////////////
      // Tablegroups
		_mnuSelectAllTables = new JMenuItem(s_stringMgr.getString("graph.selectAllTables"));
		_mnuSelectAllTables.addActionListener(e -> onSelectAllTables());

      _mnuSelectTablesByName = new JMenuItem(s_stringMgr.getString("graph.selectTablesByName"));
      _mnuSelectTablesByName.addActionListener(e -> onSelectTablesByName());
      /////////////////////////////////////////////////////////

		_mnuShowConstraintNames = new JCheckBoxMenuItem(s_stringMgr.getString("graph.showConstr"));
      _mnuShowConstraintNames.addActionListener(e -> _desktopPane.repaint());

		// i18n[graph.showQualifiedTableNames=Show qualified table names]
		_mnuShowQualifiedTableNames = new JCheckBoxMenuItem(s_stringMgr.getString("graph.showQualifiedTableNames"));
      _mnuShowQualifiedTableNames.addActionListener(e -> onShowQualifiedTableNames());

      ImageIcon toWInIcon = _graphPluginResources.getIcon(GraphPluginResources.IKeys.TO_WINDOW);
      _mnuToggleWindowTab = new JMenuItem(s_stringMgr.getString("graph.toggleWindowTab"), toWInIcon);
      _mnuToggleWindowTab.addActionListener(e -> onToggleWindowTab());


		_mnuAllTablesDbOrder = new JMenuItem(s_stringMgr.getString("graph.allTablesDbOrderRequested"));
      _mnuAllTablesDbOrder.addActionListener(e -> onAllTablesDbOrder());

		_mnuAllTablesByNameOrder = new JMenuItem(s_stringMgr.getString("graph.allTablesByNameOrderRequested"));
      _mnuAllTablesByNameOrder.addActionListener(e -> onAllTablesByNameOrder());

		_mnuAllTablesPkConstOrder = new JMenuItem(s_stringMgr.getString("graph.allTablesPkConstOrderRequested"));
      _mnuAllTablesPkConstOrder.addActionListener(e -> onAllTablesPkConstOrder());

		_mnuAllFilteredSelectedOrder = new JMenuItem(s_stringMgr.getString("graph.allTablesFilteredSelectedOrderRequested"));
      _mnuAllFilteredSelectedOrder.addActionListener(e -> onAllTablesFilteredSelectedOrder());

      if (_channel.isLink())
      {
         _popUp.add(_mnuSaveLinkAsLocalCopy);
         _popUp.add(_mnuSaveLinkedGraph);
         _popUp.add(_mnuRemoveLink);
         _popUp.add(_mnuShowLinkDetails);
      }
      else
      {
         _popUp.add(_mnuSaveGraph);
         _popUp.add(_mnuRenameGraph);
         _popUp.add(_mnuRemoveGraph);
      }
      _popUp.add(new JSeparator());
      _popUp.add(_mnuCopyGraph);
      _popUp.add(new JSeparator());
      _popUp.add(_mnuFindColumns);
      _popUp.add(new JSeparator());
      _popUp.add(_mnuRefreshAllTables);
      _popUp.add(_mnuScriptAllTables);
      _popUp.add(new JSeparator());
      /////////////////////////////////////////////////////////
      // Tablegroups
      _popUp.add(_mnuSelectAllTables);
      _popUp.add(_mnuSelectTablesByName);
      _popUp.add(new JSeparator());
      /////////////////////////////////////////////////////////
      _popUp.add(_mnuAllTablesDbOrder);
      _popUp.add(_mnuAllTablesByNameOrder);
      _popUp.add(_mnuAllTablesPkConstOrder);
      _popUp.add(_mnuAllFilteredSelectedOrder);
      _popUp.add(new JSeparator());
      _popUp.add(_mnuShowConstraintNames);
      _popUp.add(_mnuShowQualifiedTableNames);
      _popUp.add(new JSeparator());
      _popUp.add(_mnuToggleWindowTab);
      _popUp.add(new JSeparator());
      _popUp.add(_modeManager.getModeMenuItem());

      _modeManager.addModeManagerListener(new ModeManagerListener()
      {
         @Override
         public void modeChanged(Mode newMode)
         {
            _popUp.setVisible(false);
         }
      });

      _popUp.addPopupMenuListener(new PopupMenuListener()
      {
         @Override
         public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
         {
            onPopupMenuWillBecomeInvisible();
         }

         @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
         @Override public void popupMenuCanceled(PopupMenuEvent e) {}
      });

   }

   private void onFindColumns()
   {
      final List<ITableInfo> tablesInGraph
            = getTableFramesModel().getTblCtrls().stream().map(tc -> tc.getTableInfo()).collect(Collectors.toList());

      String findColumnsDlgTitle = s_stringMgr.getString("graph.findColumnsDialogTitle", _channel.getGraphName());
      FindColumnsScope findColumnsScope = new FindColumnsScope(tablesInGraph, _session, GUIUtils.getOwningWindow(_desktopPane), findColumnsDlgTitle);
      new FindColumnsCtrl(findColumnsScope);
   }

   private void createLinkSavingMenus()
   {
      _mnuSaveLinkAsLocalCopy = new JMenuItem(s_stringMgr.getString("graph.saveLinkAsLocalCopy"));
      _mnuSaveLinkAsLocalCopy.addActionListener(e -> onSaveLinkAsLocalCopy());

      _mnuSaveLinkedGraph = new JMenuItem(s_stringMgr.getString("graph.saveLinkedGraph"));
      _mnuSaveLinkedGraph.addActionListener(e -> onSaveLinkedGraph());

      _mnuRemoveLink = new JMenuItem(s_stringMgr.getString("graph.removeLink"));
      _mnuRemoveLink.addActionListener(e -> onRemoveLink());

      _mnuShowLinkDetails = new JMenuItem(s_stringMgr.getString("graph.showLinkDetails"));
      _mnuShowLinkDetails.setIcon(_graphPluginResources.getIcon(GraphPluginResources.IKeys.LINK));

      _mnuShowLinkDetails.addActionListener(e -> onShowLinkDetails());
   }

   private void createGraphSavingMenus()
   {
      // i18n[graph.saveGraph=Save graph]
      _mnuSaveGraph = new JMenuItem(s_stringMgr.getString("graph.saveGraph"));
      _mnuSaveGraph.addActionListener(e -> onSaveGraph());


      _mnuRenameGraph= new JMenuItem(s_stringMgr.getString("graph.renameGraph"));
      _mnuRenameGraph.addActionListener(e -> onRenameGraph());

      // i18n[graph.removeGraph=Remove graph]
      _mnuRemoveGraph= new JMenuItem(s_stringMgr.getString("graph.removeGraph"));
      _mnuRemoveGraph.addActionListener(e -> onRemoveGraph());
   }

   private void onPopupMenuWillBecomeInvisible()
   {
      if(null != _currentGraphControllerPopupListener)
      {
         _currentGraphControllerPopupListener.hiding();
         _currentGraphControllerPopupListener = null;
      }
   }

   private void onShowQualifiedTableNames()
   {
      _channel.showQualifiedTableNamesRequested();
   }

   private void onAllTablesPkConstOrder()
   {
      _channel.allTablesPkConstOrderRequested();
   }

   private void onAllTablesByNameOrder()
   {
      _channel.allTablesByNameOrderRequested();
   }

   private void onAllTablesDbOrder()
   {
      _channel.allTablesDbOrderRequested();
   }

   private void onAllTablesFilteredSelectedOrder()
   {
      _channel.allTablesFilteredSelectedOrderRequested();
   }

   private void onToggleWindowTab()
   {
      _channel.toggleWindowTab();
   }



   private void onScriptAllTables()
   {
      _channel.scriptAllTablesRequested();
   }

   /////////////////////////////////////////////////////////
   // Tablegroups
   private void onSelectAllTables()
   {
      for(JInternalFrame f:_desktopPane.getAllFrames()) {
    	  if(f instanceof TableFrame) {
    		  _desktopPane.addGroupFrame((TableFrame)f);
    	  }
      }
   }

   private void onRectSelected(Point dragBeginPoint, Point dragEndPoint)
   {
      for (JInternalFrame f : _desktopPane.getAllFrames())
      {
         if (f instanceof TableFrame && RectangleSelectionHandler.rectHit(f.getBounds(), dragBeginPoint, dragEndPoint))
         {
            _desktopPane.addGroupFrame((TableFrame) f);
         }
      }
   }


   private void onSelectTablesByName()
   {
      String namePattern=JOptionPane.showInputDialog(_desktopPane, s_stringMgr.getString("graph.selectTablesByName.message"), s_stringMgr.getString("graph.selectTablesByName.title"), JOptionPane.QUESTION_MESSAGE);

      if(null == namePattern || 0 == namePattern.trim().length())
      {
         return;
      }

      _desktopPane.clearGroupFrames();
      for (JInternalFrame f : _desktopPane.getAllFrames())
      {
         if (f instanceof TableFrame)
         {
            TableFrame tf = (TableFrame) f;
            if (tf.getTitle().matches(namePattern.replace('?', '.').replace("*", ".*")))
            {
               _desktopPane.addGroupFrame(tf);
            }
         }
      }
   }
   /////////////////////////////////////////////////////////

   private void onRefreshAllTables()
   {
      _channel.refreshAllTablesRequested();
   }

   private void onRemoveGraph()
   {
      if(showRemoveOptionPane("graph.delGraph") == JOptionPane.YES_OPTION)
      {
         _channel.removeRequest();
         _modeManager.graphClosed();
      }
   }

   private int showRemoveOptionPane(String msgKey)
   {
      Window parent = SwingUtilities.windowForComponent(_desktopPane);
      return JOptionPane.showConfirmDialog(parent, s_stringMgr.getString(msgKey));
   }

   private void onRenameGraph()
   {

		// i18n[graph.newName=Please enter a new name]
      Window parent = SwingUtilities.windowForComponent(_desktopPane);
		String newName = JOptionPane.showInputDialog(parent, s_stringMgr.getString("graph.newName"));
      if(null != newName && 0 != newName.trim().length())
      {
         _channel.renameRequest(newName);
      }
   }

   private void onSaveGraph()
   {
      _channel.saveGraphRequested();
   }

   private void onSaveLinkAsLocalCopy()
   {
      _channel.saveLinkAsLocalCopy();
   }

   private void onSaveLinkedGraph()
   {
      _channel.saveLinkedGraph();
   }

   private void onRemoveLink()
   {
      if(showRemoveOptionPane("graph.delLink") == JOptionPane.YES_OPTION)
      {
         _channel.removeLink();
      }
   }

   private void onShowLinkDetails()
   {
      _channel.showLinkDetails();
   }

   private void onCopyGraph()
   {
      _channel.copyGraph();
   }




   private void maybeShowPopup(MouseEvent e)
   {
      if (e.isPopupTrigger())
      {
         _mnuAllFilteredSelectedOrder.setEnabled(_modeManager.getMode().isQueryBuilder());

         _popUp.show(e.getComponent(), e.getX(), e.getY());
      }
   }



   /**
    * It's called put because it adds unique, like a Hashtable.
    */
   public void putConstraintViews(ConstraintView[] constraintViews)
   {
      _desktopPane.putGraphComponents(constraintViews);
   }

   public void removeConstraintViews(ConstraintView[] constraintViews, boolean keepFoldingPoints)
   {
      _desktopPane.removeGraphComponents(constraintViews);

      if(false == keepFoldingPoints)
      {
         for (int i = 0; i < constraintViews.length; i++)
         {
            constraintViews[i].removeAllFoldingPoints();
         }
      }
   }


   private void refreshSelection(ConstraintView hitOne, boolean allowDeselect)
   {
      if(allowDeselect)
      {
         hitOne.setSelected(!hitOne.isSelected());
      }
      else if(false == hitOne.isSelected())
      {
         hitOne.setSelected(true);
      }

      Vector<GraphComponent> graphComponents = _desktopPane.getGraphComponents();

      for (int i = 0; i < graphComponents.size(); i++)
      {
         if(false == graphComponents.elementAt(i) instanceof ConstraintView)
         {
            continue;
         }

         ConstraintView constraintView = (ConstraintView) graphComponents.elementAt(i);
         if(false == constraintView.equals(hitOne))
         {
            constraintView.setSelected(false);
         }
      }
   }

   public void onMouseReleased(final MouseEvent e)
   {
      _lastPressedConstraintView = null;

      ConstraintHitData hitData = findHit(e);
      if(ConstraintHit.LINE == hitData.getConstraintHit())
      {
         hitData.getConstraintView().mouseReleased(e);
      }
      else if(ConstraintHit.NONE == hitData.getConstraintHit())
      {
         maybeShowPopup(e);
      }
   }

   public void onMousePressed(final MouseEvent e)
   {
      final ConstraintHitData hitData = findHit(e);
      if(ConstraintHit.LINE == hitData.getConstraintHit())
      {
         _lastPressedConstraintView = hitData.getConstraintView();

         if(InputEvent.BUTTON3_MASK == e.getModifiers())
         {
            refreshSelection(hitData.getConstraintView(), false);
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  hitData.getConstraintView().mousePressed(e);
               }
            });
         }
         else
         {
            hitData.getConstraintView().mousePressed(e);
         }
      }
      else if(ConstraintHit.NONE == hitData.getConstraintHit())
      {
         maybeShowPopup(e);
      }
   }

   public void onMouseClicked(final MouseEvent e)
   {
      final ConstraintHitData hitData = findHit(e);

      if(ConstraintHit.LINE == hitData.getConstraintHit())
      {
         refreshSelection(hitData.getConstraintView(), InputEvent.BUTTON1_MASK == e.getModifiers() );
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               hitData.getConstraintView().mouseClicked(e);
            }
         });
      }
   }

   private void onMouseDragged(MouseEvent e)
   {
      if(null != _lastPressedConstraintView)
      {
         if(_lastPressedConstraintView.mouseDragged(e))
         {
            _rectangleSelectionHandler.cancelCurrentSelection();
         }
      }
   }

   private ConstraintHitData findHit(MouseEvent e)
   {
      Vector<GraphComponent> graphComponents = _desktopPane.getGraphComponents();


      for (int i = 0; i < graphComponents.size(); i++)
      {
         GraphComponent graphComponent = graphComponents.elementAt(i);

         if(graphComponent instanceof ConstraintView)
         {
            ConstraintView constraintView = (ConstraintView)graphComponents.elementAt(i);
            ConstraintHit constraintHit = constraintView.hitMe(e);
            if(ConstraintHit.NONE != constraintHit)
            {
               return new ConstraintHitData(constraintView, constraintHit);
            }
         }
      }
      return new ConstraintHitData(null, ConstraintHit.NONE);
   }


   public void repaint()
   {
      _desktopPane.repaint();
   }

   public void addFrame(JInternalFrame frame)
   {
      _desktopPane.hideStartupImage();
      _desktopPane.add(frame);
   }

   public GraphDesktopPane getDesktopPane()
   {
      return _desktopPane;
   }

   public boolean isShowConstraintNames()
   {
      return _mnuShowConstraintNames.isSelected();
   }

   public void setShowConstraintNames(boolean showConstraintNames)
   {
      _mnuShowConstraintNames.setSelected(showConstraintNames);
   }

   public Zoomer getZoomer()
   {
      return _modeManager.getZoomer();
   }

   public ZoomPrintController getZoomPrintController()
   {
      return _modeManager.getZoomPrintController();
   }

   public void sessionEnding()
   {
      _modeManager.sessionEnding();
   }

   public void setShowQualifiedTableNames(boolean showQualifiedTableNames)
   {
      _mnuShowQualifiedTableNames.setSelected(showQualifiedTableNames);
   }


   public boolean isShowQualifiedTableNames()
   {
      return _mnuShowQualifiedTableNames.isSelected();
   }

   public ModeManager getModeManager()
   {
      return _modeManager;
   }

   public GraphPluginResources getResource()
   {
      return _graphPluginResources;
   }

   public void removeGraph()
   {
      onRemoveGraph();
   }

   public void showPopupAbove(Point loc, GraphControllerPopupListener graphControllerPopupListener)
   {
      if (_popUp.isVisible())
      {
         _popUp.setVisible(false);
      }
      else
      {
         _popUp.show(_desktopPane,0,0);
         _popUp.setLocation(loc.x, loc.y - _popUp.getHeight());
      }
      _currentGraphControllerPopupListener = graphControllerPopupListener;
   }

   public void hidePopup()
   {
      _popUp.setVisible(false);
   }

   public TableFramesModel getTableFramesModel()
   {
      return _modeManager.getTableFramesModel();
   }

   public void changedFromLinkToLocalCopy()
   {
      createPopUp();
   }
}
