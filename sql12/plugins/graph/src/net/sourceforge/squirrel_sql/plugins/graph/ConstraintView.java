package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ConstraintViewXmlBean;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;


public class ConstraintView implements GraphComponent
{
   private GraphDesktopController _desktopController;

   private boolean _isSelected;

   private JPopupMenu _connectLinePopup;
   private JPopupMenu _foldingPointPopUp;

   private JMenuItem _mnuAddFoldingPoint;
   private JMenuItem _mnuShowDDL;
   private JMenuItem _mnuScriptDDL;
   private JMenuItem _mnuRemoveFoldingPoint;

   private Point _lastPopupClickPoint;

   private ConstraintGraph _constraintGraph = new ConstraintGraph();

   private ConstraintData _constraintData;
   public static final int STUB_LENGTH = 20;
   private ISession _session;
   private TableFrameController _pkFramePointingTo;
   private ConstraintViewListener _constraintViewListener;

   public ConstraintView(ConstraintData constraintData, GraphDesktopController desktopController, ISession session)
   {
      _constraintData = constraintData;
      _desktopController = desktopController;
      _session = session;

      createPopup();
   }

   public ConstraintView(ConstraintViewXmlBean constraintViewXmlBean, GraphDesktopController desktopController, ISession session)
   {
      _desktopController = desktopController;
      _session = session;
      _constraintData = new ConstraintData(constraintViewXmlBean.getConstraintDataXmlBean());
      _constraintGraph = new ConstraintGraph(constraintViewXmlBean.getConstraintGraphXmlBean());

      createPopup();
   }

   public ConstraintViewXmlBean getXmlBean()
   {
      ConstraintViewXmlBean ret = new ConstraintViewXmlBean();

      ret.setConstraintDataXmlBean(_constraintData.getXmlBean());
      ret.setConstraintGraphXmlBean(_constraintGraph.getXmlBean());

      return ret;
   }

   private void createPopup()
   {
      _connectLinePopup = new JPopupMenu();

      _mnuAddFoldingPoint = new JMenuItem("add folding point");
      _mnuAddFoldingPoint.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAddFoldingPoint();
         }
      });
      _connectLinePopup.add(_mnuAddFoldingPoint);

      _mnuShowDDL = new JMenuItem("show DDL");
      _mnuShowDDL.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onShowDDL();
         }
      });
      _connectLinePopup.add(_mnuShowDDL);

      _mnuScriptDDL = new JMenuItem("script DDL");
      _mnuScriptDDL.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onScriptDDL();
         }
      });
      _connectLinePopup.add(_mnuScriptDDL);


      _foldingPointPopUp = new JPopupMenu();

      _mnuRemoveFoldingPoint = new JMenuItem("remove folding point");
      _mnuRemoveFoldingPoint.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRemoveFoldingPoint();
         }
      });
      _foldingPointPopUp.add(_mnuRemoveFoldingPoint);
   }

   private void onRemoveFoldingPoint()
   {
      _constraintGraph.removeHitFoldingPoint();
      _desktopController.repaint();
   }

   private void onScriptDDL()
   {
      String[] lines = _constraintData.getDDL();

      StringBuffer sb = new StringBuffer();
      sb.append('\n');
      for (int i = 0; i < lines.length; i++)
      {
         sb.append(lines[i]).append('\n');
      }
      _session.getSessionSheet().getSQLEntryPanel().appendText(sb.toString());
   }

   private void onShowDDL()
   {
      final String[] lines = _constraintData.getDDL();

      final JInternalFrame ddlFrame = new JInternalFrame(_constraintData.getTitle(), true, true);

      StringBuffer sb = new StringBuffer();
      sb.append(lines[0]);
      for (int i = 1; i < lines.length; i++)
      {
         sb.append('\n').append(lines[i]);
      }

      final JTextPane txtDDL = new JTextPane();
      txtDDL.setText(sb.toString());
      txtDDL.setEditable(false);
      ddlFrame.getContentPane().add(new JScrollPane(txtDDL));

      _desktopController.addFrame(ddlFrame);

      ddlFrame.setBounds(_lastPopupClickPoint.x, _lastPopupClickPoint.y, 20, 20);
      ddlFrame.setVisible(true);

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            recalculateDDLFrameSize(ddlFrame, txtDDL, lines);
         }
      });
   }

   private void recalculateDDLFrameSize(JInternalFrame ddlFrame, JTextPane txtDDL, String[] lines)
   {

      FontMetrics fm = txtDDL.getFontMetrics(txtDDL.getFont());
      int txtHeight = fm.getHeight() * lines.length;

      int txtWidht = 0;
      for (int i = 0; i < lines.length; i++)
      {
         txtWidht = Math.max(txtWidht, fm.stringWidth(lines[i]));
      }

      BasicInternalFrameUI ui = (BasicInternalFrameUI) ddlFrame.getUI();
      int titleHeight = ui.getNorthPane().getHeight();

      ddlFrame.setSize(txtWidht + 20, txtHeight + titleHeight + 20);
   }

   private void onAddFoldingPoint()
   {
      _constraintGraph.addFoldingPointToHitConnectLine(_lastPopupClickPoint);
      _desktopController.repaint();
   }

   public void setConnectionPoints(ConnectionPoints fkPoints, ConnectionPoints pkPoints, TableFrameController pkFramePointingTo, ConstraintViewListener constraintViewListener)
   {
      _pkFramePointingTo = pkFramePointingTo;
      _constraintViewListener = constraintViewListener;

      int fkCenterY = getCenterY(fkPoints.points);
      int pkCenterY = getCenterY(pkPoints.points);

      int signFkStub = fkPoints.pointsAreLeftOfWindow ? -1:1;
      int signPkStub = pkPoints.pointsAreLeftOfWindow ? -1:1;;

      Point fkGatherPoint = new Point(fkPoints.points[0].x + signFkStub*STUB_LENGTH, fkCenterY);
      Point pkGatherPoint = new Point(pkPoints.points[0].x + signPkStub*STUB_LENGTH, pkCenterY);

      GraphLine[] fkStubLines = new GraphLine[fkPoints.points.length];
      for (int i = 0; i < fkPoints.points.length; i++)
      {
         fkStubLines[i] = new GraphLine(fkPoints.points[i], fkGatherPoint);
      }
      _constraintGraph.setFkStubLines(fkStubLines);

      GraphLine[] pkStubLines = new GraphLine[pkPoints.points.length];
      for (int i = 0; i < pkPoints.points.length; i++)
      {
         pkStubLines[i] = new GraphLine(pkPoints.points[i], pkGatherPoint);
      }
      _constraintGraph.setPkStubLines(pkStubLines);

      _constraintGraph.setFkGatherPoint(fkGatherPoint);
      _constraintGraph.setPkGatherPoint(pkGatherPoint);
   }

   public void paint(Graphics g)
   {
      GraphLine[] lines = _constraintGraph.getAllLines();
      for (int i = 0; i < lines.length; i++)
      {
         drawLine(g, lines[i]);
      }

      Vector foldingPoints = _constraintGraph.getFoldingPoints();

      for (int i = 0; i < foldingPoints.size(); i++)
      {
         drawFoldingPoint(g, (Point) foldingPoints.get(i));
      }
   }

   private void drawFoldingPoint(Graphics g, Point fp)
   {
      int rad = 4;
      if (_isSelected)
      {
         rad = 5;
      }

      g.fillOval(fp.x - rad, fp.y -rad, 2*rad, 2*rad);

   }


   private void drawLine(Graphics g, GraphLine line)
   {
      if (_isSelected)
      {
         g.fillPolygon(createPolygon(line.beg.x, line.beg.y, line.end.x, line.end.y, 1));
      }
      else
      {
         g.drawLine(line.beg.x, line.beg.y, line.end.x, line.end.y);
      }

   }

   public Polygon createPolygon(int x1, int y1, int x2, int y2, int halfThickness)
   {
      Polygon ret = new Polygon();

      if (x1 < x2 && y1 < y2)
      {
         ret.addPoint(x1 + halfThickness, y1 - halfThickness);
         ret.addPoint(x1 - halfThickness, y1 + halfThickness);
         ret.addPoint(x2 - halfThickness, y2 + halfThickness);
         ret.addPoint(x2 + halfThickness, y2 - halfThickness);
      }
      else if (x1 > x2 && y1 > y2)
      {
         ret.addPoint(x1 - halfThickness, y1 + halfThickness);
         ret.addPoint(x1 + halfThickness, y1 - halfThickness);
         ret.addPoint(x2 + halfThickness, y2 - halfThickness);
         ret.addPoint(x2 - halfThickness, y2 + halfThickness);
      }
      else
      {
         ret.addPoint(x1 + halfThickness, y1 + halfThickness);
         ret.addPoint(x1 - halfThickness, y1 - halfThickness);
         ret.addPoint(x2 - halfThickness, y2 - halfThickness);
         ret.addPoint(x2 + halfThickness, y2 + halfThickness);
      }

      //System.out.println("("+ x1 + ", " + y1 + ") - (" + x2 + ", " + y2 +")");

      return ret;
   }


   private int getCenterY(Point[] points)
   {
      int ret = 0;
      for (int i = 0; i < points.length; i++)
      {
         ret += points[i].y;
      }

      return ret / points.length;

   }

   public boolean hitMe(MouseEvent e)
   {
      Vector foldingPoints = _constraintGraph.getFoldingPoints();

      int hitDist = 8;
      for (int i = 0; i < foldingPoints.size(); i++)
      {
         Point foldingPoint = (Point) foldingPoints.get(i);
         if(     Math.abs(e.getPoint().x - foldingPoint.x) < hitDist
             &&  Math.abs(e.getPoint().y - foldingPoint.y) < hitDist )
         {
            _constraintGraph.setHitFoldingPoint(foldingPoint);
            return true;
         }
      }


      GraphLine[] lines = _constraintGraph.getConnectLines();
      for (int i = 0; i < lines.length; i++)
      {
         Polygon pg = createPolygon(lines[i].beg.x, lines[i].beg.y, lines[i].end.x, lines[i].end.y, 3);

         if (pg.contains(e.getPoint()))
         {
            _constraintGraph.setHitConnectLine(lines[i]);
            return true;
         }
      }

      return false;
   }

   public void setSelected(boolean b)
   {
      _isSelected = b;
      _desktopController.repaint();
   }

   public boolean isSelected()
   {
      return _isSelected;

   }

   public void setDesktopController(GraphDesktopController desktopController)
   {
      _desktopController = desktopController;

      if(null == desktopController)
      {
         _constraintGraph.removeAllFoldingPoints();
      }
   }

   public boolean equals(Object obj)
   {
      if (obj instanceof ConstraintView)
      {
         return ((ConstraintView) obj)._constraintData.equals(_constraintData);
      }
      else
      {
         return false;
      }
   }

   public int hashCode()
   {
      return _constraintData.hashCode();
   }


   public ConstraintData getData()
   {
      return _constraintData;
   }

   public void mouseClicked(MouseEvent e)
   {
   }

   public void mouseReleased(MouseEvent e)
   {
      maybeShowPopup(e);
   }

   private void maybeShowPopup(MouseEvent e)
   {
      if (e.isPopupTrigger())
      {
         _lastPopupClickPoint = new Point(e.getX(), e.getY());

         if(_constraintGraph.isHitOnConnectLine())
         {
            _connectLinePopup.show(e.getComponent(), e.getX(), e.getY());
         }
         else // hit is on folding point
         {
            _foldingPointPopUp.show(e.getComponent(), e.getX(), e.getY());
         }
      }
   }

   public void mousePressed(MouseEvent e)
   {
      maybeShowPopup(e);
   }

   public void mouseDragged(MouseEvent e)
   {
      if(false == _constraintGraph.isHitOnConnectLine())
      {
         // hit is on folding point
         _constraintGraph.moveLastHitFoldingPointTo(e.getPoint());
         _constraintViewListener.foldingPointMoved(this);
      }
   }

   public Point getFirstFoldingPoint()
   {
      return _constraintGraph.getFirstFoldingPoint();
   }

   public Point getLastFoldingPoint()
   {
      return _constraintGraph.getLastFoldingPoint();
   }

   public TableFrameController getPkFramePointingTo()
   {
      return _pkFramePointingTo;
   }

   public void replaceCopiedColsByReferences(ColumnInfo[] colInfos)
   {
      _constraintData.replaceCopiedColsByReferences(colInfos);
   }
}
