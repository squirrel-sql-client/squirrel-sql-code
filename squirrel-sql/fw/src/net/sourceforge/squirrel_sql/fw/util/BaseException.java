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
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * Base exception class.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class BaseException extends Exception {

    /** If this exception is wrapped around another it is stored here. */
    private Exception _wrapee;

    /**
     * Default ctor. Creates an exception with an empty string ("")
     * as its message.
     */
    public BaseException() {
        this("");
    }

    /**
     * Ctor specifying the message.
     *
     * @param   msg     The message.
     */
    public BaseException(String msg) {
        super(msg != null ? msg : "");
    }

    /**
     * Ctor specifying an exception that this one should
     * be wrapped around.
     *
     * @param   wrapee      The wrapped exception.
     */
    public BaseException(Exception wrapee) {
        super(getMessageFromException(wrapee));
        _wrapee = wrapee;
    }

    public String toString() {
        if (_wrapee != null) {
            return _wrapee.toString();
        }
        return super.toString();
    }

    public void printStackTrace() {
        if (_wrapee != null) {
            _wrapee.printStackTrace();
        } else {
            super.printStackTrace();
        }
    }

    public void printStackTrace(PrintStream s) {
        if (_wrapee != null) {
            _wrapee.printStackTrace(s);
        } else {
            super.printStackTrace(s);
        }
    }

    public void printStackTrace(PrintWriter wtr) {
        if (_wrapee != null) {
            _wrapee.printStackTrace(wtr);
        } else {
            super.printStackTrace(wtr);
        }
    }

    private static String getMessageFromException(Exception ex) {
        String rtn = "";
        if (ex != null) {
            String msg = ex.getMessage();
            if (msg != null) {
                rtn = msg;
            }
        }
        return rtn;
    }
}

