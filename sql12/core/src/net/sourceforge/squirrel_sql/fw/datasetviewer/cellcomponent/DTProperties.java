package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
 * Object to save, manage and restore DataType-specific properties. 
 * We expect most of these to be settable
 * by the user, but this can be used to save anything that is class-specific
 * in the DataType objects.
 * <P>
 * IMPORTANT: It is the responsibility of the DataType classes to put the
 * property/value pair into this class.  This class does not proactively
 * go out to the DataTypes looking for properties to be saved.
 * <P>
 * This object is created from a file during application startup by the code in
 * net.sourceforge.squirrel_sql.client.Application
 * and saved on application shutdown.
 * The application deals with one view of the data, while internally the
 * Data Type objects work with an entirely different view of the same data.
 * The data is converted from one form to the other during loading and
 * getting of the data when requesed by the Application.
 * <P>
 * The application sees the data as an array of strings.
 * <P>
 * The Data Types see the data as individual String properties.
 * The DataTypes may convert the String to some other form such as
 * a number or a boolean, but that is not the concern of this class.
 * Individual properties are accessed based on the DataType class name
 * and the name of the property.
 * DataTypes are free to create new properties at any time.
 * This class just stores what is passed to it and returns those values
 * when asked.
 * <P>
 * This is named DTProperties rather than DataTypeProperties because that
 * name would indicate that this is another DataType rather than being a 
 * set of data items within the other DataTypes.
 */

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

public class DTProperties {
	
	/**
	 * The version of the data suitable for loading/unloading from/to files.
	 * The internal representation is a HashMap containing HashMaps containing strings.
	 * The XMLReader/Writer Beans in fw.xml do not handle the general case of HashMaps,
	 * so rather than trying to handle that there, we just convert the data internally into a form
	 * that those classes can handle, i.e. an array of strings.
	 * Each string consists of the name of the class, a space, the name of
	 * a property, an equals, then the contents of the property, which must
	 * also be a string.
	 * This is used in an instance of this class created during load/unload.
	 */
	private String[] dataArray = new String[0];
	
	/**
	 * The mapping from DataType name to object (also a HashMap)
	 * containing the properties for that DataType.
	 * There is only one copy of this table for all instances of this class.
	 */
	private static HashMap<String, HashMap<String, String>> dataTypes = 
        new HashMap<String, HashMap<String, String>>();
	
	/**
	 * ctor
	 */
	public DTProperties() {}
	

	/**
	 * get data in form that can be used to output to file.
	 * This is called from an instance of this class.
	 */
	public String[] getDataArray() {
		// first convert internal data into the string array
		Iterator<String> keys = dataTypes.keySet().iterator();

		ArrayList<String> propertyList = new ArrayList<String>();
		
		// get each DataType's info
		while (keys.hasNext()) {
			String tableName = keys.next();
			HashMap<String, String> h = dataTypes.get(tableName);
			
			Set<Entry<String, String>> properties =  h.entrySet();
			for (Entry<String, String> entry : properties) {
				String propertyName = entry.getKey();
				StringBuilder tmp = new StringBuilder(tableName);
				tmp.append(" ");
            tmp.append(propertyName);
            tmp.append("=");
            tmp.append(entry.getValue());
            propertyList.add(tmp.toString());
			}
		}

		dataArray = propertyList.toArray(dataArray);
		return dataArray;
	}
	
	/**
	 * Data in the external form (array of strings) is passed in and must be converted
	 * to the internal form.
	 * This is called on an instance of this class.
	 * @param inData array of strings in form "DataTypeClass property=value"
	 */
	public void setDataArray(String[] inData) {
		dataTypes = new HashMap<String, HashMap<String, String>>();	// make sure we are starting clean
		
		// convert each string into Classname, prop, & value and fill it into the data
		for (int i=0; i< inData.length; i++) {
			int endIndex = inData[i].indexOf(" ");
			String dataTypeName = inData[i].substring(0, endIndex);
			
			int startIndex;
			startIndex = endIndex + 1;
			endIndex = inData[i].indexOf("=", startIndex);
			String propertyName = inData[i].substring(startIndex, endIndex);
			String propertyValue = inData[i].substring(endIndex+1);
			
			// if we have seen a property for this DataType before, then the
			// hashmap already exists.  Otherwise, we need to create it now.
			HashMap<String, String> h = dataTypes.get(dataTypeName);
			if (h == null) {
				h = new HashMap<String, String>();
				dataTypes.put(dataTypeName, h);
			}
			
			// put the property into the hashmap
			h.put(propertyName, propertyValue);
		}
	}

	/**
	 * add or replace a table-name/hashmap-of-column-names mapping.
	 * If map is null, remove the entry from the tables.
	 */
	public static void put(String dataTypeName, String propertyName,
		String propertyValue) {
		
		// get the hashmap for this type, or create it if this is a new property
		HashMap<String, String> h = dataTypes.get(dataTypeName);
		if (h == null) {
			h = new HashMap<String, String>();
			dataTypes.put(dataTypeName, h);
		}
		h.put(propertyName, propertyValue);
	}
	
	/**
	 * get the HashMap of column names for the given table name.
	 * it will be null if the table does not have any limitation on the columns to use.
	 */
	public static String get(String dataTypeName, String propertyName) {
		HashMap<String, String> h = dataTypes.get(dataTypeName);
		if (h == null)
			return null;
		
		return h.get(propertyName);
	}
		
}
