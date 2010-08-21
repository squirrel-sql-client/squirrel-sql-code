package net.sourceforge.squirrel_sql.plugins.dataimport.importer;
/*
 * Copyright (C) 2007 Thorsten Mürell
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

import net.sourceforge.squirrel_sql.plugins.dataimport.ImportFileType;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.csv.CSVFileImporter;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.excel.ExcelFileImporter;

/**
 * This factory creates a IFileImporter for the given type.
 * 
 * @author Thorsten Mürell
 */
public class FileImporterFactory {
	/**
	 * This file is used to create a new file importer.
	 * 
	 * @param type The type for the file importer
	 * @param importFile The import file
	 * @return An implementation of <code>IFileImporter</code>.
	 * @throws IOException An exception is thrown on I/O error
	 */
	public static IFileImporter createImporter(ImportFileType type, File importFile) throws IOException {
		IFileImporter importer = null;
		
		switch (type) {
		case CSV:
			importer = new CSVFileImporter(importFile);
			break;
		case XLS:
			importer = new ExcelFileImporter(importFile);
			break;
			default:
				throw new IllegalArgumentException("No such type: " + type.toString());
		}
		return importer;
	}

}
