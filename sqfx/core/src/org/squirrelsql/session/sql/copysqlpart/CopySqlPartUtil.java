package org.squirrelsql.session.sql.copysqlpart;

import org.squirrelsql.session.ColumnInfo;

import java.sql.Types;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CopySqlPartUtil
{
   private static final Pattern FILL_COLUMN_NAME_PATTERN = Pattern.compile(".+:([^:]+):[^:]+$");


   public static String getData(ColumnInfo colInfo, Object cellObj, StatementType statementType)
   {
      if (cellObj == null)
      {
         return getPrefixForStatType(statementType, true) + "null";
      }
      else
      {
         if(null == colInfo)
         {
            return getPrefixForStatType(statementType, false) + "'" + cellObj.toString().replaceAll("'", "''") + "'";
         }
         else
         {
            if(colInfo.getColType() == Types.SMALLINT ||
               colInfo.getColType() == Types.INTEGER ||
               colInfo.getColType() == Types.DECIMAL ||
               colInfo.getColType() == Types.DOUBLE ||
               colInfo.getColType() == Types.BIGINT ||
               colInfo.getColType() == Types.NUMERIC ||
               colInfo.getColType() == Types.TINYINT ||
               colInfo.getColType() == Types.BIT ||
               colInfo.getColType() == Types.REAL
               )
            {
               return getPrefixForStatType(statementType, false) + cellObj.toString();
            }
            else if(colInfo.getColType() == Types.TIME && cellObj instanceof java.util.Date)
            {
               java.util.Date date = (java.util.Date) cellObj;
               Calendar cal = Calendar.getInstance();
               cal.setTime(date);
               return getPrefixForStatType(statementType, false) + "{t '" + prefixNulls(cal.get(Calendar.HOUR_OF_DAY), 2) + ":" +
                               prefixNulls(cal.get(Calendar.MINUTE), 2) + ":" +
                               prefixNulls(cal.get(Calendar.SECOND), 2) + "'}";
            }
            else if(colInfo.getColType() == Types.DATE && cellObj instanceof java.util.Date)
            {
               java.util.Date date = (java.util.Date) cellObj;
               Calendar cal = Calendar.getInstance();
               cal.setTime(date);
               return getPrefixForStatType(statementType, false) + "{d '" + prefixNulls(cal.get(Calendar.YEAR), 4) + "-" +
                               prefixNulls(cal.get(Calendar.MONTH) + 1, 2) + "-" +
                               prefixNulls(cal.get(Calendar.DAY_OF_MONTH) ,2) + "'}";
            }
            else if(colInfo.getColType() == Types.TIMESTAMP && cellObj instanceof java.util.Date)
            {
               java.util.Date date = (java.util.Date) cellObj;
               Calendar cal = Calendar.getInstance();
               cal.setTime(date);
               return getPrefixForStatType(statementType, false) + "{ts '" + prefixNulls(cal.get(Calendar.YEAR), 4) + "-" +
                               prefixNulls(cal.get(Calendar.MONTH) + 1, 2) + "-" +
                               prefixNulls(cal.get(Calendar.DAY_OF_MONTH) ,2) + " " +
                               prefixNulls(cal.get(Calendar.HOUR_OF_DAY), 2) + ":" +
                               prefixNulls(cal.get(Calendar.MINUTE), 2) + ":" +
                               prefixNulls(cal.get(Calendar.SECOND), 2) + "'}";
            }
            else
            {
               return getPrefixForStatType(statementType, false) + "'" + cellObj.toString().replaceAll("'", "''") + "'";
            }
         }
      }
   }

   private static String getPrefixForStatType(StatementType statementType, boolean isNullVal)
   {
      if(isNullVal)
      {
         switch(statementType)
         {
            case IN: return "";
            case WHERE: return " is ";
            case UPDATE: return "=";
         }

      }
      else
      {
         switch(statementType)
         {
            case IN: return "";
            case WHERE: return "=";
            case UPDATE: return "=";
         }
      }

      throw new IllegalStateException("Can't happen");
   }

   private static String prefixNulls(int toPrefix, int digitCount)
   {
      String ret = "" + toPrefix;

      while(ret.length() < digitCount)
      {
         ret = 0 + ret;
      }

      return ret;
   }

   protected String getTableName(ColumnInfo colInfo)
   {


      Matcher matcher = FILL_COLUMN_NAME_PATTERN.matcher(colInfo.getQualifiedTableColumnName());
      if (matcher.matches())
      {
         return matcher.group(1);
      }
      return "PressCtrlH";
   }
}
