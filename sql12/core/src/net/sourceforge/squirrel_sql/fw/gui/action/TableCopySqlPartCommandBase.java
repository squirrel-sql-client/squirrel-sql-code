package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;

import java.sql.Types;
import java.util.Calendar;

public class TableCopySqlPartCommandBase
{
   enum StatType
   {
        IN, WHERE, UPDATE
   }


   protected String getData(ColumnDisplayDefinition colDef, Object cellObj, StatType statType)
   {
      if (cellObj == null)
      {
         return getPrefixForStatType(statType, true) + "null";
      }
      else
      {
         if(null == colDef)
         {
            return getPrefixForStatType(statType, false) + "'" + cellObj.toString().replaceAll("'", "''") + "'";
         }
         else
         {
            if(colDef.getSqlType() == Types.SMALLINT ||
               colDef.getSqlType() == Types.INTEGER ||
               colDef.getSqlType() == Types.DECIMAL ||
               colDef.getSqlType() == Types.DOUBLE ||
               colDef.getSqlType() == Types.BIGINT ||
               colDef.getSqlType() == Types.NUMERIC ||
               colDef.getSqlType() == Types.TINYINT ||
               colDef.getSqlType() == Types.BIT ||
               colDef.getSqlType() == Types.REAL
               )
            {
               return getPrefixForStatType(statType, false) + cellObj.toString();
            }
            else if(colDef.getSqlType() == Types.TIME && cellObj instanceof java.util.Date)
            {
               java.util.Date date = (java.util.Date) cellObj;
               Calendar cal = Calendar.getInstance();
               cal.setTime(date);
               return getPrefixForStatType(statType, false) + "{t '" + prefixNulls(cal.get(Calendar.HOUR_OF_DAY), 2) + ":" +
                               prefixNulls(cal.get(Calendar.MINUTE), 2) + ":" +
                               prefixNulls(cal.get(Calendar.SECOND), 2) + "'}";
            }
            else if(colDef.getSqlType() == Types.DATE && cellObj instanceof java.util.Date)
            {
               java.util.Date date = (java.util.Date) cellObj;
               Calendar cal = Calendar.getInstance();
               cal.setTime(date);
               return getPrefixForStatType(statType, false) + "{d '" + prefixNulls(cal.get(Calendar.YEAR), 4) + "-" +
                               prefixNulls(cal.get(Calendar.MONTH) + 1, 2) + "-" +
                               prefixNulls(cal.get(Calendar.DAY_OF_MONTH) ,2) + "'}";
            }
            else if(colDef.getSqlType() == Types.TIMESTAMP && cellObj instanceof java.util.Date)
            {
               java.util.Date date = (java.util.Date) cellObj;
               Calendar cal = Calendar.getInstance();
               cal.setTime(date);
               return getPrefixForStatType(statType, false) + "{ts '" + prefixNulls(cal.get(Calendar.YEAR), 4) + "-" +
                               prefixNulls(cal.get(Calendar.MONTH) + 1, 2) + "-" +
                               prefixNulls(cal.get(Calendar.DAY_OF_MONTH) ,2) + " " +
                               prefixNulls(cal.get(Calendar.HOUR_OF_DAY), 2) + ":" +
                               prefixNulls(cal.get(Calendar.MINUTE), 2) + ":" +
                               prefixNulls(cal.get(Calendar.SECOND), 2) + "'}";
            }
            else
            {
               return getPrefixForStatType(statType, false) + "'" + cellObj.toString().replaceAll("'", "''") + "'";
            }
         }
      }
   }

   private String getPrefixForStatType(StatType statType, boolean isNullVal)
   {
      if(isNullVal)
      {
         switch(statType)
         {
            case IN: return "";
            case WHERE: return " is ";
            case UPDATE: return "=";
         }

      }
      else
      {
         switch(statType)
         {
            case IN: return "";
            case WHERE: return "=";
            case UPDATE: return "=";
         }
      }

      throw new IllegalStateException("Can't happen");
   }

   private String prefixNulls(int toPrefix, int digitCount)
   {
      String ret = "" + toPrefix;

      while(ret.length() < digitCount)
      {
         ret = 0 + ret;
      }

      return ret;
   }
}
