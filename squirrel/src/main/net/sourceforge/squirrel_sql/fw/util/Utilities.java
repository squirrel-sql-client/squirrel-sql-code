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
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * General purpose utilities functions.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class Utilities {
    private static final String EMPTY_STRING = "";

    /**
     * Ctor. <TT>private</TT> as all methods are static.
     */
    private Utilities() {
        super();
    }

    /**
     * Print the current stack trace to <TT>ps</TT>.
     *
     * @param   ps  The <TT>PrintStream</TT> to print stack trace to.
     *
     * @throws  IllegalArgumentException    If a null <TT>ps</TT> passed.
     */
    public static void printStackTrace(PrintStream ps) throws IllegalArgumentException {
        if (ps == null) {
            throw new IllegalArgumentException("null PrintStream passed.");
        }

        try {
            throw new Exception();
        } catch (Exception ex) {
            ps.println(getStackTrace(ex));
        }
    }

    /**
     * Print the current stack trace to <TT>logger</TT>.
     *
     * @param   logger  The <TT>Logger</TT> to print stack trace to.
     *
     * @throws  IllegalArgumentException    If a null <TT>Logger</TT> passed.
     */
    public static void printStackTrace(Logger logger) throws IllegalArgumentException {
        if (logger == null) {
            throw new IllegalArgumentException("null Logger passed.");
        }

        try {
            throw new Exception();
        } catch (Exception ex) {
            logger.showMessage(Logger.ILogTypes.MSG, getStackTrace(ex));
        }
    }

    /**
     * Return the stack trace from the passed exception as a string
     *
     * @param   th  The exception to retrieve stack trace for.
     */
    public static String getStackTrace(Throwable th) throws IllegalArgumentException {
        if (th == null) {
            throw new IllegalArgumentException("null exception passed.");
        }

        StringWriter sw = new StringWriter();
        try {
            PrintWriter pw = new PrintWriter(sw);
            try {
                th.printStackTrace(pw);
                return sw.toString();
            } finally {
                pw.close();
            }
        } finally {
            try {
                sw.close();
            } catch (IOException ignore) {
            }
        }
    }

    /**
     * Change the passed class name to its corresponding file name. E.G.
     * change &quot;Utilities&quot; to &quot;Utilities.class&quot;.
     *
     * @param   name    Class name to be changed.
     *
     * @throws  IllegalArgumentException    If a null <TT>name</TT> passed.
     */
    public static String changeClassNameToFileName(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("null Class Name passed.");
        }
        return name.replace('.', '/').concat(".class");
    }

    /**
     * Change the passed file name to its corresponding class name. E.G.
     * change &quot;Utilities.class&quot; to &quot;Utilities&quot;.
     *
     * @param   name    Class name to be changed. If this does not represent
     *                  a Java class then <TT>null</TT> is returned.
     *
     * @throws  IllegalArgumentException    If a null <TT>name</TT> passed.
     */
    public static String changeFileNameToClassName(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("null File Name passed.");
        }
        String className = null;
        if (name.toLowerCase().endsWith(".class")) {
            className = name.replace('/', '.');
            className = className.replace('\\', '.');
            className = className.substring(0, className.length() - 6);
        }
        return className;
    }

    /**
     * Clean the passed string. Replace new line characters and tabs with
     * single spaces. If a <TT>null</TT> string passed return an empty
     * string.
     *
     * @param   str     String to be cleaned.
     *
     * @return  Cleaned string.
     */
    public static String cleanString(String str) {
        String newStr = str.replace('\n', ' ');
        newStr = newStr.replace('\r', ' ');
        newStr = newStr.replace('\t', ' ');
        return newStr;
    }
}
