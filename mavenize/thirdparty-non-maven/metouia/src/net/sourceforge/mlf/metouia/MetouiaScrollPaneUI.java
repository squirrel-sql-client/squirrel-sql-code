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
*  MetouiaScrollPaneUI.java                                                    *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalScrollBarUI;
import javax.swing.plaf.metal.MetalScrollPaneUI;

/**
 * This class represents the UI delegate for the JScrollPane component.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaScrollPaneUI extends MetalScrollPaneUI
  implements PropertyChangeListener
{

  /**
   * Creates the UI delegate for the given component.
   *
   * @param c The component to create its UI delegate.
   * @return The UI delegate for the given component.
   */
  public static ComponentUI createUI(JComponent c)
  {
    return new MetouiaScrollPaneUI();
  }

  /**
   * Installs some default values for the given scrollpane.
   * The free standing property is disabled here.
   *
   * @param c The reference of the scrollpane to install its default values.
   */
  public void installUI(JComponent c)
  {
    super.installUI(c);

    scrollpane.getHorizontalScrollBar().putClientProperty
      (MetalScrollBarUI.FREE_STANDING_PROP, Boolean.FALSE);
    scrollpane.getVerticalScrollBar().putClientProperty
      (MetalScrollBarUI.FREE_STANDING_PROP, Boolean.FALSE);
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

    // Repaint the corner's border:
    if (scrollpane.getCorner(JScrollPane.LOWER_RIGHT_CORNER) == null)
    {
      g.setColor(MetouiaLookAndFeel.getControlDarkShadow());

      Rectangle hbounds = scrollpane.getVerticalScrollBar().getBounds();
      Rectangle vbounds = scrollpane.getHorizontalScrollBar().getBounds();

      g.drawLine(hbounds.x, hbounds.y + hbounds.height,
        hbounds.x + hbounds.width, hbounds.y + hbounds.height);

      g.drawLine(vbounds.x + vbounds.width, vbounds.y,
        vbounds.x + vbounds.width, vbounds.y + vbounds.height);
    }
  }

  /**
   * Creates a property change listener that does nothing inorder to prevent the
   * free standing scrollbars.
   *
   * @return An empty property change listener.
   */
  protected PropertyChangeListener createScrollBarSwapListener()
  {
    return this;
  }

  /**
   * Simply ignore any change.
   *
   * @param event The property change event.
   */
  public void propertyChange(PropertyChangeEvent event)
  {
  }
}