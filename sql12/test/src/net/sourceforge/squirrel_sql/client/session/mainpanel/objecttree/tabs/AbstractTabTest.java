/*
 * Copyright (C) 2008 Rob Manning
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
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import java.sql.Connection;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;

public class AbstractTabTest extends BaseSQuirreLJUnit4TestCase
{
	public static final String STMT_SEP = ";";

	public static final String HINT = "aHint";

	public static final int TRANSACTION_ISOLATION = Connection.TRANSACTION_READ_COMMITTED;

	protected ISession mockSession = mockHelper.createMock("mockSession", ISession.class);

	protected ISQLConnection mockSQLConnection = mockHelper.createMock("mockSQLConnection", ISQLConnection.class);

	protected IApplication mockApplication = mockHelper.createMock("mockApplication", IApplication.class);

	protected IIdentifier mockSessionId = mockHelper.createMock(IIdentifier.class);

	protected SessionManager mockSessionManager = mockHelper.createMock("mockSessionId", SessionManager.class);

	protected TaskThreadPool mockThreadPool = mockHelper.createMock("mockThreadPool", TaskThreadPool.class);

	protected SQLDatabaseMetaData mockSQLMetaData = mockHelper.createMock("mockSQLMetaData", SQLDatabaseMetaData.class);

	protected Connection mockConnection = mockHelper.createMock("mockConnection", Connection.class);

	protected String databaseProductName = null;

	public static final String TEST_COLUMN_NAME = "aColumnName";

	protected static final String DATABASE_PRODUCT_VERSION = "1.0";

	public static final String[] mockCatalogs = new String[] { TEST_CATALOG_NAME, "testCatalogName2" };

	public static final String METADATA_OUTPUT_CLASSNAME = "aMetaDataOutputClassName";

	public AbstractTabTest()
	{
		super();
	}

}