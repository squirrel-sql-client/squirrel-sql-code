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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Clob;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
//??import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.LargeResultSetObjectInfo;

/**
 * @author gwg
 *
 * This class provides the display components for handling Clob data types,
 * specifically SQL type CLOB.
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

public class DataTypeClob
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
	
	/* The CellRenderer used for this data type */
	//??? For now, use the same renderer as everyone else.
	//??
	//?? IN FUTURE: change this to use a new instance of renederer
	//?? for this data type.
	private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();


	/**
	 * Constructor - save the data needed by this data type.
	 */
	public DataTypeClob(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();
	}
	
	/**
	 * Return the name of the java class used to hold this data type.
	 */
	public String getClassName() {
		return "net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.ClobDescriptor";
	}

	/**
	 * Determine if two objects of this data type contain the same value.
	 * Neither of the objects is null
	 */
	public boolean areEqual(Object obj1, Object obj2) {
		return ((ClobDescriptor)obj1).equals(obj2);
	}

	/*
	 * First we have the methods for in-cell and Text-table operations
	 */
	 
	/**
	 * Render a value into text for this DataType.
	 */
	public String renderObject(Object value) {
		return (String)_renderer.renderObject(value);
	}
	
	/**
	 * This Data Type can be edited in a table cell.
	 * This function is not called during the initial table load, or during
	 * normal table operations.
	 * It is called only when the user enters the cell, either to examine
	 * or to edit the data.
	 * The user may have set the LargeResultSetObjectInfo parameters to
	 * minimize the data read during the initial table load (to speed it up),
	 * but when they enter this cell we would like to show them the entire
	 * contents of the CLOB.
	 * Therefore we use a call to this function as a trigger to make sure
	 * that we have all of the CLOB data, if that is possible.
	 */
	public boolean isEditableInCell(Object originalValue) {
		// for convenience, cast the value object to its type
		ClobDescriptor cdesc = (ClobDescriptor)originalValue;
		
		// data is editable if the CLOB has been read and either
		// the size was not limited by the user, or the data is shorter
		// than the user's limit.
		if (cdesc.getClobRead() &&
			(cdesc.getUserSetClobLimit() == 0 ||
				cdesc.getUserSetClobLimit() < cdesc.getData().length()) )
				return true;
		
		// data was not fully read in before, so try to do that now
		try {
//????????????????????????????????????????????????????????????????
			String data = cdesc.getClob().getSubString(1, (int)cdesc.getClob().length());
			
			// read succeeded, so reset the ClobDescriptor to match
			cdesc.setClobRead(true);
			cdesc.setData(data);
			cdesc.setWholeClobRead(true);
			cdesc.setUserSetClobLimit(0);
			
			return true;
		}
		catch (Exception ex) {
			cdesc.setClobRead(true);
			cdesc.setWholeClobRead(false);
			cdesc.setData("Sorry Colin, could not read the data. Error was: "+ex.getMessage());
			return false;
		}	
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
						(RestorableJTextField)DataTypeClob.this._textComponent,
						evt, DataTypeClob.this._table);
					CellDataPopup.showDialog(DataTypeClob.this._table,
						DataTypeClob.this._colDef, tableEvt);
				}
			}
		});	// end of mouse listener

		return (JTextField)_textComponent;
	}

	/**
	 * Implement the interface for validating and converting to internal object.
	 * Null is a valid successful return, so errors are indicated only by
	 * existance or not of a message in the messageBuffer.
	 * If originalValue is null, then we are just checking that the data is
	 * in a valid format (for file import/export) and not actually converting
	 * the data.
	 */
	public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer) {
		// handle null, which is shown as the special string "<null>"
		if (value.equals("<null>") || value.equals(""))
			return null;
			
		// Sprcial case: when reading/writing data from/to files, this function is
		// called to verify that the string is in a valid format.  If we are able to
		// correctly convert the string to the CLOB internal format, there is no error,
		// so just return without creating/changing a ClobDescriptor.
		if (originalValue == null)
			return null;  // for CLOB, the internal data type is String, so it is ok.

		// Do the conversion into the object in a safe manner
		// Reuse the original java.sql.Clob object, but reset all of the 
		// fields to indicate that this is the entire value of the CLOB field.

		// for convenience, cast the object
		ClobDescriptor cdesc = (ClobDescriptor)originalValue;
		cdesc.setData(value);
		cdesc.setClobRead(true);
		cdesc.setWholeClobRead(true);
		cdesc.setUserSetClobLimit(0);
		return originalValue;

	}

	/*
	 * Now the functions for the Popup-related operations.
	 */
	
	/**
	 * Returns true if data type may be edited in the popup,
	 * false if not.
	 */
	public boolean isEditableInPopup(Object originalValue) {
		// use same algorithm as for cell
		return isEditableInCell(originalValue);
	}

	/*
	 * Return a JTextArea usable in the CellPopupDialog
	 * and fill in the value.
	 */
	 public JTextArea getJTextArea(Object value) {
		_textComponent = new RestorableJTextArea();
		
		
		// value is a simple string representation of the data,
		// the same one used in Text and in-cell operations.
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
	 * The following is used in both cell and popup operations.
	 */	
	
	/*
	 * Internal class for handling key events during editing
	 * of both JTextField and JTextArea.
	 */
	 private class KeyTextHandler extends KeyAdapter {
	 	public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				
				// as a coding convenience, create a reference to the text component
				// that is typecast to JTextComponent.  this is not essential, as we
				// could typecast every reference, but this makes the code cleaner
				JTextComponent _theComponent = (JTextComponent)DataTypeClob.this._textComponent;
				String text = _theComponent.getText();


				// handle cases of null
				// The processing is different when nulls are allowed and when they are not.
				//

				if ( DataTypeClob.this._isNullable) {

					// user enters something when field is null
					if (text.equals("<null>")) {
						if ((c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							// delete when null => original value
							DataTypeClob.this._textComponent.restoreText();
							e.consume();
						}
						else {
							// non-delete when null => clear field and add text
							DataTypeClob.this._textComponent.updateText("");
							// fall through to normal processing of this key stroke
						}
					}
					else {
						// check for user deletes last thing in field
						if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							if (text.length() <= 1 ) {
								// about to delete last thing in field, so replace with null
								DataTypeClob.this._textComponent.updateText("<null>");
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
						DataTypeClob.this._textComponent.restoreText();
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
		
		// We always get the CLOB.
		// Since the CLOB is just a pointer to the CLOB data rather than the
		// data itself, this operation should not take much time (as opposed
		// to getting all of the data in the clob).
		Clob clob = rs.getClob(index);

		if (rs.wasNull())
			return null;
		
		// CLOB exists, so try to read the data from it
		// based on the user's directions
		if (largeObjInfo.getReadClobs())
		{
			// User said to read at least some of the data from the clob
			String clobData = null;
			if (clob != null)
			{
				int len = (int)clob.length();
				if (len > 0)
				{

//?????????????????????????????????????????????????????????????????????????
					int charsToRead = len;
					if (!largeObjInfo.getReadCompleteClobs())
					{
						charsToRead = largeObjInfo.getReadClobsSize();
					}
					if (charsToRead > len)
					{
						charsToRead = len;
					}
					clobData = clob.getSubString(1, charsToRead);
				}
			}
			// determine whether we read all there was in the clob or not
			boolean wholeClobRead = false;
			if (largeObjInfo.getReadCompleteClobs() ||
				clobData.length() < largeObjInfo.getReadClobsSize())
				wholeClobRead = true;
				
			return new ClobDescriptor(clob, clobData, true, wholeClobRead,
				largeObjInfo.getReadClobsSize());
		}
		else
		{
			// user said not to read any of the data from the clob
			return new ClobDescriptor(clob, null, false, false, 0);
		}

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
		if (value == null || value.toString() == null || value.toString().length() == 0)
			return _colDef.getLabel() + " IS NULL";
		else
			return "";	// CLOB cannot be used in WHERE clause
	}
	
	
	/**
	 * When updating the database, insert the appropriate datatype into the
	 * prepared statment at variable position 1.
	 */
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value)
		throws java.sql.SQLException {
		if (value == null) {
			pstmt.setNull(1, _colDef.getSqlType());
		}
		else {
			// for convenience cast the object to ClobDescriptor
			ClobDescriptor cdesc = (ClobDescriptor)value;
			
			// I'm not sure whether I need to do both of the following.
			
			// first put the data into the Clob
//???????????????????????????????????????????????????????????????????????????????????
			cdesc.getClob().setString(0, cdesc.getData());
			
			// now put the clob back into the DB
			pstmt.setClob(1, cdesc.getClob());
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
	  * File is assumed to be and ASCII string of digits
	  * representing a value of this data type.
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
	 	
	 	// convert file text into a string
	 	// Special case: some systems tack a newline at the end of
	 	// the text read.  Assume that if last char is a newline that
	 	// we want everything else in the line.
	 	String fileText;
	 	if (charBuf[count-1] == KeyEvent.VK_ENTER)
	 		fileText = new String(charBuf, 0, count-1);
	 	else fileText = new String(charBuf);
	 	
	 	// test that the string is valid by converting it into an
	 	// object of this data type
	 	StringBuffer messageBuffer = new StringBuffer();
	 	validateAndConvertInPopup(fileText, null, messageBuffer);
	 	if (messageBuffer.length() > 0) {
	 		// convert number conversion issue into IO issue for consistancy
	 		throw new IOException(
	 			"Text does not represent data of type "+getClassName()+
	 			".  Text was:\n"+fileText);
	 	}
	 	
	 	// return the text from the file since it does
	 	// represent a valid data value
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
	  * File is assumed to be and ASCII string of digits
	  * representing a value of this data type.
	  */
	 public void exportObject(FileOutputStream outStream, String text)
	 	throws IOException {
	 	
	 	OutputStreamWriter outWriter = new OutputStreamWriter(outStream);
	 	
	 	// check that the text is a valid representation
	 	StringBuffer messageBuffer = new StringBuffer();
	 	validateAndConvertInPopup(text, null, messageBuffer);
	 	if (messageBuffer.length() > 0) {
	 		// there was an error in the conversion
	 		throw new IOException(new String(messageBuffer));
	 	}
	 	
	 	// just send the text to the output file
		outWriter.write(text);
		outWriter.flush();
		outWriter.close();
	 }
}
