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
package net.sourceforge.squirrel_sql.plugins.dbcopy;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.plugin.AbstractSessionPluginTest;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DBCopyPluginTest extends AbstractSessionPluginTest
{
	
	@Mock
	IDatabaseObjectInfo mockDatabaseObjectInfo;
	
	@Override
	protected IPlugin getPluginToTest() throws Exception
	{
		return new DBCopyPlugin();
	}
	
	@Test
	public void testSessionStarted() {
		PluginSessionCallback result = ((DBCopyPlugin) super.classUnderTest).sessionStarted(mockSession);
		assertNotNull(result);		
	}
	
	@Test
	public void testSessionEnding() {
		DBCopyPlugin plugin = (DBCopyPlugin) super.classUnderTest;
		plugin.setSourceSession(mockSession);
		plugin.sessionEnding(mockSession);
		assertNull(plugin.getSourceSession());
	}
	
	@Test
	public void testSetSelectedDatabaseObjects() {
		DBCopyPlugin plugin = (DBCopyPlugin) super.classUnderTest;
		List<IDatabaseObjectInfo> dbObjList = new ArrayList<IDatabaseObjectInfo>();
		dbObjList.add(mockDatabaseObjectInfo);
		plugin.setSourceDatabaseObjects(dbObjList);
		List<IDatabaseObjectInfo> result = plugin.getSourceDatabaseObjects();
		assertEquals(1, result.size());
	}
	
	@Test
	public void testSetDestSelectedDatabaseObject() {
		DBCopyPlugin plugin = (DBCopyPlugin) super.classUnderTest;
		plugin.setDestDatabaseObject(mockDatabaseObjectInfo);
		assertEquals(mockDatabaseObjectInfo, plugin.getDestDatabaseObject());
	}
	
	@Test
	public void testSetDestCopySession() {
		DBCopyPlugin plugin = (DBCopyPlugin) super.classUnderTest;
		plugin.setDestSession(mockSession);
		assertEquals(mockSession, plugin.getDestSession());
	}
}
