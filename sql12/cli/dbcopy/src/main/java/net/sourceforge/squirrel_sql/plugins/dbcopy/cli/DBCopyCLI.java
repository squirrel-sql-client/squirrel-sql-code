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

package net.sourceforge.squirrel_sql.plugins.dbcopy.cli;

import org.apache.commons.cli.MissingOptionException;


public class DBCopyCLI
{

	private static SessionUtil sessionUtil = new SessionUtil();

	private static DBCopyRunner runner = new DBCopyRunner();

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
		try
		{
			CommandLineArgumentProcessor argProcessor = new CommandLineArgumentProcessor(args);
			runner.setSourceSchemaName(argProcessor.getSourceSchemaName());
			runner.setSourceCatalogName(argProcessor.getSourceCatalogName());
			runner.setDestSchemaName(argProcessor.getDestSchemaName());
			runner.setDestCatalogName(argProcessor.getDestCatalogName());
			runner.setSourceSession(sessionUtil.getSessionForAlias(argProcessor.getSourceAliasName()));
			runner.setDestSession(sessionUtil.getSessionForAlias(argProcessor.getDestAliasName()));
			runner.run();
		}
		catch (MissingOptionException e)
		{
			// We handle printing the usage in the argProcessor, so no need to log it here.
		}
	}

}
