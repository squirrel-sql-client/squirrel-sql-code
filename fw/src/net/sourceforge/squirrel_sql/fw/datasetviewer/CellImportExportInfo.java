package net.sourceforge.squirrel_sql.fw.datasetviewer;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
 
/**
 * @author gwg
 *
 * This is the block of data needed to save/restore the import/export
 * file name and command previously selected by the user for a given
 * table column.
 * The full table+column name is stored in this data object because
 * it is needed when the application saves this info to a file
 * for re-loading the next time the app starts.
 */
public class CellImportExportInfo {

	String _tableColumnName;
	String _fileName;
	String _command;
	
	CellImportExportInfo(String tableColumnName, String fileName, String command) {
		_tableColumnName = tableColumnName;
		_fileName = fileName;
		_command = command;
	}
	
	String getTableColumnName() { return _tableColumnName;}
	
	String getFileName() { return _fileName;}
	
	String getCommand() { return _command;}
}
