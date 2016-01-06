package net.sourceforge.squirrel_sql.client.preferences;

import javax.swing.*;
import java.awt.*;

public class ColorIcon implements Icon
{
   private int iWidth;
   private int iHeight;

   private Color color;
   private Color border;
   private Insets insets;

   //---------------------------------------------------------------------------

   public ColorIcon()
   {
      this(32, 16);
   }

   //---------------------------------------------------------------------------

   public ColorIcon(int width, int height)
   {
      this(width, height, Color.black);
   }

   //---------------------------------------------------------------------------

   public ColorIcon(int width, int height, Color c)
   {
      iWidth = width;
      iHeight = height;

      color = c;
      border = Color.black;
      insets = new Insets(1, 1, 1, 1);
   }

   //---------------------------------------------------------------------------

   public void setColor(Color c)
   {
      color = c;
   }

   //---------------------------------------------------------------------------

   public Color getColor()
   {
      return color;
   }

   //---------------------------------------------------------------------------

   public void setBorderColor(Color c)
   {
      border = c;
   }

   //---------------------------------------------------------------------------
   //---
   //--- Icon interface methods
   //---
   //---------------------------------------------------------------------------

   public int getIconWidth()
   {
      return iWidth;
   }

   //---------------------------------------------------------------------------

   public int getIconHeight()
   {
      return iHeight;
   }

   //---------------------------------------------------------------------------

   public void paintIcon(Component c, Graphics g, int x, int y)
   {
      g.setColor(border);
      g.drawRect(x, y, iWidth - 1, iHeight - 2);

      x += insets.left;
      y += insets.top;

      int w = iWidth - insets.left - insets.right;
      int h = iHeight - insets.top - insets.bottom - 1;

      g.setColor(color);
      g.fillRect(x, y, w, h);
   }
}