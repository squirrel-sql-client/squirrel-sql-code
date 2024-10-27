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

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.EmptyWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IsNullWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup.CellDataPopup;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author gwg
 *
 * This class provides the display components for handling Blob data types,
 * specifically SQL type BLOB.
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

public class DataTypeBlob extends BaseDataTypeComponent implements IDataTypeComponent
{
	private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataTypeBlob.class);

	private static ILogger s_log = LoggerController.createLogger(DataTypeBlob.class);


	/* whether nulls are allowed or not */
	private boolean _isNullable;

	/* table of which we are part (needed for creating popup dialog) */
	private JTable _table;

	/* The JTextComponent that is being used for editing */
	private IRestorableTextComponent _textComponent;


	private static DataTypeBlobProperties s_properties = new DataTypeBlobProperties();

	/**
	 * Constructor - save the data needed by this data type.
	 */
	public DataTypeBlob(JTable table, ColumnDisplayDefinition colDef)
	{
		_table = table;
		_colDef = colDef;
		_isNullable = colDef.isNullable();

		s_properties.loadProperties();
	}

	public DataTypeBlobProperties getProperties()
	{
		return s_properties;
	}


	/**
	 * Return the name of the java class used to hold this data type.
	 */
	public String getClassName() {
		return "net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BlobDescriptor";
	}

	/*
	 * First we have the methods for in-cell and Text-table operations
	 */

	/**
	 * Render a value into text for this DataType.
	 */
	public String renderObject(Object value)
	{
		return DefaultColumnRenderer.renderObject(value);
	}

	private static String renderForTextArea(Object value, ColumnDisplayDefinition colDef)
	{
		// The SQL Results page puts text into the table cells
		// rather than objects of the appropriate type, so we
		// need to convert befor proceeding
		Byte[] useValue;
		if(value instanceof BlobDescriptor)
		{
			useValue = Utilities.toBoxedByteArray(((BlobDescriptor)value).getData(), BigDataRenderResult.getMaxCharsInValuePopup());
		}
		else
		{
			useValue = Utilities.toBoxedByteArray(value, BigDataRenderResult.getMaxCharsInValuePopup());
		}

		if(null != useValue && useValue.length == BigDataRenderResult.getMaxCharsInValuePopup())
		{
			BigDataRenderResult.showMaxBytesReachedMessage(colDef);
		}

		// use the default settings for the conversion
		String renderResult =
				DefaultColumnRenderer.renderObject(BinaryDisplayConverter.convertToString(useValue, BinaryDisplayConverter.HEX, false));

		return renderResult;
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
	 * contents of the BLOB.
	 * Therefore we use a call to this function as a trigger to make sure
	 * that we have all of the BLOB data, if that is possible.
	 */
	public boolean isEditableInCell(Object originalValue)
	{
		return wasWholeBlobRead(originalValue);
	}

	public boolean wasWholeBlobRead(Object originalValue)
	{
		if (originalValue instanceof BlobDescriptor)
		{
			return ((BlobDescriptor) originalValue).wasWholeBlobRead();
		}
		else
		{
			return false;
		}
	}

	/**
	 * See if a value in a column has been limited in some way and
	 * needs to be re-read before being used for editing.
	 * For read-only tables this may actually return true since we want
	 * to be able to view the entire contents of the cell even if it was not
	 * completely loaded during the initial table setup.
	 */
	public boolean needToReRead(Object originalValue) {
		// BLOBs are different from normal data types in that what is actually
		// read from the DB is a descriptor pointing to the data rather than the
		// data itself.  During the initial load of the table, the values read from the
		// descriptor may have been limited, but the descriptor itself has been
		// completely read,  Therefore we do not need to re-read the datum
		// from the Database because we know that we have the entire
		// descriptor.  If the contents of the BLOB have been limited during
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
						(RestorableJTextField)DataTypeBlob.this._textComponent,
						evt, DataTypeBlob.this._table);
					CellDataPopup.showDialog(DataTypeBlob.this._table,
						DataTypeBlob.this._colDef, tableEvt, true);
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

		//First convert the string representation into the binary bytes it is describing
		Byte[] byteClassData;
		try {
					byteClassData = BinaryDisplayConverter.convertToBytes(value,
						BinaryDisplayConverter.HEX, false);
		}
		catch (Exception e) {
			messageBuffer.append(e.toString()+"\n");
			//?? do we need the message also, or is it automatically part of the toString()?
			//messageBuffer.append(e.getMessage());
			return null;
		}

		byte[] byteData = new byte[byteClassData.length];
		for (int i=0; i<byteClassData.length; i++)
			byteData[i] = byteClassData[i].byteValue();

		// if the original object is not null, then it contains a Blob object
		// that we need to re-use, since that is the DBs reference to the blob data area.
		// Otherwise, we set the original Blob to null, and the write method needs to
		// know to set the field to null.
		BlobDescriptor bdesc;
		if (originalValue == null) {
			// no existing blob to re-use
			bdesc = new BlobDescriptor(null, byteData, true, true, 0);
		}
		else {
			// for convenience, cast the existing object
			bdesc = (BlobDescriptor)originalValue;

			// create new object to hold the different value, but use the same internal BLOB pointer
			// as the original
			bdesc = new BlobDescriptor(bdesc.getBlob(), byteData, true, true, 0);
		}
		return bdesc;

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
		return true;
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
		return wasWholeBlobRead(originalValue);
	}

	/*
	 * Return a JTextArea usable in the CellPopupDialog
	 * and fill in the value.
	 */
	public JTextArea getJTextArea(Object value, ColumnDisplayDefinition colDef)
	{
		_textComponent = new RestorableJTextArea();

		String renderRes = renderForTextArea(value, colDef);

		((RestorableJTextArea)_textComponent).setText(renderRes);

		// special handling of operations while editing this data type
		((RestorableJTextArea) _textComponent).addKeyListener(new KeyTextHandler());

		return (RestorableJTextArea) _textComponent;
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
				JTextComponent _theComponent = (JTextComponent)DataTypeBlob.this._textComponent;
				String text = _theComponent.getText();


				// handle cases of null
				// The processing is different when nulls are allowed and when they are not.
				//

				if ( DataTypeBlob.this._isNullable) {

					// user enters something when field is null
					if (text.equals(StringUtilities.NULL_AS_STRING)) {
						if ((c==KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							// delete when null => original value
							DataTypeBlob.this._textComponent.restoreText();
							e.consume();
						}
						else {
							// non-delete when null => clear field and add text
							DataTypeBlob.this._textComponent.updateText("");
							// fall through to normal processing of this key stroke
						}
					}
					else {
						// check for user deletes last thing in field
						if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
							if (text.length() <= 1 ) {
								// about to delete last thing in field, so replace with null
								DataTypeBlob.this._textComponent.updateText(StringUtilities.NULL_AS_STRING);
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


	public boolean tryReadWholeBlob(Object value)
	{
		if (value instanceof BlobDescriptor)
		{
			return _wholeBlobRead((BlobDescriptor) value);
		}
		else
		{
			return false;
		}
	}


	/*
	 * Make sure the entire BLOB data is read in.
	 * Return true if it has been read successfully, and false if not.
	 */
	private boolean _wholeBlobRead(BlobDescriptor bdesc)
	{
		if (bdesc == null)
		{
			return true;   // can use an empty blob for editing
		}

		if (bdesc.wasWholeBlobRead())
		{
			return true;   // the whole blob has been previously read in
		}

		// data was not fully read in before, so try to do that now
		try
		{
			byte[] data = bdesc.getBlob().getBytes(1, (int) bdesc.getBlob().length());

			// read succeeded, so reset the BlobDescriptor to match
			bdesc.setBlobRead(true);
			bdesc.setData(data);
			bdesc.setWholeBlobRead(true);
			bdesc.setUserSetBlobLimit(0);

			// we successfully read the whole thing
			return true;
		}
		catch (Exception ex)
		{
			bdesc.setBlobRead(false);
			bdesc.setWholeBlobRead(false);
			bdesc.setData(null);
			s_log.warn("Failed to read Blob in DataTypeBlob.wholeBlobRead(...): " + ex);
			return false;
		}
	}

	/*
	 * DataBase-related functions
	 */

	public Object readResultSet(ResultSet rs, int index, boolean limitDataRead)
		throws java.sql.SQLException {

		return staticReadResultSet(rs, index);
	}

	 /**
	  * On input from the DB, read the data from the ResultSet into the appropriate
	  * type of object to be stored in the table cell.
	  */
	public static Object staticReadResultSet(ResultSet rs, int index) throws java.sql.SQLException
	{
		s_properties.loadProperties();

		 // We always get the BLOB, even when we are not reading the contents.
		 // Since the BLOB is just a pointer to the BLOB data rather than the
		 // data itself, this operation should not take much time (as opposed
		 // to getting all of the data in the blob).
		 BlobResult blobResult;
		 try
		 {
			 blobResult = getBlobRespectingUnsupportedFeatureException(rs, index);

			 if (rs.wasNull())
			 {
				 return null;
			 }

			 if(false == blobResult.isBlob())
			 {
				 return blobResult.getAsString();
			 }

		 }
		 catch (Exception e)
		 {
			 return handleBlobReadException(rs, index, e);
		 }

		 // BLOB exists, so try to read the data from it
		 // based on the user's directions
		 if (false == s_properties.isReadBlobsOnTableLoading())
		 {
			 // user said not to read any of the data from the blob
			 return new BlobDescriptor(blobResult.getBlob(), null, false, false, 0);
		 }

		 // User said to read at least some of the data from the blob
		 byte[] blobData = null;
		 if (blobResult != null)
		 {
			 int len = (int) blobResult.getBlob().length();
			 if (len > 0)
			 {
				 int charsToRead = len;
				 if (!s_properties.isReadCompleteBlobs())
				 {
					 charsToRead = s_properties.getReadBlobsSize();;
				 }
				 if (charsToRead > len)
				 {
					 charsToRead = len;
				 }
				 blobData = blobResult.getBlob().getBytes(1, charsToRead);
			 }
		 }

		 // determine whether we read all there was in the blob or not
		 boolean wholeBlobRead = false;
		 if (s_properties.isReadCompleteBlobs() || blobData.length < s_properties.getReadBlobsSize())
		 {
			 wholeBlobRead = true;
		 }

		 return new BlobDescriptor(blobResult.getBlob(), blobData, true, wholeBlobRead, s_properties.getReadBlobsSize());
	}

	/**
	 * Was introduced for SourceForge bug #1464
	 * and
	 * https://github.com/xerial/sqlite-jdbc/issues/589
	 */
	private static BlobResult getBlobRespectingUnsupportedFeatureException(ResultSet rs, int index) throws SQLException
	{
		try
		{
			return new BlobResult(rs.getBlob(index));
		}
		catch (Throwable eBlob)
		{
			try
			{
				return new BlobResult(rs.getBytes(index), eBlob);
			}
			catch (Throwable eBytes)
			{
				return new BlobResult(rs.getString(index), eBytes);
			}
		}
	}

	/**
	 * See for example bug #1464: In SQL results SQLLite may give type Blob in ResultSetMetaData but it is a String.
	 * rs.getBlob(index) results in
	 * java.sql.SQLFeatureNotSupportedException
	 *   at org.sqlite.jdbc4.JDBC4ResultSet.unused(JDBC4ResultSet.java:347)
	 */
	private static String handleBlobReadException(ResultSet rs, int index, Exception exceptionToHandle) throws SQLException
	{
		final String msg =
				"Failed to call ResultSet.getBlob() in DataTypeBlob.staticReadResultSet(...).\n"
						+ "If in case of Blob read errors you want SQuirreL to try to treat Blobs as type \"Unknown\" switch the Blob data type preferences to read all.\n"
						+ "See menu File --> \"Global Preferences\" --> tab \"Data type controls\" --> section Blob.\n"
						+ "Hint: Check out the settings of section Unknown as well.";

		if (false == s_properties.isReadBlobsOnTableLoading() || false == s_properties.isReadCompleteBlobs())
		{
			throw new RuntimeException(msg, exceptionToHandle);
		}

		// It can be very memory and performance challenging to read Blobs.
		// That's why this workaround is done when the user chose to read complete Blobs only.
		s_log.error(msg, exceptionToHandle);
		return DataTypeUnknown.staticReadResultSet(rs, index, s_stringMgr.getString("DataTypeBlob.readAsStringError"));
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
			|| (value instanceof BlobDescriptor && ((BlobDescriptor) value).getData() == null))
		{
			return new IsNullWhereClausePart(_colDef);
		}
		else
		{
			// BLOB cannot be used in WHERE clause
			// TODO Review, if this DataType could not be used in a where clause
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
			|| (value instanceof BlobDescriptor && ((BlobDescriptor) value).getData() == null))
		{
			pstmt.setNull(position, _colDef.getSqlType());
		}
		else
		{
			// for convenience cast the object to BlobDescriptor
			byte[] data;
			if(value instanceof BlobDescriptor)
			{
				BlobDescriptor bdesc = (BlobDescriptor) value;
				data = bdesc.getData();
			}
			else
			{
				data = Utilities.toPrimitiveByteArray(value);
			}

			// There are a couple of possible ways to update the data in the DB.
			// The first is to use setString like this:
			//		bdesc.getBlob().setString(0, bdesc.getData());
			// However, the DB2 driver throws an exception saying that that function
			// is not implemented, so we have to use the other method, which is to use a stream.		
			pstmt.setBinaryStream(position, new ByteArrayInputStream(data), data.length);
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


		int fileSize = inStream.available();

		byte[] buf = new byte[fileSize];

		int count = inStream.read(buf);

		if (count != fileSize) {
			throw new IOException(
				"Could read only "+ count +
				" bytes from a total file size of " + fileSize +
				". Import failed.");
		}
		// Convert bytes to Bytes
		Byte[] bBytes = new Byte[count];
		for (int i=0; i<count; i++) {
			bBytes[i] = Byte.valueOf(buf[i]);
		}

		// return the text converted from the file 
		return BinaryDisplayConverter.convertToString(bBytes,
			BinaryDisplayConverter.HEX, false);
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

		Byte[] bBytes = BinaryDisplayConverter.convertToBytes(text,
			BinaryDisplayConverter.HEX, false);

		// check that the text is a valid representation
		StringBuffer messageBuffer = new StringBuffer();
		validateAndConvertInPopup(text, null, messageBuffer);
		if (messageBuffer.length() > 0) {
			// there was an error in the conversion
			throw new IOException(new String(messageBuffer));
		}

		// Convert Bytes to bytes
		byte[] bytes = new byte[bBytes.length];
		for (int i=0; i<bytes.length; i++)
			bytes[i] = bBytes[i].byteValue();

		// just send the text to the output file
		outStream.write(bytes);
		outStream.flush();
		outStream.close();
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

		return new DataTypeBlobOkPanel(s_properties);
	 }


}
