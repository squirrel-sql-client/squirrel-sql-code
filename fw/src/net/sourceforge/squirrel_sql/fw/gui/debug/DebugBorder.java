/*
 * Copyright (C) 2008 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.sourceforge.squirrel_sql.fw.gui.debug;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.Border;

/**
 *  This code was developed originally by Romain Guy and is also available here:
 *  
 *  http://www.jroller.com/gfx/entry/dynamic_debugging_with_swing
 *  
 *  A simple custom border that can be used to "highlight" the component that it is installed in.
 */
public class DebugBorder implements Border {
    private Border b;

    public DebugBorder(Border b) {
        this.b = b;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        b.paintBorder(c, g, x, y, width, height);
        Insets insets = b.getBorderInsets(c);
        Color layerColor = new Color(0.0f, 1.0f, 0.0f, 0.35f);
        g.setColor(layerColor);
        // top
        g.fillRect(x, y, width, insets.top);
        // left
        g.fillRect(x, y + insets.top, insets.left, height - insets.bottom - insets.top);
        // bottom
        g.fillRect(x, y + height - insets.bottom, width, insets.bottom);
        // right
        g.fillRect(x + width - insets.right, y + insets.top, insets.right, height - insets.bottom - insets.top);
    }

    public Insets getBorderInsets(Component c) {
        return b.getBorderInsets(c);
    }

    public boolean isBorderOpaque() {
        return b.isBorderOpaque();
    }

    public Border getDelegate() {
        return b;
    }
}
