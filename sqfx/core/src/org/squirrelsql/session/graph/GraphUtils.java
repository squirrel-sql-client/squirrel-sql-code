package org.squirrelsql.session.graph;

import javafx.scene.shape.Polygon;
import org.squirrelsql.session.TableInfo;

public class GraphUtils
{
   public static final double STANDARD_HALF_THICKNESS_FOR_MOUSE_HIT_CHECK = 3d;

   public static Polygon createPolygon(double x1, double y1, double x2, double y2)
   {
      return createPolygon(x1, y1, x2, y2, STANDARD_HALF_THICKNESS_FOR_MOUSE_HIT_CHECK);
   }



   public static Polygon createPolygon(double x1, double y1, double x2, double y2, double halfThickness)
   {
      Polygon ret = new Polygon();

      if (x1 < x2 && y1 < y2)
      {
         ret.getPoints().addAll(x1 + halfThickness, y1 - halfThickness);
         ret.getPoints().addAll(x1 - halfThickness, y1 + halfThickness);
         ret.getPoints().addAll(x2 - halfThickness, y2 + halfThickness);
         ret.getPoints().addAll(x2 + halfThickness, y2 - halfThickness);
      }
      else if (x1 > x2 && y1 > y2)
      {
         ret.getPoints().addAll(x1 - halfThickness, y1 + halfThickness);
         ret.getPoints().addAll(x1 + halfThickness, y1 - halfThickness);
         ret.getPoints().addAll(x2 + halfThickness, y2 - halfThickness);
         ret.getPoints().addAll(x2 - halfThickness, y2 + halfThickness);
      }
      else
      {
         ret.getPoints().addAll(x1 + halfThickness, y1 + halfThickness);
         ret.getPoints().addAll(x1 - halfThickness, y1 - halfThickness);
         ret.getPoints().addAll(x2 - halfThickness, y2 - halfThickness);
         ret.getPoints().addAll(x2 + halfThickness, y2 + halfThickness);
      }

      //System.out.println("("+ x1 + ", " + y1 + ") - (" + x2 + ", " + y2 +")");

      return ret;
   }

   static void connectColumns(String nonDbFkId, GraphColumn fkCol, TableInfo pkTableInfo, GraphColumn pkCol)
   {
      fkCol.addNonDbImportedKey(new NonDbImportedKey(nonDbFkId, pkCol, pkTableInfo));
      pkCol.addNonDbFkIdPointingAtMe(nonDbFkId);
   }
}
