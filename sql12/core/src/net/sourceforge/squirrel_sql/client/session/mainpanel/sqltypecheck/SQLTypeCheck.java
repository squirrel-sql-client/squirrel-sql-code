package net.sourceforge.squirrel_sql.client.session.mainpanel.sqltypecheck;

import org.apache.commons.lang3.StringUtils;

public class SQLTypeCheck
{
   public static SQLType getSQLType(String sql)
   {
      SQLType result = SQLType.UNKNOWN;

      if(StringUtils.startsWithIgnoreCase(sql.trim(), "INSERT"))
      {
         result = SQLType.INSERT;
      }
      if(StringUtils.startsWithIgnoreCase(sql.trim(),"UPDATE"))
      {
         result = SQLType.UPDATE;
      }
      if(StringUtils.startsWithIgnoreCase(sql.trim(),"SELECT"))
      {
         result = SQLType.SELECT;
      }
      if(StringUtils.startsWithIgnoreCase(sql.trim(),"DELETE"))
      {
         result = SQLType.DELETE;
      }
      return result;
   }

   /**
    * Returns a boolean indicating whether or not the specified querySql appears to be a SELECT statement.
    *
    * @param querySql
    *           the SQL statement to check
    * @return true if it is a SELECT statement; false otherwise.
    */
   public static boolean isSelectStatement(String querySql)
   {
      return SQLType.SELECT == getSQLType(querySql);
   }
}
