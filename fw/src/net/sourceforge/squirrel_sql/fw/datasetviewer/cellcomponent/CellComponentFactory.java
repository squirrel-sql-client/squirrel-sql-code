package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.sql.Types;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.awt.Color;
 
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.HashMap;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DefaultColumnRenderer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.LargeResultSetObjectInfo;

/**
 * @author gwg
 *
 * This class is used by other parts of SQuirreL to select and create
 * the appropriate CellRenderer, CellEditor, JTextArea (for popup dialog),
 * and text representation (for Text datasetviewer).
 * The components are actually created by separate classes which
 * attach the appropriate behavior to them, e.g. not allowing alpha chars
 * in an Integer field.
 * <P>
 * At this time we use only the SQL data type to determine which DataType
 * class to use for the requested component.  In the future it may become
 * useful to include other factors, such as the specific table and column
 * being displayed.  This info could be used to select a specialized class
 * (or a general class using an external resource file) to display table
 * and column specific translations of data, such as translating an integer
 * code in the DB into a mnemonic representation.
 * 
 * The JTable is also needed so that components can translate mouse events properly.
 */
public class CellComponentFactory {

	/* map of existing DataType objects for each column */
	static HashMap _colDataTypeObjects = new HashMap();

	/* The current JTable that we are working with.
	 * This is used only to see when the user moves
	 * to a different JTable so we know when to clear
	 * the HashMap of DataTypeObjects.
	 */
	static JTable _table = null;
	
	/**
	 * Return the name of the Java class that is used to represent
	 * this data type within the application.
	 */
	public static String getClassName(ColumnDisplayDefinition colDef) {
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		if (dataTypeObject != null)
			return dataTypeObject.getClassName();
		else
			return "java.lang.Object";
	}
	
	/**
	 * Determine if the values of two objects are the same.
	 */
	public static boolean areEqual(ColumnDisplayDefinition colDef,
		Object newValue, Object oldValue) {
			
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		if (dataTypeObject != null)
			return dataTypeObject.areEqual(newValue, oldValue);
		
		// we should never get here because the areEqual function is only
		// called when we are trying to update the database, so we know
		// that we have a DataType object for this column (or we would
		// have been stopped from editing by the isEditableXXX methods),
		// but we need a return here to keep the compiler happy.
		return false;
	}


	/*
	 * Operations for Text and in-cell work
	 */
	
	/**
	 * Render value of object as a string for text output.
	 * Used by Text version of table.
	 */
	public static String renderObject(Object value, ColumnDisplayDefinition colDef)
	{
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null)
			return dataTypeObject.renderObject(value);
		
		// default behavior: toString
		return value.toString();
	}
	
	/**
	 * Get a TableCellRenderer for the given column.
	 */
	public static TableCellRenderer getTableCellRenderer(ColumnDisplayDefinition colDef)
	{
		return new CellRenderer(getDataTypeObject(null, colDef));
	}
	

	static private final class CellRenderer extends DefaultTableCellRenderer
	{
		private final IDataTypeComponent _dataTypeObject;

		CellRenderer(IDataTypeComponent dataTypeObject)
		{
			super();

			_dataTypeObject = dataTypeObject;
		}

		public void setValue(Object value)
		{

			// default behavior if no DataType object is to use the
			// DefaultColumnRenderer with no modification.
			if (_dataTypeObject != null)
				super.setValue(_dataTypeObject.renderObject(value));
			else super.setValue(DefaultColumnRenderer.getInstance().renderObject(value));
		}
	}


	/**
	 * Return true if the data type for the column may be edited
	 * within the table cell, false if not.
	 */
	public static boolean isEditableInCell(
		ColumnDisplayDefinition colDef, Object originalValue)
	{
			
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null)
			return dataTypeObject.isEditableInCell(originalValue);
		
		// there was no data type object, so this data type is unknown
		// to squirrel and thus cannot be edited.	
		return false;
	}

	
	/**
	 * Return a DefaultCellEditor using a JTextField with appropriate
	 * handlers to manage the type of input for the cell.
	 */
	public static DefaultCellEditor getInCellEditor(
		JTable table, ColumnDisplayDefinition colDef) {


		DefaultCellEditor ed;

		IDataTypeComponent dataTypeObject = getDataTypeObject(table, colDef);
		
		JTextField textField;
		String editableText;
		
		// Default behavior if no data type found is to use a restorable text field
		// with no other special behavior and hope the object has a toString().
		if (dataTypeObject != null)
			textField = dataTypeObject.getJTextField();
		else textField = new RestorableJTextField();
		
		textField.setBackground(Color.yellow);

		ed = new CellEditorUsingRenderer(textField, dataTypeObject);
		ed.setClickCountToStart(1);
		return ed;
	}
	
	/**
	 * Call the validate and convert method in the appropriate
	 * DataType object.
	 */
	 public static Object validateAndConvert(
	 	ColumnDisplayDefinition colDef,
	 	Object originalValue,
	 	String inputValue,
	 	StringBuffer messageBuffer) {

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

		if (dataTypeObject != null) {
			// we have an appropriate data type object
			return dataTypeObject.validateAndConvert(inputValue, originalValue, messageBuffer);
		}

	 	// No appropriate DataType for this column, so do the best
	 	// we can with what we know.
	 	//
	 	// THIS MAY NOT BE THE BEST BEHAVIOR HERE!!!!!!!
	 		
	 	// Default Operation
	 	if (inputValue.equals("<null>"))
	 		return null;
	 	else return inputValue;
	}
	

	/*
	 * Operations for Popup work.
	 */
	
	/**
	 * Return true if the data type for the column may be edited
	 * in the popup, false if not.
	 */
	public static boolean isEditableInPopup(ColumnDisplayDefinition colDef, Object originalValue) {
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null)
			return dataTypeObject.isEditableInPopup(originalValue);
		
		// there was no data type object, so this data type is unknown
		// to squirrel and thus cannot be edited.	
		return false;
	}
	
	/**
	 * Return a JTextArea with appropriate handlers for editing
	 * the type of data in the cell.
	 */
	 public static JTextArea getJTextArea(ColumnDisplayDefinition colDef, Object value) {

		// The first argument is a JTable, which is only used by instances
		// of JTextField to convert coordinates on a double-click.  Since that
		// cannot happen with the JTextArea, do not bother passing the table.

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null)
			return dataTypeObject.getJTextArea(value);
		
		// default behavior if no appropriate data type found is to create
		// a simple JTextArea with no special handling.
		//
		// In Theory, this cannot happen because if there is no data type object
		// for this column's data type, then isEditableInPopup returns false, so
		// we should not get here.  If there IS a data type object, and isEditableInPopup
		// returns true, then we would have executed the return statement above.
		// Assume that the value can be represented as a string.
		RestorableJTextArea textArea = new RestorableJTextArea();
		textArea.setText(value.toString());
		return textArea;
	}
	
	/**
	 * Call the validate and convert method in the appropriate
	 * DataType object.
	 */
	 public static Object validateAndConvertInPopup(
	 	ColumnDisplayDefinition colDef,
	 	Object originalValue,
	 	String inputValue,
	 	StringBuffer messageBuffer) {

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

		if (dataTypeObject != null) {
			// we have an appropriate data type object
			return dataTypeObject.validateAndConvertInPopup(inputValue, originalValue, messageBuffer);
		}

	 	// No appropriate DataType for this column, so do the best
	 	// we can with what we know.
	 	//
	 	// THIS MAY NOT BE THE BEST BEHAVIOR HERE!!!!!!!
	 		
	 	// Default Operation
	 	if (inputValue.equals("<null>"))
	 		return null;
	 	else return inputValue;
	}


	
	
	/*
	 * DataBase-related functions
	 */
	 
	 /**
	  * On input from the DB, read the data from the ResultSet into the appropriate
	  * type of object to be stored in the table cell.
	  */
	public static Object readResultSet(ColumnDisplayDefinition colDef,
		ResultSet rs, int index,
		LargeResultSetObjectInfo largeObjInfo)
		throws java.sql.SQLException {
			
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

		if (dataTypeObject != null) {
			// we have an appropriate data type object
			return dataTypeObject.readResultSet(rs, index, largeObjInfo);
		}

		//?? Best guess: read object?
		//?? This is probably the wrong thing to do here, but
		//?? I don't know what else to try.
		return rs.getObject(index);
	}

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
	public static String getWhereClauseValue(ColumnDisplayDefinition colDef, Object value) {
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

		if (dataTypeObject != null) {
			// we have an appropriate data type object
			return dataTypeObject.getWhereClauseValue(value);
		}
		
		// if no object for this data type, then cannot use value in where clause
		return null;
	}
	
	/**
	 * When updating the database, insert the appropriate datatype into the
	 * prepared statment at variable position 1.
	 */
	public static void setPreparedStatementValue(ColumnDisplayDefinition colDef,
		PreparedStatement pstmt, Object value)
		throws java.sql.SQLException {

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

		// We should never NOT have an object here because we only get here
		// when a DataType object has claimed that the column is editable.
		// If there is no DataType for the column, then the default in the
		// isEditableXXX() methods in this class is to say that the column
		// is not editable, and therefore we should never have this method
		// called in that case.
		if (dataTypeObject != null) {
			// we have an appropriate data type object
			dataTypeObject.setPreparedStatementValue(pstmt, value);
		}
	}
	
	
	/*
	 * File IO related functions
	 */
	 
	 
	 /**
	  * Say whether or not object can be exported to and imported from
	  * a file.  We put both export and import together in one test
	  * on the assumption that all conversions can be done both ways.
	  */
	 public static boolean canDoFileIO(ColumnDisplayDefinition colDef) {

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		// if no DataType object, then there is nothing to handle File IO,
		// so cannot do it
		if (dataTypeObject == null)
			return false;

		// let DataType object speak for itself
		return dataTypeObject.canDoFileIO();
	 }
	 
	 /**
	  * Read a file and construct a valid object from its contents.
	  * Errors are returned by throwing an IOException containing the
	  * cause of the problem as its message.
	  */
	 public static String importObject(ColumnDisplayDefinition colDef,
	 	FileInputStream inStream)
	 	throws IOException {

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		// if no DataType object, then there is nothing to handle File IO,
		// so cannot do it
		if (dataTypeObject == null)
			throw new IOException(
				"No internal Data Type class for this column's SQL type");

		// let DataType object speak for itself
		return dataTypeObject.importObject(inStream);	 		
	 }

	 
	 /**
	  * Given a text string from the Popup, validate that it makes sense
	  * for the given DataType, then write it out to a file in the
	  * appropriate format.
	  * Errors are returned by throwing an IOException containing the
	  * cause of the problem as its message.
	  */
	 public static void exportObject(ColumnDisplayDefinition colDef,
	 	FileOutputStream outStream, String text)
	 	throws IOException {

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		// if no DataType object, then there is nothing to handle File IO,
		// so cannot do it
		if (dataTypeObject == null)
			throw new IOException(
				"No internal Data Type class for this column's SQL type");

		// let DataType object speak for itself
		dataTypeObject.exportObject(outStream, text);	 		
	 }



	/*
	 * Internal method used for both cell and popup work.
	 */

	/* Identify the type of data in the cell and get an instance
	 * of the appropriate DataType object to work with it.
	 * 
	 * The JTable argument is used only by the DataType objects, not here.
	 * Also, since it is used only for converting coordinates when the user
	 * double-clicks in a cell, the JTextArea component does not use it, so
	 * it may be null in that case.
	 * 
	 * NOTE: This currently gets a new copy of the DataType object for every
	 * column even when multiple columns have the same SQL data type.  JTable's
	 * Render and Edit operations typically re-use the same CellRenderer and
	 * CellEditor objects for every cell by moving the viewpoint of the component
	 * to the location of the cell to be rendered/edited, setting the value in
	 * the component to the value at that cell, telling the component to paint,
	 * then moving that same component to the next cell.  For us, the cells have
	 * specific syntax or size constraints based on the SQL data type and the
	 * metadata from the DB, so we use different CellRenderer/Editor components
	 * for each column.  However, JTable's rendering/editing algorithm allows
	 * us to re-use the same component for all cells in the same column, which
	 * is what we do.  By saving the component, we avoid the need to create
	 * new instances each time the userstarts editing, creates the popup dialog,
	 * or does an operation requireing a static method call (e.g. validateAndConvert).
	 */
	private static IDataTypeComponent getDataTypeObject(
		JTable table,ColumnDisplayDefinition colDef) {
			

		// keep a hash table of the column objects
		// so we can reuse them.
		if (table != _table) {
			// new table - clear hash map
			_colDataTypeObjects.clear();
			_table = table;
		}
		if (_colDataTypeObjects.containsKey(colDef))
			return (IDataTypeComponent)_colDataTypeObjects.get(colDef);

		// we have not already created a DataType object for this column
		// so do that now and save it
		IDataTypeComponent dataTypeComponent = null;
			
		switch (colDef.getSqlType())
			{
				case Types.NULL:	// should never happen
					//??
					break;

				// TODO: When JDK1.4 is the earliest JDK supported
				// by Squirrel then remove the hardcoding of the
				// boolean data type.
				case Types.BIT:
				case 16:
//				case Types.BOOLEAN:
					dataTypeComponent = new DataTypeBoolean(table, colDef);
					break;

				case Types.TIME :
					dataTypeComponent = new DataTypeTime(table, colDef);
					break;

				case Types.DATE :
					dataTypeComponent = new DataTypeDate(table, colDef);
					break;

				case Types.TIMESTAMP :
					dataTypeComponent = new DataTypeTimestamp(table, colDef);
					break;

				case Types.BIGINT :
					dataTypeComponent = new DataTypeLong(table, colDef);
					break;

				case Types.DOUBLE:
				case Types.FLOAT:
					dataTypeComponent = new DataTypeDouble(table, colDef);
					break;
					
				case Types.REAL:
					dataTypeComponent = new DataTypeFloat(table, colDef);
					break;

				case Types.DECIMAL:
				case Types.NUMERIC:
					dataTypeComponent = new DataTypeBigDecimal(table, colDef);
					break;

				case Types.INTEGER:
					// set up for integers
					dataTypeComponent = new DataTypeInteger(table, colDef);
					break;
					
				case Types.SMALLINT:
					dataTypeComponent = new DataTypeShort(table, colDef);
					break;
					
				case Types.TINYINT:
					dataTypeComponent = new DataTypeByte(table, colDef);
					break;

				// TODO: Hard coded -. JDBC/ODBC bridge JDK1.4
				// brings back -9 for nvarchar columns in
				// MS SQL Server tables.
				// -8 is ROWID in Oracle.
				case Types.CHAR:
				case Types.VARCHAR:
				case Types.LONGVARCHAR:
				case -9:
				case -8:
					// set up for string types
					dataTypeComponent = new DataTypeString(table, colDef);
					break;

				case Types.BINARY:
				case Types.VARBINARY:
				case Types.LONGVARBINARY:
					// set up for Binary types
					dataTypeComponent = new DataTypeBinary(table, colDef);
					break;

				case Types.BLOB:
					//??
					break;

				case Types.CLOB:
					//??
					break;

				case Types.OTHER:
					dataTypeComponent = new DataTypeOther(table, colDef);
					break;

				default:
					// data type is unknown to us.
					// It may be an unusual type like "JAVA OBJECT" or "ARRAY",
					// or it may be a DBMS-specific type
					dataTypeComponent = new DataTypeUnknown(table, colDef);

			}

		// remember this DataType object so we can reuse it
		_colDataTypeObjects.put(colDef, dataTypeComponent);

		// If we get here, then no data type object was found for this column.
		// (should not get here because switch default returns null.)
		return dataTypeComponent;
	}
}
