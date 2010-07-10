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
import java.awt.Component;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ToolBar extends JToolBar {

    public JButton add(Action action) {
        JButton btn = super.add(action);
        initialiseButton(action, btn);
        return btn;
    }

    public JButton add(Action action, JButton btn) {
        super.add(btn);
        initialiseButton(action, btn);
        return btn;
    }

    public void setUseRolloverButtons(boolean value) {
        putClientProperty("JToolBar.isRollover", value ? Boolean.TRUE : Boolean.FALSE);
    }

    protected void initialiseButton(Action action, JButton btn) {
        if (btn != null) {
            btn.setRequestFocusEnabled(false);
            btn.setText("");
            String tt = null;
            if (action != null) {
                tt = (String)action.getValue(Action.SHORT_DESCRIPTION);
            }
            btn.setToolTipText(tt != null ? tt : "");
        }
    }
}
