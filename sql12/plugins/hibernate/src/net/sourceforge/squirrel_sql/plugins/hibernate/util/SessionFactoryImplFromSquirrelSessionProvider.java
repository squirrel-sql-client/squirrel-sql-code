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

import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.plugins.hibernate.configuration.HibernateConfiguration;

import org.hibernate.impl.SessionFactoryImpl;

public class SessionFactoryImplFromSquirrelSessionProvider {

    /** The SQuirreL Session that is being used to get a SessionFactory */
    private ISession _session = null;
    
    /** The configuration that the user has chosen.  */
    private HibernateConfiguration _cfg = null;
    
    
    /**
     * The SessionFactorImpl provider class must have a method
     * with exactly this signature and name.
     */
    public SessionFactoryImpl getSessionFactoryImpl()
    {
        String rootDir = _cfg.getMappingFileRootDir();
        try {
            if (rootDir != null) {
                return (SessionFactoryImpl)HibernateUtil.getSessionFactory(_session, new String[] { rootDir } );
            } else {
                return (SessionFactoryImpl)HibernateUtil.getSessionFactory(_session, _cfg.getClassPathEntries() );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return the session
     */
    public ISession getSession() {
        return _session;
    }
    
    /**
     * @param session the session to set
     */
    public void setSession(ISession session) {
        this._session = session;
    }


    /**
     * @return the hibernateConfig
     */
    public HibernateConfiguration getHibernateConfig() {
        return _cfg;
    }


    /**
     * @param hibernateConfig the hibernateConfig to set
     */
    public void setHibernateConfig(HibernateConfiguration hibernateConfig) {
        this._cfg = hibernateConfig;
    }

    
    
}
