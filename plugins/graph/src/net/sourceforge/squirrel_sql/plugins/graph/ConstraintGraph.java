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

   private Vector<FoldingPoint> _foldingPoints = new Vector<FoldingPoint>();
   private GraphLine _hitConnectLine;
   private boolean _isHitOnConnectLine;
   private FoldingPoint _hitFoldingPoint;

   public ConstraintGraph()
   {
   }

   public ConstraintGraph(ConstraintGraphXmlBean constraintGraphXmlBean, Zoomer zoomer)
   {
      for (int i = 0; i < constraintGraphXmlBean.getFoldingPointXmlBeans().length; i++)
      {
         Point p = new Point();
         p.x = constraintGraphXmlBean.getFoldingPointXmlBeans()[i].getX();
         p.y = constraintGraphXmlBean.getFoldingPointXmlBeans()[i].getY();
         _foldingPoints.add(new FoldingPoint(p, zoomer));
      }
   }


   public ConstraintGraphXmlBean getXmlBean()
   {
      ConstraintGraphXmlBean ret = new ConstraintGraphXmlBean();
      FoldingPointXmlBean[] foldPointXmlBeans = new FoldingPointXmlBean[_foldingPoints.size()];
      for (int i = 0; i < _foldingPoints.size(); i++)
      {
         FoldingPoint point = _foldingPoints.elementAt(i);
         foldPointXmlBeans[i] = new FoldingPointXmlBean();
         foldPointXmlBeans[i].setX(point.getUnZoomedPoint().x);
         foldPointXmlBeans[i].setY(point.getUnZoomedPoint().y);
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
      Vector<GraphLine> ret = new Vector<GraphLine>();

      ret.addAll(Arrays.asList(_fkStubLines));
      ret.addAll(Arrays.asList(getConnectLines()));
      ret.addAll(Arrays.asList(_pkStubLines));

      return ret.toArray(new GraphLine[ret.size()]);
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

      ret[0] = new GraphLine(_fkGatherPoint, _foldingPoints.get(0));

      for (int i = 0; i < _foldingPoints.size() - 1; i++)
      {
         ret[i + 1] = new GraphLine(_foldingPoints.get(i), _foldingPoints.get(i+1));
      }

      ret[ret.length -1] = new GraphLine(_foldingPoints.lastElement(), _pkGatherPoint);

      return ret;

   }

   public void setHitConnectLine(GraphLine line)
   {
      _hitConnectLine = line;
      _isHitOnConnectLine = true;
   }

   public void addFoldingPointToHitConnectLine(FoldingPoint lastPopupClickPoint)
   {
      if(_fkGatherPoint.equals(_hitConnectLine.getBegin()))
      {
         _foldingPoints.insertElementAt(lastPopupClickPoint, 0);
         return;
      }

      for (int i = 0; i < _foldingPoints.size() - 1; i++)
      {
         FoldingPoint fp = _foldingPoints.get(i);
         if(_hitConnectLine.getBegin().equals(fp.getZoomedPoint()))
         {
            _foldingPoints.insertElementAt(lastPopupClickPoint, i+1);
            return;
         }
      }

      _foldingPoints.add(lastPopupClickPoint);

   }

   public Vector<FoldingPoint> getFoldingPoints()
   {
      return _foldingPoints;
   }

   public void setHitFoldingPoint(FoldingPoint foldingPoint)
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

   public void moveLastHitFoldingPointTo(FoldingPoint point)
   {
//      if(point.x < 0)
//      {
//         point.x = 0;
//      }
//      if(point.y < 0)
//      {
//         point.y = 0;
//      }


      _foldingPoints.indexOf(_hitFoldingPoint);
      _foldingPoints.set(_foldingPoints.indexOf(_hitFoldingPoint), point);
      _hitFoldingPoint = point;
   }

   public void removeAllFoldingPoints()
   {
      _foldingPoints.clear();
   }

   public FoldingPoint getFirstFoldingPoint()
   {
      if(0 == _foldingPoints.size())
      {
         return null;
      }
      else
      {
         return _foldingPoints.get(0);
      }
   }

   public FoldingPoint getLastFoldingPoint()
   {
      if(0 == _foldingPoints.size())
      {
         return null;
      }
      else
      {
         return _foldingPoints.get(_foldingPoints.size()-1);
      }

   }

   public GraphLine getMainLine()
   {
      if(0 == _foldingPoints.size())
      {
         return new GraphLine(_pkGatherPoint, _fkGatherPoint);
      }
      else
      {
         return new GraphLine(_foldingPoints.get(0), _fkGatherPoint);
      }
   }
}
