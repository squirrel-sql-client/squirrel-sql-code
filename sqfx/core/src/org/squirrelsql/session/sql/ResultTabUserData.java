package org.squirrelsql.session.sql;

public class ResultTabUserData
{
   private String _sql;

   public ResultTabUserData(String sql)
   {
      _sql = sql;
   }

   public String getSql()
   {
      return _sql;
   }
}
