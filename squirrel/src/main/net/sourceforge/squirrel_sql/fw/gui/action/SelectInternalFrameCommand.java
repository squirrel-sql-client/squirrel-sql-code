package net.sourceforge.squirrel_sql.fw.gui.action;
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
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class SelectInternalFrameCommand implements ICommand {
    private JInternalFrame _child;

    public SelectInternalFrameCommand(JInternalFrame child) {
        super();
        if (child == null) {
            throw new IllegalArgumentException("Null JInternalFrame passed");
        }
        _child = child;
    }

    public void execute() {
        try {
            if (!_child.isSelected()) {
                _child.setSelected(true);
            } else {
                _child.moveToBack();
            }
        } catch (PropertyVetoException ignore) {
        }
    }
}
