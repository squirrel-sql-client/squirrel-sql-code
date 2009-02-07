package net.sourceforge.squirrel_sql.client.preferences;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import net.sourceforge.squirrel_sql.AbstractSerializableTest;
import net.sourceforge.squirrel_sql.client.plugin.PluginStatus;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences.IJdbcDebugTypes;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *   Test class for SquirrelPreferences
 */
public class SquirrelPreferencesTest extends AbstractSerializableTest {

	SquirrelPreferences classUnderTest = null;

	@Before
	public void setUp() {
		classUnderTest = new SquirrelPreferences();
		super.serializableToTest = new SquirrelPreferences();
		PluginStatus ps = new PluginStatus("testPlugin");
		((SquirrelPreferences)serializableToTest).setPluginStatuses(new PluginStatus[] { ps });
	}

	@After
	public void tearDown() {
		classUnderTest = null;
		super.serializableToTest = null;
	}
	
	@Test
	public void testGetNewSessionView() throws Exception
	{
		classUnderTest.setNewSessionView("aTestString");
		assertEquals("aTestString", classUnderTest.getNewSessionView());
	}

	@Test
	public void testGetSessionProperties() throws Exception
	{
		classUnderTest.setSessionProperties(null);
		assertNull(classUnderTest.getSessionProperties());
	}

	@Test
	public void testGetMainFrameWindowState() throws Exception
	{
		classUnderTest.setMainFrameWindowState(null);
		assertNull(classUnderTest.getMainFrameWindowState());
	}

	@Test
	public void testGetShowContentsWhenDragging() throws Exception
	{
		classUnderTest.setShowContentsWhenDragging(true);
		assertEquals(true, classUnderTest.getShowContentsWhenDragging());
	}

	@Test
	public void testGetShowMainStatusBar() throws Exception
	{
		classUnderTest.setShowMainStatusBar(true);
		assertEquals(true, classUnderTest.getShowMainStatusBar());
	}

	@Test
	public void testGetShowMainToolBar() throws Exception
	{
		classUnderTest.setShowMainToolBar(true);
		assertEquals(true, classUnderTest.getShowMainToolBar());
	}

	@Test
	public void testGetShowAliasesToolBar() throws Exception
	{
		classUnderTest.setShowAliasesToolBar(true);
		assertEquals(true, classUnderTest.getShowAliasesToolBar());
	}

	@Test
	public void testGetShowDriversToolBar() throws Exception
	{
		classUnderTest.setShowDriversToolBar(true);
		assertEquals(true, classUnderTest.getShowDriversToolBar());
	}

	@Test
	public void testGetShowColoriconsInToolbar() throws Exception
	{
		classUnderTest.setShowColoriconsInToolbar(true);
		assertEquals(true, classUnderTest.getShowColoriconsInToolbar());
	}

	@Test
	public void testGetShowPluginFilesInSplashScreen() throws Exception
	{
		classUnderTest.setShowPluginFilesInSplashScreen(true);
		assertEquals(true, classUnderTest.getShowPluginFilesInSplashScreen());
	}

	@Test
	public void testGetLoginTimeout() throws Exception
	{
		classUnderTest.setLoginTimeout(10);
		assertEquals(10, classUnderTest.getLoginTimeout());
	}

	@Test
	public void testGetLargeScriptStmtCount() throws Exception
	{
		classUnderTest.setLargeScriptStmtCount(10);
		assertEquals(10, classUnderTest.getLargeScriptStmtCount());
	}

	@Test
	public void testGetJdbcDebugType() throws Exception
	{
		classUnderTest.setJdbcDebugType(IJdbcDebugTypes.NONE);
		assertEquals(IJdbcDebugTypes.NONE, classUnderTest.getJdbcDebugType());
	}

	@Test
	public void testGetShowToolTips() throws Exception
	{
		classUnderTest.setShowToolTips(true);
		assertEquals(true, classUnderTest.getShowToolTips());
	}

	@Test
	public void testGetUseScrollableTabbedPanes() throws Exception
	{
		classUnderTest.setUseScrollableTabbedPanes(true);
		assertEquals(true, classUnderTest.getUseScrollableTabbedPanes());
	}

	@Test
	public void testGetMaximizeSessionSheetOnOpen() throws Exception
	{
		classUnderTest.setMaximizeSessionSheetOnOpen(true);
		assertEquals(true, classUnderTest.getMaximizeSessionSheetOnOpen());
	}

	@Test
	public void testGetActionKeys() throws Exception
	{
		classUnderTest.setActionKeys(null);
		assertNotNull(classUnderTest.getActionKeys());
	}

	@Test
	public void testGetPluginStatuses() throws Exception
	{
		classUnderTest.setPluginStatuses(null);
		assertNotNull(classUnderTest.getPluginStatuses());
	}


	@Test
	public void testGetProxySettings() throws Exception
	{
		classUnderTest.setProxySettings(null);
		assertNotNull(classUnderTest.getProxySettings());
	}

	@Test
	public void testGetUpdateSettings() throws Exception
	{
		classUnderTest.setUpdateSettings(null);
		assertNotNull(classUnderTest.getUpdateSettings());
	}

	@Test
	public void testGetShowLoadedDriversOnly() throws Exception
	{
		classUnderTest.setShowLoadedDriversOnly(true);
		assertEquals(true, classUnderTest.getShowLoadedDriversOnly());
	}

	@Test
	public void testIsFirstRun() throws Exception
	{
		classUnderTest.setFirstRun(true);
		assertEquals(true, classUnderTest.isFirstRun());
	}

	@Test
	public void testGetConfirmSessionClose() throws Exception
	{
		classUnderTest.setConfirmSessionClose(true);
		assertEquals(true, classUnderTest.getConfirmSessionClose());
	}

	@Test
	public void testIsFileOpenInPreviousDir() throws Exception
	{
		classUnderTest.setFileOpenInPreviousDir(true);
		assertEquals(true, classUnderTest.isFileOpenInPreviousDir());
	}

	@Test
	public void testIsFileOpenInSpecifiedDir() throws Exception
	{
		classUnderTest.setFileOpenInSpecifiedDir(true);
		assertEquals(true, classUnderTest.isFileOpenInSpecifiedDir());
	}

	@Test
	public void testGetFileSpecifiedDir() throws Exception
	{
		classUnderTest.setFileSpecifiedDir("aTestString");
		assertEquals("aTestString", classUnderTest.getFileSpecifiedDir());
	}

	@Test
	public void testGetFilePreviousDir() throws Exception
	{
		classUnderTest.setFilePreviousDir("aTestString");
		assertEquals("aTestString", classUnderTest.getFilePreviousDir());
	}

	@Test
	public void testIsJdbcDebugToStream() throws Exception
	{
		assertEquals(false, classUnderTest.isJdbcDebugToStream());
	}

	@Test
	public void testIsJdbcDebugToWriter() throws Exception
	{
		assertEquals(false, classUnderTest.isJdbcDebugToWriter());
	}

	@Test
	public void testIsJdbcDebugDontDebug() throws Exception
	{
		assertEquals(true, classUnderTest.isJdbcDebugDontDebug());
	}

	@Test
	public void testdoJdbcDebugToStream() throws Exception
	{
	}

	@Test
	public void testdoJdbcDebugToWriter() throws Exception
	{
	}

	@Test
	public void testdontDoJdbcDebug() throws Exception
	{
	}

	@Test
	public void testGetWarnJreJdbcMismatch() throws Exception
	{
		classUnderTest.setWarnJreJdbcMismatch(true);
		assertEquals(true, classUnderTest.getWarnJreJdbcMismatch());
	}

	@Test
	public void testGetWarnForUnsavedFileEdits() throws Exception
	{
		classUnderTest.setWarnForUnsavedFileEdits(true);
		assertEquals(true, classUnderTest.getWarnForUnsavedFileEdits());
	}

	@Test
	public void testGetWarnForUnsavedBufferEdits() throws Exception
	{
		classUnderTest.setWarnForUnsavedBufferEdits(true);
		assertEquals(true, classUnderTest.getWarnForUnsavedBufferEdits());
	}

	@Test
	public void testGetShowSessionStartupTimeHint() throws Exception
	{
		classUnderTest.setShowSessionStartupTimeHint(true);
		assertEquals(true, classUnderTest.getShowSessionStartupTimeHint());
	}

	@Test
	public void testGetShowDebugLogMessage() throws Exception
	{
		assertEquals(true, classUnderTest.getShowDebugLogMessage());
	}

	@Test
	public void testGetShowInfoLogMessages() throws Exception
	{
		classUnderTest.setShowInfoLogMessages(true);
		assertEquals(true, classUnderTest.getShowInfoLogMessages());
	}

	@Test
	public void testGetShowErrorLogMessages() throws Exception
	{
		classUnderTest.setShowErrorLogMessages(true);
		assertEquals(true, classUnderTest.getShowErrorLogMessages());
	}

	@Test
	public void testGetSavePreferencesImmediately() throws Exception
	{
		classUnderTest.setSavePreferencesImmediately(true);
		assertEquals(true, classUnderTest.getSavePreferencesImmediately());
	}

	@Test
	public void testGetSelectOnRightMouseClick() throws Exception
	{
		classUnderTest.setSelectOnRightMouseClick(true);
		assertEquals(true, classUnderTest.getSelectOnRightMouseClick());
	}

	@Test
	public void testGetShowPleaseWaitDialog() throws Exception
	{
		classUnderTest.setShowPleaseWaitDialog(true);
		assertEquals(true, classUnderTest.getShowPleaseWaitDialog());
	}

	@Test
	public void testGetPreferredLocale() throws Exception
	{
		classUnderTest.setPreferredLocale("aTestString");
		assertEquals("aTestString", classUnderTest.getPreferredLocale());
	}

}