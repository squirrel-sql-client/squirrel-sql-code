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
import java.awt.event.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.LargeResultSetObjectInfo;

/**
 * @author gwg
 *
 * This class provides the display components for handling SQL Other data types,
 * specifically SQL type OTHER.
 * <P>
 * The default SQuirreL code can handle only JDBC-standard defined data types.
 * Since this data type represents DBMS-specific enhancements or
 * user-defined data types, we cannot do anything intelligent with the data.
 * We allow the user to select one of two modes of operation:
 * <DL>
 * <LI>
 * we can try to get the contents of the DB element and print it as a string, or,
 * <LI>
 * we will display an appropriately internationalized version of "<OTHER>".
 * </DL>
 * In either case, the data will be stored and processed as a String.
 * <P>
 * The user may not edit the contents of this field in either the cell or popup
 * because we do not understand the structure or limitations of the contents,
 * and therefore cannot validate it or put it back into the DB.
 * The field is not used in the WHERE clause because we do not know whether
 * or not it might contain binary data, and because we do not know how to
 * format the data for SQL operations.
 * <P>
 * To handle these data types more intelligently and allow editing on them,
 * DBMS-specific plug-ins will need to be developed to register handlers
 * for instances of this type.
 */
public class DataTypeOther
	implements IDataTypeComponent
{
	/* the whole column definition */
	private ColumnDisplayDefinition _colDef;

	/* whether nulls are allowed or not */
	private boolean _isNullable;

	/* table of which we are part (needed for creating popup dialog) */
	private JTable _table;
	
	/* The JTextComponent that is being used for editing */
	private IRestorableTextComponent _textComponent;
	
	/** Internationalized strings for this class, shared/copied from ResultSetReader. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataTypeOther.class);	

	/* The CellRenderer used for this data type */
	//??? For now, use the same renderer as everyone else.
	//??
	//?? IN FUTURE: change this to use a new instance of renederer
	//?? for this data type.
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();	

	/**
	 * Constructor - save the data needed by this data type.
	 */
	public DataTypeOther(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();
	}
	
	/**
	 * Return the name of the java class used to hold this data type.
	 * For Other, this will always be a string.
	 */
	public String getClassName() {
		return "java.lang.String";
	}

	/**
	 * Determine if two objects of this data type contain the same value.
	 * Neither of the objects is null
	 */
	public boolean areEqual(Object obj1, Object obj2) {
		return ((String)obj1).equals(obj2);
	}
	

	/*
	 * First we have the cell-related and Text-table operations.
	 */
	 
	 	
	/**
	 * Render a value into text for this DataType.
	 */
	public String renderObject(Object value) {
		return (String)_renderer.renderObject(value);
	}
	
	/**
	 * This Data Type can be edited in a table cell.
	 */
	public boolean isEditableInCell(Object originalValue) {
		return false;
	
	}
		
	/**
	 * Return a JTextField usable in a CellEditor.
	 */
	public JTextField getJTextField() {
		_textComponent = new RestorableJTextField();
		
		// special handling of operations while editing this data type
		((RestorableJTextField)_textComponent).addKeyListener(new KeyTextHandler());
				
		//
		// handle mouse events for double-click creation of popup dialog.
		// This happens only in the JTextField, not the JTextArea, so we can
		// make this an inner class within this method rather than a separate
		// inner class as is done with the KeyTextHandler class.
		//
		((RestorableJTextField)_textComponent).addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				if (evt.getClickCount() == 2)
				{
					MouseEvent tableEvt = SwingUtilities.convertMouseEvent(
						(RestorableJTextField)DataTypeOther.this._textComponent,
						evt, DataTypeOther.this._table);
					CellDataPopup.showDialog(DataTypeOther.this._table,
						DataTypeOther.this._colDef, tableEvt, true);
				}
			}
		});	// end of mouse listener

		return (JTextField)_textComponent;
	}
	
	/**
	 * Implement the interface for validating and converting to internal object.
	 * Since we do not know how to convert Other objects,
	 * just return null with no error in the messageBuffer
	 */
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer) {
		return null;
	}

	/**
	 * If true, this tells the PopupEditableIOPanel to use the
	 * binary editing panel rather than a pure text panel.
	 * The binary editing panel assumes the data is an array of bytes,
	 * converts it into text form, allows the user to change how that
	 * data is displayed (e.g. Hex, Decimal, etc.), and converts
	 * the data back from text to bytes when the user editing is completed.
	 * If this returns false, this DataType class must
	 * convert the internal data into a text string that
	 * can be displayed (and edited, if allowed) in a TextField
	 * or TextArea, and must handle all
	 * user key strokes related to editing of that data.
	 */
	public boolean useBinaryEditingPanel() {
		return false;
	}
	 
	
	
	/*
	 * Now define the Popup-related operations.
	 */

	
	/**
	 * Returns true if data type may be edited in the popup,
	 * false if not.
	 */
	public boolean isEditableInPopup(Object originalValue) {
		return false;
	}
	
	/*
	 * Return a JTextArea usable in the CellPopupDialog.
	 */
	 public JTextArea getJTextArea(Object value) {
		_textComponent = new RestorableJTextArea();
	
		// value is a simple string representation of the data,
		// the same one used in the Text and in-cell operations.
		((RestorableJTextArea)_textComponent).setText(renderObject(value));
		
		// special handling of operations while editing this data type
		((RestorableJTextArea)_textComponent).addKeyListener(new KeyTextHandler());
		
		return (RestorableJTextArea)_textComponent;
	 }

	/**
	 * Validating and converting in Popup is identical to cell-related operation.
	 */
	public Object validateAndConvertInPopup(String value, Object originalValue, StringBuffer messageBuffer) {
		return validateAndConvert(value, originalValue, messageBuffer);
	}


	/*
	 * The following is used by both in-cell and Popup operations.
	 */

	
	/*
	 * Internal class for handling key events during editing
	 * of both JTextField and JTextArea.
	 * Since neither cell nor popup are allowed to edit, just ignore
	 * anything seen here.
	 */
	 private class KeyTextHandler extends KeyAdapter {
		// special handling of operations while editing Strings
		public void keyTyped(KeyEvent e) {
			char c = e.getKeyChar();

			// as a coding convenience, create a reference to the text component
			// that is typecast to JTextComponent.  this is not essential, as we
			// could typecast every reference, but this makes the code cleaner
			JTextComponent _theComponent = (JTextComponent)DataTypeOther.this._textComponent;
			e.consume();
			_theComponent.getToolkit().beep();
		}
	}


	
	
	/*
	 * DataBase-related functions
	 */
	 
	 /**
	  * On input from the DB, read the data from the ResultSet into the appropriate
	  * type of object to be stored in the table cell.
	  */
	public Object readResultSet(ResultSet rs, int index,
		LargeResultSetObjectInfo largeObjInfo)
		throws java.sql.SQLException {

			
		String data = null;
		if (largeObjInfo.getReadSQLOther())
		{
			// Running getObject on a java class attempts
			// to load the class in memory which we don't want.
			// getString() just gets the value without loading
			// the class (at least under PostgreSQL).
			//row[i] = _rs.getObject(index);
			data = rs.getString(index);
		}
		else
		{
			data = s_stringMgr.getString("DataTypeOther.other");
		}
		
		if (rs.wasNull())
			return null;
		else return data;

		
//		String data = rs.getString(index);
//		if (rs.wasNull())
//			return null;
//		else return data;
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
	public String getWhereClauseValue(Object value) {
		if (value == null || value.toString() == null )
			return _colDef.getLabel() + " IS NULL";
		else
			return _colDef.getLabel() + "='" + value.toString() + "'";
	}
	
	
	/**
	 * When updating the database, insert the appropriate datatype into the
	 * prepared statment at the given variable position.
	 */
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
		throws java.sql.SQLException {
		if (value == null) {
			pstmt.setNull(position, _colDef.getSqlType());
		}
		else {
			pstmt.setString(position, ((String)value));
		}
	}
	
	/**
	 * Get a default value for the table used to input data for a new row
	 * to be inserted into the DB.
	 */
	public Object getDefaultValue(String dbDefaultValue) {
		if (dbDefaultValue != null) {
			// try to use the DB default value
			StringBuffer mbuf = new StringBuffer();
			Object newObject = validateAndConvert(dbDefaultValue, null, mbuf);
			
			// if there was a problem with converting, then just fall through
			// and continue as if there was no default given in the DB.
			// Otherwise, use the converted object
			if (mbuf.length() == 0)
				return newObject;
		}
		
		// no default in DB.  If nullable, use null.
		if (_isNullable)
			return null;
		
		// field is not nullable, so create a reasonable default value
		// cannot create default value for unknown data type
		return null;
	}
	
	
	/*
	 * File IO related functions
	 */
	 
	 
	 /**
	  * Say whether or not object can be exported to and imported from
	  * a file.  We put both export and import together in one test
	  * on the assumption that all conversions can be done both ways.
	  */
	 public boolean canDoFileIO() {
	 	return false;
	 }
	 
	 /**
	  * Read a file and construct a valid object from its contents.
	  * Errors are returned by throwing an IOException containing the
	  * cause of the problem as its message.
	  * <P>
	  * DataType is responsible for validating that the imported
	  * data can be converted to an object, and then must return
	  * a text string that can be used in the Popup window text area.
	  * This object-to-text conversion is the same as is done by
	  * the DataType object internally in the getJTextArea() method.
	  * 
	  * <P>
	  * File is assumed to be printable text characters,
	  * possibly including newlines and tabs but not characters
	  * that would require a binary representation to display
	  * to user.
	  */
	public String importObject(FileInputStream inStream)
	 	throws IOException {
	 	
		throw new IOException("Can not import data type OTHER");
	}

	 	 
	 /**
	  * Construct an appropriate external representation of the object
	  * and write it to a file.
	  * Errors are returned by throwing an IOException containing the
	  * cause of the problem as its message.
	  * <P>
	  * DataType is responsible for validating that the given text
	  * text from a Popup JTextArea can be converted to an object.
	  * This text-to-object conversion is the same as validateAndConvertInPopup,
	  * which may be used internally by the object to do the validation.
	  * <P>
	  * The DataType object must flush and close the output stream before returning.
	  * Typically it will create another object (e.g. an OutputWriter), and
	  * that is the object that must be flushed and closed.
	  * 
	  * <P>
	  * File is assumed to be printable text characters,
	  * possibly including newlines and tabs but not characters
	  * that would require a binary representation to display
	  * to user.
	  */
	 public void exportObject(FileOutputStream outStream, String text)
	 	throws IOException 	 {	
	 	
		throw new IOException("Can not export data type OTHER");
	 }		 
}
