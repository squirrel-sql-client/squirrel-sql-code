package org.squirrelsql.session;

public enum ColumnMetaProps
{
   // As known from DatabaseMetaData.getColumns()
   COLUMN_NAME,
   DATA_TYPE,
   TYPE_NAME,
   COLUMN_SIZE,
   DECIMAL_DIGITS,
   IS_NULLABLE,
   REMARKS;

   public String getPropName()
   {
      return toString();
   }

   public static boolean isYes(String boolColPropValue)
   {
      return "YES".equalsIgnoreCase(boolColPropValue);
   }
}
