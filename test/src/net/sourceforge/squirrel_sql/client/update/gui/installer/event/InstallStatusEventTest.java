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
package net.sourceforge.squirrel_sql.client.update.gui.installer.event;

import static net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallEventType.BACKUP_COMPLETE;
import static net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallEventType.BACKUP_STARTED;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InstallStatusEventTest {

	private InstallStatusEvent classUnderTest = null;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		classUnderTest = null;
	}

	@Test
	public final void testInstallStatusEvent() {
		classUnderTest = new InstallStatusEvent(BACKUP_COMPLETE);
		assertEquals(BACKUP_COMPLETE, classUnderTest.getType());
	}

	@Test (expected = IllegalArgumentException.class)
	public final void testInstallStatusEvent_NullArg() {
		classUnderTest = new InstallStatusEvent(null);
	}	
	
	@Test
	public final void testGetArtifactName() {
		classUnderTest = new InstallStatusEvent(BACKUP_COMPLETE);
		String name = "test.jar";
		classUnderTest.setArtifactName(name);
		assertEquals(name, classUnderTest.getArtifactName());
	}

	@Test
	public final void testSetArtifactName() {
		classUnderTest = new InstallStatusEvent(BACKUP_COMPLETE);
		String name = "test.jar";
		String newname = "test2.jar";
		classUnderTest.setArtifactName(name);
		assertEquals(name, classUnderTest.getArtifactName());
		classUnderTest.setArtifactName(newname);
		assertEquals(newname, classUnderTest.getArtifactName());
	}

	@Test (expected = IllegalArgumentException.class)
	public final void testSetArtifactName_nullarg() {
		classUnderTest = new InstallStatusEvent(BACKUP_COMPLETE);
		classUnderTest.setArtifactName(null);
	}
	
	@Test
	public final void testGetType() {
		classUnderTest = new InstallStatusEvent(BACKUP_COMPLETE);
		assertEquals(BACKUP_COMPLETE, classUnderTest.getType());
	}

	@Test
	public final void testSetType() {
		classUnderTest = new InstallStatusEvent(BACKUP_COMPLETE);
		classUnderTest.setType(BACKUP_STARTED);
		assertEquals(BACKUP_STARTED, classUnderTest.getType());
	}

}
