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
import java.awt.Frame;

import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.db.DriverMaintDialog;
import net.sourceforge.squirrel_sql.client.db.DataCache;

/**
 * This <CODE>ICommand</CODE> allows the user to create a new <TT>ISQLDriver</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class CreateDriverCommand implements ICommand {
    /** Application API. */
    private final IApplication _app;

    /** Owner of the maintenance dialog. */
    private Frame _frame;

    /**
     * Ctor.
     *
     * @param   app         Application API.
     * @param   frame       Owning <TT>Frame</TT>.
     *
     * @throws  IllegalArgumentException
     *              Thrown if a <TT>null</TT> <TT>IApplication</TT> passed.
     */
    public CreateDriverCommand(IApplication app, Frame frame) throws IllegalArgumentException {
        super();
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }

        _app = app;
        _frame = frame;
    }

    /**
     * Create a new <TT>ISQLDriver</TT> objwect and display a dialog
     * allowing the user to maintain it.
     */
    public void execute() {
        final DataCache cache = _app.getDataCache();
        final IdentifierFactory factory = IdentifierFactory.getInstance();
        final ISQLDriver driver = cache.createDriver(factory.createIdentifier());
        new DriverMaintDialog(_app, _frame, driver,
                                    DriverMaintDialog.MaintenanceType.NEW).show();
    }
}
