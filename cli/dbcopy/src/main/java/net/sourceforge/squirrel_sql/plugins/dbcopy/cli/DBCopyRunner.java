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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.plugins.dbcopy.CopyExecutor;
import net.sourceforge.squirrel_sql.plugins.dbcopy.DBCopyPlugin;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;
import net.sourceforge.squirrel_sql.plugins.dbcopy.util.DBUtil;

import org.apache.commons.lang.StringUtils;

public class DBCopyRunner
{
	private SessionInfoProvider sessionInfoProvider = new SessionInfoProviderImpl();

	private ArrayList<ITableInfo> tables = new ArrayList<ITableInfo>();

	private CLCopyListener listener = new CLCopyListener();

	private String sourceSchemaName = null;

	private String sourceCatalogName = null;

	private String destSchemaName = null;

	private String destCatalogName = null;

	public void run() throws Exception
	{
		String simpleName = StringUtils.isEmpty(destSchemaName) ? destCatalogName : destSchemaName;

		sessionInfoProvider.setSourceDatabaseObjects(DBUtil.convertTableToObjectList(tables));

		DatabaseObjectInfo destObject =
			new DatabaseObjectInfo(destCatalogName, destSchemaName, simpleName, DatabaseObjectType.SCHEMA,
				sessionInfoProvider.getDestSession().getMetaData());
		sessionInfoProvider.setDestDatabaseObject(destObject);

		PreferencesManager.initialize(new DBCopyPlugin());
		DBUtil.setPreferences(PreferencesManager.getPreferences());
		DialectFactory.isPromptForDialect = false;

		CopyExecutor executor = new CopyExecutor(sessionInfoProvider);
		executor.addListener(listener);
		executor.setPref(new CLCopyUICallback());
		executor.execute();
		
		while (!listener.isCopyFinished())
		{
			Thread.sleep(2000);
		}
		listener.checkErrors();
		
	}

	public void setSourceSession(ISession sourceSession)
	{
		sessionInfoProvider.setSourceSession(sourceSession);
	}

	public void setDestSession(ISession destSession)
	{
		sessionInfoProvider.setDestSession(destSession);
	}

	private ITableInfo getTableInfo(ISession session, String schemaName, String catalogName, String tableName)
		throws SQLException
	{
		ISQLDatabaseMetaData md = session.getMetaData();
		ITableInfo[] result = md.getTables(catalogName, schemaName, tableName, new String[] { "TABLE" }, null);
		if (result.length == 0)
		{
			throw new IllegalStateException("Source table to be copied (" + tableName
				+ ") could not be located in schema (" + schemaName + ") and/or catalog (" + catalogName
				+ ") for alias: " + session.getAlias().getName());
		}
		return result[0];
	}

	public void setSourceSchemaName(String sourceSchemaName)
	{
		this.sourceSchemaName = sourceSchemaName;
	}

	public void setSourceCatalogName(String sourceCatalogName)
	{
		this.sourceCatalogName = sourceCatalogName;
	}

	public void setTablePattern(String pattern) throws SQLException
	{
		ISession sourceSession = sessionInfoProvider.getSourceSession();
		String catalog = (sourceCatalogName==null || sourceCatalogName.equals("")) ? null : sourceCatalogName;
		String schema = (sourceSchemaName==null || sourceSchemaName.equals("")) ? null : sourceSchemaName;
		ISQLDatabaseMetaData md = sourceSession.getMetaData();
		
		ITableInfo[] infos = md.getTables(catalog, schema, pattern, new String[] { "TABLE" }, null);
		if (infos == null || infos.length == 0) {
			System.err.println("No tables were found that match the specified :");
			System.err.println(" catalog: "+catalog);
			System.err.println(" schema: "+schema);
			System.err.println(" pattern: "+pattern);
		}
		for (ITableInfo info : infos) {
			tables.add(info);
		}
	}
	
	public void setTableList(List<String> tableList) throws SQLException
	{
		for (String tableStr : tableList)
		{
			tables.add(getTableInfo(sessionInfoProvider.getSourceSession(), sourceSchemaName, sourceCatalogName,
				tableStr));
		}

	}

	public void setTableList(String tableListStr) throws SQLException
	{
		String[] parts = tableListStr.split(",");
		for (String tableStr : parts)
		{
			tables.add(getTableInfo(sessionInfoProvider.getSourceSession(), sourceSchemaName, sourceCatalogName,
				tableStr));		
		}
	}

	public void setDestSchemaName(String destSchemaName)
	{
		this.destSchemaName = destSchemaName;
	}

	public void setDestCatalogName(String destCatalogName)
	{
		this.destCatalogName = destCatalogName;
	}
}
