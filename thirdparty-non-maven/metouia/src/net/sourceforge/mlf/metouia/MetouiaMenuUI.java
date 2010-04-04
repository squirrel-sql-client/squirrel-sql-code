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
*  MetouiaMenuUI.java                                                          *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuUI;

/**
 * This class represents the UI delegate for the JMenu component.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaMenuUI extends BasicMenuUI
{

  /**
   * Creates the UI delegate for the given component.
   *
   * @param c The component to create its UI delegate.
   * @return The UI delegate for the given component.
   */
  public static ComponentUI createUI(JComponent c)
  {
    return new MetouiaMenuUI();
  }

  /**
   * Paints the given component.
   *
   * @param g The graphics context to use.
   * @param c The component to paint.
   */
  public void paint(Graphics g, JComponent c)
  {
    super.paint(g, c);
    Container parent = menuItem.getParent();

    // Paint the horizontal highlight gradient:
    MetouiaGradients.drawHighlight(
      g, new Rectangle(0, -1, parent.getWidth(), parent.getHeight() / 2),
      true, true);

    // Paint the horizontal shadow gradient:
    MetouiaGradients.drawShadow(
      g,
      new Rectangle(
        0, parent.getHeight() / 2 - 1,
        parent.getWidth(), parent.getHeight() / 2),
      true, false);
  }
}