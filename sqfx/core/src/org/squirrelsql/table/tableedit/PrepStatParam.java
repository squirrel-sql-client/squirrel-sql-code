package org.squirrelsql.table.tableedit;

public class PrepStatParam
{
   private final Object _val;
   private final int _sqlType;

   public PrepStatParam(Object val, int sqlType)
   {
      _val = val;
      _sqlType = sqlType;
   }

   public Object getVal()
   {
      return _val;
   }

   public int getSqlType()
   {
      return _sqlType;
   }
}
