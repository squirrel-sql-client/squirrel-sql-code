package net.sourceforge.squirrel_sql.plugins.dbcopy.prefs;

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

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *   Test class for DBCopyPreferenceBean
 */
public class DBCopyPreferenceBeanTest extends BaseSQuirreLJUnit4TestCase {

	DBCopyPreferenceBean classUnderTest = new DBCopyPreferenceBean();

	@Test
	public void testGetClientName() throws Exception
	{
		classUnderTest.setClientName("aTestString");
		assertEquals("aTestString", classUnderTest.getClientName());
	}

	@Test
	public void testGetClientVersion() throws Exception
	{
		classUnderTest.setClientVersion("aTestString");
		assertEquals("aTestString", classUnderTest.getClientVersion());
	}

	@Test
	public void testIsUseFileCaching() throws Exception
	{
		classUnderTest.setUseFileCaching(true);
		assertEquals(true, classUnderTest.isUseFileCaching());
	}

	@Test
	public void testGetFileCacheBufferSize() throws Exception
	{
		classUnderTest.setFileCacheBufferSize(10);
		assertEquals(10, classUnderTest.getFileCacheBufferSize());
	}

	@Test
	public void testIsUseTruncate() throws Exception
	{
		classUnderTest.setUseTruncate(true);
		assertEquals(true, classUnderTest.isUseTruncate());
	}

	@Test
	public void testIsAutoCommitEnabled() throws Exception
	{
		classUnderTest.setAutoCommitEnabled(true);
		assertEquals(true, classUnderTest.isAutoCommitEnabled());
	}

	@Test
	public void testGetCommitCount() throws Exception
	{
		classUnderTest.setCommitCount(100);
		assertEquals(100, classUnderTest.getCommitCount());
	}

	@Test
	public void testIsWriteScript() throws Exception
	{
		classUnderTest.setWriteScript(true);
		assertEquals(true, classUnderTest.isWriteScript());
	}

	@Test
	public void testIsCopyData() throws Exception
	{
		classUnderTest.setCopyData(true);
		assertEquals(true, classUnderTest.isCopyData());
	}

	@Test
	public void testIsCopyIndexDefs() throws Exception
	{
		classUnderTest.setCopyIndexDefs(true);
		assertEquals(true, classUnderTest.isCopyIndexDefs());
	}

	@Test
	public void testIsCopyForeignKeys() throws Exception
	{
		classUnderTest.setCopyForeignKeys(true);
		assertEquals(true, classUnderTest.isCopyForeignKeys());
	}

	@Test
	public void testIsPruneDuplicateIndexDefs() throws Exception
	{
		classUnderTest.setPruneDuplicateIndexDefs(true);
		assertEquals(true, classUnderTest.isPruneDuplicateIndexDefs());
	}

	@Test
	public void testIsCommitAfterTableDefs() throws Exception
	{
		classUnderTest.setCommitAfterTableDefs(true);
		assertEquals(true, classUnderTest.isCommitAfterTableDefs());
	}

	@Test
	public void testIsPromptForDialect() throws Exception
	{
		classUnderTest.setPromptForDialect(true);
		assertEquals(true, classUnderTest.isPromptForDialect());
	}

	@Test
	public void testIsCheckKeywords() throws Exception
	{
		classUnderTest.setCheckKeywords(true);
		assertEquals(true, classUnderTest.isCheckKeywords());
	}

	@Test
	public void testIsTestColumnNames() throws Exception
	{
		classUnderTest.setTestColumnNames(true);
		assertEquals(true, classUnderTest.isTestColumnNames());
	}

	@Test
	public void testIsCopyPrimaryKeys() throws Exception
	{
		classUnderTest.setCopyPrimaryKeys(true);
		assertEquals(true, classUnderTest.isCopyPrimaryKeys());
	}

	@Test
	public void testGetSelectFetchSize() throws Exception
	{
		classUnderTest.setSelectFetchSize(10);
		assertEquals(10, classUnderTest.getSelectFetchSize());
	}

	@Test
	public void testGetTableDelayMillis() throws Exception
	{
		classUnderTest.setTableDelayMillis(10);
		assertEquals(10, classUnderTest.getTableDelayMillis());
	}

	@Test
	public void testGetRecordDelayMillis() throws Exception
	{
		classUnderTest.setRecordDelayMillis(10);
		assertEquals(10, classUnderTest.getRecordDelayMillis());
	}

	@Test
	public void testIsDelayBetweenObjects() throws Exception
	{
		classUnderTest.setDelayBetweenObjects(true);
		assertEquals(true, classUnderTest.isDelayBetweenObjects());
	}

}
