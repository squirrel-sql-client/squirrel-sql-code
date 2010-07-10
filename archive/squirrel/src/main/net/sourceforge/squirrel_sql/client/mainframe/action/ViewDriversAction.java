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

import net.sourceforge.squirrel_sql.fw.gui.action.SelectInternalFrameAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.mainframe.DriversToolWindow;

/**
 * This <CODE>Action</CODE> displays the Drivers Tool Window.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ViewDriversAction extends SelectInternalFrameAction {

    /**
     * Ctor.
     *
     * @throws IllegalArgumentException
     *              Thrown if <TT>null</TT> <TT>DriversToolWindow</TT> or
     *              <TT>IApplication</TT> object passed.
     */
    public ViewDriversAction(IApplication app, DriversToolWindow window) throws IllegalArgumentException {
        super(window);
        if (app == null) {
            throw new IllegalArgumentException("null IApplication passed");
        }
        if (window == null) {
            throw new IllegalArgumentException("null DriversToolWindow passed");
        }
        app.getResources().setupAction(this);
    }
}
