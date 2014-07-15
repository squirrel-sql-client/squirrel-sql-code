package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import java.util.ArrayList;
import java.util.Collections;

public class XYPair implements Comparable<XYPair>
{
   private final Double _x;
   private final Double _y;

   public XYPair(Double x, Double y)
   {
      _x = x;
      _y = y;
   }

   public Double getX()
   {
      return _x;
   }

   public Double getY()
   {
      return _y;
   }

   @Override
   public int compareTo(XYPair other)
   {
      return _x.compareTo(other._x);
   }

   public static ArrayList<XYPair> createSortedPairs(ArrayList<Double> xValues, ArrayList<Double> yValues)
   {
      ArrayList<XYPair> ret = new ArrayList<XYPair>();

      for (int i = 0; i < xValues.size(); i++)
      {
         ret.add(new XYPair(xValues.get(i), yValues.get(i)));
      }

      Collections.sort(ret);

      return ret;

   }
}
