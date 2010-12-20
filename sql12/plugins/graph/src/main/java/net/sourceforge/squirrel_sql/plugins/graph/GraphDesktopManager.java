package net.sourceforge.squirrel_sql.plugins.graph;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;

public class GraphDesktopManager extends DefaultDesktopManager
{
   private GraphDesktopPane _graphDesktopPane;
   private HashSet<FoldingPoint> _uniqueFoldingPointsBuffer = new HashSet<FoldingPoint>();

   public GraphDesktopManager(GraphDesktopPane graphDesktopPane)
   {
      _graphDesktopPane = graphDesktopPane;
   }

   @Override
   public void dragFrame(JComponent f, int newX, int newY)
   {
      int correctX = newX;
      int correctY = newY;
      if (f instanceof TableFrame)
      {
         TableFrame tf = (TableFrame) f;


         Point correctDelta = checkBounds(newX - f.getX(), newY - f.getY(), _graphDesktopPane);
         correctX = f.getX() + correctDelta.x;
         correctY = f.getY() + correctDelta.y;

         tolerantlyCheckGroupDissolve(tf, correctDelta);

         for (TableFrame current : _graphDesktopPane.getGroupFrames())
         {
            if (current != tf)
            {
               Point newLocation = new Point(current.getX() + correctDelta.x, current.getY() + correctDelta.y);
               current.setLocation(newLocation);
            }
         }
         moveFoldingPoints(_graphDesktopPane.getGroupFrames(), correctDelta);
      }
      super.dragFrame(f, correctX, correctY);

      _graphDesktopPane.repaint(); // Needed in case tables are moved that don't have any constraints.
   }

   private void tolerantlyCheckGroupDissolve(TableFrame tf, Point correctDelta)
   {
      double tolerance = 0.005;

      if (     ((double)correctDelta.x) / ((_graphDesktopPane.getWidth())) > tolerance
            || ((double)correctDelta.y) / ((_graphDesktopPane.getHeight())) > tolerance)
      {
         if (!_graphDesktopPane.isGroupFrame(tf))
         {
            _graphDesktopPane.setGroupFrame(tf);
         }
      }
   }

   private void moveFoldingPoints(List<TableFrame> movedTableFrames, Point delta)
   {
      for (TableFrame f1 : movedTableFrames)
      {
         for (TableFrame f2 : movedTableFrames)
         {
            if(f1 != f2)
            {
               fillFoldingPointsBetween(_uniqueFoldingPointsBuffer, f1, f2);
            }
         }
      }

      for (FoldingPoint fp : _uniqueFoldingPointsBuffer)
      {
         fp.moveBy(delta);
      }
      _uniqueFoldingPointsBuffer.clear();
   }

   private void fillFoldingPointsBetween(HashSet<FoldingPoint> toFill, TableFrame f1, TableFrame f2)
   {
      Vector<GraphComponent> graphComponents = _graphDesktopPane.getGraphComponents();

      for (GraphComponent graphComponent : graphComponents)
      {
         if(graphComponent instanceof ConstraintView)
         {
            ConstraintView cv = (ConstraintView) graphComponent;

            if(0 < cv.getFoldingPoints().size() && cv.isAttachedTo(f1) && cv.isAttachedTo(f2))
            {
               Vector<FoldingPoint> pointVector = cv.getFoldingPoints();

               for (FoldingPoint foldingPoint : pointVector)
               {
                  toFill.add(foldingPoint);
               }
            }
         }
      }
   }

   @Override
   public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight)
   {
      if (f instanceof TableFrame)
      {
         TableFrame tf = (TableFrame) f;

         if (!_graphDesktopPane.isGroupFrame(tf))
         {
            _graphDesktopPane.setGroupFrame(tf);
         }

         Point delta = new Point(newX - f.getX(), newY - f.getY());
         for (JInternalFrame current : _graphDesktopPane.getGroupFrames())
         {
            if (current != f)
            {
               current.setBounds(current.getX() + delta.x, current.getY() + delta.y, newWidth, newHeight);
            }
         }
      }

      super.resizeFrame(f, newX, newY, newWidth, newHeight);
   }

   private Point checkBounds(int deltaX, int deltaY, GraphDesktopPane desktopPane)
   {
      Point rc = new Point(deltaX, deltaY);
      for (TableFrame current : desktopPane.getGroupFrames())
      {
         Point newLocation = new Point(current.getX() + deltaX, current.getY() + deltaY);
         if (newLocation.x < 0 && rc.x < -current.getX())
         {
            rc.x = -current.getX();
         }
         if (newLocation.y < 0 && rc.y < -current.getY())
         {
            rc.y = -current.getY();
         }
      }
      return rc;
   }
}
