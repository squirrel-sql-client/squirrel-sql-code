package net.sourceforge.squirrel_sql.client.session.action.dataimport;
/*
 * Copyright (C) 2007 Thorsten Mürell
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.session.ExtendedColumnInfo;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.gui.ColumnMappingTableModel;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.gui.SpecialColumnMapping;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.IFileImporter;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.importer.UnsupportedFormatException;
import net.sourceforge.squirrel_sql.client.session.action.dataimport.util.DateUtils;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * This class does the main work for importing the file into the database.
 *
 * @author Thorsten Mürell
 */
public class ImportDataIntoTableExecutor
{
   private final static ILogger s_log = LoggerController.createLogger(ImportDataIntoTableExecutor.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ImportDataIntoTableExecutor.class);

   private ISession _session;
   private ITableInfo _table;
   private ExtendedColumnInfo[] _columns;
   private ColumnMappingTableModel _columnMappingModel;
   private IFileImporter _importer;
   private List<String> _importerColumns;
   private boolean skipHeader = false;

   private final boolean _singleTransaction;
   private final int _commitAfterEveryInserts;
   private boolean _deleteExistingData;


   /**
    * The standard constructor
    *
    * @param session                 The session
    * @param table                   The table to import into
    * @param columns                 The columns of the destination table
    * @param importerColumns         The columns of the importer
    * @param mapping                 The mapping of the columns
    * @param importer                The file importer
    * @param singleTransaction
    * @param commitAfterEveryInserts
    * @param selected
    */
   public ImportDataIntoTableExecutor(ISession session,
                                      ITableInfo table,
                                      ExtendedColumnInfo[] columns,
                                      List<String> importerColumns,
                                      ColumnMappingTableModel mapping,
                                      IFileImporter importer,
                                      boolean singleTransaction,
                                      int commitAfterEveryInserts,
                                      boolean deleteExistingData)
   {
      _session = session;
      _table = table;
      _columns = columns;
      _columnMappingModel = mapping;
      _importer = importer;
      _importerColumns = importerColumns;

      _singleTransaction = singleTransaction;
      _commitAfterEveryInserts = commitAfterEveryInserts;
      _deleteExistingData = deleteExistingData;
   }

   /**
    * If the header should be skipped
    *
    * @param skip
    */
   public void setSkipHeader(boolean skip)
   {
      skipHeader = skip;
   }

   /**
    * Starts the thread that executes the insert operation.
    */
   public void execute()
	{
      ImportProgressCtrl importProgressCtrl = new ImportProgressCtrl(_table);

      Thread execThread = new Thread(() -> _execute(_singleTransaction, _commitAfterEveryInserts, _deleteExistingData, importProgressCtrl));
		execThread.setName("Dataimport Executor Thread");
		execThread.setUncaughtExceptionHandler(createUncaughtExceptionHandler());
		execThread.start();
	}

   /**
    * Performs the table copy operation.
    *  @param singleTransaction
    * @param commitAfterEveryInserts
    * @param deleteExistingData
    * @param importProgressCtrl
    */
   private void _execute(boolean singleTransaction, int commitAfterEveryInserts, boolean deleteExistingData, ImportProgressCtrl importProgressCtrl)
   {
      ISQLConnection conn = _session.getSQLConnection();

      StringBuffer insertSQL = new StringBuffer();
      insertSQL.append("insert into ").append(_table.getQualifiedName());
      insertSQL.append(" (").append(createColumnList()).append(") ");
      insertSQL.append("VALUES ");
      insertSQL.append(" (").append(getQuestionMarks(_columnMappingModel.getColumnCountExcludingSkipped(_columns))).append(")");

      PreparedStatement stmt = null;
      int currentRow = 0;
      boolean success = false;

      boolean originalAutoCommit = getOriginalAutoCommit(conn);

      try
      {
         conn.setAutoCommit(false);

         _importer.open();

         if (skipHeader)
         {
            _importer.next();
         }

         if (deleteExistingData)
         {
            String sql = "DELETE FROM " + _table.getQualifiedName();
            stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
            stmt.close();
         }

         stmt = conn.prepareStatement(insertSQL.toString());

         while (_importer.next())
         {

            if(importProgressCtrl.isCanceled())
            {
               break;
            }

            currentRow++;

            importProgressCtrl.setCurrentRow(currentRow);

            stmt.clearParameters();
            int i = 1;
            for (ExtendedColumnInfo column : _columns)
            {
               String mapping = _columnMappingModel.getMapping(column);
               if (SpecialColumnMapping.SKIP.getVisibleString().equals(mapping))
               {
                  continue;
               }
               else if (SpecialColumnMapping.FIXED_VALUE.getVisibleString().equals(mapping))
               {
                  bindFixedColumn(stmt, i++, column);
               }
               else if (SpecialColumnMapping.AUTO_INCREMENT.getVisibleString().equals(mapping))
               {
                  bindAutoincrementColumn(stmt, i++, column, currentRow);
               }
               else if (SpecialColumnMapping.NULL.getVisibleString().equals(mapping))
               {
                  stmt.setNull(i++, column.getTableColumnInfo().getDataType());
               }
               else
               {
                  bindColumn(stmt, i++, column);
               }
            }
            stmt.executeUpdate();

            if (false == singleTransaction)
            {
               if (0 < currentRow && 0 == currentRow % commitAfterEveryInserts)
               {
                  conn.commit();
               }
            }
         }
         _importer.close();
         success = true;
      }
      catch (SQLException sqle)
      {
         importProgressCtrl.failedWithSQLException(sqle, stmt, insertSQL);
      }
      catch (UnsupportedFormatException ufe)
      {
         importProgressCtrl.failedWithUnsupportedFormatException(ufe);
      }
      catch (IOException ioe)
      {
         importProgressCtrl.failedWithIoException(ioe);
      }
      catch (Throwable t)
      {
         s_log.error(t);
      }
      finally
      {
         try
         {
            try
            {
               _importer.close();
               finishTransaction(conn, success);
            }
            catch (IOException ioe)
            {
               s_log.error("Error while closing file", ioe);
            }
         }
         finally
         {
            setOriginalAutoCommit(conn, originalAutoCommit);
            SQLUtilities.closeStatement(stmt);
         }
      }

      if (success)
      {
         GUIUtils.processOnSwingEventThread(new Runnable()
         {
            @Override
            public void run()
            {
               IObjectTreeAPI treeAPI = _session.getSessionInternalFrame().getObjectTreeAPI();
               treeAPI.refreshSelectedNodes();
            }
         });

         importProgressCtrl.finishedSuccessFully();
      }
   }

   private Thread.UncaughtExceptionHandler createUncaughtExceptionHandler()
   {
      return (t, e) -> GUIUtils.processOnSwingEventThread(() -> {throw new RuntimeException(e);});
   }


   private void finishTransaction(ISQLConnection conn, boolean success)
   {
      try
      {
         if (success)
         {
            conn.commit();
         }
         else
         {
            conn.rollback();
         }
      }
      catch (SQLException e)
      {
         try
         {
            conn.rollback();
         }
         catch (SQLException e1)
         {
            s_log.error("Finally failed to rollback connection");
         }
         throw new RuntimeException(e);
      }
   }

   private void setOriginalAutoCommit(ISQLConnection conn, boolean originalAutoCommit)
   {
      try
      {
         conn.setAutoCommit(originalAutoCommit);
      }
      catch (SQLException e)
      {
         EDTMessageBoxUtil.showMessageDialogOnEDT(
               s_stringMgr.getString("ImportDataIntoTableExecutor.reestablish.autocommit.failed"),
               s_stringMgr.getString("ImportDataIntoTableExecutor.reestablish.autocommit.failed.title"),
               JOptionPane.ERROR_MESSAGE);

         throw new RuntimeException(e);
      }
   }

   private boolean getOriginalAutoCommit(ISQLConnection conn)
   {
      try
      {
         return conn.getAutoCommit();
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }


   private void bindAutoincrementColumn(PreparedStatement stmt, int index, ExtendedColumnInfo column, int counter) throws SQLException, UnsupportedFormatException
   {
      long value = 0;
      String fixedValue = _columnMappingModel.getFixedValue(column);
      try
      {
         value = Long.parseLong(fixedValue);
         value += counter;
      }
      catch (NumberFormatException nfe)
      {
         throw new UnsupportedFormatException("Could not interpret value for column " + column.getColumnName() + " as value of type long. The value is: " + fixedValue, nfe);
      }
      switch (column.getTableColumnInfo().getDataType())
      {
         case Types.BIGINT:
            stmt.setLong(index, value);
            break;
         case Types.INTEGER:
         case Types.NUMERIC:
            stmt.setInt(index, (int) value);
            break;
         default:
            throw new UnsupportedFormatException("Autoincrement column " + column.getColumnName() + "  is not numeric");
      }
   }

   private void bindFixedColumn(PreparedStatement stmt, int index, ExtendedColumnInfo column) throws SQLException, IOException, UnsupportedFormatException
   {
      String value = _columnMappingModel.getFixedValue(column);
      Date d = null;
      switch (column.getTableColumnInfo().getDataType())
      {
         case Types.BIGINT:
            try
            {
               stmt.setLong(index, Long.parseLong(value));
            }
            catch (NumberFormatException nfe)
            {
               throw new UnsupportedFormatException(nfe);
            }
            break;
         case Types.INTEGER:
            setIntOrUnsignedInt(stmt, index, column, null != value ? Integer.parseInt(value) : null, null != value ? Long.parseLong(value) : null);
         case Types.NUMERIC:
            setDouble(stmt, index, (null != value ? Double.parseDouble(value) : null), Types.DOUBLE);
            break;
         case Types.DATE:
            // Null values should be allowed
            setDateOrNull(stmt, index, value);
            break;
         case Types.TIMESTAMP:
            // Null values should be allowed
            setTimeStampOrNull(stmt, index, value);
            break;
         case Types.TIME:
            // Null values should be allowed
            setTimeOrNull(stmt, index, value);
            break;
         default:
            stmt.setString(index, value);
      }
   }

   private void setDateOrNull(PreparedStatement stmt, int index,
                              String value) throws UnsupportedFormatException, SQLException
   {
      if (null != value)
      {
         Date d = DateUtils.parseSQLFormats(value);
         if (d == null)
            throw new UnsupportedFormatException("Could not interpret value as date type. Value is: " + value);
         stmt.setDate(index, new java.sql.Date(d.getTime()));
      }
      else
      {
         stmt.setNull(index, Types.DATE);
      }
   }

   private void setTimeStampOrNull(PreparedStatement stmt, int index,
                                   String value) throws UnsupportedFormatException, SQLException
   {
      if (null != value)
      {
         Date d = DateUtils.parseSQLFormats(value);
         if (d == null)
            throw new UnsupportedFormatException("Could not interpret value as date type. Value is: " + value);
         stmt.setTimestamp(index, new java.sql.Timestamp(d.getTime()));
      }
      else
      {
         stmt.setNull(index, Types.TIMESTAMP);
      }
   }

   private void setTimeOrNull(PreparedStatement stmt, int index, String value)
         throws UnsupportedFormatException, SQLException
   {
      if (null != value)
      {
         Date d = DateUtils.parseSQLFormats(value);
         if (d == null)
            throw new UnsupportedFormatException("Could not interpret value as date type. Value is: " + value);
         stmt.setTime(index, new java.sql.Time(d.getTime()));
      }
      else
      {
         stmt.setNull(index, Types.TIME);
      }
   }

   private void bindColumn(PreparedStatement stmt, int index, ExtendedColumnInfo column) throws SQLException, IOException
   {
      int mappedColumn = getMappedColumn(column);
      switch (column.getTableColumnInfo().getDataType())
      {
         case Types.BIGINT:
            setLong(stmt, index, _importer.getLong(mappedColumn));
            break;
         case Types.INTEGER:
         case Types.SMALLINT:
            setIntOrUnsignedInt(stmt, index, column, _importer.getInt(mappedColumn), _importer.getLong(mappedColumn));
            break;
         case Types.NUMERIC:
         case Types.FLOAT:
         case Types.DOUBLE:
         case Types.DECIMAL:
            setDouble(stmt, index, _importer.getDouble(getMappedColumn(column)), column.getTableColumnInfo().getDataType());
            break;
         case Types.DATE:
            setDate(stmt, index, mappedColumn);
            break;
         case Types.TIMESTAMP:
            setTimestamp(stmt, index, mappedColumn);
            break;
         case Types.TIME:
            setTime(stmt, index, mappedColumn);
            break;
         default:
            setString(stmt, index, mappedColumn);
      }
   }

   private void setString(PreparedStatement stmt, int index, int mappedColumn)
         throws SQLException, IOException
   {
      String string = _importer.getString(mappedColumn);
      if (null != string)
      {
         stmt.setString(index, string);
      }
      else
      {
         stmt.setNull(index, Types.VARCHAR);
      }
   }

   private void setTime(PreparedStatement stmt, int index, int mappedColumn)
         throws SQLException, IOException
   {
      Date date = _importer.getDate(mappedColumn);
      if (null != date)
      {
         stmt.setTime(index, new java.sql.Time(date.getTime()));
      }
      else
      {
         stmt.setNull(index, Types.TIME);
      }
   }

   private void setTimestamp(PreparedStatement stmt, int index, int mappedColumn)
         throws SQLException, IOException
   {
      Date date = _importer.getDate(mappedColumn);
      if (null != date)
      {
         stmt.setTimestamp(index, new java.sql.Timestamp(date.getTime()));
      }
      else
      {
         stmt.setNull(index, Types.TIMESTAMP);
      }
   }

   private void setDate(PreparedStatement stmt, int index, int mappedColumn)
         throws SQLException, IOException
   {
      Date date = _importer.getDate(mappedColumn);
      if (null != date)
      {
         stmt.setDate(index, new java.sql.Date(date.getTime()));
      }
      else
      {
         stmt.setNull(index, Types.DATE);
      }

   }

   /*
    * 1968807: Unsigned INT problem with IMPORT FILE functionality
    *
    * If we are working with a signed integer, then it should be ok to store in
    * a Java integer which is always signed. However, if we are working with an
    * unsigned integer type, Java doesn't have this so use a long instead.
    */
   private void setIntOrUnsignedInt(PreparedStatement stmt, int index, ExtendedColumnInfo column, Integer integerValue, Long longValue)
         throws SQLException
   {
      String columnTypeName = column.getTableColumnInfo().getTypeName();
      if (columnTypeName != null && (columnTypeName.toUpperCase().endsWith("UNSIGNED")))
      {
         setLong(stmt, index, longValue);
      }

      setInt(stmt, index, integerValue);
   }

   private void setDouble(PreparedStatement stmt, int index, Double value, int javaSqlTypes_Type)
         throws SQLException
   {
      if ( null == value )
      {
         stmt.setNull(index, javaSqlTypes_Type);
      }
      else
      {
         stmt.setDouble(index, value);
      }
   }

   private void setLong(PreparedStatement stmt, int index, Long value)
         throws SQLException
   {
      if ( null == value )
      {
         stmt.setNull(index, Types.INTEGER);
      }
      else
      {
         stmt.setLong(index, value);
      }
   }

   private void setInt(PreparedStatement stmt, int index, Integer value)
         throws SQLException
   {
      if ( null == value )
      {
         stmt.setNull(index, Types.INTEGER);
      }
      else
      {
         stmt.setInt(index, value);
      }
   }

   private int getMappedColumn(ExtendedColumnInfo column)
   {
      return _importerColumns.indexOf(_columnMappingModel.getMapping(column));
   }

   private String createColumnList()
   {
      StringBuffer columnsList = new StringBuffer();
      for (ExtendedColumnInfo column : _columns)
      {
         String mapping = _columnMappingModel.getMapping(column);

         if (SpecialColumnMapping.SKIP.getVisibleString().equals(mapping))
         {
            continue;
         }

         if (columnsList.length() != 0)
         {
            columnsList.append(", ");
         }
         columnsList.append(column.getColumnName());
      }
      return columnsList.toString();
   }


   private String getQuestionMarks(int count)
   {
      StringBuffer result = new StringBuffer();
      for (int i = 0; i < count; i++)
      {
         result.append("?");
         if (i < count - 1)
         {
            result.append(", ");
         }
      }
      return result.toString();
   }

}
