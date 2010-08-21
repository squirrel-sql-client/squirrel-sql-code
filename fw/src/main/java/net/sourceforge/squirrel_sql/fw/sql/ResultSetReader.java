package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001-2004 Colin Bell
 * colbell@users.sourceforge.net
 * Copyright (C) 2001-2004 Johan Compagner
 * jcompagner@j-com.nl
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
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeBlob;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeClob;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeDate;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ResultSetReader
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(ResultSetReader.class);

	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ResultSetReader.class);

	/** The <TT>ResultSet</TT> being read. */
	private final ResultSet _rs;

	/**
	 * The indices into the <TT>ResultSet that we want to read, starting from
	 * 1 (not 0). If this contains {1, 5, 6} then only columns 1, 5, and 6 will
	 * be read. If <TT>null or empty then all columns are read.
	 */
	private final int[] _columnIndices;


	/**
	 * The number of columns to read. This may or may not be the same as the
	 * number of columns in the <TT>ResultSet</TT>. @see _columnIndices.
	 */
	private int _columnCount;

	/** <TT>true</TT> if an error occured reading a column in th previous row. */
	private boolean _errorOccured = false;

	/** Metadata for the <TT>ResultSet</TT>. */
	private ResultSetMetaData _rsmd;

    /** whether or not the user requested to cancel the query */
   private volatile boolean _stopExecution = false; 

   /** the type of dialect used to obtain the ResultSet to be read */ 
   private DialectType _dialectType = null;
   
	/**
    * Constructor
    * 
    * @param rs
    *           the ResultSet to read
    * @param dialectType
    *           the DialectType
    * @throws SQLException
    */
	public ResultSetReader(ResultSet rs, DialectType dialectType)
		throws SQLException
	{
		this(rs, null, dialectType);
	}



	/**
	 * @param rs
	 * @param columnIndices
	 * @param dialectType
	 * @throws SQLException
	 */
	public ResultSetReader(ResultSet rs, int[] columnIndices,
         DialectType dialectType) throws SQLException
	{
		super();
		if (rs == null)
		{
			throw new IllegalArgumentException("ResultSet == null");
		}
		_dialectType = dialectType;
		_rs = rs;

		if (columnIndices != null && columnIndices.length == 0)
		{
			columnIndices = null;
		}
		_columnIndices = columnIndices;

		_rsmd = rs.getMetaData();

		_columnCount = columnIndices != null ? columnIndices.length
            : _rsmd.getColumnCount();
	}

	/**
	 * Read the next row from the <TT>ResultSet</TT>. If no more rows then
	 * <TT>null</TT> will be returned, otherwise an <TT>Object[]</TT> will be
	 * returned where each element of the array is an object representing
	 * the contents of the column. These objects could be of type <TT>String</TT>,
	 * <TT>BigDecimal</TT> etc.
	 *
	 * <P>If an error occurs calling <TT>next()</TT> on the <TT>ResultSet</TT>
	 * then an <TT>SQLException will be thrown, however if an error occurs
	 * retrieving the data for a column an error msg will be placed in that
	 * element of the array, but no exception will be thrown. To see if an
	 * error occured retrieving column data you can call
	 * <TT>getColumnErrorInPreviousRow</TT> after the call to <TT>readRow()</TT>.
	 *
	 * @throws	SQLException	Error occured on <TT>ResultSet.next()</TT>.
	 */
	public Object[] readRow() throws SQLException
	{
		_errorOccured = false;
		if (_rs.next())
		{
			return doRead();
		}
		return null;
	}

	/**
	 * Read the next row from the <TT>ResultSet</TT> for use in the ContentTab.
	 * This is different from readRow() in that data is put into the Object array
	 * in a form controlled by the DataType objects, and may be used for editing
	 * the data and updating the DB. If no more rows then
	 * <TT>null</TT> will be returned, otherwise an <TT>Object[]</TT> will be
	 * returned where each element of the array is an object representing
	 * the contents of the column. These objects could be of type <TT>String</TT>,
	 * <TT>BigDecimal</TT> etc.
	 *
	 * <P>If an error occurs calling <TT>next()</TT> on the <TT>ResultSet</TT>
	 * then an <TT>SQLException will be thrown, however if an error occurs
	 * retrieving the data for a column an error msg will be placed in that
	 * element of the array, but no exception will be thrown. To see if an
	 * error occured retrieving column data you can call
	 * <TT>getColumnErrorInPreviousRow</TT> after the call to <TT>readRow()</TT>.
	 *
	 * @throws	SQLException	Error occured on <TT>ResultSet.next()</TT>.
	 */
	public Object[] readRow(ColumnDisplayDefinition colDefs[]) throws SQLException
	{
		_errorOccured = false;
		if (_rs.next())
		{
			return doContentTabRead(colDefs);
		}
		return null;
	}

	/**
	 * Retrieve whether an error occured reading a column in the previous row.
	 *
	 * @return	<TT>true</TT> if error occured.
	 */
	public boolean getColumnErrorInPreviousRow()
	{
		return _errorOccured;
	}

	/**
     * Attempt to get the column type name - for some drivers this results in an
     * SQLException. Log an INFO message if this is unavailable.
     * 
     * @param idx
     *        the column index to get the type name for.
     * 
     * @return the type name of the column, or the Java class, or "Unavailable"
     *         if neither are available.
     */
	private String safelyGetColumnTypeName(int idx) {
	    String columnTypeName = null;
        try
        {
           /*
            * Fails on DB2 8.1 for Linux 
            * However, Windows 8.1 fixpak 14 driver (2.10.52) works without 
            * exception
            * Also, Linux 9.0.1 server with 3.1.57 driver works fine as well
            */ 
           columnTypeName = _rsmd.getColumnTypeName(idx);
        }
        catch (SQLException e)
        {
           if (s_log.isInfoEnabled()) {
               s_log.info("doRead: ResultSetMetaData.getColumnTypeName("+
                   idx+") threw an unexpected exception - "+e.getMessage());
               s_log.info("Unable to determine column type name so " +
                    "any custom types provided by plugins will be " +
                    "unavailable.  This is a driver bug.");
           }
        }
        if (columnTypeName == null) {
            try {
                columnTypeName = _rsmd.getColumnClassName(idx);
            } catch (SQLException e) {
                if (s_log.isInfoEnabled()) {
                    s_log.info("doRead: ResultSetMetaData.getColumnClassName("+
                        idx+") threw an unexpected exception - "+e.getMessage());
                    
                }
            }
        }
        if (columnTypeName == null) {
            columnTypeName = "Unavailable";
        }
        return columnTypeName;
	}
	
	/**
    * Method used to read data for all Tabs except the ContentsTab, where the
    * data is used only for reading. The only data read in the non-ContentsTab
    * tabs is Meta-data about the DB, which means that there should be no BLOBs,
    * CLOBs, or unknown fields.
    */
   private Object[] doRead()
	{
		Object[] row = new Object[_columnCount];
		for (int i = 0; i < _columnCount && !_stopExecution; ++i)
		{
			int idx = _columnIndices != null ? _columnIndices[i] : i + 1;
			try
			{
				int columnType = _rsmd.getColumnType(idx);
				String columnTypeName = safelyGetColumnTypeName(idx);

            /*
             * See if there is a plugin-registered DataTypeComponent that can
             * handle this column.
             */
            row[i] = 
                CellComponentFactory.readResultWithPluginRegisteredDataType(_rs, 
                                                             columnType, 
                                                             columnTypeName, 
                                                             idx,
                                                             _dialectType);
            if (row[i] == null) {
				
               switch (columnType) {
               case Types.NULL:
                  row[i] = null;
                  break;

               case Types.BIT:
               case Types.BOOLEAN:
                  row[i] = readBoolean(idx);
                  break;

               case Types.TIME:
                  row[i] = _rs.getTime(idx);
                  break;

               case Types.DATE:
                  row[i] = readDate(idx);
                  break;

               case Types.TIMESTAMP:
               case -101: // Oracle's 'TIMESTAMP WITH TIME ZONE' == -101
               case -102: // Oracle's 'TIMESTAMP WITH LOCAL TIME ZONE' ==
                  // -102
                  row[i] = _rs.getTimestamp(idx);
                  break;

               case Types.BIGINT:
                  row[i] = readBigint(idx);
                  break;

               case Types.DOUBLE:
               case Types.FLOAT:
               case Types.REAL:
                  row[i] = readFloat(idx);
                  break;

               case Types.DECIMAL:
               case Types.NUMERIC:
                  row[i] = readNumeric(idx);
                  break;

               case Types.INTEGER:
               case Types.SMALLINT:
               case Types.TINYINT:
                  row[i] = readInt(idx, columnTypeName);
                  break;
               case Types.CHAR:
               case Types.NCHAR:
               case Types.VARCHAR:
               case Types.NVARCHAR:
               case Types.LONGVARCHAR:
               case Types.LONGNVARCHAR:
               case Types.ROWID:
                  row[i] = _rs.getString(idx);
                  if (_rs.wasNull()) {
                     row[i] = null;
                  }
                  break;

               case Types.BINARY:
               case Types.VARBINARY:
               case Types.LONGVARBINARY:
                  row[i] = _rs.getString(idx);
                  break;

               case Types.BLOB:
                  // Since we are reading Meta-data about the DB, we should
                  // never see a BLOB. If we do, the contents are not
                  // interpretable
                  // by Squirrel, so just tell the user that it is a BLOB and
                  // that it
                  // has data.

                  row[i] = DataTypeBlob.staticReadResultSet(_rs, idx);

                  break;

               case Types.CLOB:
                  // Since we are reading Meta-data about the DB, we should
                  // never see a CLOB. However, if we do we assume that
                  // it is printable text and that the user wants to see it, so
                  // read in the entire thing.
                  row[i] = DataTypeClob.staticReadResultSet(_rs, idx);

                  break;

               // TODO: ResultSet has it's own NCLOB support (rs.getNClob(i)).  It is probably not valid to 
               // call getClob on an NClob column ??  So, may need to implement new DataTypeNClob type 
               // component (see below):  
               // 
               //case Types.NCLOB:
               //  row[i] = DataTypeNClob.staticReadResultSet(_rs, idx);
                  
               // Add begin
               case Types.JAVA_OBJECT:
                  row[i] = readObject(idx);
                  break;
               // Add end

               case Types.OTHER:
                  row[i] = readOther(idx);
                  break;

               default:
                  if (row[i] == null) {
                     Integer colTypeInteger = Integer.valueOf(columnType);
                     row[i] = s_stringMgr.getString("ResultSetReader.unknown",
                                                    colTypeInteger);
                  }
               }
            }
			}
			catch (Throwable th)
			{
                // Don't bother the user with details about where the result fetch
                // failed if they cancelled the query.
                if (!_stopExecution) {
                    _errorOccured = true;
                    row[i] = s_stringMgr.getString("ResultSetReader.error");
                    StringBuffer msg = new StringBuffer("Error reading column data");
                    msg.append(", column index = ").append(idx);
                    s_log.error(msg.toString(), th);
                }
			}
		}

		return row;
	}

   private Object readNumeric(int columnIdx) throws SQLException {
      Object result = _rs.getObject(columnIdx);
      if (result != null && !(result instanceof BigDecimal)) {
         if (result instanceof Number) {
            Number nbr = (Number) result;
            result = new BigDecimal(nbr.doubleValue());
         } else {
            result = new BigDecimal(result.toString());
         }
      }
      return result;
   }
   
   private Object readOther(int columnIdx) throws SQLException {
         // Since we are reading Meta-data, there really should
         // never be
       // a field with SQL type Other (1111).
       // If there is, we REALLY do not know how to handle it,
       // so do not attempt to read.
      // ?? if (_largeObjInfo.getReadSQLOther())
      // ?? {
      // ?? // Running getObject on a java class attempts
      // ?? // to load the class in memory which we don't want.
      // ?? // getString() just gets the value without loading
      // ?? // the class (at least under PostgreSQL).
      // ?? //row[i] = _rs.getObject(idx);
      // ?? row[i] = _rs.getString(idx);
      // ?? }
      // ?? else
      // ?? {
      return s_stringMgr.getString("ResultSetReader.other");
      //      ?? }
      
   }
   
   private Object readObject(int columnIdx) throws SQLException {
      Object result = _rs.getObject(columnIdx);
      if (_rs.wasNull()) {
         result = null;
      }
      return result;
   }
   
   /**
	 * Reads the specified column from the resultset field.
	 * 
	 * @param columnIdx
	 *           the index of the column
	 * @param columnTypeName
	 *           the type name of the column according to the ResultSetMetaData
	 * @return an appropriate object for the value that was read
	 * @throws SQLException
	 *            if an exception occurs.
	 */
   private Object readInt(int columnIdx, String columnTypeName) throws SQLException {
      Object result = _rs.getObject(columnIdx);
      if (_rs.wasNull()) {
         return null;
      }
      
      /*
		 * Bug 1968270 (Displaying unsigned INT as signed INT in column?)
		 * 
		 * If we are working with a signed integer, then it should be ok to store in a Java integer which is 
		 * always signed.  However, if we are working with an unsigned integer type, Java doesn't have this so 
		 * use a long instead. Or if the type of the object is Long then use that instead of Integer.
		 */
      if (result instanceof Long) {
      	return result;
      }
      if ("INTEGER UNSIGNED".equalsIgnoreCase(columnTypeName)) {
      	return Long.valueOf(result.toString());
      }
      
      if (result instanceof Integer) {
      	return result;
      }
      if (result instanceof Number) {
         	Number resultNumber = (Number)result;
         	int intValue = resultNumber.intValue();
            return Integer.valueOf( intValue );
      } 
      
      return Integer.valueOf(result.toString());
   }
   
   private Object readFloat(int columnIdx) throws SQLException {
      Object result = _rs.getObject(columnIdx);
      if (result != null && !(result instanceof Double)) {
         if (result instanceof Number) {
            Number nbr = (Number) result;
            result = new Double(nbr.doubleValue());
         } else {
            result = new Double(result.toString());
         }
      }
      return result;
   }
   
   private Object readDate(int columnIdx) throws SQLException {
      Object result = null;
      if (DataTypeDate.getReadDateAsTimestamp()) {
         result = _rs.getTimestamp(columnIdx);
      } else {
         result = DataTypeDate.staticReadResultSet(_rs, columnIdx, false);
      }
      return result;
   }
   
   private Object readBigint(int columnIdx) throws SQLException {
      Object result = _rs.getObject(columnIdx);
      if (result != null
         && !(result instanceof Long))
      {
         if (result instanceof Number)
         {
            result = Long.valueOf(((Number)result).longValue());
         }
         else
         {
            result = Long.valueOf(result.toString());
         }
      }
      return result;
   }
   
   private Object readBoolean(int columnIdx) throws SQLException {
      Object result = null;
      result = _rs.getObject(columnIdx);

      if (result != null && !(result instanceof Boolean)) {
         if (result instanceof Number) {
            if (((Number) result).intValue() == 0) {
               result = Boolean.FALSE;
            } else {
               result = Boolean.TRUE;
            }
         } else {
            result = Boolean.valueOf(result.toString());
         }
      }
      return result;
   }
   
	/**
    * Method used to read data for the ContentsTab, where the data is used for
    * both reading and editing.
    */
	private Object[] doContentTabRead(ColumnDisplayDefinition colDefs[])
	{
		Object[] row = new Object[_columnCount];
		for (int i = 0; i < _columnCount && !_stopExecution; ++i)
		{
			int idx = _columnIndices != null ? _columnIndices[i] : i + 1;
			try
			{
				final int columnType = _rsmd.getColumnType(idx);
				//final String columnClassName = _rsmd.getColumnClassName(idx);
				switch (columnType)
				{
					case Types.NULL:
						row[i] = null;
						break;

					// all of the following have been converted to use the DataType objects
                    // So, why not just have case Types.NULL and default??? (this seems pointless)
                    // RMM 20070726
					case Types.BIT:
					case Types.BOOLEAN:

					case Types.DECIMAL:
					case Types.NUMERIC:

					case Types.INTEGER:
					case Types.SMALLINT:
					case Types.TINYINT:
					case Types.BIGINT :

					case Types.DOUBLE:
					case Types.FLOAT:
					case Types.REAL:

					case Types.DATE :
					case Types.TIME :
					case Types.TIMESTAMP :

					// TODO: Hard coded -. JDBC/ODBC bridge JDK1.4
					// brings back -9 for nvarchar columns in
					// MS SQL Server tables.
					// -8 is ROWID in Oracle.
					case Types.CHAR:
					case Types.VARCHAR:
					case Types.LONGVARCHAR:
					case -9:
					case -8:

					// binary types
					case Types.BINARY:
					case Types.VARBINARY:
					case Types.LONGVARBINARY:

					case Types.CLOB:
					case Types.BLOB:

					case Types.OTHER:

					default:
						row[i] = CellComponentFactory.readResultSet(
								colDefs[i], _rs, idx, true);

						break;

				}
			}
			catch (Throwable th)
			{
				_errorOccured = true;
				row[i] = s_stringMgr.getString("ResultSetReader.error");
                if (!_stopExecution) {
                    StringBuffer msg = new StringBuffer("Error reading column data");
                    msg.append(", column index = ").append(idx);
                    s_log.error(msg.toString(), th);
                }
			}
		}

		return row;
	}

    /**
     * @param _stopExecution The _stopExecution to set.
     */
    public void setStopExecution(boolean _stopExecution) {
        this._stopExecution = _stopExecution;
    }

    /**
     * @return Returns the _stopExecution.
     */
    public boolean isStopExecution() {
        return _stopExecution;
    }
}
