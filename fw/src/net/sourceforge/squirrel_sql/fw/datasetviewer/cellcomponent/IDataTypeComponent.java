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
 
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JTable;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

/**
 * @author gwg
 *
 * These are the calls needed to support the various ways of
 * displaying and editing data for each data type.
 */
public interface IDataTypeComponent
{
	/**
	 * Convert the given object into its printable String value.
	 */
	public String renderObject(Object object);
	
	/**
	 * Returns true if data type may be edited within a table cell,
	 * false if not.
	 */
	public boolean isEditableInCell();

	/**
	 * Get the JTextField component for this data type to be used in a CellEditor.
	 */
	public JTextField getJTextField();
	
	/**
	 * Returns true if data type may be edited in the popup,
	 * false if not.
	 */
	public boolean isEditableInPopup();
	
	/**
	 * Get the JTextArea component for this data type to be used in the CellPopupDialog.
	 */
	public JTextArea getJTextArea();
	
	/**
	 * Validate that the contents of a cell is in the right form for this data type
	 * and convert that text into an object of the correct (Java) type for the column
	 * Ideally this should be a static function, but the mechanics of using CellComponentFactory
	 * and the constraints of the Java language make that difficult.
	 */
	public Object validateAndConvert(String value, StringBuffer messageBuffer);
}
