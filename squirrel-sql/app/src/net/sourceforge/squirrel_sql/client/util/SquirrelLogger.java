package net.sourceforge.squirrel_sql.client.util;
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
import java.io.IOException;
import java.util.Calendar;

import net.sourceforge.squirrel_sql.fw.util.Logger;

import net.sourceforge.squirrel_sql.client.Version;

/**
 * Application Logger.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SquirrelLogger extends Logger {
    private boolean _initialised = false;

    /**
     * Creates a logger that logs to standard output.
     */
    public SquirrelLogger() {
        super();
        writeInitialisationInfo();
    }
    /**
     * Creates a logger that logs to the specified file.
     *
     * @param   fileName    Name of file to log to.
     *
     * @throws  IllegalArgumentException
     *          <TT>null</TT> or empty file name passed.
     * @throws  IOException
     *          Unable to write to the specified file.
     */
    public SquirrelLogger(String fileName) throws IllegalArgumentException, IOException {
        super(fileName);
        writeInitialisationInfo();
    }

    public synchronized void close() {
        if (_initialised) {
            showMessage(ILogTypes.MSG, Version.getVersion() + " ended: " + Calendar.getInstance().getTime());
        }
        super.close();
    }

    private void writeInitialisationInfo() {
        showMessage(ILogTypes.MSG, "=======================================================");
        showMessage(ILogTypes.MSG, "=======================================================");
        showMessage(ILogTypes.MSG, "=======================================================");
        showMessage(ILogTypes.MSG, Version.getVersion() + " started: " + Calendar.getInstance().getTime());
        showMessage(ILogTypes.MSG, Version.getCopyrightStatement());
        showMessage(ILogTypes.MSG, "java.vendor:       " + System.getProperty("java.vendor"));
        showMessage(ILogTypes.MSG, "java.version:      " + System.getProperty("java.version"));
        showMessage(ILogTypes.MSG, "java.runtime.name: " + System.getProperty("java.runtime.name"));
        showMessage(ILogTypes.MSG, "os.name:           " + System.getProperty("os.name"));
        showMessage(ILogTypes.MSG, "os.version:        " + System.getProperty("os.version"));
        showMessage(ILogTypes.MSG, "os.arch:           " + System.getProperty("os.arch"));
        showMessage(ILogTypes.MSG, "user.dir:          " + System.getProperty("user.dir"));
        showMessage(ILogTypes.MSG, "user.home:         " + System.getProperty("user.home"));
        showMessage(ILogTypes.MSG, "java.home:         " + System.getProperty("java.home"));
        showMessage(ILogTypes.MSG, "java.class.path:   " + System.getProperty("java.class.path"));

        _initialised = true;
    }

}

