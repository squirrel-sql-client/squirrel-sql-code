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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.LargeResultSetObjectInfo;

/**
 * @author gwg
 *
 * This class provides the display components for handling String data types,
 * specifically SQL types CHAR, VARCHAR, and LONGVARCHAR.
 * The display components are for:
 * <UL>
 * <LI> read-only display within a table cell
 * <LI> editing within a table cell
 * <LI> read-only or editing display within a separate window
 * </UL>
 * The class also contains 
 * <UL>
 * <LI> a function to compare two display values
 * to see if they are equal.  This is needed because the display format
 * may not be the same as the internal format, and all internal object
 * types may not provide an appropriate equals() function.
 * <LI> a function to return a printable text form of the cell contents,
 * which is used in the text version of the table.
 * </UL>
 * <P>
 * The components returned from this class extend RestorableJTextField
 * and RestorableJTextArea for use in editing table cells that
 * contain values of this data type.  It provides the special behavior for null
 * handling and resetting the cell to the original value.
 */
public class DataTypeString
	implements IDataTypeComponent
{
	/* the whole column definition */
	private ColumnDisplayDefinition _colDef;

	/* whether nulls are allowed or not */
	private boolean _isNullable;

	/* the number of characters allowed in this field */
	private int _columnSize;

	/* table of which we are part (needed for creating popup dialog) */
	private JTable _table;
	
	/* The JTextComponent that is being used for editing */
	private IRestorableTextComponent _textComponent;

	/* The CellRenderer used for this data type */
	//??? For now, use the same renderer as everyone else.
	//??
	//?? IN FUTURE: change this to use a new instance of renederer
	//?? for this data type.
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();	

	/**
	 * Constructor - save the data needed by this data type.
	 */
	public DataTypeString(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();
		_columnSize = colDef.getColumnSize();
	}
	
	/**
	 * Return the name of the java class used to hold this data type.
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
		return true;
	
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
						(RestorableJTextField)DataTypeString.this._textComponent,
						evt, DataTypeString.this._table);
					CellDataPopup.showDialog(DataTypeString.this._table,
						DataTypeString.this._colDef, tableEvt, true);
				}
			}
		});	// end of mouse listener

		return (JTextField)_textComponent;
	}
	
	/**
	 * Implement the interface for validating and converting to internal object.
	 * Null is a valid successful return, so errors are indicated only by
	 * existance or not of a message in the messageBuffer.
	 */
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer) {
		// handle null, which is shown as the special string "<null>"
		if (value.equals("<null>"))
			return null;

		// Do the conversion into the object in a safe manner
		return value;	// Special case: the input is exactly the output
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
		return true;
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
	 */
	 private class KeyTextHandler extends KeyAdapter {
		// special handling of operations while editing Strings
		public void keyTyped(KeyEvent e) {
			char c = e.getKeyChar();

			// as a coding convenience, create a reference to the text component
			// that is typecast to JTextComponent.  this is not essential, as we
			// could typecast every reference, but this makes the code cleaner
			JTextComponent _theComponent = (JTextComponent)DataTypeString.this._textComponent;
			String text = _theComponent.getText();

			//?? Is there any way to check for invalid input?  Valid input includes
			//?? at least any printable character, but could it also include unprintable
			//?? characters?

			// check for max size reached (only works when DB provides non-zero scale info
			if (DataTypeString.this._columnSize > 0 &&
				text.length()>= DataTypeString.this._columnSize &&
				c != KeyEvent.VK_BACK_SPACE &&
				c != KeyEvent.VK_DELETE) {
				// max size reached
				e.consume();
				_theComponent.getToolkit().beep();
								
				// tabs and newlines get put into the text before this check,
				// so remove them
				if (c == KeyEvent.VK_TAB || c == KeyEvent.VK_ENTER)
					((IRestorableTextComponent)_theComponent).updateText(text.substring(0, text.length()-1));
			}

			// handle cases of null
			// The processing is different when nulls are allowed and when they are not.
			//

			if ( DataTypeString.this._isNullable) {

				// user enters something when field is null
				if (text.equals("<null>")) {
					if ((c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
						// delete when null => original value
						DataTypeString.this._textComponent.restoreText();
						e.consume();
					}
					else {
						// non-delete when null => clear field and add text
						DataTypeString.this._textComponent.updateText("");
						// fall through to normal processing of this key stroke
					}
				}
				else {
					// for strings, a "blank" field is allowed, so only
					// switch to null when there is nothing left in the field
					// and user does delete
					if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
						if (text.length() == 0 ) {
							// about to delete last thing in field, so replace with null
							DataTypeString.this._textComponent.updateText("<null>");
							e.consume();
						}
					}
				}
			}
			else {
				// field is not nullable
				//
				// if the field is not allowed to have nulls, we need to let the
				// user erase the entire contents of the field so that they can enter
				// a brand-new value from scratch.  While the empty field is not a legal
				// value, we cannot avoid allowing it.  This is the normal editing behavior,
				// so we do not need to add anything special here except for the cyclic
				// re-entering of the original data if user hits delete when field is empty
				if (text.length() == 0 &&
					(c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
					// delete when null => original value
					DataTypeString.this._textComponent.restoreText();
					e.consume();
				}
			}
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
		
		String data = rs.getString(index);
		if (rs.wasNull())
			return null;
		else return data;
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
		return "";
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
	 	return true;
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
	 	
	 	InputStreamReader inReader = new InputStreamReader(inStream);
	 	
	 	int fileSize = inStream.available();
	 	
	 	char charBuf[] = new char[fileSize];
	 	
	 	int count = inReader.read(charBuf, 0, fileSize);
	 	
	 	if (count != fileSize)
	 		throw new IOException(
	 			"Could read only "+ count +
	 			" chars from a total file size of " + fileSize +
	 			". Import failed.");
	 	
	 	// convert to string
	 	// Special case: some systems tack a newline at the end of
	 	// the text read.  Assume that if last char is a newline that
	 	// we want everything else in the line.
	 	String fileText;
	 	if (charBuf[count-1] == KeyEvent.VK_ENTER)
	 		fileText = new String(charBuf, 0, count-1);
	 	else fileText = new String(charBuf);
	 	
	 	// data must fit into the column's max size
	 	if (_columnSize > 0 && fileText.length() > _columnSize)
	 		throw new IOException(
	 			"File contains "+fileText.length()+
	 			" characters which exceeds this column's limit of "+
	 			_columnSize+".\nImport Aborted.");	 	
	 	
	 	return fileText;
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
	 	throws IOException {
	 	
	 	OutputStreamWriter outWriter = new OutputStreamWriter(outStream);

	 	// for string, just send the text to the output file
	 	outWriter.write(text);
		outWriter.flush();
		outWriter.close();
	 }		 
}
