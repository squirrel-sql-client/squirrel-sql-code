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

import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.mainframe.DriversList;

/**
 * This <CODE>Action</CODE> allows the user to copy a <TT>ISQLDriver</TT>
 * and maintain the newly copied one.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class CopyDriverAction extends SquirrelAction {
    /**
     * List of all the users drivers.
     */
    private DriversList _drivers;

    /**
     * Ctor specifying the list of drivers.
     *
     * @param   app     Application API.
     * @param   list    List of <TT>ISQLDriver</TT> objects.
     *
     * @throws  IllegalArgumentException
     *              thrown if a <TT>null</TT> <TT>DriversList</TT> passed.
     */
    public CopyDriverAction(IApplication app, DriversList list)
            throws IllegalArgumentException {
        super(app);
        if (list == null) {
            throw new IllegalArgumentException("Null DriversList passed");
        }
        _drivers = list;
    }

    /**
     * Perform this action. Use the <TT>CopyDriverCommand</TT>.
     *
     * @param   evt     The current event.
     */
    public void actionPerformed(ActionEvent evt) {
        ISQLDriver driver = _drivers.getSelectedDriver();
        if (driver != null) {
            new CopyDriverCommand(getApplication(), getParentFrame(evt), driver).execute();
        }
    }
}
