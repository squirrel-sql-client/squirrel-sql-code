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

package net.sourceforge.squirrel_sql.plugins.dbdiff.gui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactoryImpl;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.IDialectFactory;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.IOUtilitiesImpl;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.IScriptFileManager;

/**
 * Base class for all DiffPresentation implementations that display a comparison of the contents of two files
 * side-by-side in some internal or external window.
 */
public abstract class AbstractSideBySideDiffPresentation extends AbstractDiffPresentation
{

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(AbstractSideBySideDiffPresentation.class);

	/** fileWrapperFactory that allows this class to avoid constructing File objects directly */
	protected FileWrapperFactory fileWrapperFactory = new FileWrapperFactoryImpl();

	/** Utility class for working with I/O */
	protected IOUtilities ioutils = new IOUtilitiesImpl();

	private IDialectFactory dialectFactory = new DialectFactoryImpl();

	/**
	 * Sub-class implementations should override this method to provide the implementation for comparing the
	 * contents of the specified script filenames.
	 * 
	 * @param script1Filename
	 *           filename of the first script to compare.
	 * @param script2Filename
	 *           filename of the second script to compare.
	 * @throws Exception
	 */
	protected abstract void executeDiff(String script1Filename, String script2Filename) throws Exception;

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.gui.IDiffPresentation#execute()
	 */
	@Override
	public void execute()
	{

		final ISession sourceSession = sessionInfoProvider.getSourceSession();
		final IScriptFileManager scriptFileManager = sessionInfoProvider.getScriptFileManager();

		final IDatabaseObjectInfo[] selectedDestObjects = sessionInfoProvider.getDestSelectedDatabaseObjects();
		final ISession destSession = sessionInfoProvider.getDestSession();
		final IDatabaseObjectInfo[] selectedSourceObjects =
			sessionInfoProvider.getSourceSelectedDatabaseObjects();

		// Here we use the same dialect for both the source and destination object.
		final HibernateDialect dialect = dialectFactory.getDialect(sourceSession.getMetaData());

		final CreateScriptPreferences csprefs = new CreateScriptPreferences();

		final List<ITableInfo> sourcetables = convertArrayToList(selectedSourceObjects);

		final List<ITableInfo> desttables = convertArrayToList(selectedDestObjects);

		try
		{
			final String script1 =
				constructScriptFromList(dialect.getCreateTableSQL(sourcetables, sourceSession.getMetaData(),
					csprefs, false));

			final String script2 =
				constructScriptFromList(dialect.getCreateTableSQL(desttables, destSession.getMetaData(), csprefs,
					false));

			final String sourceFilename = scriptFileManager.getOutputFilenameForSession(sourceSession, 1);
			final String destFilename = scriptFileManager.getOutputFilenameForSession(destSession, 2);

			writeScriptToFile(script1, sourceFilename);
			writeScriptToFile(script2, destFilename);

			executeDiff(sourceFilename, destFilename);
		}
		catch (final Exception e)
		{
			s_log.error("Unexpected exception while generating sql scripts : " + e.getMessage(), e);
		}

	}

	private void writeScriptToFile(String sqlscript, String file) throws IOException
	{
		if (s_log.isInfoEnabled())
		{
			s_log.info("Writing SQL script to file : " + file);
		}
		final FileWrapper sourcefileWrapper = fileWrapperFactory.create(file);
		ioutils.copyBytesToFile(new ByteArrayInputStream(sqlscript.getBytes()), sourcefileWrapper);
	}

	private List<ITableInfo> convertArrayToList(IDatabaseObjectInfo[] dbObjs)
	{
		final List<ITableInfo> result = new ArrayList<ITableInfo>();
		for (final IDatabaseObjectInfo dbObj : dbObjs)
		{
			if (dbObj instanceof ITableInfo)
			{
				final ITableInfo ti = (ITableInfo) dbObj;
				result.add(ti);
			}
		}
		return result;
	}

	private String constructScriptFromList(List<String> sqlscript)
	{
		final StringBuilder script = new StringBuilder();
		for (final String sql : sqlscript)
		{
			script.append(sql);
			script.append(";\n\n");
		}
		return script.toString();
	}

	/**
	 * @param fileWrapperFactory
	 *           the fileWrapperFactory to set
	 */
	public void setFileWrapperFactory(FileWrapperFactory fileWrapperFactory)
	{
		Utilities.checkNull("setFileWrapperFactory", "fileWrapperFactory", fileWrapperFactory);
		this.fileWrapperFactory = fileWrapperFactory;
	}

	/**
	 * @param ioutils
	 *           the ioutils to set
	 */
	public void setIoutils(IOUtilities ioutils)
	{
		Utilities.checkNull("setIoutils", "ioutils", ioutils);
		this.ioutils = ioutils;
	}

	/**
	 * @param dialectFactory
	 *           the dialectFactory to set
	 */
	public void setDialectFactory(IDialectFactory dialectFactory)
	{
		Utilities.checkNull("setDialectFactory", "dialectFactory", dialectFactory);
		this.dialectFactory = dialectFactory;
	}

}
