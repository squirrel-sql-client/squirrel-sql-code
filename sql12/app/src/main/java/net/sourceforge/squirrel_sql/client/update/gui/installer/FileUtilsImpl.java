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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FileUtilsImpl implements FileUtils
{

	public static String newline = System.getProperty("line.separator");

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.FileUtils#
	 * getLinesFromFile(java.lang.String, java.util.List)
	 */
	public List<String> getLinesFromFile(String filename, List<ScriptLineFixer> lineFixers) throws IOException
	{
		ArrayList<String> lines = new ArrayList<String>();

		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line = null;

		while ((line = reader.readLine()) != null)
		{
			if (lineFixers != null) {
				for (ScriptLineFixer fixer : lineFixers)
				{
					line = fixer.fixLine(line);
				}
			}
			lines.add(line);
		}
		reader.close();
		return lines;
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.update.gui.installer.FileUtils#
	 * writeLinesToFile(java.lang.String, java.util.List)
	 */
	public void writeLinesToFile(String filename, List<String> lines) throws FileNotFoundException
	{
		PrintWriter out = new PrintWriter(new File(filename));
		for (String outline : lines)
		{
			out.write(outline);
			out.write(newline);
		}
		out.close();
	}
}
