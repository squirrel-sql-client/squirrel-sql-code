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

package net.sourceforge.squirrel_sql.plugins.dbdiff;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * 
 */
public class ScriptFileManager implements IScriptFileManager
{

	/** Logger for this class. */
	private final static ILogger s_log = LoggerController.createLogger(ScriptFileManager.class);

	private final HashSet<String> scriptFiles = new HashSet<String>();

	private FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl();

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.IScriptFileManager#
	 *      getOutputFilenameForSession(net.sourceforge.squirrel_sql.client.session.ISession, int)
	 */
	public String getOutputFilenameForSession(ISession session, int number) throws IOException
	{
		final String sessionUserName = session.getAlias().getUserName();
		final File tempSessionFile =
			File.createTempFile(sessionUserName + "-" + number + "-session-for-diff-", ".sql");
		scriptFiles.add(tempSessionFile.getAbsolutePath());
		if (s_log.isDebugEnabled())
		{
			s_log.debug("Created temporary script filename for session:  " + tempSessionFile.getAbsolutePath());
		}

		return tempSessionFile.getAbsolutePath();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.IScriptFileManager#cleanupScriptFiles()
	 */
	public void cleanupScriptFiles()
	{
		for (final String scriptFile : scriptFiles)
		{
			final FileWrapper fileWrapper = fileWrapperFactory.create(scriptFile);
			if (fileWrapper.exists())
			{
				if (s_log.isDebugEnabled())
				{
					s_log.debug("Attempting to delete previously created temporary script file: " + scriptFile);
				}
				fileWrapper.delete();
			}
			else
			{
				if (s_log.isDebugEnabled())
				{
					s_log.debug("Previously created temporary script file did not exist: " + scriptFile);
				}

			}
		}
	}

	/**
	 * @param fileWrapperFactory
	 *           the fileWrapperFactory to set
	 */
	public void setFileWrapperFactory(FileWrapperFactory fileWrapperFactory)
	{
		this.fileWrapperFactory = fileWrapperFactory;
	}

}
