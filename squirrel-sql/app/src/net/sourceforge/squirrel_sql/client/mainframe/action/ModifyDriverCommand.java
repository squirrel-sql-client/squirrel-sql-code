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
import java.awt.Frame;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.db.DriverMaintDialog;

/**
 * This <CODE>ICommand</CODE> allows the user to modify an existing
 * <TT>ISQLDriver</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class ModifyDriverCommand implements ICommand {
    /** Application API. */
    private final IApplication _app;

    /** Owner of the maintenance dialog. */
    private Frame _frame;

    /** <TT>ISQLDriver</TT> to be modified. */
    private ISQLDriver _driver;

    /**
     * Ctor.
     *
     * @param   app         Application API.
     * @param   frame       Owning <TT>Frame</TT>.
     * @param   sqlDriver   <TT>ISQLDriver</TT> to be modified.
     *
     * @throws  IllegalArgumentException
     *              Thrown if a <TT>null</TT> <TT>ISQLDriver</TT> or
     *              <TT>IApplication</TT> passed.
     */
    public ModifyDriverCommand(IApplication app, Frame frame, ISQLDriver driver)
            throws IllegalArgumentException {
        super();
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        if (driver == null) {
            throw new IllegalArgumentException("Null ISQLDriver passed");
        }
        _app = app;
        _frame = frame;
        _driver = driver;
    }
    /**
     * Display a dialog allowing user to maintain the <TT>ISQLDriver</TT>.
     */
    public void execute() {
        new DriverMaintDialog(_app, _frame, _driver,
                DriverMaintDialog.MaintenanceType.MODIFY).show();
    }
}