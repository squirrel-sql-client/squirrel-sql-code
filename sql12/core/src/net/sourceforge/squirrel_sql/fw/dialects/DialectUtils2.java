package net.sourceforge.squirrel_sql.fw.dialects;

public class DialectUtils2
{
   public static String checkColumnDoubleQuotes(DialectType dialectType, String columnName)
   {
      if (dialectType == DialectType.HSQLDB || dialectType == DialectType.POSTGRES)
      {
         return "\"" + columnName + "\"";
      }
      else
      {
         return columnName;
      }
   }
}
