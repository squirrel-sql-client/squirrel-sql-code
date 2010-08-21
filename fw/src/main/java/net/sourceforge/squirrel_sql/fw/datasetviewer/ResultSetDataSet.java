package net.sourceforge.squirrel_sql.fw.datasetviewer;

/*
 * Copyright (C) 2001-2003 Colin Bell
 * colbell@users.sourceforge.net
 * Copyright (C) 2001-2003 Johan Compagner
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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class ResultSetDataSet implements IDataSet {
   private final static ILogger s_log = LoggerController.createLogger(ResultSetDataSet.class);

   // TODO: These 2 should be handled with an Iterator.
   private int _iCurrent = -1;

   private Object[] _currentRow;

   private int _columnCount;

   private DataSetDefinition _dataSetDefinition;

   private List<Object[]> _alData;

   /** If <TT>true</TT> cancel has been requested. */
   private volatile boolean _cancel = false;

   /** the result set reader, which we will notify of cancel requests */
   private ResultSetReader rdr = null;

   /**
    * The type of dialect of the session from which this data set came.  
    * Plugins can now override behavior for standard SQL types, so
    * it is necessary to know the current dialect so that the correct plugin 
    * DataTypeComponent can be chosen for rendering this dataset, if one has 
    * been registered. 
    */   
   private DialectType _dialectType = null;

   /**
    * Default constructor.
    */
   public ResultSetDataSet() {
      super();
   }

   /**
    * Form used by Tabs other than ContentsTab
    * 
    * @param rs
    *           the ResultSet to set.
    * @param dialectType
    *           the type of dialect in use.
    * @throws DataSetException
    */
   public int setResultSet(ResultSet rs, DialectType dialectType)
         throws DataSetException {
      return setResultSet(rs, null, false, dialectType);
   }

   /**
    * Form used by ContentsTab, and for SQL results
    * 
    * @param rs
    *           the ResultSet to set.
    * @param fullTableName
    *           the fully-qualified table name
    * @param dialectType
    *           the type of dialect in use.
    * @throws DataSetException
    */
   public int setContentsTabResultSet(ResultSet rs, String fullTableName,
         DialectType dialectType) throws DataSetException {
      return setResultSet(rs, fullTableName, null, false, true, dialectType);
   }

   /**
    * Sets the ResultSet that contains the data
    * 
    * @param rs
    *           the ResultSet to set.
    * @param columnIndices
    *           columns to read from the specified ResultSet
    * @param dialectType
    *           the type of dialect in use.
    * @throws DataSetException
    */
   public int setResultSet(ResultSet rs, int[] columnIndices,
         DialectType dialectType) throws DataSetException {
      return setResultSet(rs, columnIndices, false, dialectType);
   }

   /**
    * External method to read the contents of a ResultSet that is used by all
    * Tab classes except ContentsTab. This tunrs all the data into strings for
    * simplicity of operation.
    */
   public int setResultSet(ResultSet rs, int[] columnIndices,
         boolean computeWidths, DialectType dialectType) throws DataSetException {
      return setResultSet(rs, null, columnIndices, computeWidths, false, dialectType);
   }

   /**
    * Internal method to read the contents of a ResultSet that is used by all
    * Tab classes
    *
    * @return The number of rows read from the ResultSet
    *
    */
   private int setResultSet(ResultSet rs, String fullTableName,
         int[] columnIndices, boolean computeWidths, boolean useColumnDefs,
         DialectType dialectType) throws DataSetException {
      reset();
      _dialectType = dialectType;
      if (columnIndices != null && columnIndices.length == 0) {
         columnIndices = null;
      }
      _iCurrent = -1;
      _alData = new ArrayList<Object[]>();

      if (rs == null)
      {
         return 0;
      }

      try {
         ResultSetMetaData md = rs.getMetaData();
         _columnCount = columnIndices != null ? columnIndices.length
               : md.getColumnCount();

         // Done before actually reading the data from the ResultSet. If done
         // after
         // reading the data from the ResultSet Oracle throws a
         // NullPointerException
         // when processing ResultSetMetaData methods for the ResultSet
         // returned for
         // DatabasemetaData.getExportedKeys.
         ColumnDisplayDefinition[] colDefs = createColumnDefinitions(md,
                                                                     fullTableName,
                                                                     columnIndices,
                                                                     computeWidths);
         _dataSetDefinition = new DataSetDefinition(colDefs);

         // Read the entire row, since some drivers complain if columns are
         // read out of sequence
         rdr = new ResultSetReader(rs, dialectType);
         Object[] row = null;

         while (true) {
            if (useColumnDefs)
               row = rdr.readRow(colDefs);
            else
               row = rdr.readRow();

            if (row == null)
               break;

            if (_cancel) {
               return _alData.size();
            }

            // SS: now select/reorder columns
            if (columnIndices != null) {
               Object[] newRow = new Object[_columnCount];
               for (int i = 0; i < _columnCount; i++) {
                  if (columnIndices[i] - 1 < row.length) {
                     newRow[i] = row[columnIndices[i] - 1];
                  } else {
                     newRow[i] = "Unknown";
                  }
               }
               row = newRow;
            }
            _alData.add(row);
         }

         return _alData.size();

         // ColumnDisplayDefinition[] colDefs = createColumnDefinitions(md,
         // columnIndices, computeWidths);
         // _dataSetDefinition = new DataSetDefinition(colDefs);
      } catch (SQLException ex) {
         // Don't log an error message here. It is possible that the user
         // interrupted the query because it was taking too long. Just
         // throw the exception, and let the caller decide whether or not
         // the exception should be logged.
         throw new DataSetException(ex);
      }
   }

   public final int getColumnCount() {
      return _columnCount;
   }

   public DataSetDefinition getDataSetDefinition() {
      return _dataSetDefinition;
   }

   public synchronized boolean next(IMessageHandler msgHandler)
         throws DataSetException {
      // TODO: This should be handled with an Iterator
      if (++_iCurrent < _alData.size()) {
         _currentRow = _alData.get(_iCurrent);
         return true;
      }
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSet#get(int)
    */
   public Object get(int columnIndex) {
      if (_currentRow != null) {
         return _currentRow[columnIndex];
      } else {
         return null;
      }
   }

   public void cancelProcessing() {
      rdr.setStopExecution(true);
      _cancel = true;
   }

   // SS: Modified to auto-compute column widths if <computeWidths> is true
   private ColumnDisplayDefinition[] createColumnDefinitions(
         ResultSetMetaData md, String fullTableName, int[] columnIndices,
         boolean computeWidths) throws SQLException {
      // TODO?? ColumnDisplayDefinition should also have the Type (String, Date,
      // Double,Integer,Boolean)
      int[] colWidths = null;

      // SS: update dynamic column widths
      if (computeWidths) {
         colWidths = new int[_columnCount];
         for (int i = 0; i < _alData.size(); i++) {
            Object[] row = _alData.get(i);
            for (int col = 0; i < _columnCount; i++) {
               if (row[col] != null) {
                  int colWidth = row[col].toString().length();
                  if (colWidth > colWidths[col]) {
                     colWidths[col] = colWidth + 2;
                  }
               }
            }
         }
      }

      ColumnDisplayDefinition[] columnDefs = new ColumnDisplayDefinition[_columnCount];
      for (int i = 0; i < _columnCount; ++i) {
         int idx = columnIndices != null ? columnIndices[i] : i + 1;

         // save various info about the column for use in user input validation
         // when editing table contents.
         // Note that the columnDisplaySize is included two times, where the
         // first
         // entry may be adjusted for actual display while the second entry is
         // the
         // size expected by the DB.
         // The isNullable() method returns three values that we convert into
         // two
         // by saying that if it is not known whether or not a column allows
         // nulls,
         // we will allow the user to enter nulls and any problems will be
         // caught
         // when they try to save the data to the DB
         boolean isNullable = true;
         if (md.isNullable(idx) == ResultSetMetaData.columnNoNulls)
            isNullable = false;

         int precis;
         try {
            precis = md.getPrecision(idx);
         } catch (NumberFormatException ignore) {
            precis = Integer.MAX_VALUE; // Oracle throws this ex on BLOB data
                                          // types
         }

         boolean isSigned = true;
         try {
            isSigned = md.isSigned(idx); // HSQLDB 1.7.1 throws error.
         } catch (SQLException ignore) {
            // Empty block
         }

         boolean isCurrency = false;

         try {
            // Matt Dahlman: this causes problems with the JDBC driver delivered
            // with Teradata V2R05.00.00.11
            isCurrency = md.isCurrency(idx);
         } catch (SQLException e) {
            s_log.error("Failed to call ResultSetMetaData.isCurrency()", e);
         }

         boolean isAutoIncrement = false;
         try {
            isAutoIncrement = md.isAutoIncrement(idx);
         } catch (SQLException e) {
            s_log.error("Failed to call ResultSetMetaData.isAutoIncrement()", e);
         }
         
         String columnName = md.getColumnName(idx);
         String columnTypeName = md.getColumnTypeName(idx);
         int columnType = fixColumnType(columnName, md.getColumnType(idx), columnTypeName);
         
         columnDefs[i] = new ColumnDisplayDefinition(computeWidths ? colWidths[i]
                                                           : md.getColumnDisplaySize(idx),
                                                     fullTableName
                                                           + ":"
                                                           + md.getColumnLabel(idx),
                                                     columnName,
                                                     md.getColumnLabel(idx),
                                                     columnType,
                                                     columnTypeName,
                                                     isNullable,
                                                     md.getColumnDisplaySize(idx),
                                                     precis,
                                                     md.getScale(idx),
                                                     isSigned,
                                                     isCurrency,
                                                     isAutoIncrement,
                                                     _dialectType);
      }
      return columnDefs;
   }

   /**
    * The following is a synopsis of email conversations with David Crawshaw, who maintains the SQLite JDBC 
    * driver: 
    * 
    * SQLite's JDBC driver returns Types.NULL as the column type if the table has no rows.  Columns don't 
    * necessarily have a type attribute; the type is associated with the values in the column (this is 
    * referred to as manifest typing).  Columns can have an affinity (a preferred storage option) which 
    * looks just like a type in the create table statement; however, it can be whatever the user chooses, and 
    * not necessarily a standard SQL type.  Even still, SQLite exposes no API call to retrieve the column 
    * affinity (or storage clause).  However, it does make the type name that the user used available and that
    * may possibly be a valid standard SQL type.  
    * 
    * So, if the specified column type code is Types.NULL, this method attempts to adjust the type code from 
    * Types.NULL to a sensible Type based on the column type name reported by the driver.  If the column type 
    * name doesn't match (ignoring case) an existing JDBC type, then this method returns Types.VARCHAR.  
    * 
    * @param columnName the name of the column
    * @param columnType the type code that was given by the jdbc driver.
    * @param columnTypeName the type name of the column that was given by the jdbc driver
    * 
    * @return a type code that is not Types.NULL.
    */
   private int fixColumnType(String columnName, int columnType, String columnTypeName) {
   	int result = columnType;
   	if (columnType == Types.NULL) {
   		result = JDBCTypeMapper.getJdbcType(columnTypeName);
   		if (result == Types.NULL) {
   			result = Types.VARCHAR;
   		}
   	}
   	if (result != columnType) {
			if (s_log.isDebugEnabled()) {
				s_log.debug("Converting type code for column "+columnName+
					". Original column type code and name were Types.NULL and "+columnTypeName+
					"; New type code is "+JDBCTypeMapper.getJdbcTypeName(result));
			}   		
   	}
   	return result;
   }
   
   private void reset() {
      _iCurrent = -1;
      _currentRow = null;
      _columnCount = 0;
      _dataSetDefinition = null;
      _alData = null;
   }

   public void resetCursor() {
      _iCurrent = -1;
      _currentRow = null;
   }

   /**
    * Removes the row at the specified index. 
    * 
    * @param index the row number starting at 0.
    * @return the object at the specified row or null if there is not row at the
    *         specified index.
    */
   public Object removeRow(int index) {
      if (_alData.size() > index) {
         return _alData.remove(index);
      } else {
         return null;
      }
   }
   
   public String toString() {
   	StringBuilder result = new StringBuilder();
   	if (_dataSetDefinition != null) {
   		for (ColumnDisplayDefinition colDef : _dataSetDefinition.getColumnDefinitions()) {
   			String columnName = "Column";
   			if (colDef != null) {
   				columnName = colDef.getColumnName();
   			} 
   			result.append(columnName);
   			result.append("\t");
   		}
   		result.append("\n");
   	}
   	
   	
   	for (Object[] row : _alData) {
   		for (Object rowItem : row) {
   			if (rowItem == null) {
   				result.append("<null>");
   			} else {
   				result.append(rowItem.toString());
   			}
   			result.append("\t");
   		}
   		result.append("\n");
   	}
   	return result.toString();
   }


   public List<Object[]> getAllDataForReadOnly()
   {
      return _alData;
   }
}
