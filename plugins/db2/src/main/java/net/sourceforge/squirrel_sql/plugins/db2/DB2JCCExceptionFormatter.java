package net.sourceforge.squirrel_sql.plugins.db2;
/*
 * Copyright (C) 2007 Christoph Schmitz
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
import java.lang.reflect.Method;

import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;

/**
 * Formats an exception of the new DB2 JCC driver, where the human-readable
 * error message needs to be obtained from a DB2SqlCa object.
 * 
 * Invokes DB2 specific methods via reflection in order to avoid the need for
 * the proprietary DB2 class files for compilation.
 * 
 * @author Christoph Schmitz <schm4704@users.sourceforge.net>
 */
public class DB2JCCExceptionFormatter implements ExceptionFormatter {

    /*
     * As the JCC driver code is obfuscated, we do not check the full class
     * name, but resort to checking a prefix and suffix instead.
     * 
     * In my version, the full class name is "com.ibm.db2.jcc.c.SqlException"
     */

    // Prefix for the JCC SqlException class name
    private static final String JCC_EXCEPTION_PREFIX = "com.ibm.db2.jcc";

    // Class name for the JCC SqlException class
    private static final String JCC_EXCEPTION_CLASS = "SqlException";
    
    // Names of the various methods we need to invoke
    private static final String METHOD_GET_SQLCA = "getSqlca";

    private static final String METHOD_GET_SQL_STATE = "getSqlState";

    private static final String METHOD_GET_SQL_CODE = "getSqlCode";

    private static final String METHOD_GET_MESSAGE = "getMessage";

    /**
     * Checks if this {@link Throwable} is a DB2 JCC SqlException
     * (com.ibm.db2.jcc.*.SqlException) by testing for the proper prefix and
     * suffix of the class name
     * 
     * @see net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter#formatsException(Throwable)
     */
    public boolean formatsException(Throwable t) {
        if (t == null) {
            return false;
        } else {
            String className = t.getClass().getName();
            return className.startsWith(JCC_EXCEPTION_PREFIX)
                    && className.endsWith(JCC_EXCEPTION_CLASS);
        }
    }

    /**
     * @see net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter#format(Throwable)
     */
    public String format(Throwable t) throws Exception {
        StringBuilder builder = new StringBuilder();
        // DB2Sqlca sqlca = ((DB2Diagnosable) t).getSqlca();
        Method getSqlca = t.getClass().getMethod(METHOD_GET_SQLCA,
                (Class[]) null);
        Object sqlca = getSqlca.invoke(t, (Object[]) null);

        // String msg = sqlca.getMessage();
        Method getMessage = sqlca.getClass().getMethod(METHOD_GET_MESSAGE,
                (Class[]) null);
        String msg = getMessage.invoke(sqlca, (Object[]) null).toString();

        // int sqlCode = sqlca.getSqlCode();
        Method getSqlCode = sqlca.getClass().getMethod(METHOD_GET_SQL_CODE,
                (Class[]) null);
        int sqlCode = (Integer) getSqlCode.invoke(sqlca, (Object[]) null);

        // int sqlstate = sqlca.getSqlState();
        Method getSqlState = sqlca.getClass().getMethod(
                METHOD_GET_SQL_STATE, (Class[]) null);
        String sqlState = getSqlState.invoke(sqlca, (Object[]) null)
                .toString();

        builder.append(msg).append(" SQL Code: ").append(sqlCode).append(
                ", SQL State: ").append(sqlState);
        return builder.toString();
    }
}