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
*  MetouiaGradients.java                                                       *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia;

import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Component;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import net.sourceforge.mlf.metouia.MetouiaLookAndFeel;
import net.sourceforge.mlf.metouia.util.FastGradientPaintContext;

/**
 * This class represents the basic gradient used through the Metouia look & feel
 * and presents some helpful static method for drawing standard gradients.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaGradients implements Paint
{

  /**
   * The standard reflection color.
   */
  private static final Color reflection =
    MetouiaLookAndFeel.getGradientReflection();

  /**
   * The translucent version of the standard reflection color.
   */
  private static final Color reflectionFaded =
    MetouiaLookAndFeel.getGradientTranslucentReflection();

  /**
   * The standard shdow color.
   */
  private static final Color shadow =
    MetouiaLookAndFeel.getGradientShadow();

  /**
   * The translucent version of the standard shdow color.
   */
  private static final Color shadowFaded =
    MetouiaLookAndFeel.getGradientTranslucentShadow();

  /**
   * The start color of the gradient.
   */
  private int startColor;

  /**
   * The end color of the gradient.
   */
  private int endColor;

  /**
   * Is <code>true</code> if the gradient is vertical otherwise horizontal.
   */
  private boolean isVertical;

  /**
   * Is <code>true</code> if the gradient's transparency is ascending.
   */
  private boolean isAscending;

  /**
   * Creates a new Metouia gradient.
   *
   * @param start The start color of the gradient.
   * @param end The end color of the gradient.
   * @param isVertical If the gradient should be vertical or horizontal.
   * @param isAscending If the gradient's transparency should be ascending.
   */
  public MetouiaGradients(Color start, Color end, boolean isVertical,
    boolean isAscending)
  {
    this.startColor = start.getRGB();
    this.endColor = end.getRGB();
    this.isVertical = isVertical;
    this.isAscending = isAscending;
  }

  /**
   * Creates and returns a PaintContext used to generate the color pattern.
   *
   * @param cm The ColorModel that receives the <code>Paint</code> data.
   *           This is used only as a hint.
   * @param r The device space bounding box of the graphics primitive being
   *          rendered.
   * @param r2d The user space bounding box of the graphics primitive being
   *             rendered.
   * @param xform The AffineTransform from user space into device space.
   * @param hints The hint that the context object uses to choose between
   *              rendering alternatives.
   * @return The <code>PaintContext</code> for generating color patterns.
   */
  public synchronized PaintContext createContext(ColorModel cm, Rectangle r,
    Rectangle2D r2d, AffineTransform xform, RenderingHints hints)
  {
    return new FastGradientPaintContext(
      cm, r, startColor, endColor, isVertical, isAscending);
  }

  /**
   * Gets the transparency of this gradient.
   *
   * @return <code>TRANSLUCENT</code> id the end and start colors have an alpha
   * channel otherwise <code>OPAQUE</code>.
   */
  public int getTransparency()
  {
    return ((((startColor & endColor) >> 24) & 0xFF) == 0xFF)
      ? OPAQUE
      : TRANSLUCENT;
  }

  /**
   * Draws a gradient on the given rectangle.
   *
   * @param g The graphics context.
   * @param start The start color of the gradient.
   * @param end The end color of the gradient.
   * @param rectangle The rectagle on which the gradient will be painted.
   * @param isVertical If the gradient should be vertical or horizontal.
   * @param isAscending If the gradient's transparency should be ascending.
   */
  public static final void drawGradient(Graphics g, Color start, Color end,
    Rectangle rectangle, boolean isVertical, boolean isAscending)
  {
    Graphics2D g2D = (Graphics2D)g;
    Paint gradient =
      new MetouiaGradients(start, end, isVertical, isAscending);
    g2D.setPaint(gradient);
    g2D.fill(rectangle);
  }

  /**
   * Draws a highlight gradient on the given rectangle.
   *
   * @param graphics The graphics context.
   * @param rectangle The rectagle on which the gradient will be painted.
   * @param isVertical If the gradient should be vertical or horizontal.
   * @param isAscending If the gradient's transparency should be ascending.
   */
  public static final void drawHighlight(Graphics graphics, Rectangle rectangle,
    boolean isVertical, boolean isAscending)
  {
    Graphics2D graphics2D = (Graphics2D)graphics;
    graphics2D.setPaint(
      new MetouiaGradients(
        reflection, reflectionFaded, isVertical, isAscending));
    graphics2D.fill(rectangle);
  }

  /**
   * Draws a shadow gradient on the given rectangle.
   *
   * @param graphics The graphics context.
   * @param rectangle The rectagle on which the gradient will be painted.
   * @param isVertical If the gradient should be vertical or horizontal.
   * @param isAscending If the gradient's transparency should be ascending.
   */
  public static final void drawShadow(Graphics graphics, Rectangle rectangle,
    boolean isVertical, boolean isAscending)
  {
    Graphics2D graphics2D = (Graphics2D)graphics;
    graphics2D.setPaint(
      new MetouiaGradients(
        shadowFaded, shadow, isVertical, isAscending));
    graphics2D.fill(rectangle);
  }

  /**
   * Draws a horizontal highlight on the given component.
   *
   * @param g The graphics context.
   * @param c The component ob wich the gradient will be painted.
   */
  public static final void drawHorizontalHighlight(Graphics g, Component c)
  {
    drawHighlight(g,
      new Rectangle(0, 0, c.getWidth(), c.getHeight() / 2), true, true);
  }

  /**
   * Draws a horizontal shadow on the given component.
   *
   * @param g The graphics context.
   * @param c The component ob wich the gradient will be painted.
   */
  public static final void drawHorizontalShadow(Graphics g, Component c)
  {
    drawShadow(g,
      new Rectangle(0, c.getHeight() / 2, c.getWidth(), c.getHeight() / 2),
      true, false);
  }

  /**
   * Draws a vertical highlight on the given component.
   *
   * @param g The graphics context.
   * @param c The component ob wich the gradient will be painted.
   */
  public static final void drawVerticalHighlight(Graphics g, Component c)
  {
    drawHighlight(g,
      new Rectangle(0, 0, c.getWidth() / 2, c.getHeight()),
      false, true);
  }

  /**
   * Draws a vertical shadow on the given component.
   *
   * @param g The graphics context.
   * @param c The component ob wich the gradient will be painted.
   */
  public static final void drawVerticalShadow(Graphics g, Component c)
  {
    drawShadow(g,
      new Rectangle(c.getWidth() / 2, 0, c.getWidth() / 2, c.getHeight()),
      false, false);
  }
}