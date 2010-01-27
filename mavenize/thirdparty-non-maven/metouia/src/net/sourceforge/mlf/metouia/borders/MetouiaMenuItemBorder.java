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
*  MetouiaMenuItemBorder.java                                                  *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia.borders;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.ButtonModel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;
import net.sourceforge.mlf.metouia.MetouiaLookAndFeel;

/**
 * This class represents the border for menu items.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaMenuItemBorder extends AbstractBorder implements UIResource
{

  /**
   * The border insets.
   */
  protected static Insets insets = new Insets(1, 1, 1, 1);

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
    ButtonModel model = ((JMenuItem)c).getModel();

    g.translate(x, y);

    if (c.getParent() instanceof JMenuBar)
    {
      if (model.isArmed() || model.isSelected())
      {
        g.setColor(MetouiaLookAndFeel.getControlDarkShadow());
        g.drawLine(0, 0, w - 2, 0);
        g.drawLine(0, 0, 0, h - 1);

        g.setColor(MetouiaLookAndFeel.getPrimaryControlHighlight());
        g.drawLine(w - 1, 1, w - 1, h - 1);
      }
    }
    else
    {
      if (model.isArmed() || (c instanceof JMenu && model.isSelected()))
      {
        g.setColor(MetouiaLookAndFeel.getControlDarkShadow());
        g.drawLine(0, 0, w - 1, 0);

        g.setColor(MetouiaLookAndFeel.getPrimaryControlHighlight());
        g.drawLine(0, h - 1, w - 1, h - 1);
      }
      else
      {
        g.setColor(MetouiaLookAndFeel.getPrimaryControlHighlight());
        g.drawLine(0, 0, 0, h - 1);
      }
    }

    g.translate(-x, -y);
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