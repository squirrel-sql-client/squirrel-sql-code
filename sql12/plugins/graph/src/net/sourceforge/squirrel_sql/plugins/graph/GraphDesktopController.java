package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ZoomerXmlBean;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;


public class GraphDesktopController
{

   private GraphDesktopPane _desktopPane;
   private JScrollPane _scrollPane;
   private ConstraintView _lastPressedConstraintView;

   private JPopupMenu _popUp;
   private JMenuItem _mnuSaveGraph;
   private JMenuItem _mnuRenameGraph;
   private JMenuItem _mnuRemoveGraph;
   private JCheckBoxMenuItem _mnuShowConstraintNames;
   private JCheckBoxMenuItem _mnuZoomPrint;
   private GraphDesktopListener _listener;
   private ISession _session;
   private ZoomPrintController _zoomPrintController;
   private JPanel _graphPanel;


   public GraphDesktopController(GraphDesktopListener listener, ISession session)
   {
      _listener = listener;
      _session = session;
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

   void initZoomer(ZoomerXmlBean zoomerXmlBean)
   {
      _zoomPrintController = new ZoomPrintController(zoomerXmlBean);
      _graphPanel.add(_zoomPrintController.getPanel(), BorderLayout.SOUTH);
      _mnuZoomPrint.setSelected(_zoomPrintController.getZoomer().isEnabled());
      onZoomPrint();
   }

   private void createPopUp()
   {
      _popUp = new JPopupMenu();

      _mnuSaveGraph = new JMenuItem("Save graph");
      _mnuSaveGraph.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onSaveGraph();
         }
      });


      _mnuRenameGraph= new JMenuItem("Rename graph");
      _mnuRenameGraph.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRenameGraph();
         }
      });

      _mnuRemoveGraph= new JMenuItem("Remove graph");
      _mnuRemoveGraph.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRemoveGraph();
         }
      });

      _mnuShowConstraintNames = new JCheckBoxMenuItem("Show constraint names");
      _mnuShowConstraintNames.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            _desktopPane.repaint();
         }
      });

      _mnuZoomPrint = new JCheckBoxMenuItem("Zoom/Print");
      _mnuZoomPrint.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onZoomPrint();
         }
      });

      _popUp.add(_mnuSaveGraph);
      _popUp.add(_mnuRenameGraph);
      _popUp.add(_mnuRemoveGraph);
      _popUp.add(_mnuShowConstraintNames);
      _popUp.add(_mnuZoomPrint);
   }

   private void onZoomPrint()
   {
      _zoomPrintController.setVisible(_mnuZoomPrint.isSelected());
   }

   private void onRemoveGraph()
   {
      int res = JOptionPane.showConfirmDialog(_session.getApplication().getMainFrame(), "Do you really wish to delete this graph?");
      if(res == JOptionPane.YES_OPTION)
      {
         _listener.removeRequest();
      }
   }

   private void onRenameGraph()
   {
      String newName = JOptionPane.showInputDialog(_session.getApplication().getMainFrame(), "Please enter a new name");
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

      for (int i = 0; i < constraintViews.length; i++)
      {
         constraintViews[i].setDesktopController(this);
      }
   }

   public void removeConstraintViews(ConstraintView[] constraintViews)
   {
      _desktopPane.removeGraphComponents(constraintViews);

      for (int i = 0; i < constraintViews.length; i++)
      {
         constraintViews[i].setDesktopController(null);
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

      Vector graphComponents = _desktopPane.getGraphComponents();

      for (int i = 0; i < graphComponents.size(); i++)
      {
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
      Vector graphComponents = _desktopPane.getGraphComponents();


      for (int i = 0; i < graphComponents.size(); i++)
      {
         ConstraintView constraintView = (ConstraintView) graphComponents.elementAt(i);
         if(constraintView.hitMe(e))
         {
            return constraintView;
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
}
