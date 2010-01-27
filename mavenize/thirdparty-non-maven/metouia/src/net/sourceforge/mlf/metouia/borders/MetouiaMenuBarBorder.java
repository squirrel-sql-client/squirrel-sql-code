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
*  MetouiaMenuBarBorder.java                                                   *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia.borders;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import net.sourceforge.mlf.metouia.MetouiaLookAndFeel;

/**
 * This class represents the border of a menu bar.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaMenuBarBorder extends AbstractBorder implements UIResource
{

  /**
   * The border insets.
   */
  protected static Insets insets = new Insets(1, 0, 1, 0);

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
    g.setColor(MetouiaLookAndFeel.getControlHighlight());
    g.drawLine(0, 0, 0, h - 1);

    g.setColor(MetouiaLookAndFeel.getControlShadow());
    g.drawLine(0, h - 1, w - 1, h - 1);
  }

  /**
   * Gets the border insets for a given component.
   *
   * @param c The component to get its border insets.
   * @return Always returns the same insets as defined in <code>insets</code>.
   */
  public Insets getBorderInsets(Component c)
  {
    return insets;
  }
}