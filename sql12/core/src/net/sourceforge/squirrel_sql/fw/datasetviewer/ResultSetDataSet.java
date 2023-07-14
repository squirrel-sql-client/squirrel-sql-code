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

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.ResultSetReader;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultSetDataSet implements IDataSet
{
   private final static ILogger s_log = LoggerController.createLogger(ResultSetDataSet.class);

   // TODO: These 2 should be handled with an Iterator.
   private int _iCurrent = -1;

   private Object[] _currentRow;

   private int _columnCount;

   private DataSetDefinition _dataSetDefinition;

   private List<Object[]> _alData;

   /**
    * If <TT>true</TT> cancel has been requested.
    */
   private volatile boolean _cancel = false;

   /**
    * the result set reader, which we will notify of cancel requests
    */
   private ResultSetReader _rdr = null;

   /**
    * The type of dialect of the session from which this data set came.
    * Plugins can now override behavior for standard SQL types, so
    * it is necessary to know the current dialect so that the correct plugin
    * DataTypeComponent can be chosen for rendering this dataset, if one has
    * been registered.
    */
   private DialectType _dialectType = null;

   private final TableColumnInfo[] tableColumnInfos;

   private boolean _limitDataRead = false;

   /**
    * Default constructor.
    *
    * @param tableColumnInfos
    */
   public ResultSetDataSet(TableColumnInfo[] tableColumnInfos)
   {
      this.tableColumnInfos = tableColumnInfos;
   }

   public ResultSetDataSet()
   {
      this.tableColumnInfos = new TableColumnInfo[]{};
   }

   /**
    * Form used by Tabs other than ContentsTab
    *
    * @param rs          the ResultSet to set.
    * @param dialectType the type of dialect in use.
    * @throws DataSetException
    */
   public int setResultSet(ResultSet rs, DialectType dialectType) throws DataSetException
   {
      return _setResultSet(new ResultSetWrapper(rs), null, null, false, false, dialectType);
   }


   /**
    * Content Tab may wish to limit data read for big columns.
    *
    * @param limitDataRead
    */
   public void setLimitDataRead(boolean limitDataRead)
   {
      this._limitDataRead = limitDataRead;
   }

   /**
    * Form used by ContentsTab, and for SQL results
    *
    * @param rs            the ResultSet to set.
    * @param fullTableName the fully-qualified table name
    * @param dialectType   the type of dialect in use.
    * @throws DataSetException
    */
   public int setContentsTabResultSet(ResultSet rs, String fullTableName, DialectType dialectType) throws DataSetException
   {
      return _setResultSet(new ResultSetWrapper(rs), fullTableName, null, false, true, dialectType);
   }

   public int setSqlExecutionTabResultSet(ResultSetWrapper rs, String fullTableName, DialectType dialectType) throws DataSetException
   {
      return _setResultSet(rs, fullTableName, null, false, true, dialectType);
   }


   /**
    * External method to read the contents of a ResultSet that is used by all
    * Tab classes except ContentsTab. This tunrs all the data into strings for
    * simplicity of operation.
    */
   public int setResultSet(ResultSet rs, int[] columnIndices, boolean computeWidths, DialectType dialectType) throws DataSetException
   {
      return _setResultSet(new ResultSetWrapper(rs), null, columnIndices, computeWidths, false, dialectType);
   }

   /**
    * Internal method to read the contents of a ResultSet that is used by all
    * Tab classes
    *
    * @return The number of rows read from the ResultSet
    */
   private int _setResultSet(ResultSetWrapper rs,
                             String fullTableName,
                             int[] columnIndices,
                             boolean computeWidths,
                             boolean useColumnDefs,
                             DialectType dialectType) throws DataSetException
   {
      reset();
      _dialectType = dialectType;

      if (columnIndices != null && columnIndices.length == 0)
      {
         columnIndices = null;
      }

      _iCurrent = -1;
      _alData = new ArrayList<>();

      if (rs == null)
      {
         return 0;
      }

      try
      {
         ResultSetMetaData md = rs.getResultSet().getMetaData();
         _columnCount = columnIndices != null ? columnIndices.length : md.getColumnCount();

         // Done before actually reading the data from the ResultSet. If done
         // after
         // reading the data from the ResultSet Oracle throws a
         // NullPointerException
         // when processing ResultSetMetaData methods for the ResultSet
         // returned for
         // DatabasemetaData.getExportedKeys.
         ColumnDisplayDefinition[] colDefs = createColumnDefinitions(md, fullTableName, columnIndices, computeWidths);


         _dataSetDefinition = new DataSetDefinition(colDefs, columnIndices);

         // Read the entire row, since some drivers complain if columns are
         // read out of sequence
         _rdr = new ResultSetReader(rs, dialectType);

         for (; ; )
         {
            if (_cancel)
            {
               return _alData.size();
            }

            Object[] row = createRow(columnIndices, useColumnDefs, colDefs, BlockMode.FIRST_BLOCK);

            if (null == row)
            {
               break;
            }
            else
            {
               _alData.add(row);
            }
         }

         return _alData.size();

         // ColumnDisplayDefinition[] colDefs = createColumnDefinitions(md,
         // columnIndices, computeWidths);
         // _dataSetDefinition = new DataSetDefinition(colDefs);
      }
      catch (SQLException ex)
      {
         // Don't log an error message here. It is possible that the user
         // interrupted the query because it was taking too long. Just
         // throw the exception, and let the caller decide whether or not
         // the exception should be logged.
         throw new DataSetException(ex);
      }
   }

   private Object[] createRow(int[] columnIndices, boolean useColumnDefs, ColumnDisplayDefinition[] colDefs, BlockMode blockMode) throws SQLException
   {
      Object[] row;

      if (useColumnDefs)
      {
         row = _rdr.readRow(colDefs, blockMode, _limitDataRead);
      }
      else
      {
         row = _rdr.readRow(blockMode);
      }

      if (row == null)
      {
         return null;
      }


      // Now reorder columns.
      // This is used by ObjecTree tabs to define the
      // order columns displaying connection meta data are displayed.
      if (columnIndices != null)
      {
         Object[] newRow = new Object[_columnCount];
         for (int i = 0; i < _columnCount; i++)
         {
            if (columnIndices[i] - 1 < row.length)
            {
               newRow[i] = row[columnIndices[i] - 1];
            }
            else
            {
               newRow[i] = "Unknown";
            }
         }
         row = newRow;
      }
      return row;
   }

   @Override
   public final int getColumnCount()
   {
      return _columnCount;
   }

   @Override
   public DataSetDefinition getDataSetDefinition()
   {
      return _dataSetDefinition;
   }

   @Override
   public synchronized boolean next(IMessageHandler msgHandler)
         throws DataSetException
   {
      // TODO: This should be handled with an Iterator
      if (++_iCurrent < _alData.size())
      {
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
   @Override
   public Object get(int columnIndex)
   {
      if (_currentRow != null)
      {
         return _currentRow[columnIndex];
      }
      else
      {
         return null;
      }
   }

   public void cancelProcessing()
   {
      _rdr.setStopExecution(true);
      _cancel = true;
   }

   // SS: Modified to auto-compute column widths if <computeWidths> is true
   private ColumnDisplayDefinition[] createColumnDefinitions(
         ResultSetMetaData md, String fullTableName, int[] columnIndices,
         boolean computeWidths) throws SQLException
   {
      // TODO?? ColumnDisplayDefinition should also have the Type (String, Date,
      // Double,Integer,Boolean)
      int[] colWidths = null;

      // SS: update dynamic column widths
      if (computeWidths)
      {
         colWidths = new int[_columnCount];
         for (int i = 0; i < _alData.size(); i++)
         {
            Object[] row = _alData.get(i);
            for (int col = 0; i < _columnCount; i++)
            {
               if (row[col] != null)
               {
                  int colWidth = row[col].toString().length();
                  if (colWidth > colWidths[col])
                  {
                     colWidths[col] = colWidth + 2;
                  }
               }
            }
         }
      }


      ColumnDisplayDefinition[] columnDefs = new ColumnDisplayDefinition[_columnCount];
      for (int i = 0; i < _columnCount; ++i)
      {
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
         try
         {
            precis = md.getPrecision(idx);
         }
         catch (NumberFormatException ignore)
         {
            precis = Integer.MAX_VALUE; // Oracle throws this ex on BLOB data
            // types
         }

         boolean isSigned = true;
         try
         {
            isSigned = md.isSigned(idx); // HSQLDB 1.7.1 throws error.
         }
         catch (SQLException ignore)
         {
            // Empty block
         }

         boolean isCurrency = false;

         try
         {
            // Matt Dahlman: this causes problems with the JDBC driver delivered
            // with Teradata V2R05.00.00.11
            isCurrency = md.isCurrency(idx);
         }
         catch (SQLException e)
         {
            s_log.error("Failed to call ResultSetMetaData.isCurrency()", e);
         }

         boolean isAutoIncrement = false;
         try
         {
            isAutoIncrement = md.isAutoIncrement(idx);
         }
         catch (SQLException e)
         {
            s_log.error("Failed to call ResultSetMetaData.isAutoIncrement()", e);
         }

         // KLUDGE:
         // We want some info about the columns to be available for validating the
         // user input during cell editing operations.  Ideally we would get that
         // info inside the ResultSetDataSet class during the creation of the
         // columnDefinition objects by using various functions in ResultSetMetaData
         // such as isNullable(idx).  Unfortunately, in at least some DBMSs (e.g.
         // Postgres, HSDB) the results of those calls are not the same (and are less accurate
         // than) the SQLMetaData.getColumns() call used in ColumnsTab to get the column info.
         // Even more unfortunate is the fact that the set of attributes reported on by the two
         // calls is not the same, with the ResultSetMetadata listing things not provided by
         // getColumns.  Most of the data provided by the ResultSetMetaData calls is correct.
         // However, the nullable/not-nullable property is not set correctly in at least two
         // DBMSs, while it is correct for those DBMSs in the getColumns() info.  Therefore,
         // we collect the collumn nullability information from getColumns() and pass that
         // info to the ResultSet to override what it got from the ResultSetMetaData.

         if (i < tableColumnInfos.length)
         {
            TableColumnInfo info = tableColumnInfos[i];
            if (info.isNullAllowed() == DatabaseMetaData.columnNoNulls)
            {
               isNullable = false;
            }
         }

         String columnName = getColumnName(i, md, idx);
         String columnTypeName = getColumnTypeName(i, md, idx);
         int baseColumnType = getColumnType(i, md, idx);
         int columnType = fixColumnType(columnName, baseColumnType, columnTypeName, _dialectType);

         columnDefs[i] = new ColumnDisplayDefinition(computeWidths ? colWidths[i] : Math.min(md.getColumnDisplaySize(idx), 1000),
               fullTableName + ":" + md.getColumnLabel(idx),
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
               _dialectType,
               createResultSetMetaDataTable(md, idx));
      }
      return columnDefs;
   }

   private ResultMetaDataTable createResultSetMetaDataTable(ResultSetMetaData md, int idx)
   {
      try
      {
         if (StringUtilities.isEmpty(md.getTableName(idx)))
         {
            return null;
         }

         return new ResultMetaDataTable(StringUtilities.emptyToNull(md.getCatalogName(idx)), StringUtilities.emptyToNull(md.getSchemaName(idx)), md.getTableName(idx));
      }
      catch (Exception e)
      {
         s_log.error("Failed to get table info from ResultSetMetaData.", e);
         return null;
      }
   }


   private String getColumnName(int i, ResultSetMetaData md, int idx) throws SQLException
   {
      if (i < tableColumnInfos.length)
      {
         return tableColumnInfos[i].getColumnName();
      }
      return md.getColumnName(idx);
   }

   private String getColumnTypeName(int i, ResultSetMetaData md, int idx) throws SQLException
   {
      if (i < tableColumnInfos.length)
      {
         return tableColumnInfos[i].getTypeName();
      }
      return md.getColumnTypeName(idx);
   }

   private int getColumnType(int i, ResultSetMetaData md, int idx) throws SQLException
   {
      if (i < tableColumnInfos.length)
      {
         return tableColumnInfos[i].getDataType();
      }
      return md.getColumnType(idx);
   }

   /**
    * The following is a synopsis of email conversations with David Crawshaw, who maintains the SQLite JDBC
    * driver:
    * <p>
    * SQLite's JDBC driver returns Types.NULL as the column type if the table has no rows.  Columns don't
    * necessarily have a type attribute; the type is associated with the values in the column (this is
    * referred to as manifest typing).  Columns can have an affinity (a preferred storage option) which
    * looks just like a type in the create table statement; however, it can be whatever the user chooses, and
    * not necessarily a standard SQL type.  Even still, SQLite exposes no API call to retrieve the column
    * affinity (or storage clause).  However, it does make the type name that the user used available and that
    * may possibly be a valid standard SQL type.
    * <p>
    * So, if the specified column type code is Types.NULL, this method attempts to adjust the type code from
    * Types.NULL to a sensible Type based on the column type name reported by the driver.  If the column type
    * name doesn't match (ignoring case) an existing JDBC type, then this method returns Types.VARCHAR.
    *
    * For the same reason as described above, numeric types are interpreted as INTEGER, when they might be
    * NUMERIC and actually overflow.
    *
    * @param columnName     the name of the column
    * @param columnType     the type code that was given by the jdbc driver.
    * @param columnTypeName the type name of the column that was given by the jdbc driver
    * @param dialectType
    * @return a type code that is not Types.NULL.
    */
   private int fixColumnType(String columnName, int columnType, String columnTypeName, DialectType dialectType)
   {
      int result = columnType;
      if (columnType == Types.NULL)
      {
         result = JDBCTypeMapper.getJdbcType(columnTypeName);
         if (result == Types.NULL)
         {
            result = Types.VARCHAR;
         }
      }
      else if ( DialectType.SQLLITE == dialectType )
      {
         if (columnType == Types.INTEGER && "NUMERIC".equals(columnTypeName))
         {
            result = Types.NUMERIC;
         }
      }

      if (result != columnType)
      {
         if (s_log.isDebugEnabled())
         {
            s_log.debug("Converting type code for column " + columnName +
                  ". Original column type code and name were " + columnType + " (see code-constants in java.sql.Types) and " + columnTypeName +
                  "; New type code is " + result);
         }
      }
      return result;
   }

   private void reset()
   {
      _iCurrent = -1;
      _currentRow = null;
      _columnCount = 0;
      _dataSetDefinition = null;
      _alData = null;
   }

   public void resetCursor()
   {
      _iCurrent = -1;
      _currentRow = null;
   }

   /**
    * Removes the row at the specified index.
    *
    * @param index the row number starting at 0.
    * @return the object at the specified row or null if there is not row at the
    * specified index.
    */
   public Object removeRow(int index)
   {
      if (_alData.size() > index)
      {
         return _alData.remove(index);
      }
      else
      {
         return null;
      }
   }

   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();
      if (_dataSetDefinition != null)
      {
         for (ColumnDisplayDefinition colDef : _dataSetDefinition.getColumnDefinitions())
         {
            String columnName = "Column";
            if (colDef != null)
            {
               columnName = colDef.getColumnName();
            }
            result.append(columnName);
            result.append("\t");
         }
         result.append("\n");
      }


      for (Object[] row : _alData)
      {
         for (Object rowItem : row)
         {
            if (rowItem == null)
            {
               result.append(StringUtilities.NULL_AS_STRING);
            }
            else
            {
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

   public void readMoreResults()
   {
      try
      {
         for (; ; )
         {
            Object[] row = createRow(_dataSetDefinition.getColumnIndices(), true, _dataSetDefinition.getColumnDefinitions(), BlockMode.FOLLOW_UP_BLOCK);
            if (null == row)
            {
               break;
            }
            else
            {
               _alData.add(row);
            }
         }

         resetCursor();

      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   public int currentRowCount()
   {
      return _alData.size();
   }

   public boolean isAllResultsRead()
   {
      return _rdr.isAllResultsRead();
   }

   public boolean areAllPossibleResultsOfSQLRead()
   {
      return _rdr.areAllPossibleResultsOfSQLRead();
   }

   public boolean isResultLimitedByMaxRowsCount()
   {
      return _rdr.isResultLimitedByMaxRowsCount();
   }

   public void closeStatementAndResultSet()
   {
      _rdr.closeStatementAndResultSet();
   }

   public void replaceDataOnUserEdits(ArrayList<Object[]> updatedRows)
   {
      resetCursor();
      _alData = updatedRows;
   }
}
