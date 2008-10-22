package net.sourceforge.squirrel_sql.client;
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
import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
/**
 * This log4j appender writes out to the
 * <TT>ApplicationFiles.getExecutionLogFile()</TT> file and rotates the log file based on size of the log 
 * file.  Logs files will not be allowed to grow larger than 1MB and no more than 10 will be kept.
 *
 */
public class SquirrelFileSizeRollingAppender extends RollingFileAppender
{
	/**
	 * Constructor which tells log4j where the application logfile is.  Since this can be changed based on the
	 * user-settings-dir launch option, this Appender hard-codes the location of the file so that it doesn't
	 * need to be configured in two different places (squirrel-sql.sh and log4j.properties)  
	 */
	public SquirrelFileSizeRollingAppender() throws IOException
	{
		super(new PatternLayout("%-4r [%t] %-5p %c %x - %m%n"),
				getLogFile().getAbsolutePath(), true);
		super.setMaxFileSize("1MB");
		super.setMaxBackupIndex(10);
	}

	/**
	 * Retrieve the file to write the execution log to.
	 * 
	 * @return		File to write execution log to.
	 */
	private static File getLogFile()
	{
		final File logFile = new ApplicationFiles().getExecutionLogFile();
		if (logFile == null)
		{
			throw new IllegalStateException("null ExecutionLogFile in ApplicationFiles");
		}
		return logFile;
	}
}
