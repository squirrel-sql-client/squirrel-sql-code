package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.awt.Rectangle;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

/**
 * This class will position an internal frame slightly below and to the
 * right of the internal frame that it previously positioned.
 */
public class CascadeInternalFramePositioner implements IInternalFramePositioner {

    private int _x = INITIAL_POS;
    private int _y = INITIAL_POS;

    private static final int MOVE = 20;
    private static final int INITIAL_POS = 5;

    public CascadeInternalFramePositioner() {
        super();
    }

    public void positionInternalFrame(JInternalFrame child) {
        if (child == null) {
            throw new IllegalArgumentException("null JInternalFrame passed");
        }
        if (!child.isClosed()) {
            if (child.getParent() != null) {
                Rectangle parentBounds = child.getParent().getBounds();
                if (_x >= (parentBounds.width - MOVE)) {
                    _x = INITIAL_POS;
                }
                if (_y >= (parentBounds.height - MOVE)) {
                    _y = INITIAL_POS;
                }
            }
            if (child.isIcon()) {
                try {
                    child.setIcon(false);
                } catch (PropertyVetoException ignore) {
                }
            } else if (child.isMaximum()) {
                try {
                    child.setMaximum(false);
                } catch (PropertyVetoException ignore) {
                }
            }
            child.setBounds(_x, _y, child.getWidth(), child.getHeight());
            _x+= MOVE;
            _y+= MOVE;
            /*try {
                child.setSelected(true);
            } catch (PropertyVetoException ignore) {
            }*/
        }
    }
}
