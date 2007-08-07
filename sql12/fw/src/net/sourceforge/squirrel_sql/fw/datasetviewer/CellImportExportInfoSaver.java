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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Arrays;
//import net.sourceforge.squirrel_sql.fw.util.beanwrapper.StringWrapper;

/**
 * @author gwg
 *
 * Save and return Import/Export file name and command
 * based on table+column name.  Also allow for load and dump of all data
 * on application startup/shutdown.
 * 
 * There should be only one mapping from table+column to import/export info
 * for the entire application, so this is a singleton class.
 * To access the table, other classes must call
 * CellImportExportInfoSaver.getInstance() to get the singleton instance to use.
 * 
 * The loading of this from the file is particularly finicky since it involves
 * the XMLBean Reader class creating an empty instance of the class and loading
 * that instance from the file, then passing that instance in to become the 
 * singleton used by the rest of the framework.  Other loading done during startup
 * (e.g. SQLHistory) is simpler because the data is "owned" by the application-level
 * code, so the singleton is kept there (e.g. _sqlHistory in Application.java).
 * That does not make sense here because this data is "owned" and used only by
 * the fw code.
 */
public class CellImportExportInfoSaver {
	
	/**
	 * The map holding the data for lookup.
	 */
	private HashMap<String, CellImportExportInfo> map = 
        new HashMap<String, CellImportExportInfo>();
	
	/**
	 * The list of commands that user has previously entered
	 */
	private ArrayList<String> cmdList = new ArrayList<String>();
	
	/**
	 * the singleton instance of this class.
	 */
	private static CellImportExportInfoSaver instance = null;
	
	/**
	 * Null Constructor:
	 *  This should be used only by the XMLBean creator when loading from file.
	 */
	public CellImportExportInfoSaver() {};

	/**
	 * get the singleton instance of this class.
	 */
	static public CellImportExportInfoSaver getInstance(){
		if (instance == null)
			instance = new CellImportExportInfoSaver();
		return instance;
	}

	/**
	 * During application startup, an instance of this class is created by
	 * the XMLBean reader and loaded by it automatically.  After it is done
	 * loading the entries into that copy, this method is called to make
	 * that instance become the singleton used by everyone.
	 */
	static public void setInstance(CellImportExportInfoSaver newInstance) {
		if (newInstance == null)
			instance = new CellImportExportInfoSaver();	// better safe than sorry!
		else instance = newInstance;
	}
	
	/**
	 * Used by fw to save user input for export/import on column.
	 */
	public synchronized void save(String tableColumnName,
		String fileName,
		String command) {
		
		// If the table+column already has a data object in the map,
		// then remove it.
		map.remove(tableColumnName);
		
		CellImportExportInfo infoObject =
			new CellImportExportInfo(tableColumnName, fileName, command);
		
		map.put(tableColumnName, infoObject);
		
		if (command != null && command.length() > 0) {
			cmdList.add(command);
			Collections.sort(cmdList);
		}
			
	}
	
	/**
	 * Used by fw to find entries user previously entered for this column.
	 */
	public CellImportExportInfo get(String tableColumnName) {
		return map.get(tableColumnName);
	}
	
	/**
	 * Used by fw to get the list of all commands this user has associated
	 * with columns.
	 */
	public synchronized String[] getCmdList() {
		String[] data = new String[cmdList.size()];
		return cmdList.toArray(data);
	}
	
	/**
	 * Used by fw to delete an entry or to make sure entry does not
	 * exist in table, e.g. because it has been reset to defaults.
	 */
	static public void remove(String tableColumnName) {
		// make sure there is an instance
		if (instance == null)
			instance = new CellImportExportInfoSaver();	// better safe than sorry!
		instance.map.remove(tableColumnName);
	}
	
	/**
	 * method used by XMLBeans to add new item.
	 */
	public void add(CellImportExportInfo info) {
		map.put(info.getTableColumnName(), info);
	}
	
	/**
	 * Method called by reflection in the XMLBean loading of
	 * CellImportExportInfo data from
	 * the files during application startup.
	 */
	public synchronized void setData(CellImportExportInfo[] data)
	{
		for (int i = 0; i < data.length; i++) {
			map.put(data[i].getTableColumnName(), data[i]);	
		}
	}

	/**
	 * Method called by reflection in the XMLBean loading of command list
	 * data from the files during application startup.
	 */
	public synchronized void setCmdList(String[] data)
	{
		cmdList = new ArrayList<String>(Arrays.asList(data));
		Collections.sort(cmdList);
	}


	/**
	 * Method used by the application code when unloading this object
	 * into a file for storage during program shutdown.  It is called
	 * by reflection in the XMLBean Writer code.
	 */
	public synchronized CellImportExportInfo[] getData()
	{
		if (instance == null)
			instance = new CellImportExportInfoSaver();	// better safe than sorry!
			
		CellImportExportInfo[] array = new CellImportExportInfo[instance.map.size()];
		Iterator<CellImportExportInfo> iterator = instance.map.values().iterator();
		int index = 0;
		while (iterator.hasNext()) {
			array[index] = iterator.next();
			index++;
		}

		return array;
	}


}
