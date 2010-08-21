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
*  MetouiaBorderUtilities.java                                                 *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia.borders;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.basic.BasicBorders;
import net.sourceforge.mlf.metouia.MetouiaLookAndFeel;

/**
 * This is a utility class for painting simple 3d borders, and providding common
 * ones.
 *
 * @author Taoufik Romdhane
 */
public class MetouiaBorderUtilities
{

  /**
   * Cached botton border instance.
   */
  private static Border buttonBorder;

  /**
   * Cached text border instance.
   */
  private static Border textBorder;

  /**
   * Cached text field border instance.
   */
  private static Border textFieldBorder;

  /**
   * Cached toggle botton border instance.
   */
  private static Border toggleButtonBorder;

  /**
   * Draws a simple 3d border.
   *
   * @param g The graphics context.
   * @param r The rectangle object defining the bounds of the border.
   */
  static void drawSimple3DBorder(Graphics g, Rectangle r)
  {
    drawSimple3DBorder(g, r.x, r.y, r.width, r.height);
  }

  /**
   * Draws a simple 3d border.
   *
   * @param g The graphics context.
   * @param x The x coordinate of the top left corner.
   * @param y The y coordinate of the top left corner.
   * @param w The width.
   * @param h The height.
   */
  static void drawSimple3DBorder(Graphics g, int x, int y, int w, int h)
  {
    drawSimple3DBorder(g, x, y, w, h, MetouiaLookAndFeel.getControlHighlight(),
      MetouiaLookAndFeel.getControlDarkShadow());
  }

  /**
   * Draws a pressed simple 3d border.
   * It is used for things like pressed buttons.
   *
   * @param g The graphics context.
   * @param r The rectangle object defining the bounds of the border.
   */
  static void drawPressed3DBorder(Graphics g, Rectangle r)
  {
    drawPressed3DBorder(g, r.x, r.y, r.width, r.height);
  }

  /**
   * Draws a disabled simple 3d border.
   *
   * @param g The graphics context.
   * @param x The x coordinate of the top left corner.
   * @param y The y coordinate of the top left corner.
   * @param w The width.
   * @param h The height.
   */
  public static void drawDisabledBorder(Graphics g, int x, int y, int w, int h)
  {
    drawSimple3DBorder(g, x, y, w, h, MetouiaLookAndFeel.getControlHighlight(),
      MetouiaLookAndFeel.getControlShadow());
  }

  /**
   * Draws a pressed simple 3d border.
   * It is used for things like pressed buttons.
   *
   * @param g The graphics context.
   * @param x The x coordinate of the top left corner.
   * @param y The y coordinate of the top left corner.
   * @param w The width.
   * @param h The height.
   */
  static void drawPressed3DBorder(Graphics g, int x, int y, int w, int h)
  {
    drawSimple3DBorder(g, x, y, w, h, MetouiaLookAndFeel.getControlDarkShadow(),
      MetouiaLookAndFeel.getControlHighlight());
  }

  /**
   * Draws a simple 3d border with specified colors.
   *
   * @param g The graphics context.
   * @param x The x coordinate of the top left corner.
   * @param y The y coordinate of the top left corner.
   * @param w The width.
   * @param h The height.
   * @param highlight The highlight color to use.
   * @param shadow The shadow color to use.
   */
  public static final void drawSimple3DBorder(Graphics g, int x, int y, int w,
    int h, Color highlight, Color shadow)
  {
    g.translate(x, y);

    g.setColor(highlight);
    g.drawLine(0, 0, w - 2, 0);
    g.drawLine(0, 1, 0, h - 1);

    g.setColor(shadow);
    g.drawLine(w - 1, 0, w - 1, h - 2);
    g.drawLine(1, h - 1, w - 1, h - 1);

    g.translate(-x, -y);
  }

  /**
   * Draws a bevel 3d border with the specified colors.
   *
   * @param g The graphics context.
   * @param x The x coordinate of the top left corner.
   * @param y The y coordinate of the top left corner.
   * @param w The width.
   * @param h The height.
   * @param highlight The highlight color to use.
   * @param shadow The shadow color to use.
   * @param innerHighlight The inner highlight color to use.
   * @param innerShadow The inner shadow color to use.
   */
  public static final void drawBevel3DBorder(Graphics g, int x, int y, int w,
    int h, Color highlight, Color shadow, 
    Color innerHighlight, Color innerShadow)
  {
    g.translate(x, y);

    g.setColor(highlight);
    g.drawLine(0, 0, w - 2, 0);
    g.drawLine(0, 1, 0, h - 1);

    g.setColor(shadow);
    g.drawLine(w - 1, 0, w - 1, h - 2);
    g.drawLine(1, h - 1, w - 1, h - 1);

    x++;
    y++;
    w -= 1;
    h -= 1;
    
    g.setColor(innerHighlight);
    g.drawLine(0, 0, w - 2, 0);
    g.drawLine(0, 1, 0, h - 1);

    g.setColor(innerShadow);
    g.drawLine(w - 1, 0, w - 1, h - 2);
    g.drawLine(1, h - 1, w - 1, h - 1);

    g.translate(-x, -y);
  }

  /**
   * Draws a pressed simple 3d border.
   * It is used for things like text fields.
   *
   * @param g The graphics context.
   * @param x The x coordinate of the top left corner.
   * @param y The y coordinate of the top left corner.
   * @param w The width.
   * @param h The height.
   */
  static void drawPressed3DFieldBorder(Graphics g, int x, int y, int w, int h)
  {
    g.translate(x, y);

    g.setColor(MetouiaLookAndFeel.getControlHighlight());
    g.drawRect(1, 1, w - 2, h - 2);

    g.setColor(MetouiaLookAndFeel.getControlDarkShadow());
    g.drawRect(0, 0, w - 2, h - 2);

    g.translate(-x, -y);
  }

  /**
   * Draws an active button border (normal state).
   *
   * @param g The graphics context.
   * @param x The x coordinate of the top left corner.
   * @param y The y coordinate of the top left corner.
   * @param w The width.
   * @param h The height.
   */
  static void drawDefaultButtonBorder(Graphics g, int x, int y, int w, int h)
  {
    drawSimple3DBorder(g, x + 1, y + 1, w - 2, h - 2,
      MetouiaLookAndFeel.getControlHighlight(),
      MetouiaLookAndFeel.getControlShadow());

    g.setColor(MetouiaLookAndFeel.getControlDarkShadow());
    g.drawRect(x, y, w - 1, h - 1);
  }

  /**
   * Gets a border instance for a button.
   * The border instance is cached for future use.
   *
   * @return A border instance for a button.
   */
  public static Border getButtonBorder()
  {
    if (buttonBorder == null)
    {
      buttonBorder = new BorderUIResource.CompoundBorderUIResource(
        new MetouiaButtonBorder(), new BasicBorders.MarginBorder());
    }
    return buttonBorder;
  }

  /**
   * Gets a border instance for a text component.
   * The border instance is cached for future use.
   *
   * @return A border instance for a text component.
   */
  public static Border getTextBorder()
  {
    if (textBorder == null)
    {
      textBorder = new BorderUIResource.CompoundBorderUIResource(
        new MetouiaTextFieldBorder(), new BasicBorders.MarginBorder());
    }
    return textBorder;
  }

  /**
   * Gets a border instance for a text field component.
   * The border instance is cached for future use.
   *
   * @return A border instance for a text field component.
   */
  public static Border getTextFieldBorder()
  {
    if (textFieldBorder == null)
    {
      textFieldBorder = new BorderUIResource.CompoundBorderUIResource(
        new MetouiaTextFieldBorder(), new BasicBorders.MarginBorder());
    }
    return textFieldBorder;
  }

  /**
   * Gets a border instance for a toggle button.
   * The border instance is cached for future use.
   *
   * @return A border instance for a toggle button.
   */
  public static Border getToggleButtonBorder()
  {
    if (toggleButtonBorder == null)
    {
      toggleButtonBorder = new BorderUIResource.CompoundBorderUIResource(
        new MetouiaToggleButtonBorder(), new BasicBorders.MarginBorder());
    }
    return toggleButtonBorder;
  }


  /**
   * Gets a border instance for a desktop icon.
   *
   * @return A border instance for a desktop icon.
   */
  public static Border getDesktopIconBorder()
  {
    return new BorderUIResource.CompoundBorderUIResource(
      new LineBorder(MetouiaLookAndFeel.getControlDarkShadow(), 1),
      new MatteBorder(2, 2, 1, 2, MetouiaLookAndFeel.getControl()));
  }
}