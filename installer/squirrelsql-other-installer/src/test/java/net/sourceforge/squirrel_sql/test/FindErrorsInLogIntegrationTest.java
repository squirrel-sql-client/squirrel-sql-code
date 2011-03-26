package net.sourceforge.squirrel_sql.test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.IOUtilitiesImpl;

import org.junit.Assert;
import org.junit.Test;
import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;

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

@RunWith(JUnit4ClassRunner.class)
public class FindErrorsInLogIntegrationTest
{

	private final IOUtilities ioutils = new IOUtilitiesImpl();
	
	private static final String LOG_FILE = "./target/test-classes/user-settings-dir/logs/squirrel-sql.log";
	
	@Test
	public void searchForLoggerErrors() throws IOException {
		
		File logFile = new File(LOG_FILE);
		if (!logFile.exists()) {
			return;
		}
		
		List<String> lines = ioutils.getLinesFromFile(LOG_FILE, null);
		for (String line : lines) {
			if (line.contains("Exception")) {
				Assert.fail("Detected an exception in log file ("+LOG_FILE+") : "+line);
			}
			if (line.contains("error") || line.contains("Error")) {
				Assert.fail("Detected an error in log file ("+LOG_FILE+") : "+line);
			}
			
		}
		
	}
	
	
}
