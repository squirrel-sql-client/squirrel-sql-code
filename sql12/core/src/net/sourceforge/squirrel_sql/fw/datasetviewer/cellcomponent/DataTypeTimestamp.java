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
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.NoParameterWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.ParameterWhereClausePart;
import net.sourceforge.squirrel_sql.fw.datasetviewer.celldatapopup.CellDataDialogHandler;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.TemporalUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;

/**
 * @author gwg
 * <p>
 * This class provides the display components for handling Timestamp data types,
 * specifically SQL type TIMESTAMP.
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
 * <p>
 * The components returned from this class extend RestorableJTextField
 * and RestorableJTextArea for use in editing table cells that
 * contain values of this data type. It provides the special behavior for null
 * handling and resetting the cell to the original value.
 */

public class DataTypeTimestamp extends BaseDataTypeComponent implements IDataTypeComponent
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataTypeTimestamp.class);

   private static ILogger s_log = LoggerController.createLogger(DataTypeTimestamp.class);

   /* whether nulls are allowed or not */
   private boolean _isNullable;

   /* table of which we are part (needed for creating popup dialog) */
   private JTable _table;

   /* The JTextComponent that is being used for editing */
   private IRestorableTextComponent _textComponent;

   /**
    * The DTProperties key that is used to store and get the mode that
    * determines how timestamps are used in where clauses
    */
   public static final String WHERE_CLAUSE_USAGE_KEY = "whereClauseUsage";

   private static DataTypeTimestampStatics _dataTypeTimestampStatics = new DataTypeTimestampStatics();

   private boolean _renderExceptionHasBeenLogged;


   /**
    * Constructor - save the data needed by this data type.
    */
   public DataTypeTimestamp(JTable table, ColumnDisplayDefinition colDef)
   {
      _table = table;
      _colDef = colDef;
      _isNullable = colDef.isNullable();

      loadProperties();
   }

   /**
    * Internal function to get the user-settable properties from the DTProperties,
    * if they exist, and to ensure that defaults are set if the properties have
    * not yet been created.
    * <p>
    * This method may be called from different places depending on whether
    * an instance of this class is created before the user brings up the Session
    * Properties window. In either case, the data is static and is set only
    * the first time we are called.
    */
   private static void loadProperties()
   {

      //set the property values
      // Note: this may have already been done by another instance of
      // this DataType created to handle a different column.
      // get parameters previously set by user, or set default values
      _dataTypeTimestampStatics.setUseJavaDefaultFormat(true);   // set to use the Java default
      String useJavaDefaultFormatString = DataTypeProps.getProperty(DataTypeTimestamp.class.getName(), "useJavaDefaultFormat");
      if (useJavaDefaultFormatString != null && useJavaDefaultFormatString.equals("false"))
      {
         _dataTypeTimestampStatics.setUseJavaDefaultFormat(false);
      }

      _dataTypeTimestampStatics.setUseThreeDigitMillis(false);   // set to use the Java default
      String useThreeDigitMillisString = DataTypeProps.getProperty(DataTypeTimestamp.class.getName(), "useThreeDigitMillis");
      if (useThreeDigitMillisString != null && useThreeDigitMillisString.equals("true"))
      {
         _dataTypeTimestampStatics.setUseThreeDigitMillis(true);
      }

      // get which locale-dependent format to use
      _dataTypeTimestampStatics.setLocaleFormat(DateFormat.SHORT);   // set to use the Java default
      String localeFormatString = DataTypeProps.getProperty(DataTypeTimestamp.class.getName(), "localeFormat");
      if (localeFormatString != null)
      {
         _dataTypeTimestampStatics.setLocaleFormat(Integer.parseInt(localeFormatString));
      }

      // use lenient input or force user to enter exact format
      _dataTypeTimestampStatics.setLenient(true);   // set to allow less stringent input
      String lenientString = DataTypeProps.getProperty(DataTypeTimestamp.class.getName(), "lenient");
      if (lenientString != null && lenientString.equals("false"))
      {
         _dataTypeTimestampStatics.setLenient(false);
      }

      // how to use Timestamp in WHERE clauses
      _dataTypeTimestampStatics.setInternalWhereClauseUsage(DataTypeTimestampStatics.USE_JDBC_ESCAPE_FORMAT);   // default to SQL standard
      String whereClauseUsageString = DataTypeProps.getProperty(DataTypeTimestamp.class.getName(), "whereClauseUsage");
      if (whereClauseUsageString != null)
      {
         _dataTypeTimestampStatics.setInternalWhereClauseUsage(Integer.parseInt(whereClauseUsageString));
      }

      _dataTypeTimestampStatics.setTimestampScriptFormat(getTimeStampScriptFormat());

      _dataTypeTimestampStatics.initDateFormat();

   }

   public static TemporalScriptGenerationFormat getTimeStampScriptFormat()
   {
      TemporalScriptGenerationFormat ret = TemporalScriptGenerationFormat.STD_JDBC_FORMAT;
      final String formatName = DataTypeProps.getProperty(DataTypeTimestamp.class.getName(), "timestampScriptFormat");
      if(false == StringUtilities.isEmpty(formatName, true))
      {
         final TemporalScriptGenerationFormat timestampScriptFormat = TemporalScriptGenerationFormat.valueOf(formatName);
         ret = timestampScriptFormat;
      }

      return ret;
   }

   /**
    * Return the name of the java class used to hold this data type.
    */
   public String getClassName()
   {
      return "java.sql.Timestamp";
   }

   /*
    * First we have the methods for in-cell and Text-table operations
    */

   /**
    * Render a value into text for this DataType.
    */
   public String renderObject(Object value, DataTypeRenderingHint renderingHint)
   {
      if (value == null || null == _dataTypeTimestampStatics.getDateFormat() )
      {
         // use the Java default date-to-string
         return (String) DefaultColumnRenderer.renderObject(value);
      }

      // use a date formatter
      try
      {
         return (String) DefaultColumnRenderer.renderObject(_dataTypeTimestampStatics.getDateFormat().format(value));
      }
      catch (Exception e)
      {
         if (false == _renderExceptionHasBeenLogged)
         {
            _renderExceptionHasBeenLogged = true;
            s_log.error("Could not format \"" + value + "\" as date type", e);
         }
         return (String) DefaultColumnRenderer.renderObject(value);
      }
   }

   /**
    * This Data Type can be edited in a table cell.
    */
   public boolean isEditableInCell(Object originalValue)
   {
      return true;
   }

   /**
    * See if a value in a column has been limited in some way and
    * needs to be re-read before being used for editing.
    * For read-only tables this may actually return true since we want
    * to be able to view the entire contents of the cell even if it was not
    * completely loaded during the initial table setup.
    */
   public boolean needToReRead(Object originalValue)
   {
      // this DataType does not limit the data read during the initial load of the table,
      // so there is no need to re-read the complete data later
      return false;
   }

   /**
    * Return a JTextField usable in a CellEditor.
    */
   public JTextField getJTextField(JTable table)
   {
      _textComponent = new RestorableJTextField();

      // special handling of operations while editing this data type
      ((RestorableJTextField) _textComponent).addKeyListener(new KeyTextHandler());

      //
      // handle mouse events for double-click creation of popup dialog.
      // This happens only in the JTextField, not the JTextArea, so we can
      // make this an inner class within this method rather than a separate
      // inner class as is done with the KeyTextHandler class.
      //
      ((RestorableJTextField) _textComponent).addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent evt)
         {
            if (evt.getClickCount() == 2)
            {
               MouseEvent tableEvt = SwingUtilities.convertMouseEvent(
                     (RestorableJTextField) DataTypeTimestamp.this._textComponent,
                     evt, DataTypeTimestamp.this._table);
               CellDataDialogHandler.showDialog(DataTypeTimestamp.this._table,
                                                DataTypeTimestamp.this._colDef, tableEvt, true);
            }
         }
      });   // end of mouse listener

      return (JTextField) _textComponent;
   }

   /**
    * Implement the interface for validating and converting to internal object.
    * Null is a valid successful return, so errors are indicated only by
    * existance or not of a message in the messageBuffer.
    */
   public Object validateAndConvert(String value, Object originalValue, StringBuffer messageBuffer)
   {
      // handle null, which is shown as the special string StringUtilities.NULL_AS_STRING
      if (value.equals(StringUtilities.NULL_AS_STRING) || value.equals(""))
      {
         return null;
      }

      // Do the conversion into the object in a safe manner
      try
      {
         if (_dataTypeTimestampStatics.isUseJavaDefaultFormat())
         {
            Object obj = Timestamp.valueOf(value);
            return obj;
         }
         else
         {
            // use the DateFormat to parse
            java.util.Date javaDate = _dataTypeTimestampStatics.getDateFormat().parse(value);
            return new Timestamp(javaDate.getTime());
         }
      }
      catch (Exception e)
      {
         messageBuffer.append(e.toString() + "\n");
         //?? do we need the message also, or is it automatically part of the toString()?
         //messageBuffer.append(e.getMessage());
         return null;
      }
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
   public boolean useBinaryEditingPanel()
   {
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
      return true;
   }

   /*
    * Return a JTextArea usable in the CellPopupDialog
    * and fill in the value.
    */
   public RestorableRSyntaxTextArea getRestorableRSyntaxTextArea(Object value, ColumnDisplayDefinition colDef)
   {
      _textComponent = new RestorableRSyntaxTextArea();

      // value is a simple string representation of the data,
      // the same one used in Text and in-cell operations.
      ((RestorableRSyntaxTextArea) _textComponent).setText(renderObject(value));

      // special handling of operations while editing this data type
      ((RestorableRSyntaxTextArea) _textComponent).addKeyListener(new KeyTextHandler());

      return (RestorableRSyntaxTextArea) _textComponent;
   }

   /**
    * Validating and converting in Popup is identical to cell-related operation.
    */
   public Object validateAndConvertInPopup(String value, Object originalValue, StringBuffer messageBuffer)
   {
      return validateAndConvert(value, originalValue, messageBuffer);
   }

   /*
    * The following is used in both cell and popup operations.
    */

   /*
    * Internal class for handling key events during editing
    * of both JTextField and JTextArea.
    */
   private class KeyTextHandler extends BaseKeyTextHandler
   {
      public void keyTyped(KeyEvent e)
      {
         char c = e.getKeyChar();

         // as a coding convenience, create a reference to the text component
         // that is typecast to JTextComponent. this is not essential, as we
         // could typecast every reference, but this makes the code cleaner
         JTextComponent _theComponent = (JTextComponent) DataTypeTimestamp.this._textComponent;
         String text = _theComponent.getText();


         // tabs and newlines get put into the text before this check,
         // so remove them
         // This only applies to Popup editing since these chars are
         // not passed to this level by the in-cell editor.
         if (c == KeyEvent.VK_TAB || c == KeyEvent.VK_ENTER)
         {
            // remove all instances of the offending char
            int index = text.indexOf(c);
            if (index != -1)
            {
               if (index == text.length() - 1)
               {
                  text = text.substring(0, text.length() - 1);   // truncate string
               }
               else
               {
                  text = text.substring(0, index) + text.substring(index + 1);
               }
               ((IRestorableTextComponent) _theComponent).updateText(text);
               _beepHelper.beep(_theComponent);
            }
            e.consume();
         }


         // handle cases of null
         // The processing is different when nulls are allowed and when they are not.
         //

         if (DataTypeTimestamp.this._isNullable)
         {

            // user enters something when field is null
            if (text.equals(StringUtilities.NULL_AS_STRING))
            {
               if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))
               {
                  // delete when null => original value
                  DataTypeTimestamp.this._textComponent.restoreText();
                  e.consume();
               }
               else
               {
                  // non-delete when null => clear field and add text
                  DataTypeTimestamp.this._textComponent.updateText("");
                  // fall through to normal processing of this key stroke
               }
            }
            else
            {
               // check for user deletes last thing in field
               if ((c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))
               {
                  if (text.length() <= 1)
                  {
                     // about to delete last thing in field, so replace with null
                     DataTypeTimestamp.this._textComponent.updateText(StringUtilities.NULL_AS_STRING);
                     e.consume();
                  }
               }
            }
         }
         else
         {
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
         throws java.sql.SQLException
   {

      Timestamp data = rs.getTimestamp(index);
      if (rs.wasNull())
      {
         return null;
      }
      else
      {
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
    * "columnName = value"
    * or
    * "columnName is null"
    * or whatever is appropriate for this column in the database.
    */
   public IWhereClausePart getWhereClauseValue(Object value, ISQLDatabaseMetaData md)
   {
      if (_dataTypeTimestampStatics.getInternalWhereClauseUsage() == DataTypeTimestampStatics.DO_NOT_USE)
      {
         return new EmptyWhereClausePart();
      }

      if (value == null || value.toString() == null || value.toString().length() == 0)
      {
         return new IsNullWhereClausePart(_colDef);
      }
      else if (_dataTypeTimestampStatics.getInternalWhereClauseUsage() == DataTypeTimestampStatics.USE_JDBC_ESCAPE_FORMAT)
      {
         //return new NoParameterWhereClausePart(_colDef, _colDef.getColumnName() + "={ts '" + value.toString() + "'}");
         return new NoParameterWhereClausePart(_colDef, _colDef.getColumnName() + " = " + TemporalUtils.getStdJDBCFormat((java.util.Date) value, Types.TIMESTAMP));
      }
      else
      {
         return new ParameterWhereClausePart(_colDef, value, this);
      }
   }


   /**
    * When updating the database, insert the appropriate datatype into the
    * prepared statment at the given variable position.
    */
   public void setPreparedStatementValue(PreparedStatement pstmt, Object value, int position)
         throws java.sql.SQLException
   {
      if (value == null)
      {
         pstmt.setNull(position, _colDef.getSqlType());
      }
      else
      {
         pstmt.setTimestamp(position, ((Timestamp) value));
      }
   }

   /**
    * Get a default value for the table used to input data for a new row
    * to be inserted into the DB.
    */
   public Object getDefaultValue(String dbDefaultValue)
   {
      if (dbDefaultValue != null)
      {
         // try to use the DB default value
         StringBuffer mbuf = new StringBuffer();
         Object newObject = validateAndConvert(dbDefaultValue, null, mbuf);

         // if there was a problem with converting, then just fall through
         // and continue as if there was no default given in the DB.
         // Otherwise, use the converted object
         if (mbuf.length() == 0)
         {
            return newObject;
         }
      }

      // no default in DB.  If nullable, use null.
      if (_isNullable)
      {
         return null;
      }

      // field is not nullable, so create a reasonable default value
      return new Timestamp(new java.util.Date().getTime());
   }


   /*
    * File IO related functions
    */


   /**
    * Say whether or not object can be exported to and imported from
    * a file.  We put both export and import together in one test
    * on the assumption that all conversions can be done both ways.
    */
   public boolean canDoFileIO()
   {
      return true;
   }

   /**
    * Read a file and construct a valid object from its contents.
    * Errors are returned by throwing an IOException containing the
    * cause of the problem as its message.
    * <p>
    * DataType is responsible for validating that the imported
    * data can be converted to an object, and then must return
    * a text string that can be used in the Popup window text area.
    * This object-to-text conversion is the same as is done by
    * the DataType object internally in the getJTextArea() method.
    *
    * <p>
    * File is assumed to be and ASCII string of digits
    * representing a value of this data type.
    */
   public String importObject(FileInputStream inStream)
         throws IOException
   {

      InputStreamReader inReader = new InputStreamReader(inStream);

      int fileSize = inStream.available();

      char charBuf[] = new char[fileSize];

      int count = inReader.read(charBuf, 0, fileSize);

      if (count != fileSize)
      {
         throw new IOException(
               "Could read only " + count +
                     " chars from a total file size of " + fileSize +
                     ". Import failed.");
      }

      // convert file text into a string
      // Special case: some systems tack a newline at the end of
      // the text read.  Assume that if last char is a newline that
      // we want everything else in the line.
      String fileText;
      if (charBuf[count - 1] == KeyEvent.VK_ENTER)
      {
         fileText = new String(charBuf, 0, count - 1);
      }
      else
      {
         fileText = new String(charBuf);
      }

      // test that the string is valid by converting it into an
      // object of this data type
      StringBuffer messageBuffer = new StringBuffer();
      validateAndConvertInPopup(fileText, null, messageBuffer);
      if (messageBuffer.length() > 0)
      {
         // convert number conversion issue into IO issue for consistancy
         throw new IOException(
               "Text does not represent data of type " + getClassName() +
                     ".  Text was:\n" + fileText);
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
    * <p>
    * DataType is responsible for validating that the given text
    * text from a Popup JTextArea can be converted to an object.
    * This text-to-object conversion is the same as validateAndConvertInPopup,
    * which may be used internally by the object to do the validation.
    * <p>
    * The DataType object must flush and close the output stream before returning.
    * Typically it will create another object (e.g. an OutputWriter), and
    * that is the object that must be flushed and closed.
    *
    * <p>
    * File is assumed to be and ASCII string of digits
    * representing a value of this data type.
    */
   public void exportObject(FileOutputStream outStream, String text)
         throws IOException
   {

      OutputStreamWriter outWriter = new OutputStreamWriter(outStream);

      // check that the text is a valid representation
      StringBuffer messageBuffer = new StringBuffer();
      validateAndConvertInPopup(text, null, messageBuffer);
      if (messageBuffer.length() > 0)
      {
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
    * <p>
    * This is called ONLY if there is at least one property entered into the DTProperties
    * for this class.
    * <p>
    * Since this method is called by reflection on the Method object derived from this class,
    * it does not need to be included in the Interface.
    * It would be nice to include this in the Interface for consistancy, documentation, etc,
    * but the Interface does not seem to like static methods.
    */
   public static OkJPanel getControlPanel()
   {

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

      return new TimestampOkPanel(_dataTypeTimestampStatics);
   }

}
