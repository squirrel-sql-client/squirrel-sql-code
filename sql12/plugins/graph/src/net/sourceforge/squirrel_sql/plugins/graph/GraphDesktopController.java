package net.sourceforge.squirrel_sql.plugins.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.PrintXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ZoomerXmlBean;


public class GraphDesktopController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(GraphDesktopController.class);


	private GraphDesktopPane _desktopPane;
   private JScrollPane _scrollPane;
   private ConstraintView _lastPressedConstraintView;

   private JPopupMenu _popUp;
   private JMenuItem _mnuSaveGraph;
   private JMenuItem _mnuRenameGraph;
   private JMenuItem _mnuRemoveGraph;
   private JMenuItem _mnuRefreshAllTables;
   private JMenuItem _mnuScriptAllTables;
   private JCheckBoxMenuItem _mnuShowConstraintNames;
   private JCheckBoxMenuItem _mnuZoomPrint;
   private GraphDesktopListener _listener;
   private ISession _session;
   private GraphPlugin _plugin;
   private ZoomPrintController _zoomPrintController;
   private JPanel _graphPanel;
   private JMenuItem _mnuAllTablesDbOrder;
   private JMenuItem _mnuAllTablesByNameOrder;
   private JMenuItem _mnuAllTablesPkConstOrder;


   public GraphDesktopController(GraphDesktopListener listener, ISession session, GraphPlugin plugin)
   {
      _listener = listener;
      _session = session;
      _plugin = plugin;
      _desktopPane = new GraphDesktopPane();
      _desktopPane.setBackground(Color.white);

      _scrollPane = new JScrollPane(_desktopPane);

      _graphPanel = new JPanel(new BorderLayout());
      _graphPanel.add(_scrollPane, BorderLayout.CENTER);


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

   void initZoomer(ZoomerXmlBean zoomerXmlBean, PrintXmlBean printXmlBean)
   {
      EdgesListener edgesListener = new EdgesListener()
      {
         public void edgesGraphComponentChanged(EdgesGraphComponent edgesGraphComponent, boolean put)
         {
            onEdgesGraphComponentChanged(edgesGraphComponent, put);
         }
      };

      _zoomPrintController = new ZoomPrintController(zoomerXmlBean, printXmlBean, edgesListener, _desktopPane, _session, _plugin);
      _graphPanel.add(_zoomPrintController.getPanel(), BorderLayout.SOUTH);
      _mnuZoomPrint.setSelected(_zoomPrintController.getZoomer().isEnabled());
      onZoomPrint();
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

		// i18n[graph.saveGraph=Save graph]
		_mnuSaveGraph = new JMenuItem(s_stringMgr.getString("graph.saveGraph"));
      _mnuSaveGraph.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onSaveGraph();
         }
      });


		// i18n[graph.renameGraph=Rename graph]
		_mnuRenameGraph= new JMenuItem(s_stringMgr.getString("graph.renameGraph"));
      _mnuRenameGraph.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRenameGraph();
         }
      });

		// i18n[graph.removeGraph=Remove graph]
		_mnuRemoveGraph= new JMenuItem(s_stringMgr.getString("graph.removeGraph"));
      _mnuRemoveGraph.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRemoveGraph();
         }
      });


		// i18n[graph.refreshAllTables=Refresh all tables]
		_mnuRefreshAllTables = new JMenuItem(s_stringMgr.getString("graph.refreshAllTables"));
      _mnuRefreshAllTables.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRefreshAllTables();
         }
      });

		// i18n[graph.scriptAllTables=Script all tables]
		_mnuScriptAllTables = new JMenuItem(s_stringMgr.getString("graph.scriptAllTables"));
      _mnuScriptAllTables.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onScriptAllTables();
         }
      });

		// i18n[graph.showConstr=Show constraint names]
		_mnuShowConstraintNames = new JCheckBoxMenuItem(s_stringMgr.getString("graph.showConstr"));
      _mnuShowConstraintNames.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            _desktopPane.repaint();
         }
      });

		// i18n[graph.zoomPrint=Zoom/Print]
		_mnuZoomPrint = new JCheckBoxMenuItem(s_stringMgr.getString("graph.zoomPrint"));
      _mnuZoomPrint.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onZoomPrint();
         }
      });

		_mnuAllTablesDbOrder = new JMenuItem(s_stringMgr.getString("graph.allTablesDbOrderRequested"));
      _mnuAllTablesDbOrder.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAllTablesDbOrder();
         }
      });

		_mnuAllTablesByNameOrder = new JMenuItem(s_stringMgr.getString("graph.allTablesByNameOrderRequested"));
      _mnuAllTablesByNameOrder.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAllTablesByNameOrder();
         }
      });

		_mnuAllTablesPkConstOrder = new JMenuItem(s_stringMgr.getString("graph.allTablesPkConstOrderRequested"));
      _mnuAllTablesPkConstOrder.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAllTablesPkConstOrder();
         }
      });

      _popUp.add(_mnuSaveGraph);
      _popUp.add(_mnuRenameGraph);
      _popUp.add(_mnuRemoveGraph);
      _popUp.add(new JSeparator());
      _popUp.add(_mnuRefreshAllTables);
      _popUp.add(_mnuScriptAllTables);
      _popUp.add(new JSeparator());
      _popUp.add(_mnuAllTablesDbOrder);
      _popUp.add(_mnuAllTablesByNameOrder);
      _popUp.add(_mnuAllTablesPkConstOrder);
      _popUp.add(new JSeparator());
      _popUp.add(_mnuShowConstraintNames);
      _popUp.add(_mnuZoomPrint);
   }

   private void onAllTablesPkConstOrder()
   {
      _listener.allTablesPkConstOrderRequested();
   }

   private void onAllTablesByNameOrder()
   {
      _listener.allTablesByNameOrderRequested();
   }

   private void onAllTablesDbOrder()
   {
      _listener.allTablesDbOrderRequested();
   }

   private void onScriptAllTables()
   {
      _listener.scriptAllTablesRequested();
   }

   private void onRefreshAllTables()
   {
      _listener.refreshAllTablesRequested();
   }

   private void onZoomPrint()
   {
      _zoomPrintController.setVisible(_mnuZoomPrint.isSelected());
   }

   private void onRemoveGraph()
   {
		// i18n[graph.delGraph=Do you really wish to delete this graph?]
		int res = JOptionPane.showConfirmDialog(_session.getApplication().getMainFrame(), s_stringMgr.getString("graph.delGraph"));
      if(res == JOptionPane.YES_OPTION)
      {
         _listener.removeRequest();
      }
   }

   private void onRenameGraph()
   {

		// i18n[graph.newName=Please enter a new name]
		String newName = JOptionPane.showInputDialog(_session.getApplication().getMainFrame(), s_stringMgr.getString("graph.newName"));
      if(null != newName && 0 != newName.trim().length())
      {
         _listener.renameRequest(newName);
      }
   }

   private void onSaveGraph()
   {
      _listener.saveGraphRequested();
   }

   private void maybeShowPopup(MouseEvent e)
   {
      if (e.isPopupTrigger())
      {
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

      ConstraintView hitOne = findHit(e);
      if(null != hitOne)
      {
         hitOne.mouseReleased(e);
      }
      else
      {
         maybeShowPopup(e);
      }
   }

   public void onMousePressed(final MouseEvent e)
   {
      final ConstraintView hitOne = findHit(e);
      if(null != hitOne)
      {
         _lastPressedConstraintView = hitOne;

         if(InputEvent.BUTTON3_MASK == e.getModifiers())
         {
            refreshSelection(hitOne, false);
            SwingUtilities.invokeLater(new Runnable()
            {
               public void run()
               {
                  hitOne.mousePressed(e);
               }
            });
         }
         else
         {
            hitOne.mousePressed(e);
         }
      }
      else
      {
         maybeShowPopup(e);
      }
   }

   public void onMouseClicked(final MouseEvent e)
   {
      final ConstraintView hitOne = findHit(e);

      if(null != hitOne)
      {
         refreshSelection(hitOne, InputEvent.BUTTON1_MASK == e.getModifiers() );
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               hitOne.mouseClicked(e);
            }
         });
      }
   }

   private void onMouseDragged(MouseEvent e)
   {
      if(null != _lastPressedConstraintView)
      {
         _lastPressedConstraintView.mouseDragged(e);
      }
   }

   private ConstraintView findHit(MouseEvent e)
   {
      Vector<GraphComponent> graphComponents = _desktopPane.getGraphComponents();


      for (int i = 0; i < graphComponents.size(); i++)
      {
         GraphComponent graphComponent = graphComponents.elementAt(i);

         if(graphComponent instanceof ConstraintView)
         {
            ConstraintView constraintView = (ConstraintView)graphComponents.elementAt(i);
            if(constraintView.hitMe(e))
            {
               return constraintView;
            }
         }
      }
      return null;
   }


   public void repaint()
   {
      _desktopPane.repaint();
   }

   public Component getGraphPanel()
   {
      return _graphPanel;
   }

   public void addFrame(JInternalFrame frame)
   {
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
      return _zoomPrintController.getZoomer();
   }

   public ZoomPrintController getZoomPrintController()
   {
      return _zoomPrintController;
   }

   public void sessionEnding()
   {
      _zoomPrintController.sessionEnding();
   }
}
