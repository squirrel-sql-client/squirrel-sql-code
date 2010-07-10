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
import net.sourceforge.squirrel_sql.client.mainframe.AliasesToolWindow;

/**
 * This <CODE>Action</CODE> displays the Aliases Tool Window.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ViewAliasesAction extends SelectInternalFrameAction {
    /**
     * Ctor specifying the Aliases Tool Window.
     *
     * @throws IllegalArgumentException
     *              Thrown if <TT>null</TT> <TT>AliasesToolWindow</TT> passed.
     */
    public ViewAliasesAction(IApplication app, AliasesToolWindow window) throws IllegalArgumentException {
        super(window);
        if (window == null) {
            throw new IllegalArgumentException("null AliasesToolWindow passed");
        }
        app.getResources().setupAction(this);
    }
}
