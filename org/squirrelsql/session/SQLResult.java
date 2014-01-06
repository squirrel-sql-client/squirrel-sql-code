package org.squirrelsql.session;

import org.squirrelsql.table.TableLoader;

import java.sql.SQLException;

public class SQLResult
{
   private SQLException _sqlExc;
   private TableLoader _tableLoader;

   public SQLResult(SQLException sqlExc)
   {

      _sqlExc = sqlExc;
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
}
