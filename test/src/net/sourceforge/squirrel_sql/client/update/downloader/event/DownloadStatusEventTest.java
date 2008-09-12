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
package net.sourceforge.squirrel_sql.client.update.downloader.event;


import static net.sourceforge.squirrel_sql.client.update.downloader.event.DownloadEventType.DOWNLOAD_STARTED;
import static org.junit.Assert.assertEquals;
import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DownloadStatusEventTest extends BaseSQuirreLJUnit4TestCase
{

	private DownloadStatusEvent classUnderTest = null;
	
	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
		classUnderTest = null;
	}

	@Test
	public void testGetType() {
		classUnderTest = new DownloadStatusEvent(DOWNLOAD_STARTED);
		assertEquals(DOWNLOAD_STARTED, classUnderTest.getType());
	}
	
	@Test
	public void testGetSetFilename() {
		classUnderTest = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_COMPLETED);
		String filename = "somefilename";
		classUnderTest.setFilename(filename);
		assertEquals(filename, classUnderTest.getFilename());
	}
	
	@Test
	public void testGetSetFileCountTotal() {
		classUnderTest = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_COMPLETED);
		classUnderTest.setFileCountTotal(100);
		assertEquals(100, classUnderTest.getFileCountTotal());
	}
	
	@Test
	public void testGetSetException() {
		classUnderTest = new DownloadStatusEvent(DownloadEventType.DOWNLOAD_COMPLETED);
		Exception expected = new Exception("something exceptional");
		classUnderTest.setException(expected);
		assertEquals(expected, classUnderTest.getException());
	}
}
