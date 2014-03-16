package org.squirrelsql.session;

import org.squirrelsql.table.TableLoader;

import java.sql.SQLException;

public class SQLResult
{
   private SQLException _sqlExc;
   private TableLoader _tableLoader;
   private Integer _updateCount;

   public SQLResult(SQLException sqlExc)
   {
      _sqlExc = sqlExc;
   }

   public SQLResult(int updateCount)
   {
      _updateCount = updateCount;
   }

   public SQLResult(TableLoader tableLoader)
   {
      _tableLoader = tableLoader;
   }

   public SQLException getSqlException()
   {
      return _sqlExc;
   }

   public TableLoader getTableLoader()
   {
      return _tableLoader;
   }

   public Integer getUpdateCount()
   {
      return _updateCount;
   }
}
