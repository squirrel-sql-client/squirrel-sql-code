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
package net.sourceforge.squirrel_sql.client.plugin;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertNotNull;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;

import utils.EasyMockHelper;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

/**
 * This class provides common tests for plugins.  Each plugin test should simply extend this class to 
 * pickup the common tests.
 *  
 */
public abstract class AbstractPluginTest extends BaseSQuirreLJUnit4TestCase
{
	protected IPlugin classUnderTest = null;
	protected EasyMockHelper mockHelper = new EasyMockHelper();
	

	@Test
	public void testGetInternalName() {
		assertNotNull(classUnderTest.getInternalName());
	}
	
	@Test
	public void testGetDescriptiveName() {
		assertNotNull(classUnderTest.getDescriptiveName());
	}

	@Test 
	public void testGetVersion() {
		assertNotNull(classUnderTest.getVersion());
	}

	@Test 
	public void testGetAuthor() {
		assertNotNull(classUnderTest.getAuthor());
	}

	@Test 
	public void testGetChangeLogFilename() {
		assertNotNull(classUnderTest.getChangeLogFileName());
	}

	@Test 
	public void testGetHelpFilename() {
		assertNotNull(classUnderTest.getHelpFileName());
	}
	
	@Test
	public void testGetLicenseFilename() {
		assertNotNull(classUnderTest.getLicenceFileName());
	}
	
	@Test
	public void testGetWebsite() {
		assertNotNull(classUnderTest.getWebSite());
	}

	@Test
	public void testGetContributors() {
		assertNotNull(classUnderTest.getContributors());
	}

}
