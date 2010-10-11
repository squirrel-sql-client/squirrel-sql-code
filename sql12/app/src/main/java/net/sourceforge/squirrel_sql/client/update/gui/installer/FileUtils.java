package net.sourceforge.squirrel_sql.client.update.gui.installer;

/*
 * Copyright (C) 2010 Rob Manning
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Interface for file utility methods.
 */
public interface FileUtils
{

	/**
	 * Reads the file specified by filename and builds a list of lines, applying the line fixers specified.
	 * 
	 * @param filename
	 *           the name of the file to read lines from.
	 * @param lineFixers
	 *           a list of fixers to apply to each line.  This can be null if no line manipulation is required.
	 * @return a list of lines
	 * @throws IOException
	 *            if an I/O error occurs.
	 */
	List<String> getLinesFromFile(String filename, List<ScriptLineFixer> lineFixers)
		throws IOException;

	/**
	 * Writes the specified list of line to the specified filename.  This will overrite the current contents
	 * of the file.
	 * 
	 * @param filename the file to overwrite
	 * @param lines the lines to write to the file.
	 * @throws FileNotFoundException
	 */
	void writeLinesToFile(String filename, List<String> lines) throws FileNotFoundException;

}