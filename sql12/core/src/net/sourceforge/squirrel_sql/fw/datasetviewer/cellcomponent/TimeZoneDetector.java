package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import java.sql.Types;
import java.util.Locale;

public final class TimeZoneDetector
{
   public static boolean isTimestampWithTimeZone(int jdbcType, String typeName)
   {

      if(jdbcType == Types.TIMESTAMP_WITH_TIMEZONE)
      {
         return true;
      }

      // SQL Server: microsoft.sql.Types.DATETIMEOFFSET
      // Avoid a dependency on the SQL Server driver.
      if(jdbcType == -155)
      {
         return true;
      }

      // Oracle: oracle.jdbc.OracleTypes.TIMESTAMPTZ
      // Avoid a dependency on the Oracle driver.
      if(jdbcType == -101)
      {
         return true;
      }

      String name = normalize(typeName);

      return name.equals("timestamptz")
            || name.equals("datetimeoffset")
            || name.equals("timestamp with time zone")
            || name.equals("timestamp with timezone");
   }

   public static boolean isTimeWithTimeZone(int jdbcType, String typeName)
   {

      String name = normalize(typeName);

      // Standard JDBC 4.2 type.
      if(jdbcType == Types.TIME_WITH_TIMEZONE)
      {
         return true;
      }

      /*
       * PostgreSQL:
       * time with time zone is commonly reported as "timetz".
       *
       * Some drivers may report the full type name instead.
       */
      if(name.equals("timetz")
            || name.equals("time with time zone")
            || name.equals("time with timezone"))
      {
         return true;
      }

      return false;
   }


   private static String normalize(String value)
   {
      return value == null
            ? ""
            : value.trim()
                   .toLowerCase(Locale.ROOT)
                   .replaceAll("\\s+", " ");
   }
}
