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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

/**
 * @author gwg
 *
 * These are the calls needed to support the various ways of
 * displaying and editing data for each data type.
 * 
 *
 */
public interface IDataTypeComponent
{
	/**
	 * Return the name of the Java class used to store this data type
	 * in the application.
	 */
	public String getClassName();
	
	/**
	 * Determine if two objects of this data type contain the same value.
	 * Neither of the objects is null.
	 */
	public boolean areEqual(Object obj1, Object obj2);
	
	/*
	 * Cell related methods come next.
	 */
	
	/**
	 * Convert the given object into its printable String value for use
	 * in Text output and the in-cell representations (CellRenderer and CellEditor).
	 */
	public String renderObject(Object object);
	
	/**
	 * Returns true if data type may be edited within a table cell,
	 * false if not.
	 */
	public boolean isEditableInCell();

	/**
	 * Get the JTextField component for this data type to be used in a CellEditor.
	 * The value of the text field is set by the JTable mechanism using
	 * the same mechanism as the renderer.  The Assumption here is that
	 * the CellEditor uses the same string representation as the CellRenderer.
	 */
	public JTextField getJTextField();
	
	/**
	 * Validate that the contents of a cell is in the right form for this data type
	 * and convert that text into an object of the correct (Java) type for the column
	 * Ideally this should be a static function, but the mechanics of using CellComponentFactory
	 * and the constraints of the Java language make that difficult.
	 */
	public Object validateAndConvert(String value, StringBuffer messageBuffer);
	
	/*
	 * Now the Popup-related methods.
	 * These are not quite symmetric with the in-cell calls because the
	 * conversion of the object into the text to display in the popup is
	 * handled internally to the DataType object inside getJTextArea(),
	 * so we do not need a "renderObjectInPopup" function visible to
	 * the rest of the world.
	 */
	
	/**
	 * Returns true if data type may be edited in the popup,
	 * false if not.
	 */
	public boolean isEditableInPopup();
	
	/**
	 * Get the JTextArea component for this data type to be used in the CellPopupDialog
	 * and fill in the initial value in the appropriate representation.
	 * That representation may be the same as is renderObject(), or it may
	 * be different (e.g. a BLOB may have renderObject=>"<BLOB>" but fill in
	 * the actual value in the Popup TextArea).
	 */
	public JTextArea getJTextArea(Object value);
	
	/**
	 * Validate that the contents of a cell is in the right form for this data type
	 * and convert that text into an object of the correct (Java) type for the column
	 * Ideally this should be a static function, but the mechanics of using CellComponentFactory
	 * and the constraints of the Java language make that difficult.
	 */
	public Object validateAndConvertInPopup(String value, StringBuffer messageBuffer);
	
	
	
	/*
	 * DataBase-related functions
	 */
	 
	 /**
	  * On input from the DB, read the data from the ResultSet into the appropriate
	  * type of object to be stored in the table cell.
	  */
	public Object readResultSet(ColumnDisplayDefinition colDef,
		ResultSet rs, int index)
		throws java.sql.SQLException;

	/**
	 * When updating the database, generate a string form of this object value
	 * that can be used in the WHERE clause to match the value in the database.
	 * A return value of null means that this column cannot be used in the WHERE
	 * clause, while a return of "null" (or "is null", etc) means that the column
	 * can be used in the WHERE clause and the value is actually a null value.
	 * This function must also include the column label so that its output
	 * is of the form:
	 * 	"columnName = value"
	 * or
	 * 	"columnName is null"
	 * or whatever is appropriate for this column in the database.
	 */
	public String getWhereClauseValue(ColumnDisplayDefinition colDef, Object value);
	
	
	/**
	 * When updating the database, insert the appropriate datatype into the
	 * prepared statment at variable position 1.
	 */
	public void setPreparedStatementValue(ColumnDisplayDefinition colDef, 
		PreparedStatement pstmt, Object value)
		throws java.sql.SQLException;

}
