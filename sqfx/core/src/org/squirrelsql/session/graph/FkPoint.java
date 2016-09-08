package org.squirrelsql.session.graph;

import javafx.geometry.Point2D;

public class FkPoint
{
   private final GraphColumn _graphColumn;
   private final Point2D _point;

   public FkPoint(GraphColumn graphColumn, Point2D point)
   {
      _graphColumn = graphColumn;
      _point = point;
   }

   public GraphColumn getGraphColumn()
   {
      return _graphColumn;
   }

   public Point2D getPoint()
   {
      return _point;
   }
}
