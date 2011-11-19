/*
 * Copyright (C) 2011 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.dbcopy.cli;

import java.util.Iterator;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.DataCache;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;

public class SessionUtil
{

	private DataCache dataCache = null;
	
	private IApplication applicationStub = new ApplicationStub();
	
	private SQLDriverManager sqlDriverManager = new SQLDriverManager();
	
	private ApplicationFiles applicationFiles = new ApplicationFiles();
	
	private SquirrelResources squirrelResources =
		new SquirrelResources(SquirrelResources.BUNDLE_BASE_NAME);

	public SessionUtil() {
		dataCache =
			new DataCache(sqlDriverManager, applicationFiles.getDatabaseDriversFile(),
				applicationFiles.getDatabaseAliasesFile(), squirrelResources.getDefaultDriversUrl(),
				applicationStub);

	}
	
	public ISession getSessionForAlias(String alias) throws Exception
	{
		System.out.println("Creating session for alias: "+alias);
		Iterator<ISQLAlias> i = dataCache.aliases();

		while (i.hasNext())
		{
			ISQLAlias sqlAlias = i.next();
			if (alias.equals(sqlAlias.getName()))
			{

				ISQLDriver sqlDriver = dataCache.getDriver(sqlAlias.getDriverIdentifier());
				sqlDriverManager.registerSQLDriver(sqlDriver);
				SQLConnection conn =
					sqlDriverManager.getConnection(sqlDriver, sqlAlias, sqlAlias.getUserName(),
						sqlAlias.getPassword(), sqlAlias.getDriverPropertiesClone());

				SessionManager sessionManager = applicationStub.getSessionManager();
				ISession result =
					sessionManager.createSession(applicationStub, sqlDriver, (SQLAlias) sqlAlias, conn,
						sqlAlias.getUserName(), sqlAlias.getPassword());
				return result;
			}
		}
		throw new RuntimeException("Alias (" + alias + ") was not found");
	}

}
