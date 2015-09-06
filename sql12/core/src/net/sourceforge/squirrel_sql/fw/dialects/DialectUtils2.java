package net.sourceforge.squirrel_sql.fw.dialects;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class DialectUtils2
{
   public static String checkColumnDoubleQuotes(DialectType dialectType, String columnName)
   {
      if (shouldQuoteColumnName(dialectType, columnName))
      {
         return "\"" + columnName + "\"";
      }
      else
      {
         return columnName;
      }
   }

   private static boolean shouldQuoteColumnName(DialectType dialectType, String columnName)
   {
      if(dialectType == DialectType.HSQLDB || dialectType == DialectType.POSTGRES || dialectType == DialectType.H2)
      {
         return true;
      }


      if (false == StringUtilities.isEmpty(columnName))
      {

         if(false == Character.isJavaIdentifierStart(columnName.charAt(0)))
         {
            return true;
         }


         for (int i = 1; i < columnName.length(); i++)
         {
            char c = columnName.charAt(i);
            if(false == Character.isJavaIdentifierPart(c) || '.' == c || '$' == c )
            {
               return true;
            }
         }
      }


      return false;
   }
}
