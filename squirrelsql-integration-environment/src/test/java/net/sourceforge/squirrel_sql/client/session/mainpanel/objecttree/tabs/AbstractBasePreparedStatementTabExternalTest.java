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
package net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.plugins.dbcopy.cli.SessionUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public abstract class AbstractBasePreparedStatementTabExternalTest
{
	
	protected BasePreparedStatementTab classUnderTest = null;
	protected SessionUtil sessionUtil = new SessionUtil();
	protected IDatabaseObjectInfo dboi = null;
	protected Connection con = null;
	
	protected abstract String getSimpleName();
	
	protected abstract BasePreparedStatementTab getTabToTest();
	
	protected abstract String getAlias();
	
	protected List<String> getSetupStatements() {
		return new ArrayList<String>(); 
	}

	protected List<String> getTeardownStatements() {
		return new ArrayList<String>(); 
	}
	
	protected String getSchemaName() {
		return null;
	}

	@Before
	public void setup() throws Exception
	{
		classUnderTest = getTabToTest();
		ISession session = sessionUtil.getSessionForAlias(getAlias());
		con = session.getSQLConnection().getConnection();
		if (dboi == null) {
			dboi = Mockito.mock(IDatabaseObjectInfo.class);
			Mockito.when(dboi.getSchemaName()).thenReturn(getSchemaName());
			Mockito.when(dboi.getSimpleName()).thenReturn(getSimpleName());
		}
		classUnderTest.setSession(session);
		classUnderTest.setDatabaseObjectInfo(dboi);
		executeStatements(getSetupStatements());
	}
	
	@After
	public void tearDown() throws Exception
	{
		executeStatements(getTeardownStatements());
	}

	@Test
	public void testCreateStatement() throws Exception
	{
		Method m = classUnderTest.getClass().getDeclaredMethod("createStatement", (Class<?>[])null);
		m.setAccessible(true);
		Object result = m.invoke(classUnderTest, (Object[])null);
	   PreparedStatement stmt = (PreparedStatement)result;
		stmt.executeQuery();
	}
	
	private void executeStatements(List<String> statements) throws Exception {
		
		for (String sql : statements) {
			Statement stmt = null;
			try {
				stmt = con.createStatement();
				stmt.execute(sql);
			} finally {
				SQLUtilities.closeStatement(stmt);
			}
		}
	}

}
