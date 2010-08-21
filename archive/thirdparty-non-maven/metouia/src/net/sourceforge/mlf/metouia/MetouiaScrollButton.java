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
*  MetouiaScrollButton.java                                                    *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia;

import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.plaf.metal.MetalScrollButton;

/**
 * This class represents an arrow button used in scrollbars.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaScrollButton extends MetalScrollButton
{

  /**
   * Creates a new scroll button with the given direction and width.
   *
   * @param direction The arrow direction of the button.
   * @param width The button's width.
   * @param freeStanding The button's state (stand alonne or in scrollbar).
   */
  public MetouiaScrollButton(int direction, int width, boolean freeStanding)
  {
    super(direction, width, freeStanding);
  }

  /**
   * Paints the scroll button.
   *
   * @param g The graphics context to use.
   */
  public void paint(Graphics g)
  {
    super.paint(g);

    int width = getWidth();
    int height = getHeight();

    // This is a hack! See ScrollPaneUI where the buttons borders are drawed
    // on the lower right corner if exists.
    if (getModel().isPressed())
    {
      g.setColor(MetouiaLookAndFeel.getControlShadow());
    }
    else
    {
      g.setColor(getBackground());
    }

    // Restitute the backround of the one border part because its already drawn.
    if (getDirection() == SOUTH)
    {
      g.drawLine(1, height - 1, width - 1, height - 1);
    }
    else if (getDirection() == EAST)
    {
      g.drawLine(width - 1, 1, width - 1, height - 1);
    }
    else if (getDirection() == NORTH)
    {
    }
    else if (getDirection() == WEST)
    {
    }

    Rectangle reflection;
    Rectangle shadow;
    boolean isVertical = (getDirection() == EAST || getDirection() == WEST);

    if (isVertical)
    {
      reflection = new Rectangle(1, 1, width - 2, height / 2);
      shadow = new Rectangle(1, height / 2, width - 2, height / 2 + 1);
    }
    else
    {
      reflection = new Rectangle(1, 1, width / 2, height - 2);
      shadow = new Rectangle(width / 2, 1, width / 2 + 1, height - 2);
    }

    // Paint the highlight gradient:
    MetouiaGradients.drawHighlight(g, reflection, isVertical, true);

    // Paint the shadow gradient:
    MetouiaGradients.drawShadow(g, shadow, isVertical, false);
  }
}