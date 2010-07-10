package net.sourceforge.squirrel_sql.fw.util;
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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

import net.sourceforge.squirrel_sql.fw.util.Debug;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class Logger implements IMessageHandler {

    public interface ILogTypes {
        int DEBUG = 1;
        int ERROR = 2;
        int MSG = 3;
        int STATUS = 4;
    }

    private interface ILogTypeDescriptions {
        String DEBUG = "[Debug]";
        String ERROR = "[Error]";
        String MSG = "[Msg]";
        String STATUS = "[Status]";
        String UNKNOWN = "[Unknown]";
    }

    private boolean _initialised = false;

    private BufferedWriter _wtr;

    /**
     * Creates a logger that logs to standard output.
     */
    public Logger() {
        super();
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
    public Logger(String fileName) throws IllegalArgumentException, IOException {
        super();
        if (fileName == null || fileName.trim().length() == 0) {
            throw new IllegalArgumentException("Null or empty file name passed");
        }
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        if (!file.canWrite()) {
            throw new IOException("Cannot write to file: " + fileName);
        }
        _wtr = new BufferedWriter(new FileWriter(fileName, true));
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } catch (Throwable ignore) {
        }
        super.finalize();
    }

    public synchronized void close() {
        if (_wtr != null) {
            try {
                _wtr.close();
            } catch (IOException ex) {
                ex.printStackTrace(System.out);
            }
            _wtr = null;
        }
    }

    public synchronized void showMessage(Throwable th) {
        showMessage(ILogTypes.ERROR, th);
    }

    public synchronized void showMessage(int logType, Throwable th) {
        logString(logType, Utilities.getStackTrace(th));
        flush();
    }

    public synchronized void showMessage(String msg) {
        showMessage(ILogTypes.MSG, msg);
    }

    public synchronized void showMessage(int logType, String msg) {
        logString(logType, msg);
        flush();
    }

    private void flush() {
        if (_wtr != null) {
            try {
                _wtr.flush();
            } catch (IOException ex) {
                ex.printStackTrace(System.out);
            }
        } else {
            System.out.flush();
        }
    }

    private void logString(int logType, String msg) {
        String line = getLogTypeDescription(logType) + msg;
        if (_wtr != null) {
            try {
                _wtr.write(line);
                _wtr.newLine();
            } catch (IOException ex) {
                ex.printStackTrace(System.out);
            }
        } else {
            System.out.println(line);
        }
    }

    private String getLogTypeDescription(int logType) {
        switch(logType) {
            case ILogTypes.DEBUG: {
                return ILogTypeDescriptions.DEBUG;
            }
            case ILogTypes.ERROR: {
                return ILogTypeDescriptions.ERROR;
            }
            case ILogTypes.MSG: {
                return ILogTypeDescriptions.MSG;
            }
            case ILogTypes.STATUS: {
                return ILogTypeDescriptions.STATUS;
            }
            default: {
                return ILogTypeDescriptions.UNKNOWN;
            }
        }
    }
}
