package org.squirrelsql.session.sql;

import org.squirrelsql.table.TableLoader;

import java.sql.SQLException;

public class SQLResult
{
   private SQLException _sqlExc;
   private TableLoader _resultTableLoader;
   private TableLoader _resultMetaDateTableLoader;
   private Integer _maxResults;
   private Integer _updateCount;

   public SQLResult(SQLException sqlExc)
   {
      _sqlExc = sqlExc;
   }

   public SQLResult(int updateCount)
   {
      _updateCount = updateCount;
   }

   public SQLResult(TableLoader resultTableLoader, TableLoader resultMetaDateTableLoader, Integer maxResults)
   {
      _resultTableLoader = resultTableLoader;
      _resultMetaDateTableLoader = resultMetaDateTableLoader;
      _maxResults = maxResults;
   }

   public SQLException getSqlException()
   {
      return _sqlExc;
   }

   public TableLoader getResultTableLoader()
   {
      return _resultTableLoader;
   }

   public TableLoader getResultMetaDataTableLoader()
   {
      return _resultMetaDateTableLoader;
   }

   public Integer getUpdateCount()
   {
      return _updateCount;
   }

   public Integer getMaxResults()
   {
      return _maxResults;
   }

   public boolean isMaxResultsReached()
   {
      return null != _maxResults && _maxResults <= _resultTableLoader.size();
   }
}
