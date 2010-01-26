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


//$Id: RestoreIcon.java,v 1.1 2010-01-26 21:09:41 manningr Exp $
package net.infonode.gui.icon.button;

import net.infonode.gui.GraphicsUtil;

import java.awt.*;

/**
 * @author $Author: manningr $
 * @version $Revision: 1.1 $
 */
public class RestoreIcon extends AbstractButtonIcon {
  private static final long serialVersionUID = 4019344427358669254L;

  public RestoreIcon() {
    super();
  }

  public RestoreIcon(Color color) {
    super(color);
  }

  public RestoreIcon(Color color, int size) {
    super(color, size);
  }

  public RestoreIcon(int size) {
    super(size);
  }

  protected void paintIcon(Component c, Graphics g, int x1, int y1, int x2, int y2) {
    int boxHeight = (2 * (y2 - y1 + 1)) / 3 - 1;
    int boxWidth = (5 * (x2 - x1 + 1)) / 6 - 1;

    // Upper box
    GraphicsUtil.drawOptimizedLine(g, x2 - boxWidth, y1, x2, y1);
    GraphicsUtil.drawOptimizedLine(g, x2, y1, x2, y1 + boxHeight);
    GraphicsUtil.drawOptimizedLine(g, x2 - boxWidth, y1 + 1, x2 - boxWidth, y2 - boxHeight);
    GraphicsUtil.drawOptimizedLine(g, x1 + boxWidth + 1, y1 + boxHeight, x2 - 1, y1 + boxHeight);

    // Lower box
    GraphicsUtil.drawOptimizedLine(g, x1, y2 - boxHeight, x1, y2);
    GraphicsUtil.drawOptimizedLine(g, x1 + 1, y2, x1 + boxWidth, y2);
    GraphicsUtil.drawOptimizedLine(g, x1 + boxWidth, y2 - 1, x1 + boxWidth, y2 - boxHeight + 1);
    GraphicsUtil.drawOptimizedLine(g, x1 + 1, y2 - boxHeight, x1 + boxWidth, y2 - boxHeight);
  }
}
