package org.squirrelsql.session.graph;

public class PkPoint
{
   private final double _x;
   private final double _y;
   private final double _arrowAngle;

   /**
    * Values are between 0 and PI/2
    */
   private double _angleFromSimpleTriangle;

   public PkPoint(double x, double y, double arrowAngle, double angleFromSimpleTriangle)
   {
      _x = x;
      _y = y;
      _arrowAngle = arrowAngle;
      _angleFromSimpleTriangle = angleFromSimpleTriangle;
   }

   public double getArrowAngle()
   {
      return _arrowAngle;
   }

   public double getAngleFromSimpleTriangle()
   {
      return _angleFromSimpleTriangle;
   }

   public double getArrowX()
   {
      double signum = Math.signum(Math.cos(_arrowAngle));

      double offset;
      if(signum < 0)
      {
         offset = 0;
      }
      else
      {
         offset = -GraphConstants.IMAGE_WIDTH;
      }

      return _x + offset;
   }

   public double getArrowY()
   {

      double h = GraphConstants.IMAGE_HEIGHT;
      double w = GraphConstants.IMAGE_WIDTH;

//      double f = h * (1 - 2* Math.abs(Math.sin(_arrowAngle)));
//      System.out.println("PkPoint.getArrowY " + _arrowAngle/ (2*Math.PI) * 360 + " f is " + f);


      double offset = -(h * Math.sin(_angleFromSimpleTriangle)) - w/2.0 * Math.cos(_angleFromSimpleTriangle);

      if (isAboveGatherPointY())
      {
         offset = offset;
      }
      else
      {
         offset = offset + h * Math.sin(_angleFromSimpleTriangle);
      }

//      double offset = - w * Math.cos(_angleFromSimpleTriangle);
//
//      if(isAboveGatherPointY())
//      {
//
//         double v = -w * Math.sin(_angleFromSimpleTriangle);
//
//         System.out.println("############ _angleFromSimpleTriangle = " + 1/(2*Math.PI) * 360 * _angleFromSimpleTriangle + " arrowAngle = " + 1/(2*Math.PI) * 360 * _arrowAngle + " v="+ v);
//
//         offset = v;
//      }


      //System.out.println("_y="  + _y + " _angleFromSimpleTriangle=" + _angleFromSimpleTriangle/ (2*Math.PI) * 360 + " offset=" + offset);

//      if(signum <= 0)
//      {
//         offset -= h;
//      }

      return _y + offset;
   }

   private boolean isAboveGatherPointY()
   {
      return 0 < _arrowAngle && _arrowAngle < Math.PI;
   }

   public double getX()
   {
      return _x;
   }

   public double getY()
   {
      return _y;
   }
}
