package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.sql.Types;
import java.awt.Color;

import java.util.HashMap;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DefaultColumnRenderer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;

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
	 * Render value of object as a string for text output.
	 * Used by Text version of table.
	 */
	public static String renderObject(Object value, ColumnDisplayDefinition colDef)
	{
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null)
			return dataTypeObject.renderObject(value);
		
		// default behaveior: toString
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
	public static boolean isEditableInCell(ColumnDisplayDefinition colDef) {
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null)
			return dataTypeObject.isEditableInCell();
		
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
		
		// Default behavior if no data type found is to use a restorable text field
		// with no other special behavior.
		if (dataTypeObject != null)
			textField = dataTypeObject.getJTextField();
		else textField = new RestorableJTextField();
		
		textField.setBackground(Color.yellow);
		ed = new DefaultCellEditor(textField);

		ed.setClickCountToStart(1);
		return ed;
	}


	/**
	 * Return true if the data type for the column may be edited
	 * in the popup, false if not.
	 */
	public static boolean isEditableInPopup(ColumnDisplayDefinition colDef) {
		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null)
			return dataTypeObject.isEditableInPopup();
		
		// there was no data type object, so this data type is unknown
		// to squirrel and thus cannot be edited.	
		return false;
	}
	
	/**
	 * Return a JTextArea with appropriate handlers for editing
	 * the type of data in the cell.
	 */
	 public static JTextArea getJTextArea(ColumnDisplayDefinition colDef) {

		// The first argument is a JTable, which is only used by instances
		// of JTextField to convert coordinates on a double-click.  Since that
		// cannot happen with the JTextArea, do not bother passing the table.

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
		
		if (dataTypeObject != null)
			return dataTypeObject.getJTextArea();
		
		// default behavior if no appropriate data type found is to create
		// a simple JTextArea with no special handling
		return new RestorableJTextArea();
	}
	
	/**
	 * Call the validate and convert method in the appropriate
	 * DataType object.
	 */
	 public static Object validateAndConvert(
	 	ColumnDisplayDefinition colDef,
	 	String inputValue,
	 	StringBuffer messageBuffer) {

		IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

		if (dataTypeObject != null) {
			// we have an appropriate data type object
			return dataTypeObject.validateAndConvert(inputValue, messageBuffer);
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
					//??
					break;

				case Types.TIME :
					//??
					break;

				case Types.DATE :
					//??
					break;

				case Types.TIMESTAMP :
					//??
					break;

				case Types.BIGINT :
					//??
					break;

				case Types.DOUBLE:
				case Types.FLOAT:
				case Types.REAL:
					//??
					break;

				case Types.DECIMAL:
				case Types.NUMERIC:
					//??
					break;

				case Types.INTEGER:
				case Types.SMALLINT:
				case Types.TINYINT:
					// set up for integers
					dataTypeComponent = new DataTypeInteger(table, colDef);
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
					//??
					break;

				case Types.VARBINARY:
					//??
					break;

				case Types.LONGVARBINARY:
					//??
					break;

				case Types.BLOB:
					//??
					break;

				case Types.CLOB:
					//??
					break;

				case Types.OTHER:
					//??
					break;

				default:	// should never happen
					return null;	// data type is unknown to us

			}

		// remember this DataType object so we can reuse it
		_colDataTypeObjects.put(colDef, dataTypeComponent);

		// If we get here, then no data type object was found for this column.
		// (should not get here because switch default returns null.)
		return dataTypeComponent;
	}
}
