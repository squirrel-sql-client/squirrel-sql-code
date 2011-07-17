package net.sourceforge.squirrel_sql.plugins.dbcopy.cli;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.DataCache;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ISQLAlias;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.sql.TableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.plugins.dbcopy.CopyExecutor;
import net.sourceforge.squirrel_sql.plugins.dbcopy.DBCopyPlugin;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;
import net.sourceforge.squirrel_sql.plugins.dbcopy.util.DBUtil;

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

public class DBCopyCLI
{
	private static IApplication applicationStub = new ApplicationStub();

	private static ApplicationFiles applicationFiles = new ApplicationFiles();

	private static SquirrelResources squirrelResources =
		new SquirrelResources(SquirrelResources.BUNDLE_BASE_NAME);

	private static SQLDriverManager sqlDriverManager = new SQLDriverManager();

	private static DataCache dataCache = null;

	private static ISession sourceSession = null;

	// private static ISession destSession = null;

	private static SessionInfoProvider sessionInfoProvider = new SessionInfoProviderImpl();

	private static ArrayList<ITableInfo> tables = new ArrayList<ITableInfo>();

	private static CLCopyListener listener = new CLCopyListener();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		CommandLineArgumentProcessor argProcessor = new CommandLineArgumentProcessor(args);
		String sourceSchemaName = argProcessor.getSourceSchemaName();
		String sourceCatalogName = argProcessor.getSourceCatalogName();
		String destSchemaName = argProcessor.getDestSchemaName();
		String destCatalogName = argProcessor.getDestCatalogName();
		String simpleName = StringUtils.isEmpty(destSchemaName) ? destCatalogName : destSchemaName;

		dataCache =
			new DataCache(sqlDriverManager, applicationFiles.getDatabaseDriversFile(),
				applicationFiles.getDatabaseAliasesFile(), squirrelResources.getDefaultDriversUrl(),
				applicationStub);

		ISession destSession = getSessionForAlias(argProcessor.getDestAliasName());

		sessionInfoProvider.setSourceSession(getSessionForAlias(argProcessor.getSourceAliasName()));
		sessionInfoProvider.setDestSession(destSession);
		for (String tableStr : argProcessor.getTableList())
		{
			tables.add(getTableInfo(sessionInfoProvider.getSourceSession(), sourceSchemaName, sourceCatalogName,
				tableStr));
		}

		sessionInfoProvider.setSourceDatabaseObjects(DBUtil.convertTableToObjectList(tables));

		DatabaseObjectInfo destObject =
			new DatabaseObjectInfo(destCatalogName, destSchemaName, simpleName, DatabaseObjectType.SCHEMA,
				destSession.getMetaData());
		sessionInfoProvider.setDestDatabaseObject(destObject);

		PreferencesManager.initialize(new DBCopyPlugin());
		DBUtil.setPreferences(PreferencesManager.getPreferences());
		DialectFactory.isPromptForDialect = false;

		CopyExecutor executor = new CopyExecutor(sessionInfoProvider);
		executor.addListener(listener);
		executor.setPref(new CLCopyUICallback());
		executor.execute();
		
		while (!listener.isCopyFinished()) {
			System.out.print(".");
			Thread.sleep(2000);
		}

	}

	private static ITableInfo getTableInfo(ISession session, String schemaName, String catalogName,
		String tableName) throws SQLException
	{
		ISQLDatabaseMetaData md = session.getMetaData();
		ITableInfo[] result = md.getTables(catalogName, schemaName, tableName, new String[] { "TABLE" }, null);
		return result[0];
	}

	private static ISession getSessionForAlias(String alias) throws Exception
	{
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

				// Class.forName(driver.getDriverClassName());

				// Connection con =
				// DriverManager.getConnection(sqlAlias.getUrl(), sqlAlias.getUserName(), sqlAlias.getPassword());

				// SQLConnection conn = new SQLConnection(con, sqlAlias.getDriverPropertiesClone(), driver);
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
