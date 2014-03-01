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

import java.io.IOException;

import net.sourceforge.squirrel_sql.client.session.ISession;

/**
 * Interface for manager of script files. Ihis allows the plugin to cleanup any temporary script files that
 * were created for comparison purposes.
 */
public interface IScriptFileManager
{

	/**
	 * Creates a filename (for a temporary file) based on the specified session information. This should have
	 * the side effect of storing the filename off for cleanup later.
	 * 
	 * @param session
	 *           the session in which objects have been selected to have their definitions compared
	 * @param number
	 *           which of the two sessions (generally, 1 or 2) is one being passed.
	 * @return a temporary filename
	 * @throws IOException
	 */
	String getOutputFilenameForSession(ISession session, int number) throws IOException;

	/**
	 * Remove the previously created filenames if they exist.
	 */
	void cleanupScriptFiles();

}