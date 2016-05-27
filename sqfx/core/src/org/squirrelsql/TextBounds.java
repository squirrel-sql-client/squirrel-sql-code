package org.squirrelsql;

public class TextBounds
{

   private final double _textWidth;
   private final int _rows;

   public TextBounds(double textWidth, int rows)
   {
      _textWidth = textWidth;
      _rows = rows;
   }

   public double getTextWidth()
   {
      return _textWidth;
   }

   public int getRows()
   {
      return _rows;
   }
}
