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
*  MetouiaCheckBoxIcon.java                                                    *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):   Christian Walker                                         *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.plaf.metal.MetalCheckBoxIcon;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * This class represents a check box icon.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaCheckBoxIcon extends MetalCheckBoxIcon
{

  /**
   * Draws the check box icon at the specified location.
   *
   * @param c The component to draw on.
   * @param g The graphics context.
   * @param x The x coordinate of the top left corner.
   * @param y The y coordinate of the top left corner.
   */
  public void paintIcon(Component c, Graphics g, int x, int y)
  {
    super.paintIcon(c, g, x, y);

    int controlSize = getControlSize();

    // Paint the horizontal highlight gradient:
    MetouiaGradients.drawHighlight(
      g, new Rectangle(
        x + 1, y + 1, controlSize - 3, controlSize / 2), true, true);

    // Paint the horizontal shadow gradient:
    MetouiaGradients.drawShadow(
      g, new Rectangle(
        x + 1, y + controlSize / 2, controlSize - 3, controlSize / 2),
      true, false);

    //fix for missing pixel in top right corner (aesthetic fix)
    //works with positional changes, and controlSize changes.
    g.setColor(MetalLookAndFeel.getControlDarkShadow());
    g.drawLine(x+controlSize-2,y+1,x+controlSize-2,y+1);
  }
}