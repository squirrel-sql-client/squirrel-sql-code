package org.squirrelsql.session;

public class ColumnInfo
{
   private final String _colName;
   private final int _colType;
   private final String _colTypeName;
   private final Integer _colSize;
   private Integer _decDigits;
   private final boolean _nullable;
   private final String _remarks;

   public ColumnInfo(String colName, int colType, String colTypeName, Integer colSize, Integer decDigits, boolean nullable, String remarks)
   {

      _colName = colName;
      _colType = colType;
      _colTypeName = colTypeName;
      _colSize = colSize;
      _decDigits = decDigits;
      _nullable = nullable;
      _remarks = remarks;
   }

   public String getColName()
   {
      return _colName;
   }

   public int getColType()
   {
      return _colType;
   }

   public String getColTypeName()
   {
      return _colTypeName;
   }

   public Integer getColSize()
   {
      return _colSize;
   }

   public boolean isNullable()
   {
      return _nullable;
   }

   public String getRemarks()
   {
      return _remarks;
   }

   public String getDescription()
   {
      return _colName + " " + _colTypeName + getSizes() + " " + (_nullable ? "NULL" : "NOT NULL");
   }

   private String getSizes()
   {
      if(null == _colSize)
      {
        return "";
      }

      return "(" + _colSize + (null == _decDigits || 0 == _decDigits ? "" : "," + _decDigits) + ")";

   }
}
