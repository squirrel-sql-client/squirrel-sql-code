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


// $Id: BackgroundPainter.java,v 1.1 2010-01-26 21:09:41 manningr Exp $
package net.infonode.gui;

import net.infonode.gui.componentpainter.ComponentPainter;

/**
 * An object that paints its background using a {@link ComponentPainter}.
 *
 * @author $Author: manningr $
 * @version $Revision: 1.1 $
 * @since IDW 1.2.0
 */
public interface BackgroundPainter {
  /**
   * Returns the {@link ComponentPainter} that is used to paint the background of this object.
   *
   * @return the {@link ComponentPainter} that is used to paint the background of this object, null if there is none
   */
  ComponentPainter getComponentPainter();
}
