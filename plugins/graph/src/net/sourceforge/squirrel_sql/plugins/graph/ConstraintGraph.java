package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ConstraintGraphXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.FoldingPointXmlBean;

import java.awt.*;
import java.util.Vector;
import java.util.Arrays;


public class ConstraintGraph
{
   private GraphLine[] _fkStubLines;
   private GraphLine[] _pkStubLines;
   private Point _fkGatherPoint;
   private Point _pkGatherPoint;

   private Vector _foldingPoints = new Vector();
   private GraphLine _hitConnectLine;
   private boolean _isHitOnConnectLine;
   private Point _hitFoldingPoint;

   public ConstraintGraph()
   {
   }

   public ConstraintGraph(ConstraintGraphXmlBean constraintGraphXmlBean)
   {
      for (int i = 0; i < constraintGraphXmlBean.getFoldingPointXmlBeans().length; i++)
      {
         Point p = new Point();
         p.x = constraintGraphXmlBean.getFoldingPointXmlBeans()[i].getX();
         p.y = constraintGraphXmlBean.getFoldingPointXmlBeans()[i].getY();
         _foldingPoints.add(p);
      }
   }


   public ConstraintGraphXmlBean getXmlBean()
   {
      ConstraintGraphXmlBean ret = new ConstraintGraphXmlBean();
      FoldingPointXmlBean[] foldPointXmlBeans = new FoldingPointXmlBean[_foldingPoints.size()];
      for (int i = 0; i < _foldingPoints.size(); i++)
      {
         Point point = (Point) _foldingPoints.elementAt(i);
         foldPointXmlBeans[i] = new FoldingPointXmlBean();
         foldPointXmlBeans[i].setX(point.x);
         foldPointXmlBeans[i].setY(point.y);
      }
      ret.setFoldingPointXmlBeans(foldPointXmlBeans);

      return ret;

   }


   public void setFkStubLines(GraphLine[] fkStubLines)
   {
      _fkStubLines = fkStubLines;
   }

   public void setPkStubLines(GraphLine[] pkStubLines)
   {
      _pkStubLines = pkStubLines;
   }

   public void setFkGatherPoint(Point fkGatherPoint)
   {
      _fkGatherPoint = fkGatherPoint;
   }

   public void setPkGatherPoint(Point pkGatherPoint)
   {
      _pkGatherPoint = pkGatherPoint;
   }


   public GraphLine[] getAllLines()
   {
      Vector ret = new Vector();

      ret.addAll(Arrays.asList(_fkStubLines));
      ret.addAll(Arrays.asList(getConnectLines()));
      ret.addAll(Arrays.asList(_pkStubLines));

      return (GraphLine[]) ret.toArray(new GraphLine[ret.size()]);
   }

   public GraphLine[] getLinesToArrow()
   {
      return _pkStubLines;
   }

   public GraphLine[] getConnectLines()
   {
      if(0 == _foldingPoints.size())
      {
         return new GraphLine[]{new GraphLine(_fkGatherPoint, _pkGatherPoint)};
      }

      GraphLine[] ret = new GraphLine[_foldingPoints.size() + 1];

      ret[0] = new GraphLine(_fkGatherPoint, (Point) _foldingPoints.get(0));

      for (int i = 0; i < _foldingPoints.size() - 1; i++)
      {
         ret[i + 1] = new GraphLine((Point) _foldingPoints.get(i), (Point) _foldingPoints.get(i+1));
      }

      ret[ret.length -1] = new GraphLine((Point) _foldingPoints.lastElement(), _pkGatherPoint);

      return ret;

   }

   public void setHitConnectLine(GraphLine line)
   {
      _hitConnectLine = line;
      _isHitOnConnectLine = true;
   }

   public void addFoldingPointToHitConnectLine(Point lastPopupClickPoint)
   {
      if(_fkGatherPoint.equals(_hitConnectLine.beg))
      {
         _foldingPoints.insertElementAt(lastPopupClickPoint, 0);
         return;
      }

      for (int i = 0; i < _foldingPoints.size() - 1; i++)
      {
         if(_hitConnectLine.beg.equals(_foldingPoints.get(i)))
         {
            _foldingPoints.insertElementAt(lastPopupClickPoint, i+1);
            return;
         }
      }

      _foldingPoints.add(lastPopupClickPoint);

   }

   public Vector getFoldingPoints()
   {
      return _foldingPoints;
   }

   public void setHitFoldingPoint(Point foldingPoint)
   {
      _isHitOnConnectLine = false;
      _hitFoldingPoint = foldingPoint;
   }

   public boolean isHitOnConnectLine()
   {
      return _isHitOnConnectLine;
   }

   public void removeHitFoldingPoint()
   {
      _foldingPoints.remove(_hitFoldingPoint);
   }

   public void moveLastHitFoldingPointTo(Point point)
   {
      if(point.x < 0)
      {
         point.x = 0;
      }
      if(point.y < 0)
      {
         point.y = 0;
      }


      _foldingPoints.indexOf(_hitFoldingPoint);
      _foldingPoints.set(_foldingPoints.indexOf(_hitFoldingPoint), point);
      _hitFoldingPoint = point;
   }

   public void removeAllFoldingPoints()
   {
      _foldingPoints.clear();
   }

   public Point getFirstFoldingPoint()
   {
      if(0 == _foldingPoints.size())
      {
         return null;
      }
      else
      {
         return (Point) _foldingPoints.get(0);
      }
   }

   public Point getLastFoldingPoint()
   {
      if(0 == _foldingPoints.size())
      {
         return null;
      }
      else
      {
         return (Point) _foldingPoints.get(_foldingPoints.size()-1);
      }

   }

}
