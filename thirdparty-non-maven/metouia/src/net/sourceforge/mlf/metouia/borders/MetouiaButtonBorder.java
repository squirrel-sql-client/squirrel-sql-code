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
*  MetouiaButtonBorder.java                                                    *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia.borders;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

/**
 * This is a simple 3d border class used for buttons.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaButtonBorder extends AbstractBorder implements UIResource
{

  /**
   * The border insets.
   */
  protected static final Insets insets = new Insets(2, 2, 2, 2);

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
    JButton button = (JButton)c;
    ButtonModel model = ((JButton)c).getModel();

    // Optimizations are welcome here!
    if (model.isEnabled())
    {
      if (model.isPressed() && model.isArmed())
      {
        MetouiaBorderUtilities.drawPressed3DBorder(g, x, y, w, h);
      }
      else
      {
        if (button.isDefaultButton())
        {
          MetouiaBorderUtilities.drawDefaultButtonBorder(g, x, y, w, h);
        }
        else
        {
          if (button.isRolloverEnabled())
          {
            if (model.isRollover())
            {
              MetouiaBorderUtilities.drawSimple3DBorder(g, x, y, w, h);
            }
          }
          else
          {
            MetouiaBorderUtilities.drawSimple3DBorder(g, x, y, w, h);
          }
        }
      }
    }
    else
    {
      if (!button.isRolloverEnabled())
      {
        MetouiaBorderUtilities.drawDisabledBorder(g, x, y, w - 1, h - 1);
      }
    }
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