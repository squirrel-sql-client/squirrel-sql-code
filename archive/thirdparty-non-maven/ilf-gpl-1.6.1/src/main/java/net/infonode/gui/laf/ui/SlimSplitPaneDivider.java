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


// $Id: SlimSplitPaneDivider.java,v 1.1 2010-01-26 21:09:41 manningr Exp $
package net.infonode.gui.laf.ui;

import net.infonode.gui.icon.button.ArrowIcon;
import net.infonode.util.Direction;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;

/**
 * @author $Author: manningr $
 * @version $Revision: 1.1 $
 */
public class SlimSplitPaneDivider extends BasicSplitPaneDivider {
  public SlimSplitPaneDivider(BasicSplitPaneUI ui) {
    super(ui);
  }

  protected JButton createLeftOneTouchButton() {
    ArrowIcon icon = new ArrowIcon(8, Direction.LEFT);
    icon.setShadowEnabled(false);
    JButton button = new JButton(icon);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setRequestFocusEnabled(false);
    return button;
  }

  protected JButton createRightOneTouchButton() {
    ArrowIcon icon = new ArrowIcon(8, Direction.RIGHT);
    icon.setShadowEnabled(false);
    JButton button = new JButton(icon);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setRequestFocusEnabled(false);
    return button;
  }
}
