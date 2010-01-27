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
*  MetouiaToolBarBorder.java                                                   *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia.borders;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalLookAndFeel;
import net.sourceforge.mlf.metouia.MetouiaLookAndFeel;

/**
 * This class represents the border of toolbars.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaToolBarBorder extends AbstractBorder
  implements UIResource, SwingConstants
{

  /**
   * The drag "dots" for floatable toolbars.
   */
  protected MetouiaDots dots = new MetouiaDots(5, 5);

  /**
   * Draws a simple 3d border for the given component.
   *
   * @param c The component to draw its border.
   * @param g The graphics context.
   * @param x The x coordinate of the top left corner.
   * @param y The y coordinate of the top left corner.
   * @param w The width.
   * @param h The height.
   */
  public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
  {
    boolean isHorizontal = ((JToolBar)c).getOrientation() == HORIZONTAL;

    g.setColor(MetouiaLookAndFeel.getControlHighlight());
    if (isHorizontal)
    {
      g.drawLine(0, 0, w - 1, 0);
    }
    else
    {
      g.drawLine(0, 0, 0, h - 1);
    }

    g.setColor(MetouiaLookAndFeel.getControlShadow());
    if (isHorizontal)
    {
      g.drawLine(0, h - 1, w - 1, h - 1);
    }
    else
    {
      g.drawLine(w - 1, 0, w - 1, h - 1);
    }

    g.translate(x, y);

    if (((JToolBar)c).isFloatable())
    {
      if (((JToolBar)c).getOrientation() == HORIZONTAL)
      {
        dots.setDotsArea(5, c.getSize().height - 4);
        if (c.getComponentOrientation().isLeftToRight())
        {
          dots.paintIcon(c, g, 2, 2);
        }
        else
        {
          dots.paintIcon(c, g, c.getBounds().width - 12, 2);
        }
      }
      else
      {
        dots.setDotsArea(c.getSize().width - 4, 5);
        dots.paintIcon(c, g, 2, 2);
      }

    }

    g.translate(-x, -y);
  }

  /**
   * Gets the border insets for a given component.
   * When the toolbar is flaotable, space for for the drag "dots" is also taken
   * in the insets.
   *
   * @param c The component to get its border insets.
   * @return The toolbar border insets.
   */
  public Insets getBorderInsets(Component c)
  {
    Insets insets = new Insets(3, 3, 3, 3);

    if (((JToolBar)c).isFloatable())
    {
      if (((JToolBar)c).getOrientation() == HORIZONTAL)
      {
        insets.left = 8;
      }
      else
      {
        insets.top = 8;
      }
    }

    return insets;
  }
}