package net.sourceforge.squirrel_sql.client.db;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.ObjectCacheChangeListener;
import net.sourceforge.squirrel_sql.fw.sql.BaseSQLException;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.Logger;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

/**
 * XML cache of JDBC drivers and aliases.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DataCache {
    private final static Class SQL_ALIAS_IMPL = SQLAlias.class;
    private final static Class SQL_DRIVER_IMPL = SQLDriver.class;

    /** Application API. */
    private IApplication _app;

    /** Cache that contains data. */
    private XMLObjectCache _cache = new XMLObjectCache();

    /**
     * Ctor. Loads drivers and aliases from the XML document.
     *
     * @param   app     Application API.
     *
     * @throws  IllegalArgumentException
     *              Thrown if <TT>null</TT> <TT>IApplication</TT>
     *              passed.
     *
     * @throws  IllegalStateException
     *              Thrown if no <TT>SQLDriverManager</TT> or <TT>Logger</TT>
     *              exists in IApplication.
     */
    public DataCache(IApplication app) throws IllegalArgumentException {
        super();
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        if (app.getSQLDriverManager() == null) {
            throw new IllegalStateException("No SQLDriverManager in IApplication");
        }
        if (app.getLogger() == null) {
            throw new IllegalStateException("No Logger in IApplication");
        }

        _app = app;

        loadDrivers();
        loadAliases();
    }

    /**
     * Save cached objects. JDBC drivers are saved to
     * <CODE>ApplicationFiles.getUserDriversFileName()</CODE> and aliases are
     * saved to <CODE>ApplicationFiles.getUserAliasesFileName()</CODE>.
     */
    public void save() {
        final Logger logger = _app.getLogger();
        try {
            _cache.saveAllForClass(ApplicationFiles.USER_DRIVER_FILE_NAME, SQL_DRIVER_IMPL);
        } catch (IOException ex) {
            logger.showMessage(Logger.ILogTypes.ERROR, "Error occured saving drivers to " + ApplicationFiles.USER_DRIVER_FILE_NAME);
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
        } catch (XMLException ex) {
            logger.showMessage(Logger.ILogTypes.ERROR, "Error occured saving drivers to " + ApplicationFiles.USER_DRIVER_FILE_NAME);
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
        }
        try {
            _cache.saveAllForClass(ApplicationFiles.USER_ALIAS_FILE_NAME, SQL_ALIAS_IMPL);
        } catch (IOException ex) {
            logger.showMessage(Logger.ILogTypes.ERROR, "Error occured saving aliases to " + ApplicationFiles.USER_ALIAS_FILE_NAME);
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
        } catch (XMLException ex) {
            logger.showMessage(Logger.ILogTypes.ERROR, "Error occured saving aliases to " + ApplicationFiles.USER_ALIAS_FILE_NAME);
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
        }
    }

    /**
     * Return the <TT>ISQLDriver</TT> for the passed identifier.
     */
    public ISQLDriver getDriver(IIdentifier id) {
        return (ISQLDriver)_cache.get(SQL_DRIVER_IMPL, id);
    }

    public void addDriver(ISQLDriver sqlDriver) throws ClassNotFoundException,
            IllegalAccessException, InstantiationException, DuplicateObjectException {
        _app.getSQLDriverManager().registerSQLDriver(sqlDriver);
        _cache.add(sqlDriver);
    }

    public void removeDriver(ISQLDriver sqlDriver) {
        _cache.remove(SQL_DRIVER_IMPL, sqlDriver.getIdentifier());
        try {
            _app.getSQLDriverManager().unregisterSQLDriver(sqlDriver);
        } catch (Exception ex) {
            final Logger logger = _app.getLogger();
            logger.showMessage(Logger.ILogTypes.ERROR, "Error occured removing driver from cache");
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
        }
    }

    public Iterator drivers() {
        return _cache.getAllForClass(SQL_DRIVER_IMPL);
    }

    public void addDriversListener(ObjectCacheChangeListener lis) {
        _cache.addChangesListener(lis, SQL_DRIVER_IMPL);
    }

    public void removeDriversListener(ObjectCacheChangeListener lis) {
        _cache.removeChangesListener(lis, SQL_DRIVER_IMPL);
    }

    public ISQLAlias getAlias(IIdentifier id) {
        return (ISQLAlias)_cache.get(SQL_ALIAS_IMPL, id);
    }

    public Iterator aliases() {
        return _cache.getAllForClass(SQL_ALIAS_IMPL);
    }

    public void addAlias(ISQLAlias alias) throws DuplicateObjectException {
        _cache.add(alias);
    }

    public void removeAlias(ISQLAlias alias) {
        _cache.remove(SQL_ALIAS_IMPL, alias.getIdentifier());
    }

    public Iterator getAliasesForDriver(ISQLDriver driver) {
        ArrayList data = new ArrayList();
        for (Iterator it = aliases(); it.hasNext();) {
            ISQLAlias alias = (ISQLAlias)it.next();
            if (driver.equals(getDriver(alias.getDriverIdentifier()))) {
                data.add(alias);
            }
        }
        return data.iterator();
    }

    public void addAliasesListener(ObjectCacheChangeListener lis) {
        _cache.addChangesListener(lis, SQL_ALIAS_IMPL);
    }

    public void removeAliasesListener(ObjectCacheChangeListener lis) {
        _cache.removeChangesListener(lis, SQL_ALIAS_IMPL);
    }

    /**
     * Load <TT>IISqlDriver</TT> objects from XML file.
     */
    private void loadDrivers() {
        final Logger logger = _app.getLogger();
        try {
            _cache.load(ApplicationFiles.USER_DRIVER_FILE_NAME);
            if (!drivers().hasNext()) {
                loadDefaultDrivers();
            }
        } catch (FileNotFoundException ex) {
            loadDefaultDrivers();// first time user has run pgm.
        } catch (XMLException ex) {
            logger.showMessage(Logger.ILogTypes.ERROR, "Error loading driver file: "
                        + ApplicationFiles.USER_DRIVER_FILE_NAME
                        + ". Default drivers loaded instead.");
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
            loadDefaultDrivers();
        } catch (DuplicateObjectException ex) {
            logger.showMessage(Logger.ILogTypes.ERROR, "Error loading driver file: "
                        + ApplicationFiles.USER_DRIVER_FILE_NAME
                        + "Default drivers loaded instead.");
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
            loadDefaultDrivers();
        }

        registerDrivers();
    }

    public ISQLAlias createAlias(IIdentifier id) {
        return new SQLAlias(id);
    }

    public ISQLDriver createDriver(IIdentifier id) {
        return new SQLDriver(id);
    }

    private void loadDefaultDrivers() {
        final URL url = _app.getResources().getDefaultDriversUrl();
        try {
            InputStreamReader isr = new InputStreamReader(url.openStream());
            try {
                _cache.load(isr);
            } finally {
                isr.close();
            }
        } catch (Exception ex) {
            final Logger logger = _app.getLogger();
            logger.showMessage(Logger.ILogTypes.ERROR,
                        "Error loading default driver file: " + url != null ? url.toExternalForm() : "");
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
        }
    }

    private void registerDrivers() {
        SQLDriverManager driverMgr = _app.getSQLDriverManager();
        for (Iterator it = drivers(); it.hasNext();) {
            ISQLDriver sqlDriver = (ISQLDriver)it.next();
            try {
                driverMgr.registerSQLDriver(sqlDriver);
            } catch (Throwable th) {
                final Logger logger = _app.getLogger();
                logger.showMessage(Logger.ILogTypes.STATUS, "Unable to register JDCB driver " + sqlDriver.getName());
                logger.showMessage(Logger.ILogTypes.STATUS, th.toString());
            }
        }
    }

    private void loadAliases() {
        final Logger logger = _app.getLogger();
        try {
            _cache.load(ApplicationFiles.USER_ALIAS_FILE_NAME);
        } catch (FileNotFoundException ignore) { // first time user has run pgm.
        } catch (XMLException ex) {
            logger.showMessage(Logger.ILogTypes.ERROR, "Error loading aliases file: " + ApplicationFiles.USER_ALIAS_FILE_NAME);
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
        } catch (DuplicateObjectException ex) {
            logger.showMessage(Logger.ILogTypes.ERROR, "Error loading aliases file: " + ApplicationFiles.USER_ALIAS_FILE_NAME);
            logger.showMessage(Logger.ILogTypes.ERROR, ex);
        }
    }
}
