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
 
import java.util.HashMap;

/**
 * @author gwg
 *
 * Save and return Import/Export file name and command
 * based on table+column name.  Also allow for load and dump of all data
 * on application startup/shutdown.
 * 
 * There should be only one mapping from table+column to import/export info
 * for the entire application, so we do not bother making an instance of
 * this class.  All operations are static.
 */
public class CellImportExportInfoSaver {
	
	static HashMap map = new HashMap();
	
	// make sure no one can make an instance
	private CellImportExportInfoSaver() {};
	
	static public void save(String tableColumnName,
		String fileName,
		String command) {
		
		// If the table+column already has a data object in the map,
		// then remove it.
		map.remove(tableColumnName);
		
		CellImportExportInfo infoObject =
			new CellImportExportInfo(tableColumnName, fileName, command);
		
		map.put(tableColumnName, infoObject);
	}
	
	static public CellImportExportInfo get(String tableColumnName) {
		return (CellImportExportInfo)map.get(tableColumnName);
	}

}
