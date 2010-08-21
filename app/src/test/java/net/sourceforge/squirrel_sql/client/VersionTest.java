/*
 * Copyright (C) 2010 Rob Manning
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
package net.sourceforge.squirrel_sql.client;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VersionTest
{

	@Test
	public void testGetApplicationName()
	{
		assertNotNull(Version.getApplicationName());
	}

	@Test
	public void testGetShortVersion()
	{
		assertNotSame("${squirrelsql.version}", Version.getShortVersion());
	}

	@Test
	public void testGetVersion()
	{
		assertNotNull(Version.getVersion());
	}

	@Test
	public void testIsSnapshotVersion()
	{
		if (Version.getShortVersion().toLowerCase().startsWith("snapshot")) {
			assertTrue(Version.isSnapshotVersion());
		} else {
			assertFalse(Version.isSnapshotVersion());
		}
	}

	@Test
	public void testGetCopyrightStatement()
	{
		assertNotNull(Version.getCopyrightStatement());
	}

	@Test
	public void testGetWebSite()
	{
		assertNotNull(Version.getWebSite());
	}

	@Test
	public void testSupportsUsedJDK()
	{
		assertTrue(Version.supportsUsedJDK());
	}

	@Test
	public void testGetUnsupportedJDKMessage()
	{
		assertNotNull(Version.getUnsupportedJDKMessage());
	}

	@Test
	public void testIsJDK14()
	{
		assertFalse(Version.isJDK14());
	}

	@Test
	public void testIsJDK16OrAbove()
	{
		assertTrue(Version.isJDK16OrAbove());
	}

}
