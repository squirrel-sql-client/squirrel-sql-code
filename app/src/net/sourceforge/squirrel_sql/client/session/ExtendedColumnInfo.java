package net.sourceforge.squirrel_sql.client.session;


public class ExtendedColumnInfo
{
   private String _columnName;
   private String _columnType;
   private int _columnSize;
   private int _decimalDigits;
   private boolean _nullable;

   public ExtendedColumnInfo(String columnName, String columnType, int columnSize, int decimalDigits, boolean nullable)
   {
      _columnName = columnName;
      _columnType = columnType;
      _columnSize = columnSize;
      _decimalDigits = decimalDigits;
      _nullable = nullable;
   }

   public String getColumnName()
   {
      return _columnName;
   }

   public String getColumnType()
   {
      return _columnType;
   }

   public int getColumnSize()
   {
      return _columnSize;
   }

   public int getDecimalDigits()
   {
      return _decimalDigits;
   }

   public boolean isNullable()
   {
      return _nullable;
   }

}
