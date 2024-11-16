/*
 * Copyright (C) 2011 Stefan Willinger
 * wis775@users.sourceforge.net
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
package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The implementation of {@link IExportData} for exporting data of a {@link ResultSet}
 * This class encapsulate the access to the {@link ResultSet}.
 * The implementation does not read the whole result set at once. It reads only a single row at a time.
 * So the memory footprint should be very small, even for huge result sets.
 *
 * @author Stefan Willinger
 */
public class ResultSetExportData implements IExportData
{
   private final static ILogger log = LoggerController.createLogger(ResultSetExportData.class);

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ResultSetExportData.class);

   /**
    * ResultSet to export and its Statement.
    * The ResultSet has an associated Statement because mostly Statements allow only a single ResultSet at a time.
    */
   private Statement _stat;
   private ResultSet _resultSet;

   /**
    * The {@link ColumnDisplayDefinition} for each column.
    */
   private List<ColumnDisplayDefinition> _colDispDef;

   private DialectType _dialect;

   /**
    * The current row index
    */
   private int rowIndex = 0;

   /**
    * Constructs the data on a given {@link ResultSet} and a specific dialect.
    *
    * @param resultSet The result set to use.
    * @param dialect   The dialect to use.
    * @throws SQLException if the meta data of the result set could not be read.
    */
   public ResultSetExportData(Statement stat, String sqlToWriteToFile, DialectType dialect) throws SQLException
   {
      _stat = stat;
      _resultSet = stat.executeQuery(sqlToWriteToFile);
      _colDispDef = new ArrayList<>();
      _dialect = dialect;

      int columnCount = this._resultSet.getMetaData().getColumnCount();
      for (int i = 1; i <= columnCount; i++)
      {
         _colDispDef.add(new ColumnDisplayDefinition(_resultSet, i, this._dialect, true));
      }
   }

   /**
    * Gets the headers for the data.
    * This depends on the extracted meta data of the result set.
    *
    * @see IExportData#getHeaderColumns()
    */
   @Override
   public Iterator<String> getHeaderColumns()
   {
      List<String> headers = new ArrayList<>();
      for (ColumnDisplayDefinition col : this._colDispDef)
      {
         String headerValue = col.getColumnHeading();
         headers.add(headerValue);
      }
      return headers.iterator();

   }

   /**
    * Iterates over the result set.
    * The result set will not be read into the memory. It creates an iterator, which reads one row at a time.
    * The amount of the used heap depends on the number of rows, that the JDBC-driver reads at once.
    *
    * @see IExportData#getRows()
    */
   @Override
   public Iterator<ExportDataRow> getRows()
   {

      return new Iterator<>()
      {

         @Override
         public void remove()
         {
            throw new IllegalStateException("not supported");
         }

         /**
          * Reads the next row from the result set.
          *
          * @return A new IExportDataRow, created from the current row of the result set.
          * @see java.util.Iterator#next()
          */
         @Override
         public ExportDataRow next()
         {
            return createRow();
         }

         /**
          * @return true, if the result set has more data.
          * @see java.util.Iterator#hasNext()
          */
         @Override
         public boolean hasNext()
         {
            return hasNextRow();
         }
      };

   }

   @Override
   public int getColumnCount()
   {
      return _colDispDef.size();
   }

   @Override
   public void close()
   {
      SQLUtilities.closeResultSet(_resultSet);
      SQLUtilities.closeStatement(_stat);
   }

   private boolean hasNextRow()
   {
      try
      {
         return _resultSet.next();
      }
      catch (SQLException e)
      {
         log.error(s_stringMgr.getString("ResultSetExportData.errorReadingResultSet"), e);
         throw new RuntimeException(s_stringMgr.getString("ResultSetExportData.errorReadingResultSet"), e);
      }
   }

   private ExportDataRow createRow()
   {
      try
      {
         List<ExportCellData> cells = new ArrayList<>();
         for (int i = 1; i <= _colDispDef.size(); i++)
         {
            ColumnDisplayDefinition colDef = _colDispDef.get(i - 1);
            Object object = CellComponentFactory.readResultSet(colDef, _resultSet, i, false);
            ExportCellData cell = new ExportCellData(colDef, object, rowIndex, i - 1);
            cells.add(cell);
         }
         ExportDataRow data = new ExportDataRow(cells, rowIndex);
         rowIndex++;
         return data;
      }
      catch (SQLException e)
      {
         log.error(s_stringMgr.getString("ResultSetExportData.errorReadingResultSet"), e);
         throw new RuntimeException(s_stringMgr.getString("ResultSetExportData.errorReadingResultSet"), e);
      }
   }

}
