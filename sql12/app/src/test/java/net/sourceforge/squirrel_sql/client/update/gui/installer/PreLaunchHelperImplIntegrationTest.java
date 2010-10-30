/*
 * Copyright (C) 2007 Rob Manning
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
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners( {})
@ContextConfiguration(locations = {
		"/net/sourceforge/squirrel_sql/fw/util/net.sourceforge.squirrel_sql.fw.util.applicationContext.xml",
		"/net/sourceforge/squirrel_sql/client/update/gui/installer/net.sourceforge.squirrel_sql.client.update.gui.installer.applicationContext.xml",
		"/net/sourceforge/squirrel_sql/client/update/gui/installer/event/net.sourceforge.squirrel_sql.client.update.gui.installer.event.applicationContext.xml",
		"/net/sourceforge/squirrel_sql/client/update/gui/installer/util/net.sourceforge.squirrel_sql.client.update.gui.installer.util.applicationContext.xml",
		"/net/sourceforge/squirrel_sql/client/update/util/net.sourceforge.squirrel_sql.client.update.util.applicationContext.xml" })
public class PreLaunchHelperImplIntegrationTest extends AbstractJUnit4SpringContextTests
{

	static
	{
		ApplicationArguments.initialize(new String[] { "-home", "./target" });
	}
	
	public static final String beanIdToTest =
		"net.sourceforge.squirrel_sql.client.update.gui.installer.PreLaunchHelper";

	private static final String SOURCE_SCRIPT_FILE_TO_TEST = "src/test/resources/squirrel-sql.sh";
	
	private static final String TARGET_SCRIPT_FILE_TO_TEST = "target/squirrel-sql-copy.sh";

	@Autowired
	private IOUtilities ioutils;

	@Autowired
	private FileWrapperFactory _fileWrapperFactory;
	
	/**
	 * This test confirms that the launch script can be updated to include the new Splash screen icon
	 * configuration.
	 * 
	 * @throws IOException
	 */
	@Test
	public void testUpdateLaunchScript() throws IOException
	{
		
		FileWrapper sourceScriptFile = _fileWrapperFactory.create(SOURCE_SCRIPT_FILE_TO_TEST);
		FileWrapper targetScriptFile = _fileWrapperFactory.create(TARGET_SCRIPT_FILE_TO_TEST);
		ioutils.copyFile(sourceScriptFile, targetScriptFile);

		// Confirm that the script doesn't contain the splash screen icon setting.
		checkScriptFile(false);
		
		PreLaunchHelperImpl beanToTest = (PreLaunchHelperImpl) applicationContext.getBean(beanIdToTest);
		beanToTest.setScriptLocation(TARGET_SCRIPT_FILE_TO_TEST);
		beanToTest.updateLaunchScript();
		
		// Confirm that the script now contain the splash screen icon setting.
		checkScriptFile(true);
	}

	private void checkScriptFile(boolean containsSplashIconArgument) throws IOException
	{
		List<String> linesFromScriptFile = ioutils.getLinesFromFile(TARGET_SCRIPT_FILE_TO_TEST, null);
		boolean foundMainClassLine = false;
		for (String line : linesFromScriptFile)
		{
			if (line.contains(SplashScreenFixer.CLIENT_MAIN_CLASS))
			{
				foundMainClassLine = true;
				if (!containsSplashIconArgument) {
					assertFalse(line.contains(SplashScreenFixer.SPLASH_ICON));
				} else {
					assertTrue(line.contains(SplashScreenFixer.SPLASH_ICON));
				}
			}
		}
		Assert.assertTrue(foundMainClassLine);
	}
}
