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
import java.beans.PropertyVetoException;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.mainframe.DriversToolWindow;

/**
 * This <CODE>Action</CODE> allows the user to create a new <TT>ISQLDriver</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class CreateDriverAction extends SquirrelAction {

    /**
     * Ctor.
     *
     * @param   app     Application API.
     */
    public CreateDriverAction(IApplication app) {
        super(app);
    }

    /**
     * Perform this action. Execute the create driver command.
     *
     * @param   evt     The current event.
     */
    public void actionPerformed(ActionEvent evt) {
		IApplication app = getApplication();
		DriversToolWindow tw = app.getMainFrame().getDriversToolWindow();
		tw.moveToFront();
		try {
			tw.setSelected(true);
		} catch (PropertyVetoException ignore) {
		}
        new CreateDriverCommand(app, getParentFrame(evt)).execute();
    }
}
