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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IsNullWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.EmptyWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.ParameterWhereClausePart;
import net.sourceforge.squirrel_sql.fw.gui.IntegerField;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;


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
public class DataTypeString extends BaseDataTypeComponent
	implements IDataTypeComponent
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DataTypeString.class);

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
	 * default length of strings when truncated
	 */
	private final static int DEFAULT_LIMIT_READ_LENGTH = 100;

	/**
	 * Name of this class, which is needed because the class name is needed
	 * by the static method getControlPanel, so we cannot use something
	 * like getClass() to find this name.
	 */
	private static final String thisClassName =
		"net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeString";

	/*
	 * Properties settable by the user
	 */
	// flag for whether we have already loaded the properties or not
	private static boolean propertiesAlreadyLoaded = false;

	/**
	 * If <tt>true</tt> then show newlines as "\n" for the in-cell display,
	 * otherwise do not display newlines in the in-cell display
	 * (i.e. they are thrown out by JTextField when it loads the text document behind the cell).
	 */
	private static boolean _makeNewlinesVisibleInCell = true;

	/**
	 * If <tt>true</tt> then use the LONGVARCHAR data type in the WHERE clause,
	 * otherwise do not include it.
	 * Oracle does not allow that type to be used in a WHERE clause
	 */
	private static boolean _useLongInWhere = true;

	/**
	 * If <tt>true</tt> then limit the size of string data that is read
	 * during the initial table load.
	 */
	private static boolean _limitRead = false;

	/**
	 * If <tt>_limitRead</tt> is <tt>true</tt> then this is how many characters
	 * to read during the initial table load.
	 */
	private static int _limitReadLength = DEFAULT_LIMIT_READ_LENGTH;

	/**
	 * If <tt>_limitRead</tt> is <tt>true</tt> and
	 * this is <tt>true</tt>, then only columns whose label is listed in
	 * <tt>_limitReadColumnList</tt> are limited.
	 */
	private static boolean _limitReadOnSpecificColumns = false;

	/**
	 * If <tt>_limitRead</tt> is <tt>true</tt> and
	 * <tt>_limitReadOnSpecificColumns is <tt>true</tt>, then only columns whose label is listed here.
	 * The column names are converted to ALL CAPS before being put on this list
	 * so that they will match the label retrieved from _colDef.
	 */
	private static HashMap<String, String> _limitReadColumnNameMap = 
	    new HashMap<String, String>();


	/**
	 * Constructor - save the data needed by this data type.
	 */
	public DataTypeString(JTable table, ColumnDisplayDefinition colDef) {
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();
		_columnSize = colDef.getColumnSize();

		loadProperties();
	}

	/**
	 * For sub-classes
	 */
	protected DataTypeString() {
	    
	}
	
	/** Internal function to get the user-settable properties from the DTProperties,
	 * if they exist, and to ensure that defaults are set if the properties have
	 * not yet been created.
	 * <P>
	 * This method may be called from different places depending on whether
	 * an instance of this class is created before the user brings up the Session
	 * Properties window.  In either case, the data is static and is set only
	 * the first time we are called.
	 */
	private static void loadProperties() {

		if (propertiesAlreadyLoaded == false) {
			// get parameters previously set by user, or set default values
			_makeNewlinesVisibleInCell = true;	// set to the default
			String makeNewlinesVisibleString = DTProperties.get(thisClassName, "makeNewlinesVisibleInCell");
			if (makeNewlinesVisibleString != null && makeNewlinesVisibleString.equals("false"))
				_makeNewlinesVisibleInCell = false;

			_useLongInWhere = true;	// set to the default
			String useLongInWhereString = DTProperties.get(thisClassName, "useLongInWhere");
			if (useLongInWhereString != null && useLongInWhereString.equals("false"))
				_useLongInWhere = false;

			_limitRead = false;	// set to default
			String limitReadString = DTProperties.get(thisClassName, "limitRead");
			if (limitReadString != null && limitReadString.equals("true"))
				_limitRead = true;

			_limitReadLength = DEFAULT_LIMIT_READ_LENGTH;	// set to default
			String limitReadLengthString = DTProperties.get(thisClassName, "limitReadLength");
			if (limitReadLengthString != null)
				_limitReadLength = Integer.parseInt(limitReadLengthString);

			_limitReadOnSpecificColumns = false;	// set to default
			String limitReadOnSpecificColumnsString = DTProperties.get(thisClassName, "limitReadOnSpecificColumns");
			if (limitReadOnSpecificColumnsString != null && limitReadOnSpecificColumnsString.equals("true"))
				_limitReadOnSpecificColumns = true;

			// the list of specific column names is in comma-separated format
			// with a comma in front of the first entry as well
			_limitReadColumnNameMap.clear();	// empty the map of old values

			String nameString = DTProperties.get(thisClassName, "limitReadColumnNames");
			int start = 0;
			int end;
			String name;

			while (nameString != null && start < nameString.length()) {
				end = nameString.indexOf(',', start + 1);
				if (end > -1) {
					name = nameString.substring(start+1, end);
					start = end;
				}
				else {
					name = nameString.substring(start+1);
					start = nameString.length();
				}

				_limitReadColumnNameMap.put(name, null);
			}

			propertiesAlreadyLoaded = true;
		}
	}

	/**
	 * Return the name of the java class used to hold this data type.
	 */
	public String getClassName() {
		return "java.lang.String";
	}

	/*
	 * First we have the cell-related and Text-table operations.
	 */


	/**
	 * Render a value into text for this DataType.
	 */
	public String renderObject(Object value) {
		String text = (String)_renderer.renderObject(value);
		if (_makeNewlinesVisibleInCell) {
			 text = text.replaceAll("\n", "/\\n");
		}
		return text;
	}

	/**
	 * This Data Type can be edited in a table cell.
	 * <P>
	 * If the data includes newlines, the user must not be allowed to edit it
	 * in the cell because the CellEditor uses a JTextField which filters out newlines.
	 * If we try to use anything other than a JTextField, or use a JTextField with no
	 * newline filtering, the text is not visible in the cell, so the user cannot even read
	 * the text, much less edit it.  The simplest solution is to allow editing of multi-line
	 * text only in the Popup window.
	 */
	public boolean isEditableInCell(Object originalValue) {
		//			prevent editing if text contains newlines
		 if (originalValue != null && ((String)originalValue).indexOf('\n') > -1)
			 return false;
		else return true;
	}

	/**
	 * See if a value in a column has been limited in some way and
	 * needs to be re-read before being used for editing.
	 * For read-only tables this may actually return true since we want
	 * to be able to view the entire contents of the cell even if it was not
	 * completely loaded during the initial table setup.
	 */
	public boolean needToReRead(Object originalValue) {
		// if we are not limiting anything, return false
		if (_limitRead == false)
			return false;

		// if the value is null, then it was read ok
		if (originalValue == null)
			return false;

		// we are limiting some things.
		// if the string we have is less than the limit, then we are ok
		// and do not need to re-read (because we already have the whole thing).
		if (((String)originalValue).length() < _limitReadLength)
			return false;

		// if the data is longer than the limit, then we have previously
		// re-read the contents and we do not need to re-read it again
		if (((String)originalValue).length() > _limitReadLength)
			return false;

		// if we are limiting all columns, then we need to re-read
		// because we do not know if we have all the data or not
		if (_limitReadOnSpecificColumns == false)
			return true;

		// check for the case where we are limiting some columns
		// but not limiting this particular column
		if (_limitReadColumnNameMap.containsKey(_colDef.getColumnName()))
			return true;	// column is limited and length == limit, so need to re-read
		else return false;	// column is not limited, so we have the whole thing
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
		// The only thing that would prevent us from editing a string in the popup
		// is if that string has been truncated when read from the DB.
		// Thus, being able to edit the string is the same as not needing to re-read
		// the data.
		return ! needToReRead(originalValue);
	}

	/*
		 * Return a JTextArea usable in the CellPopupDialog.
		 */
	 public JTextArea getJTextArea(Object value) {
		_textComponent = new RestorableJTextArea();

		// value is a simple string representation of the data,
		// but NOT the same one used in the Text and in-cell operations.
		// The in-cell version may replace newline chars with "\n" while this version
		// does not.  In other respects it is the same as the in-cell version because both
		// use the _renderer object to do the rendering.
		((RestorableJTextArea)_textComponent).setText((String)_renderer.renderObject(value));

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
	 private class KeyTextHandler extends BaseKeyTextHandler {
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
				_beepHelper.beep(_theComponent);

				// Note: tabs and newlines are allowed in string fields, even though they are unusual.
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
                handleNotNullableField(text, c, e, _textComponent);
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
	public Object readResultSet(ResultSet rs, int index, boolean limitDataRead)
		throws java.sql.SQLException {

		String data = rs.getString(index);
		if (rs.wasNull())
			return null;
		else {
			// if this column is being limited, then truncate the data if needed
			// (start with a quick check for the data being shorter than the limit,
			// in which case we don't need to worry about it).
			if (limitDataRead == true && _limitRead == true
				&& data.length() >= _limitReadLength) {

				// data is longer than the limit, so we need to do more checking
				if (_limitReadOnSpecificColumns == false ||
					(_limitReadOnSpecificColumns == true &&
						_limitReadColumnNameMap.containsKey(_colDef.getColumnName()))) {
					// this column is limited, so truncate the data
					data = data.substring(0, _limitReadLength);
				}

			}
			return data;

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
	public IWhereClausePart getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
		// first do special check to see if we should use LONGVARCHAR
		// in the WHERE clause.
		// (Oracle does not allow this.)
		if (_colDef.getSqlType() == Types.LONGVARCHAR &&
			_useLongInWhere == false)
			return null;	// this column cannot be used in a WHERE clause

		if (value == null || value.toString() == null )
			return new IsNullWhereClausePart(_colDef);
		else {
			// We cannot use this data in the WHERE clause if it has been truncated.
			// Since being truncated is the same as needing to re-read,
			// only use this in the WHERE clause if we do not need to re-read
			if ( ! needToReRead(value))
			{
				return new ParameterWhereClausePart(_colDef, value, this);
			}
			else 
			{ 
				return new EmptyWhereClausePart();	// value is truncated, so do not use in WHERE clause
			}
		}
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


	/*
		 * Property change control panel
		 */

	 /**
	  * Generate a JPanel containing controls that allow the user
	  * to adjust the properties for this DataType.
	  * All properties are static accross all instances of this DataType. 
	  * However, the class may choose to apply the information differentially,
	  * such as keeping a list (also entered by the user) of table/column names
	  * for which certain properties should be used.
	  * <P>
	  * This is called ONLY if there is at least one property entered into the DTProperties
	  * for this class.
	  * <P>
	  * Since this method is called by reflection on the Method object derived from this class,
	  * it does not need to be included in the Interface.
	  * It would be nice to include this in the Interface for consistancy, documentation, etc,
	  * but the Interface does not seem to like static methods.
	  */
	 public static OkJPanel getControlPanel() {

		/*
				 * If you add this method to one of the standard DataTypes in the
				 * fw/datasetviewer/cellcomponent directory, you must also add the name
				 * of that DataType class to the list in CellComponentFactory, method
				 * getControlPanels, variable named initialClassNameList.
				 * If the class is being registered with the factory using registerDataType,
				 * then you should not include the class name in the list (it will be found
				 * automatically), but if the DataType is part of the case statement in the
				 * factory method getDataTypeObject, then it does need to be explicitly listed
				 * in the getControlPanels method also.
				 */

		 // if this panel is called before any instances of the class have been
		 // created, we need to load the properties from the DTProperties.
		 loadProperties();

		return new ClobOkJPanel();
	 }



	 /**
	  * Inner class that extends OkJPanel so that we can call the ok()
	  * method to save the data when the user is happy with it.
	  */
	 private static class ClobOkJPanel extends OkJPanel {
		/*
		 * GUI components - need to be here because they need to be
		 * accessible from the event handlers to alter each other's state.
		 */

        private static final long serialVersionUID = -578848466466561988L;

        // check box for whether to show newlines as "\n" for in-cell display
		private JCheckBox _makeNewlinesVisibleInCellChk =
			// i18n[dataTypeString.newlines=Show newlines as \\n within cells]
			new JCheckBox(s_stringMgr.getString("dataTypeString.newlines"));

		// check box for whether to use LONGVARCHAR in WHERE clause
		// (Oracle does not allow that type in WHERE clause)
		private JCheckBox _useLongInWhereChk =
			// i18n[dataTypeString.allowLongVarchar=Allow LONGVARCHAR type to be used in WHERE clause]
			new JCheckBox(s_stringMgr.getString("dataTypeString.allowLongVarchar"));

		// check box for whether to do any limiting of the data read during initial table load
		private JCheckBox _limitReadChk =
			// i18n[dataTypeString.limitSize=Limit size of strings read during initial table load to max of:]
			new JCheckBox(s_stringMgr.getString("dataTypeString.limitSize"));

		// check box for whether to show newlines as "\n" for in-cell display
		private IntegerField _limitReadLengthTextField =
			new IntegerField(5);

		// check box for whether to show newlines as "\n" for in-cell display
		private JCheckBox _limitReadOnSpecificColumnsChk =
			// i18n[dataTypeString.limitReadOnly=Limit read only on columns with these names:]
			new JCheckBox(s_stringMgr.getString("dataTypeString.limitReadOnly"));

		// check box for whether to show newlines as "\n" for in-cell display
		private JTextArea _limitReadColumnNameTextArea =
			new JTextArea(5, 12);


		public ClobOkJPanel() {

			/* set up the controls */

			// checkbox for displaying newlines as \n in-cell
			_makeNewlinesVisibleInCellChk.setSelected(_makeNewlinesVisibleInCell);

			// checkbox for using LONG in WHERE clause
			_useLongInWhereChk.setSelected(_useLongInWhere);

			// checkbox for limit/no-limit on data read during initial table load
			_limitReadChk.setSelected(_limitRead);
			_limitReadChk.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					_limitReadLengthTextField.setEnabled(_limitReadChk.isSelected());
					_limitReadOnSpecificColumnsChk.setEnabled(_limitReadChk.isSelected());
					_limitReadColumnNameTextArea.setEnabled(_limitReadChk.isSelected() &&
						(_limitReadOnSpecificColumnsChk.isSelected()));
				}
			});


			// fill in the current limit length
			_limitReadLengthTextField.setInt(_limitReadLength);

			// set the flag for whether or not to limit only on specific fields
			_limitReadOnSpecificColumnsChk.setSelected(_limitReadOnSpecificColumns);
			_limitReadOnSpecificColumnsChk.addChangeListener(new ChangeListener(){
				public void stateChanged(ChangeEvent e) {
					_limitReadColumnNameTextArea.setEnabled(
						_limitReadOnSpecificColumnsChk.isSelected());
				}
			});

			// fill in list of column names to check against
			Iterator<String> names = _limitReadColumnNameMap.keySet().iterator();
			StringBuffer namesText = new StringBuffer();
			while (names.hasNext()) {
				if (namesText.length() > 0)
					namesText.append("\n" + names.next());
				else namesText.append(names.next());
			}
			_limitReadColumnNameTextArea.setText(namesText.toString());

			// handle cross-connection between fields
			_limitReadLengthTextField.setEnabled(_limitReadChk.isSelected());
			_limitReadOnSpecificColumnsChk.setEnabled(_limitReadChk.isSelected());
			_limitReadColumnNameTextArea.setEnabled(_limitReadChk.isSelected() &&
				(_limitReadOnSpecificColumnsChk.isSelected()));;

			/*
			 * Create the panel and add the GUI items to it
			  */

			setLayout(new GridBagLayout());

			setBorder(BorderFactory.createTitledBorder(
				// i18n[dataTypeString.typeChar=CHAR, VARCHAR, LONGVARCHAR   (SQL types 1, 12, -1)]
				s_stringMgr.getString("dataTypeString.typeChar")));
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);
			gbc.anchor = GridBagConstraints.WEST;

			gbc.gridx = 0;
			gbc.gridy = 0;

			gbc.gridwidth = GridBagConstraints.REMAINDER;
			add(_makeNewlinesVisibleInCellChk, gbc);

			gbc.gridx = 0;
			gbc.gridy++;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			add(_useLongInWhereChk, gbc);

			gbc.gridy++;
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			add(_limitReadChk, gbc);

			gbc.gridx++;
			gbc.gridwidth = 1;
			add(_limitReadLengthTextField, gbc);

			gbc.gridy++;
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			add(_limitReadOnSpecificColumnsChk, gbc);

			gbc.gridx++;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			JScrollPane scrollPane = new JScrollPane();

			// If we don't always show the scrollbars the whole DataTypePreferencesPanel is flickering like hell.
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			scrollPane.setViewportView(_limitReadColumnNameTextArea);
			add(scrollPane, gbc);


		} // end of constructor for inner class


		/**
		  * User has clicked OK in the surrounding JPanel,
		 * so save the current state of all variables
		  */
		public void ok() {
			// get the values from the controls and set them in the static properties
			_makeNewlinesVisibleInCell = _makeNewlinesVisibleInCellChk.isSelected();
			DTProperties.put(thisClassName,
				"makeNewlinesVisibleInCell", Boolean.valueOf(_makeNewlinesVisibleInCell).toString());

			_useLongInWhere = _useLongInWhereChk.isSelected();
			DTProperties.put(thisClassName,
				"useLongInWhere", Boolean.valueOf(_useLongInWhere).toString());

			_limitRead = _limitReadChk.isSelected();
			DTProperties.put(thisClassName,
				"limitRead", Boolean.valueOf(_limitRead).toString());

			_limitReadLength = _limitReadLengthTextField.getInt();
			DTProperties.put(thisClassName,
				"limitReadLength", Integer.toString(_limitReadLength));

			_limitReadOnSpecificColumns = _limitReadOnSpecificColumnsChk.isSelected();
			DTProperties.put(thisClassName,
				"limitReadOnSpecificColumns", Boolean.valueOf(_limitReadOnSpecificColumns).toString());

			// Handle list of column names

			// remove old name list from map
			_limitReadColumnNameMap.clear();
			// extract column names from text area
			String columnNameText = _limitReadColumnNameTextArea.getText();

			int start = 0;
			int end;
			String name;
			String propertyString = "";

			while (start < columnNameText.length()) {
				// find the next name in the text
				end = columnNameText.indexOf('\n', start+1);
				if (end > -1) {
					name = columnNameText.substring(start, end);
					start = end;
				}
				else {
					name = columnNameText.substring(start);
					start = columnNameText.length();
				}

				// cleanup and standardize the name, and add it to the map
				name = name.trim().toUpperCase();
				if (name.length() == 0)
					continue;	// skip blank lines

				_limitReadColumnNameMap.put(name.trim().toUpperCase(), null);

				// add name to comma-separated string for saving in properties
				propertyString += "," + name.trim().toUpperCase();
			}	// end while

			DTProperties.put(thisClassName,
				"limitReadColumnNames", propertyString);

		}	// end ok

	 } // end of inner class

}
