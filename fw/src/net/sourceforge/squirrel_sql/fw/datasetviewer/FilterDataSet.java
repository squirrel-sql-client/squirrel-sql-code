/*
 * Copyright (C) 2009 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.fw.datasetviewer;

import java.util.Map;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;

/**
 * An IDataSet implementation that wraps another IDataSet implementation and filters calls to 
 * get with the specified replacements list.  The list index specifies the column and the map 
 * at that index specifies the replacement original value as the key and what to replace it 
 * with as the value.  For example, suppose you wanted to replace the second column of the dataset
 * with values according to the following:
 * 
 * Column 1: 
 *   A => "Alpha"
 *   B => "Beta"
 * 
 * Column 2:
 *   1 => "ONE"
 *   2 => "TWO"
 *
 * Then the replacements list would be created with the following code:
 * 
 * Map<Integer, Map<String,String>> replacement = new HashMap<Integer, Map<String,String>>();
 * HashMap<String,String> map1 = new HashMap<String,String>();
 * map1.put("A", "Alpha");
 * map1.put("B", "Beta");
 * 	 
 * HashMap<String,String> map2 = new HashMap<String,String>();
 * map2.put("1", "ONE");
 * map2.put("2", "TWO");
 * 
 * replacement.put(0, map1);  // Column index is zero-based, so first column index is 0.
 * replacement.put(1, map2);
 */
public class FilterDataSet implements IDataSet {

	private final IDataSet _toBeFiltered;
	private final Map<Integer, Map<String, String>> _replacements;
	
	public FilterDataSet(IDataSet toBeFiltered, Map<Integer, Map<String, String>> replacements) {
		this._toBeFiltered = toBeFiltered;
		this._replacements = replacements;
		
	}
	
	@Override
	public Object get(int columnIndex) throws DataSetException
	{
		Object result = _toBeFiltered.get(columnIndex);
		if (result == null) {
			return result;
		}
		
		Map<String, String> replacementMap = _replacements.get(columnIndex);
		if (replacementMap != null) {
			String value = replacementMap.get(result.toString());
			if (value != null) {
				result = value;
			}
		}
		return result; 
	}

	@Override
	public int getColumnCount() throws DataSetException
	{
		return _toBeFiltered.getColumnCount();
	}

	@Override
	public DataSetDefinition getDataSetDefinition() throws DataSetException
	{
		return _toBeFiltered.getDataSetDefinition();
	}

	@Override
	public boolean next(IMessageHandler msgHandler) throws DataSetException
	{
		return _toBeFiltered.next(msgHandler);
	}
	
}