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
*  MetouiaDotsBuffer.java                                                      *
*   Original Author:  Taoufik Romdhane                                         *
*   Contributor(s):                                                            *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package net.sourceforge.mlf.metouia.borders;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import net.sourceforge.mlf.metouia.MetouiaLookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * This class represents a dots buffer providing images filled with dots.
 *
 * @author Taoufik Romdhane
 */
class MetouiaDotsBuffer
{

  /**
   * The frame object, needed for th graphics context.
   */
  private static Frame frame;

  /**
   * The component, needed for the image object.
   */
  private static Component component;

  /**
   * The image itself, used by MetouiaDots.
   */
  private transient Image image;

  /**
   * The image size: should be a multiplicator of the dots size with insets.
   */
  private static final int IMAGE_SIZE = 125;

  /**
   * The image dimension.
   */
  private static Dimension imageSize = new Dimension(IMAGE_SIZE, IMAGE_SIZE);

  /**
   * The dots background color: usually the control's background.
   */
  private Color background;

  /**
   * The dots highlight color: almost white.
   */
  private Color highlight;

  /**
   * The dots shadow color: ordered by their light values.
   */
  private Color shadow;

  /**
   * The dots darker shadow color: ordered by their light values.
   */
  private Color darkshadow;

  /**
   * The dots even darker shadow color: ordered by their light values.
   */
  private Color matteshadow;

  /**
   * Creates a new Metouia dots buffer with standard colors taken from the
   * current theme.
   */
  public MetouiaDotsBuffer()
  {
    // Create a frame in order to get a graphics context:
    if (frame == null)
    {
      frame = new Frame("bufferCreator");
    }
    if (component == null)
    {
      component = new Canvas();
      frame.add(component, BorderLayout.CENTER);
    }
    frame.addNotify();

    // Create the image to draw on:
    image = component.createImage(IMAGE_SIZE, IMAGE_SIZE);

    // Set the colors:
    background = MetalLookAndFeel.getControl();
    matteshadow = MetouiaLookAndFeel.getPrimaryControl();
    shadow = MetouiaLookAndFeel.getControlShadow();
    darkshadow = MetouiaLookAndFeel.getControlDarkShadow();
    highlight = MetouiaLookAndFeel.getPrimaryControlHighlight();

    // Fill the buffer with dots:
    fillBumpBuffer();
  }

  /**
   * Gets the buffers image which is filled with the Metouia dots.
   *
   * @return The buffer's image filld with dots.
   */
  public Image getImage()
  {
    if (image == null)
    {
      image = component.createImage(IMAGE_SIZE, IMAGE_SIZE);
      fillBumpBuffer();
    }
    return image;
  }

  /**
   * Gets the buffer's image size.
   *
   * @return The size of buffer's image.
   */
  public Dimension getImageSize()
  {
    return imageSize;
  }

  /**
   * Fills the buffer with the metouia dots.
   */
  protected void fillBumpBuffer()
  {
    Graphics g = image.getGraphics();

    g.setColor(background);
    g.fillRect(0, 0, IMAGE_SIZE, IMAGE_SIZE);

    g.setColor(matteshadow);
    for (int x = 0; x < IMAGE_SIZE; x += 5)
    {
      for (int y = 0; y < IMAGE_SIZE; y += 5)
      {
        g.drawLine(x, y, x, y);
      }
    }

    g.setColor(shadow);
    for (int x = 1; x < IMAGE_SIZE; x += 5)
    {
      for (int y = 0; y < IMAGE_SIZE; y += 5)
      {
        g.drawLine(x, y, x, y);
        g.drawLine(x-1, y+1, x-1, y+1);
      }
    }

    g.setColor(darkshadow);
    for (int x = 1; x < IMAGE_SIZE; x += 5)
    {
      for (int y = 1; y < IMAGE_SIZE; y += 5)
      {
        g.drawLine(x, y, x, y);
      }
    }

    g.setColor(highlight);
    for (int x = 2; x < IMAGE_SIZE; x += 5)
    {
      for (int y = 1; y < IMAGE_SIZE; y += 5)
      {
        g.drawLine(x, y, x, y+1);
        g.drawLine(x+1, y+1, x+1, y+1);
      }
    }

    g.dispose();
  }
}