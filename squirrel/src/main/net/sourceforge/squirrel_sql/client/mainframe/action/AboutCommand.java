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

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.mainframe.AboutBoxDialog;

/**
 * This <CODE>ICommand</CODE> allows the user to copy an existing
 * <TT>ISQLAlias</TT> to a new one and then maintain the new one.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class AboutCommand implements ICommand {
    /** Application API. */
    private IApplication _app;

    /** Owner of the About Box. */
    private Frame _frame;

    /**
     * Ctor.
     *
     * @param   app     Application API.
     * @param   frame   Owning <TT>Frame</TT>.
     *
     * @throws  IllegalArgumentException
     *              Thrown if a <TT>null</TT> <TT>IApplication</TT> passed.
     */
    public AboutCommand(IApplication app, Frame frame) {
        super();
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        _app = app;
        _frame = frame;
    }

    /**
     * Display the ABout Box.
     */
    public void execute() {
        new AboutBoxDialog(_app, _frame).show();
    }
}
