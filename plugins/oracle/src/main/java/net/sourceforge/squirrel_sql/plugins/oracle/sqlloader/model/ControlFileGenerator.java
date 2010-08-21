/*
 Copyright (C) 2009  Jos� David Moreno Ju�rez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.model;

import static java.io.File.separator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class for writing SQL*Loader control files from table information.
 * 
 * @author Jos� David Moreno Ju�rez
 */
public class ControlFileGenerator {
	
	/**
	 * Writes a SQL*Loader control file using the specified settings.
	 * 
	 * @param table				table name
	 * @param columns			column names
	 * @param append			whether to append or replace
	 * @param fieldSeparator	field separator used in the data file
	 * @param stringDelimitator	string delimitator used in the data file
	 * @param directory			directory to store the control file
	 * @throws IOException		thrown when an error occurs while writing the
	 * 							control file
	 */
	public static void writeControlFile(String table, String[] columns, boolean append, String fieldSeparator, String stringDelimitator, String directory) throws IOException {
		final String controlFileExtension = ".ctl";
		BufferedWriter controlFileWriter = null;
		try {
			controlFileWriter = new BufferedWriter(
					new FileWriter(normalizeDirectoryPath(directory) + table + controlFileExtension));
			
			controlFileWriter.write("load data\n\t" +
										(append?"append\n\t":"replace\n\t") +
										"into table " + table + "\n\t" +
										"fields terminated by '" + fieldSeparator + "' " + 
										(stringDelimitator.length()==0?"":"optionally enclosed by " + (stringDelimitator.equals("'")?"\"'\"":"'" + stringDelimitator + "'") + "\n\t("));
			
			int lastFieldIndex = columns.length - 1;
			for (int i = 0; i < lastFieldIndex; i++) {
				controlFileWriter.write(columns[i] + ", ");
			}
			/* No comma after the last column name */
			controlFileWriter.write(columns[lastFieldIndex] + ")\n");
		} finally {
			if (controlFileWriter != null) {
				try {
					controlFileWriter.close();
				} catch (IOException e1) {
					// Ignore
				}
			}
		}
	}

	/**
	 * Adds a file separator to the specified directory name if it doesn't
	 * have one
	 * 
	 * @param directory	directory name to normalize
	 * 
	 * @return	the directory name ended by a file separator
	 */
	private static String normalizeDirectoryPath(String directory) {
		/* Adds a path separator (e.g.: /) at the end of the directory path
		 * if it doesn't already have one */
		if (directory!=null && directory.length()>0 && !directory.endsWith(separator)) {
			return directory + separator;
		}
		return directory;
	}
}
