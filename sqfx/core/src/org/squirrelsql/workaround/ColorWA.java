package org.squirrelsql.workaround;

import javafx.scene.paint.Color;


public class ColorWA
{

   // The Color.rgb(int r,int g,int b) method needs this values to reproduce a color.
   // As we see there are not so bright programmers working for Oracle, too.
   public static int getRed(Color color)
   {
      return (int) Math.round(color.getRed() * 255.0);
   }

   public static int getGreen(Color color)
   {
      return (int) Math.round(color.getGreen() * 255.0);
   }

   public static int getBlue(Color color)
   {
      return (int) Math.round(color.getBlue() * 255.0);
   }
}
