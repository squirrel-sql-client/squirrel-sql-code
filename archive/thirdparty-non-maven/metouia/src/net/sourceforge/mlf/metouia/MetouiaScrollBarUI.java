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
*  MetouiaScrollBarUI.java                                                     *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalScrollBarUI;
import net.sourceforge.mlf.metouia.borders.MetouiaBorderUtilities;
import net.sourceforge.mlf.metouia.borders.MetouiaDots;

/**
 * This class represents the UI delegate for the JScrollBar component.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaScrollBarUI extends MetalScrollBarUI
{

  /**
   * The scrollbar's highlight color.
   */
  private static Color highlightColor;

  /**
   * The scrollbar's dark shadow color.
   */
  private static Color darkShadowColor;

  /**
   * The thumb's shadow color.
   */
  private static Color thumbShadow;

  /**
   * The thumb's highlight color.
   */
  private static Color thumbHighlightColor;

  /**
   * The reference of the metouia dots used for the thumb.
   */
  protected MetouiaDots dots;

  /**
   * The free standing property of this scrollbar UI delegate.
   */
  private boolean freeStanding = false;

  /**
   * Installs some default values.
   * Initializes the metouia dots used for the thumb.
   */
  protected void installDefaults()
  {
    super.installDefaults();

    dots = new MetouiaDots(5, 5);
  }

  /**
   * Creates the UI delegate for the given component.
   *
   * @param c The component to create its UI delegate.
   * @return The UI delegate for the given component.
   */
  public static ComponentUI createUI(JComponent c)
  {
    return new MetouiaScrollBarUI();
  }

  /**
   * Creates the decrease button of the scrollbar.
   *
   * @param orientation The button's orientation.
   * @return The created button.
   */
  protected JButton createDecreaseButton(int orientation)
  {
    decreaseButton =
      new MetouiaScrollButton(orientation, scrollBarWidth - 0, freeStanding);
    return decreaseButton;
  }

  /**
   * Creates the increase button of the scrollbar.
   *
   * @param orientation The button's orientation.
   * @return The created button.
   */
  protected JButton createIncreaseButton(int orientation)
  {
    increaseButton =
      new MetouiaScrollButton(orientation, scrollBarWidth, freeStanding);
    return increaseButton;
  }

  /**
   * Paints the scrollbar's thumb.
   *
   * @param g The graphics context to use.
   * @param c The component to paint.
   * @param thumbBounds The track bounds.
   */
  protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
  {
    boolean leftToRight = c.getComponentOrientation().isLeftToRight();

    g.translate(thumbBounds.x, thumbBounds.y);

    if (scrollbar.getOrientation() == JScrollBar.VERTICAL)
    {
      if (!freeStanding)
      {
        if (!leftToRight)
        {
          thumbBounds.width += 1;
          g.translate(-1, 0);
        }
        else
        {
          thumbBounds.width += 2;
        }

      }

      g.setColor(thumbShadow);
      g.drawRect(0, 0, thumbBounds.width - 2, thumbBounds.height - 1);

      dots.setDotsArea(thumbBounds.width - 6, thumbBounds.height - 7);
      dots.paintIcon(c, g, 4, 4);

      if (!freeStanding)
      {
        if (!leftToRight)
        {
          thumbBounds.width -= 1;
          g.translate(1, 0);
        }
        else
        {
          thumbBounds.width -= 2;
        }
      }
    }
    else  // HORIZONTAL
    {
      if (!freeStanding)
      {
        thumbBounds.height += 2;
      }
      g.setColor(thumbHighlightColor);

      g.setColor(thumbShadow);
      g.drawRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 2);

      dots.setDotsArea(thumbBounds.width - 7, thumbBounds.height - 6);
      dots.paintIcon(c, g, 4, 5);

      if (!freeStanding)
      {
        thumbBounds.height -= 2;
      }
    }
    g.translate(-thumbBounds.x, -thumbBounds.y);

    // colors for the reflection gradient
    Color colorReflection = MetouiaLookAndFeel.getGradientReflection();
    Color colorReflectionFaded =
      MetouiaLookAndFeel.getGradientTranslucentReflection();
    // colors for the shadow gradient
    Color colorShadow = MetouiaLookAndFeel.getGradientShadow();
    Color colorShadowFaded = MetouiaLookAndFeel.getGradientTranslucentShadow();

    Rectangle rectReflection;  // rectangle for the reflection gradient
    Rectangle rectShadow;  // rectangle for the shadow gradient
    if (scrollbar.getOrientation() == JScrollBar.VERTICAL)
    {
      rectReflection = new Rectangle(thumbBounds.x + 1, thumbBounds.y + 1,
        thumbBounds.width / 2, thumbBounds.height - 2);
      rectShadow = new Rectangle(thumbBounds.x + thumbBounds.width / 2,
        thumbBounds.y + 1, thumbBounds.width / 2 + 1, thumbBounds.height - 2);
    }
    else
    {
      rectReflection = new Rectangle(thumbBounds.x + 1, thumbBounds.y + 1,
        thumbBounds.width - 2, thumbBounds.height / 2);
      rectShadow = new Rectangle(thumbBounds.x + 1,
        thumbBounds.y + thumbBounds.height / 2, thumbBounds.width - 2,
        thumbBounds.height / 2 + 1);
    }

    boolean isVertical = (scrollbar.getOrientation() == JScrollBar.HORIZONTAL);
    MetouiaGradients.drawGradient(g, colorReflection, colorReflectionFaded,
      rectReflection, isVertical, true);
    MetouiaGradients.drawGradient(g, colorShadowFaded, colorShadow,
      rectShadow, isVertical, false);
  }

  /**
   * Initializes the scrollbar color from the current theme.
   */
  protected void configureScrollBarColors()
  {
    super.configureScrollBarColors();

    highlightColor = UIManager.getColor("ScrollBar.highlight");
    darkShadowColor = UIManager.getColor("ScrollBar.darkShadow");

    thumbShadow = MetouiaLookAndFeel.getControlDarkShadow();
    thumbHighlightColor = MetouiaLookAndFeel.getMenuBackground();
  }

  /**
   * Paints the scrollbar's track.
   *
   * @param g The graphics context to use.
   * @param c The component to paint.
   * @param trackBounds The track bounds.
   */
  protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds)
  {
    g.translate(trackBounds.x, trackBounds.y);

    boolean leftToRight = c.getComponentOrientation().isLeftToRight();


    if (scrollbar.getOrientation() == JScrollBar.VERTICAL)
    {
      if (!freeStanding)
      {
        if (!leftToRight)
        {
          trackBounds.width += 1;
          g.translate(-1, 0);
        }
        else
        {
          trackBounds.width += 2;
        }
      }

      if (c.isEnabled())
      {
        g.setColor(darkShadowColor);
        g.drawLine(0, 0, 0, trackBounds.height - 1);
        g.drawLine(trackBounds.width - 2, 0, trackBounds.width - 2,
          trackBounds.height - 1);
        g.drawLine(1, trackBounds.height - 1, trackBounds.width - 1,
          trackBounds.height - 1);
        g.drawLine(1, 0, trackBounds.width - 1, 0);
        g.setColor(highlightColor);
        g.drawLine(trackBounds.width - 1, 0, trackBounds.width - 1,
          trackBounds.height - 1);
      }
      else
      {
        MetouiaBorderUtilities.drawDisabledBorder(
          g, 0, 0, trackBounds.width, trackBounds.height);
      }

      if (!freeStanding)
      {
        if (!leftToRight)
        {
          trackBounds.width -= 1;
          g.translate(1, 0);
        }
        else
        {
          trackBounds.width -= 2;
        }
      }
    }


    else  // HORIZONTAL
    {
      if (!freeStanding)
      {
        trackBounds.height += 2;
      }

      if (c.isEnabled())
      {
        g.setColor(darkShadowColor);
        g.drawLine(0, 0, trackBounds.width - 1, 0);  // top
        g.drawLine(0, 1, 0, trackBounds.height - 1); // left
        g.drawLine(0, trackBounds.height - 2,
          trackBounds.width - 1, trackBounds.height - 2); // bottom
        g.drawLine(trackBounds.width - 1, 1,
          trackBounds.width - 1, trackBounds.height - 1); // right

      }
      else
      {
        MetouiaBorderUtilities.drawDisabledBorder(
          g, 0, 0, trackBounds.width, trackBounds.height);
      }

      if (!freeStanding)
      {
        trackBounds.height -= 2;
      }
    }

    g.translate(-trackBounds.x, -trackBounds.y);
  }
}