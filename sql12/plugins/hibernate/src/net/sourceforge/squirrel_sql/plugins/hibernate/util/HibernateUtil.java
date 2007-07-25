/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.plugins.hibernate.util;

import java.io.File;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.DialectFactory;

/**
 * Class that provides utility methods for obtaining SessionFactory objects.
 * 
 * @author manningr
 *
 */
public class HibernateUtil {

    /** Logger for this class. */
    private static final ILogger s_log =
        LoggerController.createLogger(HibernateUtil.class);
    
    public static SessionFactory getSessionFactory(ISession session, String[] classPathEntries) 
        throws SQLException 
    {
        SessionFactory result = null;
        Dialect dialect = getHibernateDialectForSession(session);
        String dialectName = dialect.getClass().getName();
        Configuration cfg = new Configuration();
        ISQLAliasExt alias = session.getAlias();
        ISQLDriver driver = session.getDriver();
        if (s_log.isDebugEnabled()) {
            s_log.debug("getSessionFactory: url="+alias.getUrl());
        }
        cfg.setProperty(Environment.URL, alias.getUrl());
        cfg.setProperty(Environment.USER, alias.getUserName());
        cfg.setProperty(Environment.PASS, alias.getPassword());
        cfg.setProperty(Environment.DRIVER, driver.getDriverClassName());
        cfg.setProperty(Environment.DIALECT, dialectName);
        cfg.setProperty(Environment.SHOW_SQL, "true");
        cfg.setProperty(Environment.HBM2DDL_AUTO, "create");
        for (int i = 0; i < classPathEntries.length; i++) {
            String file = classPathEntries[i];
            cfg.addDirectory(new File(file));
        }
        
        result = cfg.buildSessionFactory();
        return result;
    }
    
    /**
     * Returns the Hibernate dialect for the specified session.
     * 
     * @param session the SQuirreL ISession implementation
     * 
     * @return an instance of Dialect. 
     * 
     * @throws SQLException
     */
    public static Dialect getHibernateDialectForSession(ISession session) 
        throws SQLException 
    {
        Dialect result = null;
        ISQLDatabaseMetaData md = session.getMetaData();
        String databaseName = md.getDatabaseProductName();
        int databaseMajorVersion = md.getDatabaseMajorVersion();
       
        result = 
            DialectFactory.determineDialect(databaseName, databaseMajorVersion);
        
        if (s_log.isInfoEnabled()) {
            s_log.info(
                "Dialect returned from DialectFactory for databaseName="+
                databaseName+" and databaseMajorVersion="+databaseMajorVersion+
                " was "+result.getClass().getName());
        }
        
        return result;
    }
    
}
