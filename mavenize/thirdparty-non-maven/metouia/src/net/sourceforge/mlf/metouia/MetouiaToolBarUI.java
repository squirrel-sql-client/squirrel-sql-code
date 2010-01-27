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
*  MetouiaToolBarUI.java                                                       *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalToolBarUI;

/**
 * This class represents the UI delegate for the JToolBar component.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaToolBarUI extends MetalToolBarUI
{

  /**
   * The Cached UI delegate.
   */
  private final static MetouiaToolBarUI toolBarUI = new MetouiaToolBarUI();

  /**
   * These insets are forced inner margin for the toolbar buttons.
   */
  private Insets insets = new Insets(2, 2, 2, 2);

  /**
   * Creates the UI delegate for the given component.
   *
   * @param c The component to create its UI delegate.
   * @return The UI delegate for the given component.
   */
  public static ComponentUI createUI(JComponent c)
  {
    return toolBarUI;
  }

  /**
   * Installs some default values for the given toolbar.
   * The gets a rollover property.
   *
   * @param c The reference of the toolbar to install its default values.
   */
  public void installUI(JComponent c)
  {
    super.installUI(c);
    c.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
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

    int orientation = SwingConstants.HORIZONTAL;

    if (c instanceof JToolBar)
    {
      orientation = ((JToolBar)c).getOrientation();
    }

    if (orientation == SwingConstants.HORIZONTAL)
    {
      // Paint the horizontal highlight gradient:
      MetouiaGradients.drawHorizontalHighlight(g, c);

      // Paint the horizontal shadow gradient:
      MetouiaGradients.drawHorizontalShadow(g, c);
    }
    else
    {
      // Paint the vertical highlight gradient:
      MetouiaGradients.drawVerticalHighlight(g, c);

      // Paint the vertical shadow gradient:
      MetouiaGradients.drawVerticalShadow(g, c);
    }

  }

  /**
   * Sets the border of the given component to a rollover border.
   *
   * @param c The component to set its border.
   */
  protected void setBorderToRollover(Component c)
  {
    if (c instanceof AbstractButton)
    {
      AbstractButton button = (AbstractButton)c;

      if (!button.isRolloverEnabled())
      {
        button.setRolloverEnabled(true);
      }
      if (button.isContentAreaFilled())
      {
        button.setContentAreaFilled(false);
      }
      if (button.isFocusPainted())
      {
        button.setFocusPainted(false);
      }
      button.setMargin(insets);
    }
  }
}