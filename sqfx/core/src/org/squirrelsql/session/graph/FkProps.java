package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;
import org.squirrelsql.services.CollectionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FkProps
{
   private String _fkName;
   private boolean _selected;
   private ArrayList<Point2D> _foldingPoints = new ArrayList<>();

   public FkProps(String fkName)
   {
      _fkName = fkName;
   }

   public boolean isSelected()
   {
      return _selected;
   }

   public void setSelected(boolean selected)
   {
      _selected = selected;
   }

   public void addFoldingPoint(Point2D p)
   {
      _foldingPoints.add(p);
   }

   public List<Point2D> getFoldingPoints()
   {
      return _foldingPoints;
   }

   public void removeFoldingPoint(Point2D fp)
   {
      _foldingPoints.remove(fp);
   }

   public void replaceFoldingPoint(Point2D oldFP, Point2D newFP)
   {
      _foldingPoints.set(_foldingPoints.indexOf(oldFP), newFP);
   }

   public void addFoldingPointAt(Point2D fp, int listIndex)
   {
      _foldingPoints.add(listIndex, fp);
   }


   public FkPropsPersistence toPersistence()
   {
      FkPropsPersistence ret = new FkPropsPersistence();

      ret.setFkName(_fkName);

      ret.setFoldingPointPersistences(CollectionUtil.transform(_foldingPoints, fp -> toFpPersistence(fp)));

      return ret;
   }

   private FoldingPointPersistence toFpPersistence(Point2D fp)
   {
      FoldingPointPersistence ret = new FoldingPointPersistence();
      ret.setX(fp.getX());
      ret.setY(fp.getY());

      return ret;
   }

}
