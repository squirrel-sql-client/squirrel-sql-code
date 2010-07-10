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
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

import com.ice.util.JFCUtilities;

public class BasePopupMenu extends JPopupMenu {

    /**
     * Safely position this popup menu so that it doesn't vanish off
     * the screen.
     *
     * @param   evt     <TT>MouseEvent</TT> asking to display this popup.
     */
    public void show(MouseEvent evt) {
        Point pt = JFCUtilities.computePopupLocation(evt, evt.getComponent(), this);
        show(evt.getComponent(), pt.x, pt.y);
    }
}

