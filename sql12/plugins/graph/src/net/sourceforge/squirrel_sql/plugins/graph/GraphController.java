package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphControllerXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphXmlSerializer;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.TableFrameControllerXmlBean;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import org.apache.xml.dtm.ref.DTMNodeProxy;


public class GraphController
{
   private ISession _session;
   private GraphMainPanelTab _graphPane;
   private GraphDesktopController _desktopController;

   private Vector _openTableFrameCtrls = new Vector();
   private TableFrameControllerListener _tableFrameControllerListener;
   private static final int BORDER_X = ConstraintView.STUB_LENGTH + 10;
   private static final int BORDER_Y = 10;
   private AddTableListener _addTableListener;
   private GraphDesktopListener _graphDesktopListener;
   private GraphPlugin _plugin;
   private GraphXmlSerializer _xmlSerializer;

   public GraphController(ISession session, GraphPlugin plugin, GraphXmlSerializer xmlSerializer)
   {
      _session = session;
      _plugin = plugin;

      _graphDesktopListener = new GraphDesktopListener()
      {
         public void saveGraphRequested()
         {
            saveGraph();
         }

         public void renameRequest(String newName)
         {
            renameGraph(newName);
         }

         public void removeRequest()
         {
            removeGraph();
         }

      };

      _desktopController = new GraphDesktopController(_graphDesktopListener, _session);
      _graphPane = new GraphMainPanelTab(_desktopController);

      if(null == xmlSerializer)
      {
         _xmlSerializer = new GraphXmlSerializer(_plugin, _session, _graphPane, null);
      }
      else
      {
         _xmlSerializer = xmlSerializer;
      }


      _tableFrameControllerListener = new TableFrameControllerListener()
      {
         public void closed(TableFrameController tfc)
         {
            onTableFrameControllerClosed(tfc);
         }
      };

      _addTableListener = new AddTableListener()
      {
         public void addTablesRequest(String[] tablenames)
         {
            onAddTablesRequest(tablenames);
         }
      };

      GraphControllerXmlBean graphControllerXmlBean = null;
      if(null != xmlSerializer)
      {
         graphControllerXmlBean = _xmlSerializer.read();
         _graphPane.setTitle(graphControllerXmlBean.getTitle());
         _desktopController.setShowConstraintNames(graphControllerXmlBean.isShowConstraintNames());
      }
      else
      {
         _graphPane.setTitle(_plugin.patchName(_graphPane.getTitle(), _session));
      }

      _session.getSessionSheet().addMainTab(_graphPane);

      if(null != graphControllerXmlBean)
      {
         TableFrameControllerXmlBean[] tableFrameControllerXmls = graphControllerXmlBean.getTableFrameControllerXmls();
         for (int i = 0; i < tableFrameControllerXmls.length; i++)
         {
            addTableIntern(null, null, tableFrameControllerXmls[i]);

         }

      }


   }

   private void removeGraph()
   {
      _xmlSerializer.remove();
      _session.getSessionSheet().removeMainTab(_graphPane);
      _plugin.removeGraphController(this, _session);
   }

   private void renameGraph(String newName)
   {
      if(newName.equals(_graphPane.getTitle()))
      {
         return;
      }

      newName = _plugin.patchName(newName, _session);
      _xmlSerializer.rename(newName);
      int index = _session.getSessionSheet().removeMainTab(_graphPane);
      _graphPane.setTitle(newName);
      _session.getSessionSheet().insertMainTab(_graphPane, index);
      saveGraph();
   }

   public void saveGraph()
   {
      GraphControllerXmlBean xmlBean = new GraphControllerXmlBean();
      xmlBean.setTitle(_graphPane.getTitle());
      xmlBean.setShowConstraintNames(_desktopController.isShowConstraintNames());

      TableFrameControllerXmlBean[] frameXmls = new TableFrameControllerXmlBean[_openTableFrameCtrls.size()];

      for (int i = 0; i < _openTableFrameCtrls.size(); i++)
      {
         TableFrameController tableFrameController = (TableFrameController) _openTableFrameCtrls.elementAt(i);
         frameXmls[i] = tableFrameController.getXmlBean(); 
      }
      xmlBean.setTableFrameControllerXmls(frameXmls);

      _xmlSerializer.write(xmlBean);
   }




   private void onAddTablesRequest(String[] tablenames)
   {
      Point[] refCascadeIndent = new Point[1];
      for (int i = 0; i < tablenames.length; i++)
      {
         addTableIntern(refCascadeIndent, tablenames[i], null);
      }
   }

   private void onTableFrameControllerClosed(TableFrameController tfc)
   {
      _openTableFrameCtrls.remove(tfc);
      for (int i = 0; i < _openTableFrameCtrls.size(); i++)
      {
         TableFrameController tableFrameController = (TableFrameController) _openTableFrameCtrls.elementAt(i);
         tableFrameController.tableFrameRemoved(tfc);
      }

   }

   public void addTable(ObjectTreeNode selectedNode, final Point[] refCascadeIndent)
   {
      addTableIntern(refCascadeIndent, selectedNode.getDatabaseObjectInfo().getSimpleName(), null);
   }


   private void addTableIntern(final Point[] refCascadeIndent, String tableName, final TableFrameControllerXmlBean xmlBean)
   {


      final TableFrameController tfc;

      if(null == xmlBean)
      {
         tfc = new TableFrameController(_session, _desktopController, _addTableListener, tableName, null);
      }
      else
      {
         tfc = new TableFrameController(_session, _desktopController, _addTableListener, null, xmlBean);
      }

      if (_openTableFrameCtrls.contains(tfc))
      {
         return;
      }


      tfc.addTableFrameControllerListener(_tableFrameControllerListener);
      _desktopController.addFrame(tfc.getFrame());
      _openTableFrameCtrls.add(tfc);

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            initsAfterFrameAdded(tfc, refCascadeIndent, null == xmlBean);
         }
      });

   }

   private void calcPosition(final TableFrameController tfc, Point[] lastCascadePointRef)
   {
      Dimension frmSize = tfc.getFrame().getSize();

      ///////////////////////////////////////////////////////////////////////////
      // We try to find a completely free space for the new table frame.
      for (int y = 0; y < _desktopController.getDesktopPane().getSize().getHeight(); y += frmSize.height + BORDER_Y)
      {
         for (int x = 0; x < _desktopController.getDesktopPane().getSize().getWidth(); x += frmSize.width + BORDER_X)
         {
            Point leftUp = new Point(x, y);
            Point rightDown = new Point(x + frmSize.width, y + frmSize.height);

            if (isInDesktop(leftUp) && isInDesktop(rightDown) && false == isRectangleOccupied(leftUp, rightDown, tfc))
            {
               tfc.getFrame().setBounds(leftUp.x, leftUp.y, tfc.getFrame().getBounds().width, tfc.getFrame().getBounds().height);
               return;
            }
         }
      }
      //
      //////////////////////////////////////////////////////////////////////////


      ////////////////////////////////////////////////////////////////////////////////
      // We try to cascade
      int cascadeIndent = tfc.getFrame().getTitlePane().getSize().height;

      if (null == lastCascadePointRef[0])
      {
         lastCascadePointRef[0] = new Point(cascadeIndent, cascadeIndent);
      }
      else
      {
         lastCascadePointRef[0] = new Point(lastCascadePointRef[0].x + cascadeIndent, lastCascadePointRef[0].y + cascadeIndent);
      }

      for (int x = lastCascadePointRef[0].x; x < _desktopController.getDesktopPane().getSize().getWidth(); x += cascadeIndent)
      {
         for (int y = lastCascadePointRef[0].y; y < _desktopController.getDesktopPane().getSize().getHeight(); y += cascadeIndent)
         {
            Point leftUp = new Point(x, y);
            Point rightDown = new Point(x + frmSize.width, y + frmSize.height);

            if (isInDesktop(leftUp) && isInDesktop(rightDown))
            {
               tfc.getFrame().setBounds(leftUp.x, leftUp.y, tfc.getFrame().getBounds().width, tfc.getFrame().getBounds().height);
               lastCascadePointRef[0].y += cascadeIndent;
               return;
            }
         }
         lastCascadePointRef[0].y = cascadeIndent;
      }
      //
      //////////////////////////////////////////////////////////////////////////////////


      // If we reach here we could not calculate a place to add the new table frame.
      // Now it will automatically be added at (0,0).


   }

   private boolean isRectangleOccupied(Point leftUp, Point rightDown, TableFrameController toExclude)
   {
      for (int i = 0; i < _openTableFrameCtrls.size(); i++)
      {
         TableFrameController tfc = (TableFrameController) _openTableFrameCtrls.elementAt(i);

         if (tfc.equals(toExclude))
         {
            continue;
         }

         Rectangle rectTfc = tfc.getFrame().getBounds();

         Rectangle rectParam = new Rectangle(leftUp.x - BORDER_X, leftUp.y - BORDER_Y, rightDown.x - leftUp.x + BORDER_X, rightDown.y - leftUp.y + BORDER_Y);

         Rectangle2D interSect = rectParam.createIntersection(rectTfc);

         if (0 < interSect.getWidth() && 0 < interSect.getHeight())
         {
            return true;
         }
      }
      return false;

   }


   private boolean isInDesktop(Point p)
   {
      if (_desktopController.getDesktopPane().getSize().width >= p.x && _desktopController.getDesktopPane().getSize().height >= p.y)
      {
         return true;
      }
      return false;
   }

   private void initsAfterFrameAdded(TableFrameController tfc, Point[] refCascadeIndent, boolean resetBounds)
   {
      for (int i = 0; i < _openTableFrameCtrls.size(); i++)
      {
         TableFrameController buf = (TableFrameController) _openTableFrameCtrls.elementAt(i);
         if (false == buf.equals(tfc))
         {
            buf.tableFrameOpen(tfc);
         }
      }

      Vector others = (Vector) _openTableFrameCtrls.clone();
      others.remove(tfc);
      TableFrameController[] othersArr = (TableFrameController[]) others.toArray(new TableFrameController[others.size()]);
      tfc.initAfterAddedToDesktop(othersArr, resetBounds);

      if(resetBounds)
      {
         calcPosition(tfc, refCascadeIndent);
      }

   }

   public String getTitle()
   {
      return _graphPane.getTitle();
   }

   public String toString()
   {
      return getTitle();
   }
}

