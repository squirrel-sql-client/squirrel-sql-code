package net.sourceforge.squirrel_sql.plugins.codecompletion;


public class CodeCompletionColumnInfo extends CodeCompletionInfo
{
   private String _columnName;
   private String _columnType;
   private int _columnSize;
   private boolean _nullable;

   private String _toString;


   public CodeCompletionColumnInfo(String columnName, String columnType, int columnSize, boolean nullable)
   {
      _columnName = columnName;
      _columnType = columnType;
      _columnSize = columnSize;
      _nullable = nullable;

      _toString = _columnName + "  " + _columnType + "(" + _columnSize + ") " + (_nullable? "NULL": "NOT NULL");
   }

   public String getCompletionString()
   {
      return _columnName;
   }

   public String toString()
   {
      return _toString;
   }
}
