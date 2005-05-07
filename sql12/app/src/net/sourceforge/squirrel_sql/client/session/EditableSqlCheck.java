package net.sourceforge.squirrel_sql.client.session;

import java.util.regex.Pattern;


public class EditableSqlCheck
{
   private boolean _allowEditing = false;
   private String _tableNameFromSQL = "";


   public EditableSqlCheck(SQLExecutionInfo exInfo)
   {
      // if the sql contains  results from only one table, the user
      // may choose to edit it later.  If so, we need to have the
      // full name of the table available.
      // First determine if the SQL is a query on only one table
      // The following assumes SQL is either:
      //		select <fields> FROM <tables>
      //	or
      //		select <fields> FROM <tables> WHERE <etc>
      // and that the presence of multiple tables is indicated by
      // a comma separating the table names
      String sqlString = exInfo != null ? exInfo.getSQL() : null;
      if (sqlString != null && sqlString.trim().substring(0, "SELECT".length()).equalsIgnoreCase("SELECT")) {
         sqlString = sqlString.toUpperCase();
         int selectIndex = sqlString.indexOf("SELECT");
         int fromIndex = sqlString.indexOf("FROM");
         if (selectIndex > -1 && fromIndex > -1 && selectIndex < fromIndex) {
            int whereIndex = sqlString.indexOf("WHERE");
            if (whereIndex == -1)
               whereIndex = sqlString.length();	//???need -1?

            String fromClause = sqlString.substring(fromIndex+4, whereIndex);
            boolean foundJoin = Pattern.compile("\\s[J|j][O|o][I|i][N|n]\\s").matcher(fromClause).find();

            if (fromClause.indexOf(',') == -1 && false == foundJoin)
               _allowEditing = true;	// no comma, so only one table selected from

            _tableNameFromSQL = sqlString.substring(fromIndex+4, whereIndex).trim();
         }
      }

   }

   public boolean allowsEditing()
   {
      return _allowEditing;
   }

   public String getTableNameFromSQL()
   {
      return _tableNameFromSQL;
   }
}
