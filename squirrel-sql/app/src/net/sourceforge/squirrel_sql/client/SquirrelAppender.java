package net.sourceforge.squirrel_sql.client;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.io.File;
import java.io.IOException;

import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
/**
 * This log4j appender writes out to the
 * <TT>ApplicationFiles.getExecutionLogFile()</TT> file.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SquirrelAppender extends FileAppender {

	public SquirrelAppender() throws IOException, IllegalStateException {
		super(new PatternLayout("%-4r [%t] %-5p %c %x - %m%n"), getLogFile().getAbsolutePath());
	}

	private static File getLogFile() throws IllegalStateException {
		final File logFile = new ApplicationFiles().getExecutionLogFile();
		if (logFile == null) {
			throw new IllegalStateException("null ExecutionLogFile in ApplicationFiles");
		}
		return logFile;
	}
}

