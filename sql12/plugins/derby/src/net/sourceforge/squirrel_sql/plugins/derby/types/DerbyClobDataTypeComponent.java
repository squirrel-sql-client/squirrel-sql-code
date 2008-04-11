package net.sourceforge.squirrel_sql.plugins.derby.types;

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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseKeyTextHandler;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DefaultColumnRenderer;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IRestorableTextComponent;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

/**
 * @author manningr
 * 
 * This class provides the display components for handling Derby Clob data
 * types, specifically SQL type CLOB. This is a necessary override of the
 * default implementation provided in SQuirreL's fw module since newer versions
 * of Derby disallow access to a CLOB Locator once the transaction has been
 * committed. This implementation therefore doesn't support partial reading the
 * clob contents or placeholders to allow the user to later fetch the remaining
 * data from the saved CLOB. There is no point to saving the CLOB so all of the
 * data is fetched, always. This should probably be re-implemented somehow
 * (perhaps using the primary key to fetch the data when placeholders are
 * clicked).
 * 
 * 
 * The display components are for:
 * <UL>
 * <LI> read-only display within a table cell
 * <LI> editing within a table cell
 * <LI> read-only or editing display within a separate window
 * </UL>
 * The class also contains
 * <UL>
 * <LI> a function to compare two display values to see if they are equal. This
 * is needed because the display format may not be the same as the internal
 * format, and all internal object types may not provide an appropriate equals()
 * function.
 * <LI> a function to return a printable text form of the cell contents, which
 * is used in the text version of the table.
 * </UL>
 * <P>
 * The components returned from this class extend RestorableJTextField and
 * RestorableJTextArea for use in editing table cells that contain values of
 * this data type. It provides the special behavior for null handling and
 * resetting the cell to the original value.
 */
public class DerbyClobDataTypeComponent extends BaseDataTypeComponent implements
      IDataTypeComponent {

   /* whether nulls are allowed or not */
   private boolean _isNullable;

   /* The JTextComponent that is being used for editing */
   private IRestorableTextComponent _textComponent;

   /* The CellRenderer used for this data type */
   private DefaultColumnRenderer _renderer = DefaultColumnRenderer.getInstance();

   /**
    * Default Constructor
    */
   public DerbyClobDataTypeComponent() {
   }

   /**
    * Return the name of the java class used to hold this data type.
    */
   @Override
   public String getClassName() {
      return "net.sourceforge.squirrel_sql.plugins.derby.types.ClobDescriptor";
   }

   /**
    * Used to provide manual override in cases where we are exporting data.
    * 
    * @return the current value of _readCompleteClob
    */
   public static boolean getReadCompleteClob() {
      return true;
   }

   /**
    * Used to provide manual override in cases where we are exporting data.
    * 
    * @param val
    *           the new value of _readCompleteClob
    */
   public static void setReadCompleteClob(boolean val) {
      // Do nothing
   }

   /**
    * Determine if two objects of this data type contain the same value. Neither
    * of the objects is null
    */
   public boolean areEqual(Object obj1, Object obj2) {
      if (obj1 == obj2) {
         return true;
      }

      // if both objs are null, then they matched in the previous test,
      // so at this point we know that at least one of them (or both) is not
      // null.
      // However, one of them may still be null, and we cannot call equals() on
      // the null object, so make sure that the one we call it on is not null.
      // The equals() method handles the other one being null, if it is.
      if (obj1 != null)
         return ((DerbyClobDescriptor) obj1).equals((DerbyClobDescriptor) obj2);
      else
         return ((DerbyClobDescriptor) obj2).equals((DerbyClobDescriptor) obj1);
   }

   /*
    * First we have the methods for in-cell and Text-table operations
    */

   /**
    * Render a value into text for this DataType.
    */
   @Override
   public String renderObject(Object value) {
      return (String) _renderer.renderObject(value);
   }

   /**
    * This Data Type can be edited in a table cell. This function is not called
    * during the initial table load, or during normal table operations. It is
    * called only when the user enters the cell, either to examine or to edit
    * the data. The user may have set the DataType properties to minimize the
    * data read during the initial table load (to speed it up), but when they
    * enter this cell we would like to show them the entire contents of the
    * CLOB. Therefore we use a call to this function as a trigger to make sure
    * that we have all of the CLOB data, if that is possible.
    * <P>
    * If the data includes newlines, the user must not be allowed to edit it in
    * the cell because the CellEditor uses a JTextField which filters out
    * newlines. If we try to use anything other than a JTextField, or use a
    * JTextField with no newline filtering, the text is not visible in the cell,
    * so the user cannot even read the text, much less edit it. The simplest
    * solution is to allow editing of multi-line text only in the Popup window.
    */
   public boolean isEditableInCell(Object originalValue) {
      // for convenience, cast the value object to its type
      DerbyClobDescriptor cdesc = (DerbyClobDescriptor) originalValue;

      // all the data from the clob has been read.
      // make sure there are no newlines in it
      if (cdesc != null && cdesc.getData() != null
            && cdesc.getData().indexOf('\n') > -1) {
         return false;
      } else {
         return true;
      }
   }

   /**
    * Derby doesn't support using a Clob descriptor after the transaction that
    * read it is closed. So we never have to re-read.
    */
   public boolean needToReRead(Object originalValue) {
      return false;
   }

   /**
    * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.BaseDataTypeComponent#setColumnDisplayDefinition(net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition)
    */
   @Override
   public void setColumnDisplayDefinition(ColumnDisplayDefinition def) {
      super.setColumnDisplayDefinition(def);

      this._isNullable = def.isNullable();
   }

   /**
    * Implement the interface for validating and converting to internal object.
    * Null is a valid successful return, so errors are indicated only by
    * existance or not of a message in the messageBuffer. If originalValue is
    * null, then we are just checking that the data is in a valid format (for
    * file import/export) and not actually converting the data.
    */
   @Override
   public Object validateAndConvert(String value, Object originalValue,
         StringBuffer messageBuffer) {
      // handle null, which is shown as the special string "<null>"
      if (value.equals("<null>")) {
         return null;
      }

      // Do the conversion into the object in a safe manner

      // if the original object is not null, then it contains a Clob object
      // that we need to re-use, since that is the DBs reference to the clob
      // data area.
      // Otherwise, we set the original Clob to null, and the write method needs
      // to
      // know to set the field to null.
      DerbyClobDescriptor cdesc;
      if (originalValue == null) {
         // no existing clob to re-use
         cdesc = new DerbyClobDescriptor(value);
      } else {
         // for convenience, cast the existing object
         cdesc = (DerbyClobDescriptor) originalValue;

         // create new object to hold the different value, but use the same
         // internal CLOB pointer
         // as the original
         cdesc = new DerbyClobDescriptor(value);
      }
      return cdesc;

   }

   /**
    * If true, this tells the PopupEditableIOPanel to use the binary editing
    * panel rather than a pure text panel. The binary editing panel assumes the
    * data is an array of bytes, converts it into text form, allows the user to
    * change how that data is displayed (e.g. Hex, Decimal, etc.), and converts
    * the data back from text to bytes when the user editing is completed. If
    * this returns false, this DataType class must convert the internal data
    * into a text string that can be displayed (and edited, if allowed) in a
    * TextField or TextArea, and must handle all user key strokes related to
    * editing of that data.
    */
   public boolean useBinaryEditingPanel() {
      return false;
   }

   /*
    * Now the functions for the Popup-related operations.
    */

   /**
    * Returns true if data type may be edited in the popup, false if not.
    */
   public boolean isEditableInPopup(Object originalValue) {
      return true;
   }

   /**
    * Validating and converting in Popup is identical to cell-related operation.
    */
   @Override
   public Object validateAndConvertInPopup(String value, Object originalValue,
         StringBuffer messageBuffer) {
      return validateAndConvert(value, originalValue, messageBuffer);
   }

   /** 
    * If any custom key handling behavior is required, this can be set by 
    * sub-class implementations
    */
   @Override
   protected KeyListener getKeyListener(IRestorableTextComponent component) {
       return new KeyTextHandler();
   }

   
   /*
    * The following is used in both cell and popup operations.
    */

   /*
    * Internal class for handling key events during editing of both JTextField
    * and JTextArea.
    */
   private class KeyTextHandler extends BaseKeyTextHandler {
      public void keyTyped(KeyEvent e) {
         char c = e.getKeyChar();

         // as a coding convenience, create a reference to the text component
         // that is typecast to JTextComponent. this is not essential, as we
         // could typecast every reference, but this makes the code cleaner
         JTextComponent _theComponent = (JTextComponent) DerbyClobDataTypeComponent.this._textComponent;
         String text = _theComponent.getText();

         // handle cases of null
         // The processing is different when nulls are allowed and when they are
         // not.
         //

         if (DerbyClobDataTypeComponent.this._isNullable) {

            // user enters something when field is null
            if (text.equals("<null>")) {
               if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
                  // delete when null => original value
                  DerbyClobDataTypeComponent.this._textComponent.restoreText();
                  e.consume();
               } else {
                  // non-delete when null => clear field and add text
                  DerbyClobDataTypeComponent.this._textComponent.updateText("");
                  // fall through to normal processing of this key stroke
               }
            } else {
               // check for user deletes last thing in field
               if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)) {
                  if (text.length() <= 1) {
                     // about to delete last thing in field, so replace with
                     // null
                     DerbyClobDataTypeComponent.this._textComponent.updateText("<null>");
                     e.consume();
                  }
               }
            }
         } else {
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
    * On input from the DB, read the data from the ResultSet into the
    * appropriate type of object to be stored in the table cell.
    */
   public Object readResultSet(ResultSet rs, int index, boolean limitDataRead)
         throws java.sql.SQLException {
      return staticReadResultSet(rs, index);
   }

   /**
    * Derby does not allow to read a Clob, end the transaction, then attempt to
    * get it's contents later. So we simply read the entire thing. 
    * 
    * @param rs
    * @param index
    * @return
    * @throws java.sql.SQLException
    */
   public static Object staticReadResultSet(ResultSet rs, int index)
         throws java.sql.SQLException {

      Clob clob = rs.getClob(index);

      if (rs.wasNull()) {
         return null;
      }
      
      String clobData = clob.getSubString(1, (int)clob.length());
      return new DerbyClobDescriptor(clobData);
   }

   /**
    * When updating the database, generate a string form of this object value
    * that can be used in the WHERE clause to match the value in the database. A
    * return value of null means that this column cannot be used in the WHERE
    * clause, while a return of "null" (or "is null", etc) means that the column
    * can be used in the WHERE clause and the value is actually a null value.
    * This function must also include the column label so that its output is of
    * the form: "columnName = value" or "columnName is null" or whatever is
    * appropriate for this column in the database.
    */
   public String getWhereClauseValue(Object value, ISQLDatabaseMetaData md) {
      if (value == null || ((DerbyClobDescriptor) value).getData() == null)
         return _colDef.getLabel() + " IS NULL";
      else
         return ""; // CLOB cannot be used in WHERE clause
   }

   /**
    * When updating the database, insert the appropriate datatype into the
    * prepared statment at the given variable position.
    */
   public void setPreparedStatementValue(PreparedStatement pstmt, Object value,
         int position) throws java.sql.SQLException {
      if (value == null || ((DerbyClobDescriptor) value).getData() == null) {
         pstmt.setNull(position, _colDef.getSqlType());
      } else {
         // for convenience cast the object to ClobDescriptor
         DerbyClobDescriptor cdesc = (DerbyClobDescriptor) value;

         pstmt.setCharacterStream(position,
                                  new StringReader(cdesc.getData()),
                                  cdesc.getData().length());
      }
   }

   /**
    * Get a default value for the table used to input data for a new row to be
    * inserted into the DB.
    */
   public Object getDefaultValue(String dbDefaultValue) {
      if (dbDefaultValue != null) {
         // try to use the DB default value
         StringBuffer mbuf = new StringBuffer();
         Object newObject = validateAndConvert(dbDefaultValue, null, mbuf);

         // if there was a problem with converting, then just fall through
         // and continue as if there was no default given in the DB.
         // Otherwise, use the converted object
         if (mbuf.length() == 0) {
            return newObject;
         }
      }

      // no default in DB. If nullable, use null.
      if (_isNullable) {
         return null;
      }

      // field is not nullable, so create a reasonable default value
      return null;
   }

   /*
    * File IO related functions
    */

   /**
    * Can always do File I/O with Derby clobs.
    *  
    * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent#canDoFileIO()
    */
   public boolean canDoFileIO() {
      return true;
   }

   /**
    * Read a file and construct a valid object from its contents. Errors are
    * returned by throwing an IOException containing the cause of the problem as
    * its message.
    * <P>
    * DataType is responsible for validating that the imported data can be
    * converted to an object, and then must return a text string that can be
    * used in the Popup window text area. This object-to-text conversion is the
    * same as is done by the DataType object internally in the getJTextArea()
    * method.
    * 
    * <P>
    * File is assumed to be and ASCII string of digits representing a value of
    * this data type.
    */
   @Override
   public String importObject(FileInputStream inStream) throws IOException {

      InputStreamReader inReader = new InputStreamReader(inStream);

      int fileSize = inStream.available();

      char charBuf[] = new char[fileSize];

      int count = inReader.read(charBuf, 0, fileSize);

      if (count != fileSize)
         throw new IOException("Could read only " + count
               + " chars from a total file size of " + fileSize
               + ". Import failed.");

      // convert file text into a string
      // Special case: some systems tack a newline at the end of
      // the text read. Assume that if last char is a newline that
      // we want everything else in the line.
      String fileText;
      if (charBuf[count - 1] == KeyEvent.VK_ENTER)
         fileText = new String(charBuf, 0, count - 1);
      else
         fileText = new String(charBuf);

      // test that the string is valid by converting it into an
      // object of this data type
      StringBuffer messageBuffer = new StringBuffer();
      validateAndConvertInPopup(fileText, null, messageBuffer);
      if (messageBuffer.length() > 0) {
         // convert number conversion issue into IO issue for consistancy
         throw new IOException("Text does not represent data of type "
               + getClassName() + ".  Text was:\n" + fileText);
      }

      // return the text from the file since it does
      // represent a valid data value
      return fileText;
   }

   /**
    * Construct an appropriate external representation of the object and write
    * it to a file. Errors are returned by throwing an IOException containing
    * the cause of the problem as its message.
    * <P>
    * DataType is responsible for validating that the given text text from a
    * Popup JTextArea can be converted to an object. This text-to-object
    * conversion is the same as validateAndConvertInPopup, which may be used
    * internally by the object to do the validation.
    * <P>
    * The DataType object must flush and close the output stream before
    * returning. Typically it will create another object (e.g. an OutputWriter),
    * and that is the object that must be flushed and closed.
    * 
    * <P>
    * File is assumed to be and ASCII string of digits representing a value of
    * this data type.
    */
   @Override
   public void exportObject(FileOutputStream outStream, String text)
         throws IOException {

      OutputStreamWriter outWriter = null;
      try {
         outWriter = new OutputStreamWriter(outStream);

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

      } finally {
         if (outWriter != null) {
            try {
               outWriter.close();
            } catch (Exception e) {/* Do nothing */
            }
         }
      }
   }

}
