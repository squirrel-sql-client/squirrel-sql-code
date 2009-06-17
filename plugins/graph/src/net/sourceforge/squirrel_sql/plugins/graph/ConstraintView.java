package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ConstraintViewXmlBean;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;


public class ConstraintView implements GraphComponent
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ConstraintView.class);

	private GraphDesktopController _desktopController;

   private boolean _isSelected;

   private JPopupMenu _connectLinePopup;
   private JPopupMenu _foldingPointPopUp;

   private JMenuItem _mnuAddFoldingPoint;
   private JMenuItem _mnuShowDDL;
   private JMenuItem _mnuScriptDDL;
   private JCheckBoxMenuItem _mnuShowThisConstraintName;
   private JMenuItem _mnuRemoveNonDbConstraint;
   private JMenuItem _mnuConfigureNonDbConstraint;
   private JMenuItem _mnuRemoveFoldingPoint;

   private Point _lastPopupClickPoint;

   private ConstraintGraph _constraintGraph = new ConstraintGraph();

   private ConstraintData _constraintData;
   public static final int STUB_LENGTH = 20;
   private ISession _session;
   private TableFrameController _fkFrameOriginatingFrom;
   private TableFrameController _pkFramePointingTo;
   private Vector<ConstraintViewListener> _constraintViewListeners = new Vector<ConstraintViewListener>();

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
      _constraintGraph = new ConstraintGraph(constraintViewXmlBean.getConstraintGraphXmlBean(), _desktopController.getZoomer());

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

		// i18n[graph.addFoldingPoint=add folding point]
		_mnuAddFoldingPoint = new JMenuItem(s_stringMgr.getString("graph.addFoldingPoint"));
      _mnuAddFoldingPoint.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAddFoldingPoint();
         }
      });
      _connectLinePopup.add(_mnuAddFoldingPoint);

      // i18n[graph.showddl=show DDL]
		_mnuShowDDL = new JMenuItem(s_stringMgr.getString("graph.showddl"));
      _mnuShowDDL.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onShowDDL();
         }
      });
      _connectLinePopup.add(_mnuShowDDL);

		// i18n[graph.scriptdd=script DDL]
		_mnuScriptDDL = new JMenuItem(s_stringMgr.getString("graph.scriptdd"));
      _mnuScriptDDL.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onScriptDDL();
         }
      });
      _connectLinePopup.add(_mnuScriptDDL);

		// i18n[graph.showThisConstraintName=show this constraint name]
		_mnuShowThisConstraintName = new JCheckBoxMenuItem(s_stringMgr.getString("graph.showThisConstraintName"));
      _mnuShowThisConstraintName.setSelected(_constraintData.isShowThisConstraintName());
      _mnuShowThisConstraintName.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onShowThisConstraintName();
         }
      });
      _connectLinePopup.add(_mnuShowThisConstraintName);

      if (_constraintData.isNonDbConstraint())
      {
         // i18n[graph.removeNonDbConstraint=remove non DB constraint]
         _mnuRemoveNonDbConstraint = new JMenuItem(s_stringMgr.getString("graph.removeNonDbConstraint"));
         _mnuRemoveNonDbConstraint.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               onRemoveNonDbConstraint();
            }
         });
         _connectLinePopup.add(_mnuRemoveNonDbConstraint);

         // i18n[graph.removeNonDbConstraint=remove non DB constraint]
         _mnuConfigureNonDbConstraint = new JMenuItem(s_stringMgr.getString("graph.configureNonDbConstraint"));
         _mnuConfigureNonDbConstraint.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               onConfigureNonDbConstraint();
            }
         });
         _connectLinePopup.add(_mnuConfigureNonDbConstraint);
      }


      _foldingPointPopUp = new JPopupMenu();

		// i18n[graph.removeFoldingPoint=remove folding point]
		_mnuRemoveFoldingPoint = new JMenuItem(s_stringMgr.getString("graph.removeFoldingPoint"));
      _mnuRemoveFoldingPoint.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRemoveFoldingPoint();
         }
      });
      _foldingPointPopUp.add(_mnuRemoveFoldingPoint);
   }

   private void onShowThisConstraintName()
   {
      _constraintData.setShowThisConstraintName(_mnuShowThisConstraintName.isSelected());
      _desktopController.repaint();
   }

   private void onConfigureNonDbConstraint()
   {
      new ConfigureNonDbConstraintController(_session, this, _fkFrameOriginatingFrom, _pkFramePointingTo);
      _fkFrameOriginatingFrom.refresh();
   }

   private void onRemoveNonDbConstraint()
   {
      ConstraintViewListener[] clone = (ConstraintViewListener[]) _constraintViewListeners.toArray(new ConstraintViewListener[0]);

      for (ConstraintViewListener listener : clone)
      {
         listener.removeNonDbConstraint(this);
      }
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
      double zoom = _desktopController.getZoomer().getZoom();
      Point backTransformedPoint = new Point((int)(_lastPopupClickPoint.x/zoom+0.5), (int)(_lastPopupClickPoint.y/zoom+0.5));

      _constraintGraph.addFoldingPointToHitConnectLine(new FoldingPoint(backTransformedPoint, _desktopController.getZoomer()));
      _desktopController.repaint();
   }

   public void setConnectionPoints(ConnectionPoints fkPoints, ConnectionPoints pkPoints, TableFrameController fkFrameOriginatingFrom, TableFrameController pkFramePointingTo,  ConstraintViewListener constraintViewListener)
   {
      double zoom = 1;

      if(null != _desktopController.getZoomer())
      {
         zoom = _desktopController.getZoomer().getZoom();
      }

      _fkFrameOriginatingFrom = fkFrameOriginatingFrom;
      _pkFramePointingTo = pkFramePointingTo;
      addConstraintViewListener(constraintViewListener);

      int fkCenterY = getCenterY(fkPoints.points);
      int pkCenterY = getCenterY(pkPoints.points);

      int signFkStub = fkPoints.pointsAreLeftOfWindow ? -1 : 1;
      int signPkStub = pkPoints.pointsAreLeftOfWindow ? -1 : 1;

      Point fkGatherPoint = new Point((int) (fkPoints.points[0].x + signFkStub * STUB_LENGTH * zoom + 0.5), fkCenterY);
      Point pkGatherPoint = new Point((int) (pkPoints.points[0].x + signPkStub * STUB_LENGTH * zoom + 0.5), pkCenterY);

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

   public void paint(Graphics g, boolean isPrinting)
   {
      Color colBuf = g.getColor();

      if(_constraintData.isNonDbConstraint())
      {
         g.setColor(Color.blue);
      }
      else
      {
         g.setColor(Color.black);
      }

      GraphLine[] lines = _constraintGraph.getAllLines();
      for (int i = 0; i < lines.length; i++)
      {
         drawLine(g, lines[i]);
      }

      GraphLine[] linesToArrow = _constraintGraph.getLinesToArrow();

      for (int i = 0; i < linesToArrow.length; i++)
      {
         paintArrow(g, linesToArrow[i].getEnd().x, linesToArrow[i].getEnd().y, linesToArrow[i].getBegin().x, linesToArrow[i].getBegin().y);
      }

      if(_desktopController.isShowConstraintNames() || _constraintData.isShowThisConstraintName())
      {
         GraphLine mainLine = _constraintGraph.getMainLine();
         drawConstraintNameOnLine(g, mainLine);
      }

      Vector<FoldingPoint> foldingPoints = _constraintGraph.getFoldingPoints();

      if(false == isPrinting)
      {
         for (int i = 0; i < foldingPoints.size(); i++)
         {
            drawFoldingPoint(g, foldingPoints.get(i));
         }
      }

      g.setColor(colBuf);
   }

   private void drawConstraintNameOnLine(Graphics g, GraphLine line)
   {
      Graphics2D g2d = (Graphics2D) g;
      AffineTransform origTrans = g2d.getTransform();

      try
      {
         double zoom = _desktopController.getZoomer().getZoom();
         StringBuffer drawText = new StringBuffer(_constraintData.getConstraintName());

//         if(line.begIsFoldingPoint || line.endIsFoldingPoint)
//         {
//            line = new GraphLine(line, zoom);
//         }

         int lineLen = (int) Math.sqrt((line.getBegin().x - line.getEnd().x) * (line.getBegin().x - line.getEnd().x) + (line.getBegin().y - line.getEnd().y) * (line.getBegin().y - line.getEnd().y));

         FontMetrics fontMetrics = g2d.getFontMetrics(g2d.getFont());
         while (lineLen < (fontMetrics.stringWidth(drawText.toString()) * zoom + 0.5))
         {
            if (0 == drawText.length())
            {
               break;
            }
            drawText.setLength(drawText.length() - 1);
         }

         Point right;
         Point left;

         if (line.getBegin().x > line.getEnd().x)
         {
            right = line.getBegin();
            left = line.getEnd();
         }
         else if (line.getBegin().x < line.getEnd().x)
         {
            right = line.getEnd();
            left = line.getBegin();
         }
         else
         {
            if (line.getBegin().y < line.getEnd().y)
            {
               right = line.getEnd();
               left = line.getBegin();
            }
            else
            {
               right = line.getBegin();
               left = line.getEnd();
            }
         }

         double angle;

         if (0 != right.x - left.x)
         {
            angle = Math.atan((double) (right.y - left.y) / (double) (right.x - left.x));
         }
         else
         {
            angle = Math.PI / 2;
         }


         AffineTransform at = new AffineTransform();
         at.setToRotation(angle);
         at.scale(zoom, zoom);
         g2d.transform(at);

         Point invTransBeg = (Point) at.inverseTransform(left, new Point());
         g2d.drawString(drawText.toString(), invTransBeg.x, invTransBeg.y);

         g2d.setTransform(origTrans);
      }
      catch (NoninvertibleTransformException e)
      {
         throw new RuntimeException(e);
      }
      finally
      {
         g2d.setTransform(origTrans);
      }
   }

   public Dimension getRequiredSize()
   {
      Dimension ret = new Dimension();
      for (int i = 0; i < _constraintGraph.getFoldingPoints().size(); i++)
      {
         FoldingPoint fp = _constraintGraph.getFoldingPoints().get(i);

         if (fp.getZoomedPoint().x > ret.width)
         {
            ret.width = fp.getZoomedPoint().x;
         }

         if (fp.getZoomedPoint().y > ret.height)
         {
            ret.height = fp.getZoomedPoint().y;
         }
      }

      ret.width += 5;
      ret.height += 5;

      return ret;
   }

   private void paintArrow(Graphics g, int x1, int y1, int x2, int y2)
   {
      double zoom = _desktopController.getZoomer().getZoom();

      // defines the opening angle of the arrow (not rad or so but something fancy)
      double sAng = 0.5;

      Point c = new Point(x2, y2);
      Point a = new Point((int) (x1 + sAng * (y2 - y1)), (int) (y1 - sAng * (x2 - x1)));
      Point b = new Point((int) (x1 - sAng * (y2 - y1)), (int) (y1 + sAng * (x2 - x1)));

      // defines the size of the arrow
      double sLen = 10 / Math.sqrt((a.x - c.x) * (a.x - c.x) + (a.y - c.y) * (a.y - c.y)) * zoom;

      Point arrPa = new Point((int) (c.x + sLen * (a.x - c.x)), (int) (c.y + sLen * (a.y - c.y)));
      Point arrPb = new Point((int) (c.x + sLen * (b.x - c.x)), (int) (c.y + sLen * (b.y - c.y)));


      Polygon pg = new Polygon();
      pg.addPoint(arrPa.x, arrPa.y);
      pg.addPoint(arrPb.x, arrPb.y);
      pg.addPoint(c.x, c.y);
      g.fillPolygon(pg);
   }


   private void drawFoldingPoint(Graphics g, FoldingPoint fp)
   {
      int rad = 4;
      if (_isSelected)
      {
         rad = 5;
      }

      //double zoom = _desktopController.getZoomer().getZoom();

      //g.fillOval((int)(zoom*fp.x + 0.5) - rad, (int)(zoom*fp.y+0.5) - rad, 2 * rad, 2 * rad);
      g.fillOval((int)(fp.getZoomedPoint().x + 0.5) - rad, (int)(fp.getZoomedPoint().y+0.5) - rad, 2 * rad, 2 * rad);

   }


   private void drawLine(Graphics g, GraphLine line)
   {
      if (_isSelected)
      {
         g.fillPolygon(createPolygon(line.getBegin().x, line.getBegin().y, line.getEnd().x, line.getEnd().y, 1));
//         if(line.begIsFoldingPoint && line.endIsFoldingPoint)
//         {
//            g.fillPolygon(createPolygon((int)(zoom*line.getBegin().x + 0.5), (int)(zoom*line.getBegin().y + 0.5), (int)(zoom*line.getEnd().x + 0.5), (int)(zoom*line.getEnd().y + 0.5), 1));
//         }
//         else if(line.begIsFoldingPoint && false ==line.endIsFoldingPoint)
//         {
//            g.fillPolygon(createPolygon((int)(zoom*line.getBegin().x + 0.5), (int)(zoom*line.getBegin().y + 0.5), line.getEnd().x, line.getEnd().y, 1));
//         }
//         else if(false == line.begIsFoldingPoint && line.endIsFoldingPoint)
//         {
//            g.fillPolygon(createPolygon(line.getBegin().x, line.getBegin().y, (int)(zoom*line.getEnd().x + 0.5), (int)(zoom*line.getEnd().y + 0.5), 1));
//         }
//         else
//         {
//            g.fillPolygon(createPolygon(line.getBegin().x, line.getBegin().y, line.getEnd().x, line.getEnd().y, 1));
//         }
      }
      else
      {
           g.drawLine(line.getBegin().x, line.getBegin().y, line.getEnd().x, line.getEnd().y);

//         if(line.begIsFoldingPoint && line.endIsFoldingPoint)
//         {
//            g.drawLine((int)(zoom*line.getBegin().x + 0.5), (int)(zoom*line.getBegin().y + 0.5), (int)(zoom*line.getEnd().x + 0.5), (int)(zoom*line.getEnd().y + 0.5));
//         }
//         else if(line.begIsFoldingPoint && false ==line.endIsFoldingPoint)
//         {
//            g.drawLine((int)(line.getBegin().x + 0.5), (int)(line.getBegin().y + 0.5), line.getEnd().x, line.getEnd().y);
//         }
//         else if(false == line.begIsFoldingPoint && line.endIsFoldingPoint)
//         {
//            g.drawLine(line.getBegin().x, line.getBegin().y, (int)(line.getEnd().x + 0.5), (int)(line.getEnd().y + 0.5));
//         }
//         else
//         {
//            g.drawLine(line.getBegin().x, line.getBegin().y, line.getEnd().x, line.getEnd().y);
//         }
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
      Vector<FoldingPoint> foldingPoints = _constraintGraph.getFoldingPoints();


      int hitDist = 8;
      for (int i = 0; i < foldingPoints.size(); i++)
      {
         FoldingPoint foldingPoint = foldingPoints.get(i);
         if (Math.abs(e.getPoint().x - foldingPoint.getZoomedPoint().x) < hitDist
            && Math.abs(e.getPoint().y - foldingPoint.getZoomedPoint().y) < hitDist)
         {
            _constraintGraph.setHitFoldingPoint(foldingPoint);
            return true;
         }
      }


      GraphLine[] lines = _constraintGraph.getConnectLines();
      for (int i = 0; i < lines.length; i++)
      {
         Polygon pg = createPolygon(lines[i].getBegin().x, lines[i].getBegin().y, lines[i].getEnd().x, lines[i].getEnd().y, 3);

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

   public void removeAllFoldingPoints()
   {
      _constraintGraph.removeAllFoldingPoints();
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

   @SuppressWarnings("unused")
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

         if (_constraintGraph.isHitOnConnectLine())
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
      if (false == _constraintGraph.isHitOnConnectLine())
      {
         double zoom = _desktopController.getZoomer().getZoom();

         // hit is on folding point
         Point backTransformedPoint = e.getPoint();
         backTransformedPoint.x = (int)(backTransformedPoint.x/zoom +0.5);
         backTransformedPoint.y = (int)(backTransformedPoint.y/zoom +0.5);
         //_constraintGraph.moveLastHitFoldingPointTo(point);

         _constraintGraph.moveLastHitFoldingPointTo(new FoldingPoint(backTransformedPoint, _desktopController.getZoomer()));


         ConstraintViewListener[] listeners = 
             _constraintViewListeners.toArray(new ConstraintViewListener[_constraintViewListeners.size()]);
         for (int i = 0; i < listeners.length; i++)
         {
            listeners[i].foldingPointMoved(this);
         }
      }
   }

   public FoldingPoint getFirstFoldingPoint()
   {
      return _constraintGraph.getFirstFoldingPoint();
   }

   public FoldingPoint getLastFoldingPoint()
   {
      return _constraintGraph.getLastFoldingPoint();
   }

   public TableFrameController getPkFramePointingTo()
   {
      return _pkFramePointingTo;
   }

   public void replaceCopiedColsByReferences(ColumnInfo[] colInfos, boolean retainImportData)
   {
      _constraintData.replaceCopiedColsByReferences(colInfos, retainImportData);
   }

   public void addConstraintViewListener(ConstraintViewListener constraintViewListener)
   {
      _constraintViewListeners.remove(constraintViewListener);
      _constraintViewListeners.add(constraintViewListener);
   }

   public void setData(ConstraintData constraintData)
   {
      _constraintData = constraintData;
   }

   public void clearColumnImportData()
   {
      _constraintData.clearColumnImportData();
   }

   public boolean hasOverlap(ConstraintView other)
   {
      return _constraintData.hasOverlap(other._constraintData);
   }
}
