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


// $Id: ColorProviderUtil.java,v 1.1 2010-01-26 21:09:41 manningr Exp $
package net.infonode.gui.colorprovider;

import java.awt.*;

/**
 * Utility methods for {@link ColorProvider}'s.
 *
 * @author $Author: manningr $
 * @version $Revision: 1.1 $
 */
public class ColorProviderUtil {
  private ColorProviderUtil() {
  }

  /**
   * Returns a {@link ColorProvider} for the color. If the color is null the default provider is returned.
   *
   * @param color           the color for which to return a provider
   * @param defaultProvider the default provider
   * @return a color provider for the color, if the color is null the default provider is returned
   */
  public static ColorProvider getColorProvider(Color color, ColorProvider defaultProvider) {
    return color == null ? (ColorProvider) defaultProvider : new FixedColorProvider(color);
  }

}
