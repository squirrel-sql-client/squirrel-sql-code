package net.sourceforge.squirrel_sql.client.session;
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
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import net.sourceforge.squirrel_sql.client.IApplication;

/**
 * This class creates <TT>ISession</TT> objects as required.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionFactory {
    /**
     * Private ctor as all access is through static methods.
     */
    private SessionFactory() {
        super();
    }

    /**
     * Create a new session.
     *
     * @param   app     Application API.
     * @param   driver  JDBC driver for session.
     * @param   alias   Defines URL to database.
     * @param   conn    Connection to database.
     *
     * @throws IllegalArgumentException if any parameter is null.
     */
    public static ISession createSession(IApplication app, ISQLDriver driver, ISQLAlias alias,
                                            SQLConnection conn) {
        if (app == null) {
            throw new IllegalArgumentException("null IApplication passed");
        }
        if (driver == null) {
            throw new IllegalArgumentException("null ISQLDriver passed");
        }
        if (alias == null) {
            throw new IllegalArgumentException("null ISQLAlias passed");
        }
        if (conn == null) {
            throw new IllegalArgumentException("null SQLConnection passed");
        }
        return new Session(app, driver, alias, conn);
    }
}
