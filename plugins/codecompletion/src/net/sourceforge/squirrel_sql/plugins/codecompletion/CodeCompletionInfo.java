package net.sourceforge.squirrel_sql.plugins.codecompletion;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public abstract class CodeCompletionInfo implements Comparable
{
   private String _upperCaseCompletionString;

   public abstract String getCompletionString();

   public int compareTo(Object obj)
   {
      CodeCompletionInfo other = (CodeCompletionInfo)obj;

      if(null == _upperCaseCompletionString)
      {
         _upperCaseCompletionString = getCompletionString().toUpperCase();
      }

      if(null == other._upperCaseCompletionString)
      {
         other._upperCaseCompletionString = other.getCompletionString().toUpperCase();
      }

      return _upperCaseCompletionString.compareTo(other._upperCaseCompletionString);
   }

   /**
    * Param must be an upper case string if not the result will always be false
    */
   public boolean upperCaseCompletionStringStartsWith(String testString)
   {
      if(null == _upperCaseCompletionString)
      {
         _upperCaseCompletionString = getCompletionString().toUpperCase();
      }

      return _upperCaseCompletionString.startsWith(testString);
   }

   /**
    * Param must be an upper case string if not the result will always be false
    */
   public boolean upperCaseCompletionStringEquals(String testString)
   {
      if(null == _upperCaseCompletionString)
      {
         _upperCaseCompletionString = getCompletionString().toUpperCase();
      }

      return _upperCaseCompletionString.equals(testString);
   }

   /**
    * Default implementation
    */
   public CodeCompletionInfo[] getColumns(DatabaseMetaData jdbcMetaData, String colNamePattern) throws SQLException
   {
      return new CodeCompletionInfo[0];
   }

   /**
    * Default implementation
    */
   public boolean hasColumns()
   {
      return false;
   }

}
