/*
 * Java CSV is a stream based library for reading and writing
 * CSV and other delimited data.
 *
 * Copyright (C) Bruce Dunwiddie bruce@csvreader.com
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */
package net.sourceforge.squirrel_sql.plugins.dataimport.importer.csv.csvreader;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.text.NumberFormat;

/**
 * A stream based parser for parsing delimited text data from a file or a
 * stream.
 */
public class CsvReader
{
   /**
    * Double up the text qualifier to represent an occurance of the text
    * qualifier.
    */
   public static final int ESCAPE_MODE_DOUBLED = 1;

   /**
    * Use a backslash character before the text qualifier to represent an
    * occurance of the text qualifier.
    */
   public static final int ESCAPE_MODE_BACKSLASH = 2;

   private Reader _inputStream;


   // this holds all the values for switches that the user is allowed to set
   private UserSettings _userSettings = new UserSettings();

   private Charset _charset;

   private boolean _useCustomRecordDelimiter = false;

   // this will be our working buffer to hold data chunks
   // read in from the data file

   private DataBuffer _dataBuffer = new DataBuffer();

   private ColumnBuffer _columnBuffer = new ColumnBuffer();

   private RawRecordBuffer _rawBuffer = new RawRecordBuffer();

   private boolean[] _isQualified;

   private String _rawRecord = "";

   private HeadersHolder _headersHolder = new HeadersHolder();

   // these are all more or less global loop variables
   // to keep from needing to pass them all into various
   // methods during parsing

   private boolean _startedColumn = false;

   private boolean _startedWithQualifier = false;

   private boolean _hasMoreData = true;

   private char _lastLetter = '\0';

   private boolean _hasReadNextLine = false;

   private int _columnsCount = 0;

   private long _currentRecord = 0;

   private String[] _values = new String[StaticSettings.INITIAL_COLUMN_COUNT];

   private boolean _closed = false;

   /**
    * Constructs a {@link CsvReader CsvReader} object using a
    * {@link java.io.Reader Reader} object as the data source.
    *  @param inputStream The stream to use as the data source.
    * @param importCharset
    * @param delimiter   The character to use as the column delimiter.
    * @param useTextQualifier
    */
   public CsvReader(Reader inputStream, Charset charset, Character delimiter, boolean trimValues, boolean useTextQualifier)
   {
      if (inputStream == null)
      {
         throw new IllegalArgumentException("Parameter inputStream can not be null.");
      }

      _inputStream = inputStream;
      _charset = charset;
      _userSettings.delimiter = delimiter;
      _userSettings.trimWhitespace = trimValues;
      _userSettings.useTextQualifier = useTextQualifier;

      _isQualified = new boolean[_values.length];
   }

   public boolean getCaptureRawRecord()
   {
      return _userSettings.captureRawRecord;
   }

   public void setCaptureRawRecord(boolean captureRawRecord)
   {
      _userSettings.captureRawRecord = captureRawRecord;
   }

   public String getRawRecord()
   {
      return _rawRecord;
   }

   /**
    * Gets whether leading and trailing whitespace characters are being trimmed
    * from non-textqualified column data. Default is true.
    *
    * @return Whether leading and trailing whitespace characters are being
    * trimmed from non-textqualified column data.
    */
   public boolean getTrimWhitespace()
   {
      return _userSettings.trimWhitespace;
   }

   /**
    * Sets whether leading and trailing whitespace characters should be trimmed
    * from non-textqualified column data or not. Default is true.
    *
    * @param trimWhitespace Whether leading and trailing whitespace characters should be
    *                       trimmed from non-textqualified column data or not.
    */
   public void setTrimWhitespace(boolean trimWhitespace)
   {
      _userSettings.trimWhitespace = trimWhitespace;
   }

   /**
    * Gets the character being used as the column delimiter. Default is comma,
    * ','.
    *
    * @return The character being used as the column delimiter.
    */
   public Character getDelimiter()
   {
      return _userSettings.delimiter;
   }

   /**
    * Sets the character to use as the column delimiter. Default is comma, ','.
    *
    * @param delimiter The character to use as the column delimiter.
    */
   public void setDelimiter(Character delimiter)
   {
      _userSettings.delimiter = delimiter;
   }

   public char getRecordDelimiter()
   {
      return _userSettings.recordDelimiter;
   }

   /**
    * Sets the character to use as the record delimiter.
    *
    * @param recordDelimiter The character to use as the record delimiter. Default is
    *                        combination of standard end of line characters for Windows,
    *                        Unix, or Mac.
    */
   public void setRecordDelimiter(char recordDelimiter)
   {
      _useCustomRecordDelimiter = true;
      _userSettings.recordDelimiter = recordDelimiter;
   }

   /**
    * Gets the character to use as a text qualifier in the data.
    *
    * @return The character to use as a text qualifier in the data.
    */
   public char getTextQualifier()
   {
      return _userSettings.textQualifier;
   }

   /**
    * Sets the character to use as a text qualifier in the data.
    *
    * @param textQualifier The character to use as a text qualifier in the data.
    */
   public void setTextQualifier(char textQualifier)
   {
      _userSettings.textQualifier = textQualifier;
   }

   /**
    * Whether text qualifiers will be used while parsing or not.
    *
    * @return Whether text qualifiers will be used while parsing or not.
    */
   public boolean getUseTextQualifier()
   {
      return _userSettings.useTextQualifier;
   }

   /**
    * Sets whether text qualifiers will be used while parsing or not.
    *
    * @param useTextQualifier Whether to use a text qualifier while parsing or not.
    */
   public void setUseTextQualifier(boolean useTextQualifier)
   {
      _userSettings.useTextQualifier = useTextQualifier;
   }

   /**
    * Gets the character being used as a comment signal.
    *
    * @return The character being used as a comment signal.
    */
   public char getComment()
   {
      return _userSettings.comment;
   }

   /**
    * Sets the character to use as a comment signal.
    *
    * @param comment The character to use as a comment signal.
    */
   public void setComment(char comment)
   {
      _userSettings.comment = comment;
   }

   /**
    * Gets whether comments are being looked for while parsing or not.
    *
    * @return Whether comments are being looked for while parsing or not.
    */
   public boolean getUseComments()
   {
      return _userSettings.useComments;
   }

   /**
    * Sets whether comments are being looked for while parsing or not.
    *
    * @param useComments Whether comments are being looked for while parsing or not.
    */
   public void setUseComments(boolean useComments)
   {
      _userSettings.useComments = useComments;
   }

   /**
    * Gets the current way to escape an occurance of the text qualifier inside
    * qualified data.
    *
    * @return The current way to escape an occurance of the text qualifier
    * inside qualified data.
    */
   public int getEscapeMode()
   {
      return _userSettings.escapeMode;
   }

   /**
    * Sets the current way to escape an occurance of the text qualifier inside
    * qualified data.
    *
    * @param escapeMode The way to escape an occurance of the text qualifier inside
    *                   qualified data.
    * @throws IllegalArgumentException When an illegal value is specified for escapeMode.
    */
   public void setEscapeMode(int escapeMode) throws IllegalArgumentException
   {
      if (escapeMode != ESCAPE_MODE_DOUBLED && escapeMode != ESCAPE_MODE_BACKSLASH)
      {
         throw new IllegalArgumentException("Parameter escapeMode must be a valid value.");
      }

      _userSettings.escapeMode = escapeMode;
   }

   public boolean getSkipEmptyRecords()
   {
      return _userSettings.skipEmptyRecords;
   }

   public void setSkipEmptyRecords(boolean skipEmptyRecords)
   {
      _userSettings.skipEmptyRecords = skipEmptyRecords;
   }

   /**
    * Safety caution to prevent the parser from using large amounts of memory
    * in the case where parsing settings like file encodings don't end up
    * matching the actual format of a file. This switch can be turned off if
    * the file format is known and tested. With the switch off, the max column
    * lengths and max column count per record supported by the parser will
    * greatly increase. Default is true.
    *
    * @return The current setting of the safety switch.
    */
   public boolean getSafetySwitch()
   {
      return _userSettings.safetySwitch;
   }

   /**
    * Safety caution to prevent the parser from using large amounts of memory
    * in the case where parsing settings like file encodings don't end up
    * matching the actual format of a file. This switch can be turned off if
    * the file format is known and tested. With the switch off, the max column
    * lengths and max column count per record supported by the parser will
    * greatly increase. Default is true.
    *
    * @param safetySwitch
    */
   public void setSafetySwitch(boolean safetySwitch)
   {
      _userSettings.safetySwitch = safetySwitch;
   }

   /**
    * Gets the count of columns found in this record.
    *
    * @return The count of columns found in this record.
    */
   public int getColumnCount()
   {
      return _columnsCount;
   }

   /**
    * Gets the index of the current record.
    *
    * @return The index of the current record.
    */
   public long getCurrentRecord()
   {
      return _currentRecord - 1;
   }

   /**
    * Gets the count of headers read in by a previous call to
    * {@link CsvReader#readHeaders readHeaders()}.
    *
    * @return The count of headers read in by a previous call to
    * {@link CsvReader#readHeaders readHeaders()}.
    */
   public int getHeaderCount()
   {
      return _headersHolder.length;
   }

   /**
    * Returns the header values as a string array.
    *
    * @return The header values as a String array.
    * @throws IOException Thrown if this object has already been closed.
    */
   public String[] getHeaders() throws IOException
   {
      checkClosed();

      if (_headersHolder.headers == null)
      {
         return null;
      }
      else
      {
         // use clone here to prevent the outside code from
         // setting values on the array directly, which would
         // throw off the index lookup based on header name
         String[] clone = new String[_headersHolder.length];
         System.arraycopy(_headersHolder.headers, 0, clone, 0,
               _headersHolder.length);
         return clone;
      }
   }

   public void setHeaders(String[] headers)
   {
      _headersHolder.headers = headers;

      _headersHolder.indexByName.clear();

      if (headers != null)
      {
         _headersHolder.length = headers.length;
      }
      else
      {
         _headersHolder.length = 0;
      }

      // use headersHolder.Length here in case headers is null
      for (int i = 0; i < _headersHolder.length; i++)
      {
         _headersHolder.indexByName.put(headers[i], Integer.valueOf(i));
      }
   }

   public String[] getValues() throws IOException
   {
      checkClosed();

      // need to return a clone, and can't use clone because values.Length
      // might be greater than columnsCount
      String[] clone = new String[_columnsCount];
      System.arraycopy(_values, 0, clone, 0, _columnsCount);
      return clone;
   }

   /**
    * Returns the current column value for a given column index.
    *
    * @param columnIndex The index of the column.
    * @return The current column value.
    * @throws IOException Thrown if this object has already been closed.
    */
   public String get(int columnIndex) throws IOException
   {
      checkClosed();

      if (columnIndex > -1 && columnIndex < _columnsCount)
      {
         return _values[columnIndex];
      }
      else
      {
         return "";
      }
   }

   /**
    * Returns the current column value for a given column header name.
    *
    * @param headerName The header name of the column.
    * @return The current column value.
    * @throws IOException Thrown if this object has already been closed.
    */
   public String get(String headerName) throws IOException
   {
      checkClosed();

      return get(getIndex(headerName));
   }

   /**
    * Reads another record.
    *
    * @return Whether another record was successfully read or not.
    * @throws IOException Thrown if an error occurs while reading data from the
    *                     source stream.
    */
   public boolean readRecord() throws IOException
   {
      checkClosed();

      _columnsCount = 0;
      _rawBuffer.position = 0;

      _dataBuffer.lineStart = _dataBuffer.position;

      _hasReadNextLine = false;

      // check to see if we've already found the end of data

      if (_hasMoreData)
      {
         // loop over the data stream until the end of data is found
         // or the end of the record is found

         do
         {
            if (_dataBuffer.position == _dataBuffer.count)
            {
               checkDataLength();
            }
            else
            {
               _startedWithQualifier = false;

               // grab the current letter as a char

               char currentLetter = _dataBuffer.buffer[_dataBuffer.position];

               if (_userSettings.useTextQualifier
                     && currentLetter == _userSettings.textQualifier)
               {
                  // this will be a text qualified column, so
                  // we need to set startedWithQualifier to make it
                  // enter the seperate branch to handle text
                  // qualified columns

                  _lastLetter = currentLetter;

                  // read qualified
                  _startedColumn = true;
                  _dataBuffer.columnStart = _dataBuffer.position + 1;
                  _startedWithQualifier = true;
                  boolean lastLetterWasQualifier = false;

                  char escapeChar = _userSettings.textQualifier;

                  if (_userSettings.escapeMode == ESCAPE_MODE_BACKSLASH)
                  {
                     escapeChar = Letters.BACKSLASH;
                  }

                  boolean eatingTrailingJunk = false;
                  boolean lastLetterWasEscape = false;
                  boolean readingComplexEscape = false;
                  int escape = ComplexEscape.UNICODE;
                  int escapeLength = 0;
                  char escapeValue = (char) 0;

                  _dataBuffer.position++;

                  do
                  {
                     if (_dataBuffer.position == _dataBuffer.count)
                     {
                        checkDataLength();
                     }
                     else
                     {
                        // grab the current letter as a char

                        currentLetter = _dataBuffer.buffer[_dataBuffer.position];

                        if (eatingTrailingJunk)
                        {
                           _dataBuffer.columnStart = _dataBuffer.position + 1;

                           if (null != _userSettings.delimiter && currentLetter == _userSettings.delimiter)
                           {
                              endColumn();
                           }
                           else if ((!_useCustomRecordDelimiter && (currentLetter == Letters.CR || currentLetter == Letters.LF))
                                 || (_useCustomRecordDelimiter && currentLetter == _userSettings.recordDelimiter))
                           {
                              endColumn();

                              endRecord();
                           }
                        }
                        else if (readingComplexEscape)
                        {
                           escapeLength++;

                           switch (escape)
                           {
                              case ComplexEscape.UNICODE:
                                 escapeValue *= (char) 16;
                                 escapeValue += hexToDec(currentLetter);

                                 if (escapeLength == 4)
                                 {
                                    readingComplexEscape = false;
                                 }

                                 break;
                              case ComplexEscape.OCTAL:
                                 escapeValue *= (char) 8;
                                 escapeValue += (char) (currentLetter - '0');

                                 if (escapeLength == 3)
                                 {
                                    readingComplexEscape = false;
                                 }

                                 break;
                              case ComplexEscape.DECIMAL:
                                 escapeValue *= (char) 10;
                                 escapeValue += (char) (currentLetter - '0');

                                 if (escapeLength == 3)
                                 {
                                    readingComplexEscape = false;
                                 }

                                 break;
                              case ComplexEscape.HEX:
                                 escapeValue *= (char) 16;
                                 escapeValue += hexToDec(currentLetter);

                                 if (escapeLength == 2)
                                 {
                                    readingComplexEscape = false;
                                 }

                                 break;
                           }

                           if (!readingComplexEscape)
                           {
                              appendLetter(escapeValue);
                           }
                           else
                           {
                              _dataBuffer.columnStart = _dataBuffer.position + 1;
                           }
                        }
                        else if (currentLetter == _userSettings.textQualifier)
                        {
                           if (lastLetterWasEscape)
                           {
                              lastLetterWasEscape = false;
                              lastLetterWasQualifier = false;
                           }
                           else
                           {
                              updateCurrentValue();

                              if (_userSettings.escapeMode == ESCAPE_MODE_DOUBLED)
                              {
                                 lastLetterWasEscape = true;
                              }

                              lastLetterWasQualifier = true;
                           }
                        }
                        else if (_userSettings.escapeMode == ESCAPE_MODE_BACKSLASH
                              && lastLetterWasEscape)
                        {
                           switch (currentLetter)
                           {
                              case 'n':
                                 appendLetter(Letters.LF);
                                 break;
                              case 'r':
                                 appendLetter(Letters.CR);
                                 break;
                              case 't':
                                 appendLetter(Letters.TAB);
                                 break;
                              case 'b':
                                 appendLetter(Letters.BACKSPACE);
                                 break;
                              case 'f':
                                 appendLetter(Letters.FORM_FEED);
                                 break;
                              case 'e':
                                 appendLetter(Letters.ESCAPE);
                                 break;
                              case 'v':
                                 appendLetter(Letters.VERTICAL_TAB);
                                 break;
                              case 'a':
                                 appendLetter(Letters.ALERT);
                                 break;
                              case '0':
                              case '1':
                              case '2':
                              case '3':
                              case '4':
                              case '5':
                              case '6':
                              case '7':
                                 escape = ComplexEscape.OCTAL;
                                 readingComplexEscape = true;
                                 escapeLength = 1;
                                 escapeValue = (char) (currentLetter - '0');
                                 _dataBuffer.columnStart = _dataBuffer.position + 1;
                                 break;
                              case 'u':
                              case 'x':
                              case 'o':
                              case 'd':
                              case 'U':
                              case 'X':
                              case 'O':
                              case 'D':
                                 switch (currentLetter)
                                 {
                                    case 'u':
                                    case 'U':
                                       escape = ComplexEscape.UNICODE;
                                       break;
                                    case 'x':
                                    case 'X':
                                       escape = ComplexEscape.HEX;
                                       break;
                                    case 'o':
                                    case 'O':
                                       escape = ComplexEscape.OCTAL;
                                       break;
                                    case 'd':
                                    case 'D':
                                       escape = ComplexEscape.DECIMAL;
                                       break;
                                 }

                                 readingComplexEscape = true;
                                 escapeLength = 0;
                                 escapeValue = (char) 0;
                                 _dataBuffer.columnStart = _dataBuffer.position + 1;

                                 break;
                              default:
                                 break;
                           }

                           lastLetterWasEscape = false;

                           // can only happen for ESCAPE_MODE_BACKSLASH
                        }
                        else if (currentLetter == escapeChar)
                        {
                           updateCurrentValue();
                           lastLetterWasEscape = true;
                        }
                        else
                        {
                           if (lastLetterWasQualifier)
                           {
                              if (null != _userSettings.delimiter && currentLetter == _userSettings.delimiter)
                              {
                                 endColumn();
                              }
                              else if ((!_useCustomRecordDelimiter && (currentLetter == Letters.CR || currentLetter == Letters.LF))
                                    || (_useCustomRecordDelimiter && currentLetter == _userSettings.recordDelimiter))
                              {
                                 endColumn();

                                 endRecord();
                              }
                              else
                              {
                                 _dataBuffer.columnStart = _dataBuffer.position + 1;

                                 eatingTrailingJunk = true;
                              }

                              // make sure to clear the flag for next
                              // run of the loop

                              lastLetterWasQualifier = false;
                           }
                        }

                        // keep track of the last letter because we need
                        // it for several key decisions

                        _lastLetter = currentLetter;

                        if (_startedColumn)
                        {
                           _dataBuffer.position++;

                           if (_userSettings.safetySwitch
                                 && _dataBuffer.position
                                 - _dataBuffer.columnStart
                                 + _columnBuffer.position > 100000)
                           {
                              close();

                              throw new IOException(
                                    "Maximum column length of 100,000 exceeded in column "
                                          + NumberFormat
                                          .getIntegerInstance()
                                          .format(
                                                _columnsCount)
                                          + " in record "
                                          + NumberFormat
                                          .getIntegerInstance()
                                          .format(
                                                _currentRecord)
                                          + ". Set the SafetySwitch property to false"
                                          + " if you're expecting column lengths greater than 100,000 characters to"
                                          + " avoid this error.");
                           }
                        }
                     } // end else

                  } while (_hasMoreData && _startedColumn);
               }
               else if (null != _userSettings.delimiter && currentLetter == _userSettings.delimiter)
               {
                  // we encountered a column with no data, so
                  // just send the end column

                  _lastLetter = currentLetter;

                  endColumn();
               }
               else if (_useCustomRecordDelimiter
                     && currentLetter == _userSettings.recordDelimiter)
               {
                  // this will skip blank lines
                  if (_startedColumn || _columnsCount > 0
                        || !_userSettings.skipEmptyRecords)
                  {
                     endColumn();

                     endRecord();
                  }
                  else
                  {
                     _dataBuffer.lineStart = _dataBuffer.position + 1;
                  }

                  _lastLetter = currentLetter;
               }
               else if (!_useCustomRecordDelimiter
                     && (currentLetter == Letters.CR || currentLetter == Letters.LF))
               {
                  // this will skip blank lines
                  if (_startedColumn
                        || _columnsCount > 0
                        || (!_userSettings.skipEmptyRecords && (currentLetter == Letters.CR || _lastLetter != Letters.CR)))
                  {
                     endColumn();

                     endRecord();
                  }
                  else
                  {
                     _dataBuffer.lineStart = _dataBuffer.position + 1;
                  }

                  _lastLetter = currentLetter;
               }
               else if (_userSettings.useComments && _columnsCount == 0
                     && currentLetter == _userSettings.comment)
               {
                  // encountered a comment character at the beginning of
                  // the line so just ignore the rest of the line

                  _lastLetter = currentLetter;

                  skipLine();
               }
               else if (_userSettings.trimWhitespace
                     && (currentLetter == Letters.SPACE || currentLetter == Letters.TAB))
               {
                  // do nothing, this will trim leading whitespace
                  // for both text qualified columns and non

                  _startedColumn = true;
                  _dataBuffer.columnStart = _dataBuffer.position + 1;
               }
               else
               {
                  // since the letter wasn't a special letter, this
                  // will be the first letter of our current column

                  _startedColumn = true;
                  _dataBuffer.columnStart = _dataBuffer.position;
                  boolean lastLetterWasBackslash = false;
                  boolean readingComplexEscape = false;
                  int escape = ComplexEscape.UNICODE;
                  int escapeLength = 0;
                  char escapeValue = (char) 0;

                  boolean firstLoop = true;

                  do
                  {
                     if (!firstLoop
                           && _dataBuffer.position == _dataBuffer.count)
                     {
                        checkDataLength();
                     }
                     else
                     {
                        if (!firstLoop)
                        {
                           // grab the current letter as a char
                           currentLetter = _dataBuffer.buffer[_dataBuffer.position];
                        }

                        if (!_userSettings.useTextQualifier
                              && _userSettings.escapeMode == ESCAPE_MODE_BACKSLASH
                              && currentLetter == Letters.BACKSLASH)
                        {
                           if (lastLetterWasBackslash)
                           {
                              lastLetterWasBackslash = false;
                           }
                           else
                           {
                              updateCurrentValue();
                              lastLetterWasBackslash = true;
                           }
                        }
                        else if (readingComplexEscape)
                        {
                           escapeLength++;

                           switch (escape)
                           {
                              case ComplexEscape.UNICODE:
                                 escapeValue *= (char) 16;
                                 escapeValue += hexToDec(currentLetter);

                                 if (escapeLength == 4)
                                 {
                                    readingComplexEscape = false;
                                 }

                                 break;
                              case ComplexEscape.OCTAL:
                                 escapeValue *= (char) 8;
                                 escapeValue += (char) (currentLetter - '0');

                                 if (escapeLength == 3)
                                 {
                                    readingComplexEscape = false;
                                 }

                                 break;
                              case ComplexEscape.DECIMAL:
                                 escapeValue *= (char) 10;
                                 escapeValue += (char) (currentLetter - '0');

                                 if (escapeLength == 3)
                                 {
                                    readingComplexEscape = false;
                                 }

                                 break;
                              case ComplexEscape.HEX:
                                 escapeValue *= (char) 16;
                                 escapeValue += hexToDec(currentLetter);

                                 if (escapeLength == 2)
                                 {
                                    readingComplexEscape = false;
                                 }

                                 break;
                           }

                           if (!readingComplexEscape)
                           {
                              appendLetter(escapeValue);
                           }
                           else
                           {
                              _dataBuffer.columnStart = _dataBuffer.position + 1;
                           }
                        }
                        else if (_userSettings.escapeMode == ESCAPE_MODE_BACKSLASH
                              && lastLetterWasBackslash)
                        {
                           switch (currentLetter)
                           {
                              case 'n':
                                 appendLetter(Letters.LF);
                                 break;
                              case 'r':
                                 appendLetter(Letters.CR);
                                 break;
                              case 't':
                                 appendLetter(Letters.TAB);
                                 break;
                              case 'b':
                                 appendLetter(Letters.BACKSPACE);
                                 break;
                              case 'f':
                                 appendLetter(Letters.FORM_FEED);
                                 break;
                              case 'e':
                                 appendLetter(Letters.ESCAPE);
                                 break;
                              case 'v':
                                 appendLetter(Letters.VERTICAL_TAB);
                                 break;
                              case 'a':
                                 appendLetter(Letters.ALERT);
                                 break;
                              case '0':
                              case '1':
                              case '2':
                              case '3':
                              case '4':
                              case '5':
                              case '6':
                              case '7':
                                 escape = ComplexEscape.OCTAL;
                                 readingComplexEscape = true;
                                 escapeLength = 1;
                                 escapeValue = (char) (currentLetter - '0');
                                 _dataBuffer.columnStart = _dataBuffer.position + 1;
                                 break;
                              case 'u':
                              case 'x':
                              case 'o':
                              case 'd':
                              case 'U':
                              case 'X':
                              case 'O':
                              case 'D':
                                 switch (currentLetter)
                                 {
                                    case 'u':
                                    case 'U':
                                       escape = ComplexEscape.UNICODE;
                                       break;
                                    case 'x':
                                    case 'X':
                                       escape = ComplexEscape.HEX;
                                       break;
                                    case 'o':
                                    case 'O':
                                       escape = ComplexEscape.OCTAL;
                                       break;
                                    case 'd':
                                    case 'D':
                                       escape = ComplexEscape.DECIMAL;
                                       break;
                                 }

                                 readingComplexEscape = true;
                                 escapeLength = 0;
                                 escapeValue = (char) 0;
                                 _dataBuffer.columnStart = _dataBuffer.position + 1;

                                 break;
                              default:
                                 break;
                           }

                           lastLetterWasBackslash = false;
                        }
                        else
                        {
                           if (null != _userSettings.delimiter && currentLetter == _userSettings.delimiter)
                           {
                              endColumn();
                           }
                           else if ((!_useCustomRecordDelimiter && (currentLetter == Letters.CR || currentLetter == Letters.LF))
                                 || (_useCustomRecordDelimiter && currentLetter == _userSettings.recordDelimiter))
                           {
                              endColumn();

                              endRecord();
                           }
                        }

                        // keep track of the last letter because we need
                        // it for several key decisions

                        _lastLetter = currentLetter;
                        firstLoop = false;

                        if (_startedColumn)
                        {
                           _dataBuffer.position++;

                           if (_userSettings.safetySwitch
                                 && _dataBuffer.position
                                 - _dataBuffer.columnStart
                                 + _columnBuffer.position > 100000)
                           {
                              close();

                              throw new IOException(
                                    "Maximum column length of 100,000 exceeded in column "
                                          + NumberFormat
                                          .getIntegerInstance()
                                          .format(
                                                _columnsCount)
                                          + " in record "
                                          + NumberFormat
                                          .getIntegerInstance()
                                          .format(
                                                _currentRecord)
                                          + ". Set the SafetySwitch property to false"
                                          + " if you're expecting column lengths greater than 100,000 characters to"
                                          + " avoid this error.");
                           }
                        }
                     } // end else
                  } while (_hasMoreData && _startedColumn);
               }

               if (_hasMoreData)
               {
                  _dataBuffer.position++;
               }
            } // end else
         } while (_hasMoreData && !_hasReadNextLine);

         // check to see if we hit the end of the file
         // without processing the current record

         if (_startedColumn || (null != _userSettings.delimiter && _lastLetter == _userSettings.delimiter))
         {
            endColumn();

            endRecord();
         }
      }

      if (_userSettings.captureRawRecord)
      {
         if (_hasMoreData)
         {
            if (_rawBuffer.position == 0)
            {
               _rawRecord = new String(_dataBuffer.buffer,
                     _dataBuffer.lineStart, _dataBuffer.position
                     - _dataBuffer.lineStart - 1);
            }
            else
            {
               _rawRecord = new String(_rawBuffer.buffer, 0,
                     _rawBuffer.position)
                     + new String(_dataBuffer.buffer,
                     _dataBuffer.lineStart, _dataBuffer.position
                     - _dataBuffer.lineStart - 1);
            }
         }
         else
         {
            // for hasMoreData to ever be false, all data would have had to
            // have been
            // copied to the raw buffer
            _rawRecord = new String(_rawBuffer.buffer, 0, _rawBuffer.position);
         }
      }
      else
      {
         _rawRecord = "";
      }

      return _hasReadNextLine;
   }

   /**
    * @throws IOException Thrown if an error occurs while reading data from the
    *                     source stream.
    */
   private void checkDataLength() throws IOException
   {
      updateCurrentValue();

      if (_userSettings.captureRawRecord && _dataBuffer.count > 0)
      {
         if (_rawBuffer.buffer.length - _rawBuffer.position < _dataBuffer.count - _dataBuffer.lineStart)
         {
            int newLength = _rawBuffer.buffer.length + Math.max(_dataBuffer.count - _dataBuffer.lineStart, _rawBuffer.buffer.length);

            char[] holder = new char[newLength];

            System.arraycopy(_rawBuffer.buffer, 0, holder, 0, _rawBuffer.position);

            _rawBuffer.buffer = holder;
         }

         System.arraycopy(_dataBuffer.buffer, _dataBuffer.lineStart,
               _rawBuffer.buffer, _rawBuffer.position, _dataBuffer.count
                     - _dataBuffer.lineStart);

         _rawBuffer.position += _dataBuffer.count - _dataBuffer.lineStart;
      }

      try
      {
         _dataBuffer.count = _inputStream.read(_dataBuffer.buffer, 0, _dataBuffer.buffer.length);
      }
      catch (IOException ex)
      {
         close();

         throw ex;
      }

      // if no more data could be found, set flag stating that
      // the end of the data was found

      if (_dataBuffer.count == -1)
      {
         _hasMoreData = false;
      }

      _dataBuffer.position = 0;
      _dataBuffer.lineStart = 0;
      _dataBuffer.columnStart = 0;
   }

   /**
    * Read the first record of data as column headers.
    *
    * @return Whether the header record was successfully read or not.
    * @throws IOException Thrown if an error occurs while reading data from the
    *                     source stream.
    */
   public boolean readHeaders() throws IOException
   {
      boolean result = readRecord();

      // copy the header data from the column array
      // to the header string array

      _headersHolder.length = _columnsCount;

      _headersHolder.headers = new String[_columnsCount];

      for (int i = 0; i < _headersHolder.length; i++)
      {
         String columnValue = get(i);

         _headersHolder.headers[i] = columnValue;

         // if there are duplicate header names, we will save the last one
         _headersHolder.indexByName.put(columnValue, Integer.valueOf(i));
      }

      if (result)
      {
         _currentRecord--;
      }

      _columnsCount = 0;

      return result;
   }

   /**
    * Returns the column header value for a given column index.
    *
    * @param columnIndex The index of the header column being requested.
    * @return The value of the column header at the given column index.
    * @throws IOException Thrown if this object has already been closed.
    */
   public String getHeader(int columnIndex) throws IOException
   {
      checkClosed();

      // check to see if we have read the header record yet

      // check to see if the column index is within the bounds
      // of our header array

      if (columnIndex > -1 && columnIndex < _headersHolder.length)
      {
         // return the processed header data for this column

         return _headersHolder.headers[columnIndex];
      }
      else
      {
         return "";
      }
   }

   public boolean isQualified(int columnIndex) throws IOException
   {
      checkClosed();

      if (columnIndex < _columnsCount && columnIndex > -1)
      {
         return _isQualified[columnIndex];
      }
      else
      {
         return false;
      }
   }

   /**
    * @throws IOException Thrown if a very rare extreme exception occurs during
    *                     parsing, normally resulting from improper data format.
    */
   private void endColumn() throws IOException
   {
      String currentValue = "";

      // must be called before setting startedColumn = false
      if (_startedColumn)
      {
         if (_columnBuffer.position == 0)
         {
            if (_dataBuffer.columnStart < _dataBuffer.position)
            {
               int lastLetter = _dataBuffer.position - 1;

               if (_userSettings.trimWhitespace && !_startedWithQualifier)
               {
                  while (lastLetter >= _dataBuffer.columnStart && (_dataBuffer.buffer[lastLetter] == Letters.SPACE || _dataBuffer.buffer[lastLetter] == Letters.TAB))
                  {
                     lastLetter--;
                  }
               }

               currentValue = new String(_dataBuffer.buffer, _dataBuffer.columnStart, lastLetter - _dataBuffer.columnStart + 1);
            }
         }
         else
         {
            updateCurrentValue();

            int lastLetter = _columnBuffer.position - 1;

            if (_userSettings.trimWhitespace && !_startedWithQualifier)
            {
               while (lastLetter >= 0 && (_columnBuffer.buffer[lastLetter] == Letters.SPACE || _columnBuffer.buffer[lastLetter] == Letters.SPACE))
               {
                  lastLetter--;
               }
            }

            currentValue = new String(_columnBuffer.buffer, 0, lastLetter + 1);
         }
      }

      _columnBuffer.position = 0;

      _startedColumn = false;

      if (_columnsCount >= 100000 && _userSettings.safetySwitch)
      {
         close();

         throw new IOException(
               "Maximum column count of 100,000 exceeded in record "
                     + NumberFormat.getIntegerInstance().format(
                     _currentRecord)
                     + ". Set the SafetySwitch property to false"
                     + " if you're expecting more than 100,000 columns per record to"
                     + " avoid this error.");
      }

      // check to see if our current holder array for
      // column chunks is still big enough to handle another
      // column chunk

      if (_columnsCount == _values.length)
      {
         // holder array needs to grow to be able to hold another column
         int newLength = _values.length * 2;

         String[] holder = new String[newLength];

         System.arraycopy(_values, 0, holder, 0, _values.length);

         _values = holder;

         boolean[] qualifiedHolder = new boolean[newLength];

         System.arraycopy(_isQualified, 0, qualifiedHolder, 0, _isQualified.length);

         _isQualified = qualifiedHolder;
      }

      _values[_columnsCount] = currentValue;

      _isQualified[_columnsCount] = _startedWithQualifier;

      currentValue = "";

      _columnsCount++;
   }

   private void appendLetter(char letter)
   {
      if (_columnBuffer.position == _columnBuffer.buffer.length)
      {
         int newLength = _columnBuffer.buffer.length * 2;

         char[] holder = new char[newLength];

         System.arraycopy(_columnBuffer.buffer, 0, holder, 0,
               _columnBuffer.position);

         _columnBuffer.buffer = holder;
      }
      _columnBuffer.buffer[_columnBuffer.position++] = letter;
      _dataBuffer.columnStart = _dataBuffer.position + 1;
   }

   private void updateCurrentValue()
   {
      if (_startedColumn && _dataBuffer.columnStart < _dataBuffer.position)
      {
         if (_columnBuffer.buffer.length - _columnBuffer.position < _dataBuffer.position
               - _dataBuffer.columnStart)
         {
            int newLength = _columnBuffer.buffer.length
                  + Math.max(
                  _dataBuffer.position - _dataBuffer.columnStart,
                  _columnBuffer.buffer.length);

            char[] holder = new char[newLength];

            System.arraycopy(_columnBuffer.buffer, 0, holder, 0,
                  _columnBuffer.position);

            _columnBuffer.buffer = holder;
         }

         System.arraycopy(_dataBuffer.buffer, _dataBuffer.columnStart,
               _columnBuffer.buffer, _columnBuffer.position,
               _dataBuffer.position - _dataBuffer.columnStart);

         _columnBuffer.position += _dataBuffer.position
               - _dataBuffer.columnStart;
      }

      _dataBuffer.columnStart = _dataBuffer.position + 1;
   }

   /**
    * @throws IOException Thrown if an error occurs while reading data from the
    *                     source stream.
    */
   private void endRecord() throws IOException
   {
      // this flag is used as a loop exit condition
      // during parsing

      _hasReadNextLine = true;

      _currentRecord++;
   }

   /**
    * Gets the corresponding column index for a given column header name.
    *
    * @param headerName The header name of the column.
    * @return The column index for the given column header name.&nbsp;Returns
    * -1 if not found.
    * @throws IOException Thrown if this object has already been closed.
    */
   public int getIndex(String headerName) throws IOException
   {
      checkClosed();

      Integer indexValue = _headersHolder.indexByName.get(headerName);

      if (indexValue != null)
      {
         return indexValue.intValue();
      }
      else
      {
         return -1;
      }
   }

   /**
    * Skips the next record of data by parsing each column.&nbsp;Does not
    * increment
    * {@link CsvReader#getCurrentRecord getCurrentRecord()}.
    *
    * @return Whether another record was successfully skipped or not.
    * @throws IOException Thrown if an error occurs while reading data from the
    *                     source stream.
    */
   public boolean skipRecord() throws IOException
   {
      checkClosed();

      boolean recordRead = false;

      if (_hasMoreData)
      {
         recordRead = readRecord();

         if (recordRead)
         {
            _currentRecord--;
         }
      }

      return recordRead;
   }

   /**
    * Skips the next line of data using the standard end of line characters and
    * does not do any column delimited parsing.
    *
    * @return Whether a line was successfully skipped or not.
    * @throws IOException Thrown if an error occurs while reading data from the
    *                     source stream.
    */
   public boolean skipLine() throws IOException
   {
      checkClosed();

      // clear public column values for current line

      _columnsCount = 0;

      boolean skippedLine = false;

      if (_hasMoreData)
      {
         boolean foundEol = false;

         do
         {
            if (_dataBuffer.position == _dataBuffer.count)
            {
               checkDataLength();
            }
            else
            {
               skippedLine = true;

               // grab the current letter as a char

               char currentLetter = _dataBuffer.buffer[_dataBuffer.position];

               if (currentLetter == Letters.CR
                     || currentLetter == Letters.LF)
               {
                  foundEol = true;
               }

               // keep track of the last letter because we need
               // it for several key decisions

               _lastLetter = currentLetter;

               if (!foundEol)
               {
                  _dataBuffer.position++;
               }

            } // end else
         } while (_hasMoreData && !foundEol);

         _columnBuffer.position = 0;

         _dataBuffer.lineStart = _dataBuffer.position + 1;
      }

      _rawBuffer.position = 0;
      _rawRecord = "";

      return skippedLine;
   }

   /**
    * Closes and releases all related resources.
    */
   public void close()
   {
      if (!_closed)
      {
         close(true);

         _closed = true;
      }
   }

   /**
    *
    */
   private void close(boolean closing)
   {
      if (!_closed)
      {
         if (closing)
         {
            _charset = null;
            _headersHolder.headers = null;
            _headersHolder.indexByName = null;
            _dataBuffer.buffer = null;
            _columnBuffer.buffer = null;
            _rawBuffer.buffer = null;
         }

         try
         {
            _inputStream.close();
         }
         catch (Exception e)
         {
         }

         _inputStream = null;

         _closed = true;
      }
   }

   /**
    * @throws IOException Thrown if this object has already been closed.
    */
   private void checkClosed() throws IOException
   {
      if (_closed)
      {
         throw new IOException(
               "This instance of the CsvReader class has already been closed.");
      }
   }

   /**
    *
    */
   protected void finalize()
   {
      close(false);
   }

   private static char hexToDec(char hex)
   {
      char result;

      if (hex >= 'a')
      {
         result = (char) (hex - 'a' + 10);
      }
      else if (hex >= 'A')
      {
         result = (char) (hex - 'A' + 10);
      }
      else
      {
         result = (char) (hex - '0');
      }

      return result;
   }

}