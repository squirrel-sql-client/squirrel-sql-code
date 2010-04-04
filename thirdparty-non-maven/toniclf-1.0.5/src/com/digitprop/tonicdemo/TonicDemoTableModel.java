package com.digitprop.tonicdemo;


import java.util.*;

import javax.swing.table.*;
import javax.swing.event.*;


/**	The table model for the Tonic look and feel demo. This model 
 * 	is intended to provide a few mockup table entries, to popuplate
 * 	the demo table.
 * 
 * 	@author	Markus Fischer
 *
 *  	<p>This software is under the <a href="http://www.gnu.org/copyleft/lesser.html" target="_blank">GNU Lesser General Public License</a>
 */

/*
 * ------------------------------------------------------------------------
 * Copyright (C) 2004 Markus Fischer
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 * MA 02111-1307  USA
 * 
 * You can contact the author at:
 *    Markus Fischer
 *    www.digitprop.com
 *    info@digitprop.com
 * ------------------------------------------------------------------------
 */
class TonicDemoTableModel implements TableModel
{
	/**	Registers the specified TableModelListener with this model */
	public void addTableModelListener(TableModelListener l)
	{
		// No listener notification in this model 
	}


	/**	Returns the class of the specified column. Returns String for
	 * 	all columns except for the first, for which the class is
	 * 	Boolean.
	 */
	public Class getColumnClass(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0:
				return Boolean.class;
					
			default:
				return String.class;
		}
	}


	/**	Returns the number of columns */
	public int getColumnCount()
	{
		return 4;
	}

	
	/**	Returns the name of the specified column */
	public String getColumnName(int columnIndex)
	{
		switch(columnIndex)
		{
			case 0:
				return "C";
					
			case 1:
				return "Description";
					
			case 2:
				return "Status";
					
			case 3:
			default:
				return "Location";
		}
	}


	/**	Returns the number of rows */		
	public int getRowCount()
	{
		return 20;
	}
		

	/**	Returns the cell value at the specified row and column */
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		String desc[]=
			{
				"Replace nuclear fusion cell in flux generator",
				"Repair core cooling circuit",
				"Return lightning staff to Moonglow",
				"Negotiate with brotherhood of steel",
				"Realign satellite dish",
				"Prepare for download",
				"Never buy again from global supermarket chain"
			};
				
		String status[]=
			{
				"Open",
				"Closed",
				"Rejected",
				"Filed"
			};
				
		String loc[]=
			{
				"\\data\\storage\\all",
				"\\data\\enhanced",
				"\\data",
				"\\aux\\project",
				"\\aux\\project\\optional",
				"\\rec\\music"
			};
				
				
		Random r=new Random(rowIndex);
		for(int i=0; i<rowIndex; i++)
			r.nextInt();
				
		switch(columnIndex)
		{
			case 0:
				return new Boolean(r.nextBoolean());
					
			case 1:
				return desc[rowIndex%desc.length];
					
			case 2:
				return status[r.nextInt(status.length)];
					
			case 3:
			default:
				return loc[r.nextInt(loc.length)];		
		}
	}


	/**	Returns true if the cell at the specified position is
	 * 	editable. With this model, no cells are editable.
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	
	/**	Removes the specified TableModelListener from this model. */
	public void removeTableModelListener(TableModelListener l)
	{
		// No listener notification in this model
	}
	

	/**	Sets the value of the cell at the specified position */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	{
		// Table is not editable
	}
}