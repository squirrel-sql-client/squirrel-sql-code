package org.squirrelsql.session.sql;

import org.squirrelsql.table.TableLoader;

public class ResultTabUserData
{
   private String _sql;
   private TableLoader _resultTableLoader;

   public ResultTabUserData(String sql, TableLoader resultTableLoader)
   {
      _sql = sql;
      _resultTableLoader = resultTableLoader;
   }

   public String getSql()
   {
      return _sql;
   }

   public TableLoader getResultTableLoader()
   {
      return _resultTableLoader;
   }
}
