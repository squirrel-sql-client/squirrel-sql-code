package net.sourceforge.squirrel_sql.client.mainframe.action;
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
import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.action.TileInternalFramesAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.MainFrame;

/**
 * This <CODE>Action</CODE> tiles the internal frames.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class TileAction extends TileInternalFramesAction {

    /**
     * Ctor.
     *
     * @param   app     Application API.
     */
    public TileAction(IApplication app) {
        super();
        app.getResources().setupAction(this);
    }

    public void actionPerformed(ActionEvent evt) {
        CursorChanger cursorChg = new CursorChanger(MainFrame.getInstance());
        cursorChg.show();
        try {
            super.actionPerformed(evt);
        } finally {
            cursorChg.restore();
        }
    }
}
