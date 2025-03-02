package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;
/*
 * Copyright (C) 2001-2004 Colin Bell
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

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.EmptyWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IsNullWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup.CellDataDialogHandler;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
 * to see if they are equal. This is needed because the display format
 * may not be the same as the internal format, and all internal object
 * types may not provide an appropriate equals() function.
 * <LI> a function to return a printable text form of the cell contents,
 * which is used in the text version of the table.
 * </UL>
 * <P>
 * The components returned from this class extend RestorableJTextField
 * and RestorableJTextArea for use in editing table cells that
 * contain values of this data type. It provides the special behavior for null
 * handling and resetting the cell to the original value.
 */
public class DataTypeClob extends BaseDataTypeComponent implements IDataTypeComponent
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataTypeClob.class);

	/* whether nulls are allowed or not */
	private boolean _isNullable;

	/* table of which we are part (needed for creating popup dialog) */
	private JTable _table;

	/* The JTextComponent that is being used for editing */
	private IRestorableTextComponent _textComponent;

	private static DataTypeClobProperties s_properties = new DataTypeClobProperties();

    
	/**
	 * Constructor - save the data needed by this data type.
	 */
	public DataTypeClob(JTable table, ColumnDisplayDefinition colDef)
	{
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();

		s_properties.loadProperties();
	}

	public DataTypeClobProperties getProperties()
	{
		return s_properties;
	}



	/** Internal function to get the user-settable properties from the DTProperties,
	 * if they exist, and to ensure that defaults are set if the properties have
	 * not yet been created.
	 * <P>
	 * This method may be called from different places depending on whether
	 * an instance of this class is created before the user brings up the Session
	 * Properties window. In either case, the data is static and is set only
	 * the first time we are called.
	 */

	/**
	 * Return the name of the java class used to hold this data type.
	 */
	public String getClassName() {
		return "net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.ClobDescriptor";
	}

	/*
	 * First we have the methods for in-cell and Text-table operations
	 */

	/**
	 * Render a value into text for this DataType.
	 */
	public String renderObject(Object value, DataTypeRenderingHint noGroupingSeparator)
	{
		String text = (String) DefaultColumnRenderer.renderObject(value);
		if(s_properties.isMakeNewlinesVisibleInCell())
		{
			text = text.replaceAll("\n", "\\\\n");
		}
		return text;
	}

	/**
	 * This Data Type can be edited in a table cell.
	 * This function is not called during the initial table load, or during
	 * normal table operations.
	 * It is called only when the user enters the cell, either to examine
	 * or to edit the data.
	 * The user may have set the DataType properties to
	 * minimize the data read during the initial table load (to speed it up),
	 * but when they enter this cell we would like to show them the entire
	 * contents of the CLOB.
	 * Therefore we use a call to this function as a trigger to make sure
	 * that we have all of the CLOB data, if that is possible.
	 * <P>
	 * If the data includes newlines, the user must not be allowed to edit it
	 * in the cell because the CellEditor uses a JTextField which filters out newlines.
	 * If we try to use anything other than a JTextField, or use a JTextField with no
	 * newline filtering, the text is not visible in the cell, so the user cannot even read
	 * the text, much less edit it. The simplest solution is to allow editing of multi-line
	 * text only in the Popup window.
	 */
	public boolean isEditableInCell(Object originalValue)
	{
		if(wasWholeClobRead(originalValue))
		{
			ClobDescriptor cdesc = (ClobDescriptor) originalValue;

			if(cdesc.wasWholeClobRead())
			{
				// Editing in cell is possible without new lines only.
				if (cdesc != null && cdesc.getData() != null && cdesc.getData().indexOf('\n') > -1)
				{
					return false;
				}
				else
				{
					return true;
				}
			}
		}

		return false;
	}

	public boolean wasWholeClobRead(Object value)
	{
		if(value instanceof ClobDescriptor)
		{
			return ((ClobDescriptor) value).wasWholeClobRead();
		}

		return false;
	}


	/**
	 * See if a value in a column has been limited in some way and
	 * needs to be re-read before being used for editing.
	 * For read-only tables this may actually return true since we want
	 * to be able to view the entire contents of the cell even if it was not
	 * completely loaded during the initial table setup.
	 */
	public boolean needToReRead(Object originalValue) {
		// CLOBs are different from normal data types in that what is actually
		// read from the DB is a descriptor pointing to the data rather than the
		// data itself. During the initial load of the table, the values read from the
		// descriptor may have been limited, but the descriptor itself has been
		// completely read, Therefore we do not need to re-read the datum
		// from the Database because we know that we have the entire
		// descriptor. If the contents of the CLOB have been limited during
		// the initial table load, that will be discovered when we check if
		// the cell is editable and the full data will be read at that time using
		// this descriptor.
		return false;
	}

	/**
	 * Return a JTextField usable in a CellEditor.
	 */
	public JTextField getJTextField(JTable table) {
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
					CellDataDialogHandler.showDialog(DataTypeClob.this._table,
                                                DataTypeClob.this._colDef, tableEvt, true);
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
		if (value.equals(StringUtilities.NULL_AS_STRING))
			return null;

		// Do the conversion into the object in a safe manner

		// if the original object is not null, then it contains a Clob object
		// that we need to re-use, since that is the DBs reference to the clob data area.
		// Otherwise, we set the original Clob to null, and the write method needs to
		// know to set the field to null.
		ClobDescriptor cdesc;
		if (originalValue == null) {
			// no existing clob to re-use
			cdesc = new ClobDescriptor(null, value, true, true, 0);
		}
		else {
			// for convenience, cast the existing object
			cdesc = (ClobDescriptor)originalValue;

			// create new object to hold the different value, but use the same internal CLOB pointer
			// as the original
			cdesc = new ClobDescriptor(cdesc.getClob(),value,
				true, true, 0);
		}
		return cdesc;

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
	 * user keystrokes related to editing of that data.
	 */
	public boolean useBinaryEditingPanel() {
		return false;
	}


	/*
	 * Now the functions for the Popup-related operations.
	 */

	/**
	 * Returns true if data type may be edited in the popup,
	 * false if not.
	 */
	public boolean isEditableInPopup(Object originalValue)
	{
		return wasWholeClobRead(originalValue);
	}

	public boolean tryReadWholeClob(Object value)
	{
		if (false == value instanceof ClobDescriptor )
		{
			return false;
		}

		return _wholeClobRead((ClobDescriptor) value);
	}


	/*
	 * Return a JTextArea usable in the CellPopupDialog
	 * and fill in the value.
	 */
	public RestorableRSyntaxTextArea getRestorableRSyntaxTextArea(Object value, ColumnDisplayDefinition colDef)
	{
		_textComponent = new RestorableRSyntaxTextArea();

		// value is a simple string representation of the data,
		// but NOT the same one used in the Text and in-cell operations.
		// The in-cell version may replace newline chars with "\n" while this version
		// does not.  In other respects it is the same as the in-cell version because both
		// use the _renderer object to do the rendering.
		String renderedValue = DefaultColumnRenderer.renderObject(value);

		if(renderedValue.length() > BigDataRenderResult.getMaxCharsInValuePopup())
		{
			renderedValue = renderedValue.substring(0, BigDataRenderResult.getMaxCharsInValuePopup());
			BigDataRenderResult.showStringLenReachedMessage(colDef);
		}

		((RestorableRSyntaxTextArea) _textComponent).setText(renderedValue);

		// special handling of operations while editing this data type
		((RestorableRSyntaxTextArea) _textComponent).addKeyListener(new KeyTextHandler());

		return (RestorableRSyntaxTextArea) _textComponent;
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
	 private class KeyTextHandler extends BaseKeyTextHandler {
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
					if (text.equals(StringUtilities.NULL_AS_STRING)) {
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
								DataTypeClob.this._textComponent.updateText(StringUtilities.NULL_AS_STRING);
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
	 * Make sure the entire CLOB data is read in.
	 * Return true if it has been read successfully, and false if not.
	 */
	private boolean _wholeClobRead(ClobDescriptor cdesc)
	{
		if(cdesc == null)
		{
			return true;   // can use an empty clob for editing
		}

		if(cdesc.wasWholeClobRead())
		{
			return true;   // the whole clob has been previously read in
		}

		// data was not fully read in before, so try to do that now
		try
		{
			String data = cdesc.getClob().getSubString(1, (int) cdesc.getClob().length());

			// read succeeded, so reset the ClobDescriptor to match
			cdesc.setClobRead(true);
			cdesc.setData(data);
			cdesc.setWholeClobRead(true);
			cdesc.setUserSetClobLimit(0);

			// we successfully read the whole thing
			return true;
		}
		catch (Exception ex)
		{
			cdesc.setClobRead(false);
			cdesc.setWholeClobRead(false);
			cdesc.setData(null);
			//?? What to do with this error?
			//?? error message = "Could not read the complete data. Error was: "+ex.getMessage());
			return false;
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
		throws java.sql.SQLException
	{
		return staticReadResultSet(rs, index);
	}


	public static Object staticReadResultSet(ResultSet rs, int index)
		throws java.sql.SQLException
	{
		s_properties.loadProperties();


		// We always get the CLOB, even when we are not reading the contents.
		// Since the CLOB is just a pointer to the CLOB data rather than the
		// data itself, this operation should not take much time (as opposed
		// to getting all of the data in the clob).
		Clob clob = rs.getClob(index);

		if (rs.wasNull())
		{
			return null;
		}

		// CLOB exists, so try to read the data from it
		// based on the user's directions
		if (s_properties.isReadClobsOnTableLoading())
		{
			// User said to read at least some of the data from the clob
			String clobData = null;
			if (clob != null)
			{
				int len = (int)clob.length();
				if (len > 0)
				{
					int charsToRead = len;
					if ( !s_properties.isReadCompleteClobs())
					{
						charsToRead = s_properties.getReadClobsSize();
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
			if (s_properties.isReadCompleteClobs() || clobData == null || clobData.length() < s_properties.getReadClobsSize())
			{
				wholeClobRead = true;
			}

			return new ClobDescriptor(clob, clobData, true, wholeClobRead, s_properties.getReadClobsSize());
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
	public IWhereClausePart getWhereClauseValue(Object value, ISQLDatabaseMetaData md)
	{
		if(   value == null
				|| (value instanceof ClobDescriptor && ((ClobDescriptor) value).getData() == null))
		{
			return new IsNullWhereClausePart(_colDef);
		}
		else
		// CLOB cannot be used in WHERE clause
		// TODO Review, if this DataType could not be used in a where clause
		{
			return new EmptyWhereClausePart();
		}
	}

	/**
	 * When updating the database, insert the appropriate datatype into the
	 * prepared statment at the given variable position.
	 */
	public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
			throws java.sql.SQLException
	{
		if(   value == null
				|| (value instanceof ClobDescriptor && ((ClobDescriptor) value).getData() == null))
		{
			pstmt.setNull(position, _colDef.getSqlType());
		}
		else
		{
			// for convenience cast the object to ClobDescriptor
			String data;
			if(value instanceof ClobDescriptor)
			{
				ClobDescriptor cdesc = (ClobDescriptor) value;
				data = cdesc.getData();
			}
			else
			{
				data = new String(Utilities.toPrimitiveByteArray(value));
			}

			// There are a couple of possible ways to update the data in the DB.
			// The first is to use setString like this:
			//		cdesc.getClob().setString(0, cdesc.getData());
			// However, the DB2 driver throws an exception saying that that function
			// is not implemented, so we have to use the other method, which is to use a stream.
			pstmt.setCharacterStream(position, new StringReader(data), data.length());
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
		 s_properties.loadProperties();

		return new DataTypeClobOkJPanel(s_properties);
	 }

	 @Override
	protected String quoteTextConditionValue(String value) {
		 return StringUtilities.singleQuote(value);
	}

}
