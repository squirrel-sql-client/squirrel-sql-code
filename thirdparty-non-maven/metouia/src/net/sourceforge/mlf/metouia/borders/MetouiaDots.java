/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*        Metouia Look And Feel: a free pluggable look and feel for java        *
*                         http://mlf.sourceforge.net                           *
*          (C) Copyright 2002, by Taoufik Romdhane and Contributors.           *
*                                                                              *
*   This library is free software; you can redistribute it and/or modify it    *
*   under the terms of the GNU Lesser General Public License as published by   *
*   the Free Software Foundation; either version 2.1 of the License, or (at    *
*   your option) any later version.                                            *
*                                                                              *
*   This library is distributed in the hope that it will be useful,            *
*   but WITHOUT ANY WARRANTY; without even the implied warranty of             *
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                       *
*   See the GNU Lesser General Public License for more details.                *
*                                                                              *
*   You should have received a copy of the GNU General Public License along    *
*   with this program; if not, write to the Free Software Foundation, Inc.,    *
*   59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.                    *
*                                                                              *
*  MetouiaDots.java                                                            *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia.borders;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 * This class implements the dots used throughout the Metouia Look and Feel.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaDots implements Icon
{

  /**
   * The horizontal dots count.
   */
  protected int xDots;

  /**
   * The vertical dots count.
   */
  protected int yDots;

  /**
   * The dots buffer that can be reused for painting dots.
   */
  protected MetouiaDotsBuffer buffer;

  /**
   * This constructor creates a dots area with the given size.
   *
   * @param width The width of the dots area.
   * @param height The height of the dots area.
   */
  public MetouiaDots(int width, int height)
  {
    setDotsArea(width, height);

    if (buffer == null)
    {
      buffer = new MetouiaDotsBuffer();
    }
  }

  /**
   * Sets the dots area to the given size.
   *
   * @param width The width of the dots area.
   * @param height The height of the dots area.
   */
  public void setDotsArea(int width, int height)
  {
    xDots = width / 5;
    yDots = height / 5;
  }

  /**
   * Draws the dots area at the specified location.
   *
   * @param c The component to draw on.
   * @param g The graphics context.
   * @param x The x coordinate of the top left corner.
   * @param y The y coordinate of the top left corner.
   */
  public void paintIcon(Component c, Graphics g, int x, int y)
  {
    int bufferWidth = buffer.getImageSize().width;
    int bufferHeight = buffer.getImageSize().height;
    int iconWidth = getIconWidth();
    int iconHeight = getIconHeight();

    int x2 = x + iconWidth;
    int y2 = y + iconHeight;

    int savex = x;
    while (y < y2)
    {
      int h = Math.min(y2 - y, bufferHeight);
      for (x = savex; x < x2; x += bufferWidth)
      {
        int w = Math.min(x2 - x, bufferWidth);
        g.drawImage(buffer.getImage(), x, y, x + w, y + h, 0, 0, w, h, null);
      }
      y += bufferHeight;
    }
  }

  /**
   * Returns the icon's width.
   *
   * @return An int specifying the fixed width of the icon.
   */
  public int getIconWidth()
  {
    return xDots * 5;
  }

  /**
   * Returns the icon's height.
   *
   * @return An int specifying the fixed height of the icon.
   */
  public int getIconHeight()
  {
    return yDots * 5;
  }
}