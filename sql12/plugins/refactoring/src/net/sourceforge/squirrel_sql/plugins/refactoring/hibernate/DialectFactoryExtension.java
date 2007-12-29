package net.sourceforge.squirrel_sql.plugins.refactoring.hibernate;
/*
 * Copyright (C) 2007 Daniel Regli & Yannick Winiger
 * http://sourceforge.net/projects/squirrel-sql
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

import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.UnknownDialectException;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.util.HashMap;

/**
 * Defines additional functionality (extended Dialects) to DialectFactory.
 */
public class DialectFactoryExtension extends DialectFactory {
    /**
     * Internationalized strings for this class.
     */
    private static final StringManager s_stringMgr =
            StringManagerFactory.getStringManager(DialectFactory.class);

    private static final HashMap<String, IHibernateDialectExtension> dbNameDialectMap =
            new HashMap<String, IHibernateDialectExtension>();

    private static final PostgreSQLDialectExtension postgreSQLDialect =
            new PostgreSQLDialectExtension();

    private static final UnsupportedDatabaseDialect unsupportedDatabaseDialect = 
   	 new UnsupportedDatabaseDialect();
    
    /**
     * The keys to dbNameDialectMap are displayed to the user in the dialect
     * chooser widget, so be sure to use something that is intelligable to
     * an end user
     */
    static {
        /*  dbNameDialectMap.put(axionDialect.getDisplayName(), axionDialect);
          dbNameDialectMap.put(db2Dialect.getDisplayName(), db2Dialect);
          //dbNameDialectMap.put("DB2/390", db2390Dialect);
          //dbNameDialectMap.put("DB2/400", db2400Dialect);
          dbNameDialectMap.put(daffodilDialect.getDisplayName(), daffodilDialect);
          dbNameDialectMap.put(derbyDialect.getDisplayName(), derbyDialect);
          dbNameDialectMap.put(firebirdDialect.getDisplayName(), firebirdDialect);
          dbNameDialectMap.put(frontbaseDialect.getDisplayName(), frontbaseDialect);
          dbNameDialectMap.put(hadbDialect.getDisplayName(), hadbDialect);
          dbNameDialectMap.put(hsqlDialect.getDisplayName(), hsqlDialect);
          dbNameDialectMap.put(h2Dialect.getDisplayName(), h2Dialect);
          dbNameDialectMap.put(informixDialect.getDisplayName(), informixDialect);
          dbNameDialectMap.put(ingresDialect.getDisplayName(), ingresDialect);
          dbNameDialectMap.put(interbaseDialect.getDisplayName(), interbaseDialect);
          dbNameDialectMap.put(maxDbDialect.getDisplayName(), maxDbDialect);
          dbNameDialectMap.put(mckoiDialect.getDisplayName(), mckoiDialect);
          dbNameDialectMap.put(sqlserverDialect.getDisplayName(), sqlserverDialect);
          dbNameDialectMap.put(mysqlDialect.getDisplayName(), mysqlDialect);
          dbNameDialectMap.put(oracle9iDialect.getDisplayName(), oracle9iDialect);
          dbNameDialectMap.put(pointbaseDialect.getDisplayName(), pointbaseDialect);     */
        dbNameDialectMap.put(postgreSQLDialect.getDisplayName(), postgreSQLDialect);
        /* dbNameDialectMap.put(progressDialect.getDisplayName(), progressDialect);
      dbNameDialectMap.put(sybaseDialect.getDisplayName(), sybaseDialect);
      dbNameDialectMap.put(timestenDialect.getDisplayName(), timestenDialect);     */
    }


    /**
     * Gets the specified dialect from the meta info.
     *
     * @param sessionType DialectFactory session type.
     * @param parent      frame for the dialect chooser dialog.
     * @param md          metadata to detect the current dialect.
     * @return a dialect.
     * @throws UserCancelledOperationException
     *          thrown if the user cancel the operation.
     */
    public static IHibernateDialectExtension getDialect(int sessionType,
                                                        JFrame parent,
                                                        ISQLDatabaseMetaData md)
            throws UserCancelledOperationException {
        IHibernateDialectExtension result;

        // User doesn't wish for us to try to auto-detect the dest db.
        if (isPromptForDialect) {
            result = showDialectDialog(parent, sessionType);
        } else {
            try {
                result = getDialect(md);
            } catch (UnknownDialectException e) {
                // Failed to detect the dialect that should be used.  Ask the user.
                result = showDialectDialog(parent, sessionType);
            }
        }
        return result;
    }


    /**
     * @param md metadata to detect the current dialect.
     * @return a dialect.
     * @throws UnknownDialectException thrown if the user cancel the operation.
     */
    public static IHibernateDialectExtension getDialect(ISQLDatabaseMetaData md)
            throws UnknownDialectException {
        /*
        if (isAxion(md)) {
        return axionDialect;
        }
        if (isDaffodil(md)) {
        return daffodilDialect;
        }
        if (isDB2(md)) {
        return db2Dialect;
        }
        if (isDerby(md)) {
        return derbyDialect;
        }
        if (isFirebird(md)) {
        return firebirdDialect;
        }
        if (isFrontBase(md)) {
        return frontbaseDialect;
        }
        if (isHADB(md)) {
        return hadbDialect;
        }
        if (isH2(md)) {
        return h2Dialect;
        }
        if (isHSQL(md)) {
        return hsqlDialect;
        }
        if (isInformix(md)) {
        return informixDialect;
        }
        if (isIngres(md)) {
        return ingresDialect;
        }
        if (isInterbase(md)) {
        return ingresDialect;
        }
        if (isMaxDB(md)) {
        return maxDbDialect;
        }
        if (isMcKoi(md)) {
        return mckoiDialect;
        }
        if (isMySQL(md)) {
        return mysqlDialect;
        }
        if (isMSSQLServer(md)) {
        return sqlserverDialect;
        }
        if (isOracle(md)) {
        return oracle9iDialect;
        }
        if (isPointbase(md)) {
        return pointbaseDialect;
        }
        */
        if (isPostgreSQL(md)) {
            return postgreSQLDialect;
        } 
        /*
        if (isProgress(md)) {
        return progressDialect;
        }
        if (isSyBase(md)) {
        return sybaseDialect;
        }
        if (isTimesTen(md)) {
        return timestenDialect;
        }
        */
        return unsupportedDatabaseDialect;
    }


    public static IHibernateDialectExtension getDialect(String dbName) {
        return dbNameDialectMap.get(dbName);
    }


    /**
     * Shows the user a dialog explaining that we failed to detect the dialect
     * of the destination database, and we are offering the user the
     * opportunity to pick one from our supported dialects list.  If the user
     * cancels this dialog, null is returned to indicate that the user doesn't
     * wish to continue the paste operation.
     *
     * @param parent      frame for the dialect chooser dialog.
     * @param sessionType DialectFactory session type.
     * @return a dialect.
     * @throws UserCancelledOperationException
     *          thrown if the user cancel the operation.
     */
    private static IHibernateDialectExtension showDialectDialog(JFrame parent,
                                                                int sessionType)
            throws UserCancelledOperationException {
        Object[] dbNames = getDbNames();
        String chooserTitle = s_stringMgr.getString("dialectChooseTitle");
        String typeStr = null;
        if (sessionType == SOURCE_TYPE) {
            typeStr = s_stringMgr.getString("sourceSessionTypeName");
        }
        if (sessionType == DEST_TYPE) {
            typeStr = s_stringMgr.getString("destSessionTypeName");
        }
        String message =
                s_stringMgr.getString("dialectDetectFailedMessage", typeStr);
        if (isPromptForDialect) {
            message = s_stringMgr.getString("autoDetectDisabledMessage", typeStr);
        }
        String dbName =
                (String) JOptionPane.showInputDialog(parent,
                        message,
                        chooserTitle,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        dbNames,
                        dbNames[0]);
        if (dbName == null || "".equals(dbName)) {
            throw new UserCancelledOperationException();
        }
        return dbNameDialectMap.get(dbName);
    }
}
