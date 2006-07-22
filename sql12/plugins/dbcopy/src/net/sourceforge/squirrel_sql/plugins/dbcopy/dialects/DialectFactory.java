/*
 * Copyright (C) 2005 Rob Manning
 * manningr@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.plugins.dbcopy.dialects;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dbcopy.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.DBCopyPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;

/**
 * This class maps ISession instances to their corresponding Hibernate dialect.  
 */
public class DialectFactory {

    /** this is used to indicate that the sesion is being copied from */
    public static final int SOURCE_TYPE = 0;
    
    /** this is used to indicate that the sesion is being copied to */
    public static final int DEST_TYPE = 1;
    
    private static final AxionDialect axionDialect = new AxionDialect();
    
    private static final DB2Dialect db2Dialect = new DB2Dialect();
    
    // TODO: subclass these hibernate dialects to provide the "canPasteTo" 
    //       api method in HibernateDialect interface.
    //private static final DB2390Dialect db2390Dialect = new DB2390Dialect();
    
    //private static final DB2400Dialect db2400Dialect = new DB2400Dialect();
    
    private static final DaffodilDialect daffodilDialect = new DaffodilDialect();
    
    private static final DerbyDialect derbyDialect = new DerbyDialect();
    
    private static final FirebirdDialect firebirdDialect = new FirebirdDialect();
    
    private static final FrontBaseDialect frontbaseDialect = new FrontBaseDialect();
    
    private static final H2Dialect h2Dialect = new H2Dialect();
    
    private static final HSQLDialect hsqlDialect = new HSQLDialect();
    
    private static final InformixDialect informixDialect = new InformixDialect();
    
    private static final InterbaseDialect interbaseDialect = new InterbaseDialect();
    
    private static final IngresDialect ingresDialect = new IngresDialect();
    
    private static final MAXDBDialect maxDbDialect = new MAXDBDialect();
    
    private static final McKoiDialect mckoiDialect = new McKoiDialect();
    
    private static final MySQLDialect mysqlDialect = new MySQLDialect();
    
    private static final Oracle9iDialect oracle9iDialect = new Oracle9iDialect();
    
    private static final PointbaseDialect pointbaseDialect = 
                                                         new PointbaseDialect();
    
    private static final PostgreSQLDialect postgreSQLDialect = 
                                                        new PostgreSQLDialect();

    private static final ProgressDialect progressDialect = new ProgressDialect();
    
    private static final SybaseDialect sybaseDialect = new SybaseDialect();
    
    private static final SQLServerDialect sqlserverDialect = new SQLServerDialect();
    
    private static final TimesTenDialect timestenDialect = new TimesTenDialect();
    
    private static HashMap dbNameDialectMap = new HashMap();
    
    private static DBCopyPreferenceBean prefs = 
                                            PreferencesManager.getPreferences();
    
    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
                  StringManagerFactory.getStringManager(DialectFactory.class);
    
    /** 
     * The keys to dbNameDialectMap are displayed to the user in the dialect
     * chooser widget, so be sure to use something that is intelligable to 
     * an end user 
     */
    static {
        dbNameDialectMap.put(axionDialect.getDisplayName(), axionDialect);
        dbNameDialectMap.put(db2Dialect.getDisplayName(), db2Dialect);
        //dbNameDialectMap.put("DB2/390", db2390Dialect);
        //dbNameDialectMap.put("DB2/400", db2400Dialect);
        dbNameDialectMap.put(daffodilDialect.getDisplayName(), daffodilDialect);
        dbNameDialectMap.put(derbyDialect.getDisplayName(), derbyDialect);
        dbNameDialectMap.put(firebirdDialect.getDisplayName(), firebirdDialect);
        dbNameDialectMap.put(frontbaseDialect.getDisplayName(), frontbaseDialect);
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
        dbNameDialectMap.put(pointbaseDialect.getDisplayName(), pointbaseDialect);
        dbNameDialectMap.put(postgreSQLDialect.getDisplayName(), postgreSQLDialect);
        dbNameDialectMap.put(progressDialect.getDisplayName(), progressDialect);
        dbNameDialectMap.put(sybaseDialect.getDisplayName(), sybaseDialect);
        dbNameDialectMap.put(timestenDialect.getDisplayName(), timestenDialect);
    }
    
    /** cache previous decisions about which dialect to use */
    private static HashMap sessionDialectMap = new HashMap();
    
    public static boolean isAxionSession(ISession session) {
        return testDriverName(session, prefs.getAxionDriverClass())
                || testSessionDialect(session, AxionDialect.class);
    }
    
    public static boolean isDaffodilSession(ISession session) {
        return testDriverName(session, prefs.getDaffodilDriverClass())
                || testSessionDialect(session, DaffodilDialect.class);
    }
    
    public static boolean isDB2Session(ISession session) {
        return testDriverName(session, prefs.getDb2DriverClass())
                || testSessionDialect(session, DB2Dialect.class);
    }

    public static boolean isDerbySession(ISession session) {
        return testDriverName(session, prefs.getDerbyDriverClass())
                || testSessionDialect(session, DerbyDialect.class);
    }    
    
    public static boolean isFirebirdSession(ISession session) {
        return testDriverName(session, prefs.getFirebirdDriverClass())
                || testSessionDialect(session, FirebirdDialect.class);
    }
    
    public static boolean isFrontBaseSession(ISession session) {
        return testDriverName(session, prefs.getFrontbaseDriverClass())
                || testSessionDialect(session, FrontBaseDialect.class);
    }
    
    public static boolean isH2Dialect(ISession session) {
        return testDriverName(session, prefs.getH2DriverClass())
                || testSessionDialect(session, H2Dialect.class);
    }
    
    public static boolean isHSQLSession(ISession session) {
        return testDriverName(session, prefs.getHypersonicDriverClass())
                || testSessionDialect(session, HSQLDialect.class);
    }    
    
    public static boolean isIngresSession(ISession session) {
        return testDriverName(session, prefs.getIngresDriverClass())
                || testSessionDialect(session, IngresDialect.class);
    }
    
    public static boolean isMaxDBSession(ISession session) {
        return testDriverName(session, prefs.getMaxDbDriverClass())
                || testSessionDialect(session, MAXDBDialect.class);
    }
    
    public static boolean isMcKoiSession(ISession session) {
        return testDriverName(session, prefs.getMckoiDriverClass())
                || testSessionDialect(session, McKoiDialect.class);        
    }

    public static boolean isMSSQLServerSession(ISession session) {
        return testDriverName(session, prefs.getMssqlserverDriverClass())
                || testSessionDialect(session, SQLServerDialect.class);
    }            
    
    public static boolean isMySQLSession(ISession session) {
        return testDriverName(session, prefs.getMysqlDriverClass())
                || testSessionDialect(session, MySQLDialect.class);
    }        
    
    public static boolean isOracleSession(ISession session) {
        return testDriverName(session, prefs.getOracleDriverClass())
                || testSessionDialect(session, Oracle9iDialect.class);        
    }
    
    public static boolean isPointbase(ISession session) {
        return testDriverName(session, prefs.getPointbaseDriverClass())
                || testSessionDialect(session, PointbaseDialect.class);        
    }

    public static boolean isPostgreSQL(ISession session) {
        return testDriverName(session, prefs.getPostgresqlDriverClass())
                || testSessionDialect(session, PostgreSQLDialect.class);        
    }    
    
    public static boolean isProgressSQL(ISession session) {
        return testDriverName(session, prefs.getProgressDriverClass())
                || testSessionDialect(session, ProgressDialect.class);        
    }
    
    public static boolean isSyBaseSession(ISession session) {
        return testDriverName(session, prefs.getSybaseDriverClass())
                || testSessionDialect(session, SybaseDialect.class);        
    }
    
    /**
     * Examines the driver class name from the specified session to see if it
     * begins with any of the space-delimited string tokens in the specified 
     * nameToMatch.
     *  
     * @param session the ISession to check
     * @param nameToMatch a space-delimited string of driver class package 
     *                    prefixes 
     * @return true if there is a match of any string in the nameToMatch and 
     *              the ISession's driver class name; false otherwise.
     */
    private static boolean testDriverName(ISession session, String nameToMatch) {
        boolean result = false;
        if (session != null && nameToMatch != null) {
            ISQLDriver driver = session.getDriver();
            if (driver != null) {
                String driverClassName = driver.getDriverClassName();
                if (driverClassName != null) {
                    String driverClassNameLC = driverClassName.toLowerCase();
                    StringTokenizer st = new StringTokenizer(nameToMatch);
                    while (st.hasMoreTokens()) {
                        String token = st.nextToken();
                        if (driverClassNameLC.startsWith(token.toLowerCase())) {
                            result = true;
                            break;
                        }
                    }
                    
                }
            }
        }
        return result;
    }
    
    /**
     * 
     * @param session
     * @param dialectClass
     * @return
     */
    private static boolean testSessionDialect(ISession session, 
                                              Class dialectClass) 
    {
        boolean result = false;
        if (sessionDialectMap.containsKey(session)) {
            HibernateDialect dialect = 
                (HibernateDialect)sessionDialectMap.get(session);
            if (dialect != null) {
                String sessionDialectClassName = dialect.getClass().getName();
                String dialectClassName = dialectClass.getName();
                if (sessionDialectClassName.equals(dialectClassName)) { 
                    result = true;
                }
            }
        }
        return result;
    }
    
    public static HibernateDialect getDialect(ISession session, int sessionType) 
        throws UserCancelledOperationException 
    {
        HibernateDialect result = null;
        if (sessionDialectMap.containsKey(session)) {
            result = (HibernateDialect)sessionDialectMap.get(session);
        } else {
            result = _getDialect(session, sessionType);
            sessionDialectMap.put(session, result);
        }        
        return result;
    }
    
    private static HibernateDialect _getDialect(ISession session,
                                                int sessionType) 
        throws UserCancelledOperationException 
    {
        // User doesn't wish for us to try to auto-detect the dest db.
        if (prefs.isPromptForDialect()) {
            return showDialectDialog(session, sessionType);
        }
        // TODO: Perhaps we would rather use Hibernate's method for determining
        // what dialect should be used (DialectFactory.buildDialect() - but for
        // this we need product name and version.  For instance, Oracle 8 has
        // a different dialect then Oracle 9.  So in this case it's wrong to 
        // return the oracle9Dialect for Oracle version 8.
        if (isAxionSession(session)) {
            return axionDialect;
        }
        if (isDaffodilSession(session)) {
            return daffodilDialect;
        }
        if (isDB2Session(session)) {
            return db2Dialect;
        }
        if (isDerbySession(session)) {
            return derbyDialect;
        }
        if (isFirebirdSession(session)) {
            return firebirdDialect;
        }
        if (isFrontBaseSession(session)) {
            return frontbaseDialect;
        }
        if (isH2Dialect(session)) {
            return h2Dialect;
        }
        if (isHSQLSession(session)) {
            return hsqlDialect;
        }
        if (isIngresSession(session)) {
            return ingresDialect;
        }
        if (isMaxDBSession(session)) {
            return maxDbDialect;
        }
        if (isMcKoiSession(session)) {
            return mckoiDialect;
        }
        if (isMySQLSession(session)) {
            return mysqlDialect;
        }
        if (isMSSQLServerSession(session)) {
            return sqlserverDialect;
        }
        if (isOracleSession(session)) {
            return oracle9iDialect;
        }
        if (isPointbase(session)) {
            return pointbaseDialect;
        }
        if (isPostgreSQL(session)) {
            return postgreSQLDialect;
        }
        if (isProgressSQL(session)) {
            return progressDialect;
        }
        if (isSyBaseSession(session)) {
            return sybaseDialect;
        }
        // Failed to detect the dialect that should be used.  Ask the user.
        return showDialectDialog(session, sessionType);
    }

    /**
     * Shows the user a dialog explaining that we failed to detect the dialect
     * of the destination database, and we are offering the user the 
     * opportunity to pick one from our supported dialects list.  If the user
     * cancels this dialog, null is returned to indicate that the user doesn't
     * wish to continue the paste operation.
     * 
     * @param destSession
     * @param sessionType TODO
     * @return
     */
    private static HibernateDialect showDialectDialog(ISession destSession, 
                                                      int sessionType) 
        throws UserCancelledOperationException 
    {
        JFrame f = destSession.getApplication().getMainFrame();
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
        if (prefs.isPromptForDialect()) {
            message = s_stringMgr.getString("autoDetectDisabledMessage", typeStr);
        } 
        String dbName = 
            (String)JOptionPane.showInputDialog(f,
                                                message,
                                                chooserTitle,
                                                JOptionPane.INFORMATION_MESSAGE, 
                                                null, 
                                                dbNames, 
                                                dbNames[0]);
        if (dbName == null || "".equals(dbName)) {
            throw new UserCancelledOperationException();
        }
        return (HibernateDialect)dbNameDialectMap.get(dbName);
    }
    
    /**
     * 
     * @return
     */
    public static Object[] getDbNames() {
        Set keyset = dbNameDialectMap.keySet();
        Object[] keys = keyset.toArray();
        Arrays.sort(keys);
        return keys;
    }
    
    /**
     * Returns an array of HibernateDialect instances, one for each supported 
     * dialect.
     * 
     * @return
     */
    public static Object[] getSupportedDialects() {
        Collection c = dbNameDialectMap.values();
        return c.toArray();
    }
    
    /**
     * When a session is closed, it's important to not hold onto it for GC
     * purposes.
     * 
     * @param session
     */
    public static void removeSession(ISession session) {
        if (sessionDialectMap.containsKey(session)) {
            sessionDialectMap.remove(session);
        }
    }
    
}
