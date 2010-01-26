/*
 * Copyright (C) 2004 NNL Technology AB
 * Visit www.infonode.net for information about InfoNode(R) 
 * products and how to contact NNL Technology AB.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, 
 * MA 02111-1307, USA.
 */


// $Id: ColorValue.java,v 1.1 2010-01-26 21:09:41 manningr Exp $
package net.infonode.gui.laf.value;

import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * @author $Author: manningr $
 * @version $Revision: 1.1 $
 */
public class ColorValue {
  private ColorUIResource color;
  private ColorUIResource defaultColor;

  public ColorValue() {
    this(Color.BLACK);
  }

  public ColorValue(int r, int g, int b) {
    this(new ColorUIResource(r, g, b));
  }

  public ColorValue(Color defaultColor) {
    this(new ColorUIResource(defaultColor));
  }

  public ColorValue(ColorUIResource defaultColor) {
    this.defaultColor = defaultColor;
  }

  public ColorUIResource getColor() {
    return color == null ? defaultColor : color;
  }

  public void setColor(Color color) {
    setColor(new ColorUIResource(color));
  }

  public void setColor(ColorUIResource color) {
    this.color = color;
  }

  public void setDefaultColor(Color defaultColor) {
    setDefaultColor(new ColorUIResource(defaultColor));
  }

  public void setDefaultColor(ColorUIResource defaultColor) {
    this.defaultColor = defaultColor;
  }

  public void setDefaultColor(ColorValue defaultColor) {
    setDefaultColor(defaultColor.getColor());
  }
}
