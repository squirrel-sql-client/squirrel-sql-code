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

import java.io.IOException;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * A DiffPresentation implementation that creates a script from both schemas to be compared, then launches a
 * diff window or configurable external diff tool to compare the schema definitions side-by-side.
 */
public class ExternalToolSideBySideDiffPresentation extends AbstractSideBySideDiffPresentation
{

	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ExternalToolSideBySideDiffPresentation.class);

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.gui.AbstractSideBySideDiffPresentation#
	 *      executeDiff(java.lang.String, java.lang.String)
	 */
	@Override
	protected void executeDiff(String script1Filename, String script2Filename) throws IOException
	{
		final String toolCommand =
			preferenceBean.getGraphicalToolCommand() + " " + script1Filename + " " + script2Filename;

		if (s_log.isInfoEnabled())
		{
			s_log.info("Launching external diff tool with the following command: " + toolCommand);
		}

		Runtime.getRuntime().exec(toolCommand);
	}

}
