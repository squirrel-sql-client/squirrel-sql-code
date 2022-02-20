package net.sourceforge.squirrel_sql.fw.util;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeDate;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeTime;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeTimestamp;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.TemporalScriptGenerationFormat;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

public class TemporalUtils
{
   public static String format(java.util.Date date, int sqlType)
   {
      return format(date, sqlType, null);
   }

   /**
    * @param session may be null. Takes effect for sqlType = {@link Types#TIMESTAMP} only.
    *                Is used to find if and how to respect {@link  Timestamp#getNanos()}.
    *                If null the generated escape sting will not contain nanos.
    */
   public static String format(java.util.Date date, int sqlType, ISession session)
   {
      if(useStdJDBCFormat(sqlType))
      {
         return getStdJDBCFormat(date, sqlType, session);
      }
      else
      {
         return getStringFormat(date, sqlType, session);
      }
   }

   private static boolean useStdJDBCFormat(int sqlType)
   {
      if(sqlType == Types.TIMESTAMP)
      {
         return DataTypeTimestamp.getTimeStampScriptFormat() == TemporalScriptGenerationFormat.STD_JDBC_FORMAT;
      }
      else if(sqlType == Types.DATE)
      {
         return DataTypeDate.getDateScriptFormat() == TemporalScriptGenerationFormat.STD_JDBC_FORMAT;
      }
      else if(sqlType == Types.TIME)
      {
         return DataTypeTime.getTimeScriptFormat() == TemporalScriptGenerationFormat.STD_JDBC_FORMAT;
      }

      return true;
   }

   public static String getStdJDBCFormat(java.util.Date date, int sqlType)
   {
      return getStdJDBCFormat(date, sqlType,null);
   }

   public static String getStdJDBCFormat(java.util.Date date, int sqlType, ISession session)
   {
      if(sqlType == Types.TIMESTAMP)
      {
         if(date instanceof Timestamp)
         {
            return getStdJDBCFormat((Timestamp) date, session);
         }
         else
         {
            return getStdJDBCFormat(new Timestamp(date.getTime()), session);
         }
      }
      else if(sqlType == Types.DATE)
      {
         if(date instanceof java.sql.Date)
         {
            return getStdJDBCFormat((java.sql.Date) date);
         }
         else
         {
            return getStdJDBCFormat(new java.sql.Date(date.getTime()));
         }
      }
      else if(sqlType == Types.TIME)
      {
         if(date instanceof java.sql.Time)
         {
            return getStdJDBCFormat((java.sql.Time) date);
         }
         else
         {
            return getStdJDBCFormat(new java.sql.Time(date.getTime()));
         }
      }
      else
      {
         throw new IllegalArgumentException("Unknown temporal SQLType = " + sqlType);
      }
   }


   public static String getStringFormat(java.util.Date date, int sqlType, ISession session)
   {
      if(sqlType == Types.TIMESTAMP)
      {
         if(date instanceof Timestamp)
         {
            return getStringFormat((Timestamp) date);
         }
         else
         {
            return getStringFormat(new Timestamp(date.getTime()));
         }
      }
      else if(sqlType == Types.DATE)
      {
         if(date instanceof java.sql.Date)
         {
            return getStringFormat((java.sql.Date) date);
         }
         else
         {
            return getStringFormat(new java.sql.Date(date.getTime()));
         }
      }
      else if(sqlType == Types.TIME)
      {
         if(date instanceof java.sql.Time)
         {
            return getStringFormat((java.sql.Time) date);
         }
         else
         {
            return getStringFormat(new java.sql.Time(date.getTime()));
         }
      }
      else
      {
         throw new IllegalArgumentException("Unknown temporal SQLType = " + sqlType);
      }
   }

   public static String getStdJDBCFormat(java.sql.Timestamp timeStamp)
   {
      return getStdJDBCFormat(timeStamp, null);
   }

   /**
    * @param session may be null. Takes effect for sqlType = {@link Types#TIMESTAMP} only.
    *                Is used to find if and how to respect {@link  Timestamp#getNanos()}.
    *                If null the generated escape sting will not contain nanos.
    */
   public static String getStdJDBCFormat(java.sql.Timestamp timeStamp, ISession session)
   {
      if(null == session)
      {
         return "{ts '" + timeStamp + "'}";
      }
      else
      {
         final Calendar calendar = Calendar.getInstance();
         calendar.setTime(timeStamp);
         String esc = "{ts '" +
                      StringUtilities.prefixNulls(calendar.get(Calendar.YEAR), 4) + "-" +
                      StringUtilities.prefixNulls(calendar.get(Calendar.MONTH) + 1, 2) + "-" +
                      StringUtilities.prefixNulls(calendar.get(Calendar.DAY_OF_MONTH), 2) + " " +
                      StringUtilities.prefixNulls(calendar.get(Calendar.HOUR_OF_DAY), 2) + ":" +
                      StringUtilities.prefixNulls(calendar.get(Calendar.MINUTE), 2) + ":" +
                      StringUtilities.prefixNulls(calendar.get(Calendar.SECOND), 2) + "." +
                      TemporalUtils.getNanos(timeStamp, session) +
                      "'}";

         return esc;
      }
   }

   public static String getStringFormat(java.sql.Timestamp timeStamp)
   {
      return "'" + timeStamp + "'";
   }

   public static String getStdJDBCFormat(java.sql.Date sqlDate)
   {
      return "{d '" + sqlDate + "'}";
   }

   public static String getStringFormat(java.sql.Date sqlDate)
   {
      return "'" + sqlDate + "'";
   }

   public static String getStdJDBCFormat(java.sql.Time sqlTime)
   {
      return "{t '" + sqlTime + "'}";
   }

   public static String getStringFormat(java.sql.Time sqlTime)
   {
      return "'" + sqlTime + "'";
   }

   /**
	 * Returns the sub-second precision value from the specified timestamp if supported by the session's
	 * dialect.
	 *
	 * @param ts
	 *           the Timestamp to get the nanosecond value from
	 * @param dialectSupportsSubSecondTimestamps1
    * @param session
    * @return a string representing the nanosecond value.
	 */
	public static String getNanos(Timestamp ts, ISession session)
   {
      if(null == session)
      {
         return "";
      }

		ISQLDatabaseMetaData md = session.getMetaData();
		HibernateDialect dialect = DialectFactory.getDialect(md);

      if (false == dialect.supportsSubSecondTimestamps() || dialect.getTimestampMaximumFractionalDigits() == 0)
      {
         return "";
      }

		String result = "" + ts.getNanos();

      int timestampMaximumFractionalDigits = dialect.getTimestampMaximumFractionalDigits();
      if(result.length() >= timestampMaximumFractionalDigits)
      {
         result = result.substring(0, timestampMaximumFractionalDigits);
      }
		return result;
	}
}
