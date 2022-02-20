package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.util.TemporalUtils;

import java.sql.Types;
import java.util.Calendar;
import java.util.Formatter;

public class TableCopySqlPartCommandBase
{
   private ISession _session;

   enum StatType
   {
        IN, WHERE, UPDATE
   }

   public TableCopySqlPartCommandBase(ISession session)
   {
      _session = session;
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

               //return getPrefixForStatType(statType, false) + "{t '" + prefixNulls(cal.get(Calendar.HOUR_OF_DAY), 2) + ":" +
               //                prefixNulls(cal.get(Calendar.MINUTE), 2) + ":" +
               //                prefixNulls(cal.get(Calendar.SECOND), 2) + "'}";
               return getPrefixForStatType(statType, false) + TemporalUtils.format(date, Types.TIME, _session);

            }
            else if(colDef.getSqlType() == Types.DATE && cellObj instanceof java.util.Date)
            {
               java.util.Date date = (java.util.Date) cellObj;
               Calendar cal = Calendar.getInstance();
               cal.setTime(date);
               //return getPrefixForStatType(statType, false) + "{d '" + prefixNulls(cal.get(Calendar.YEAR), 4) + "-" +
               // prefixNulls(cal.get(Calendar.MONTH) + 1, 2) + "-" +
               // prefixNulls(cal.get(Calendar.DAY_OF_MONTH) ,2) + "'}";
               return getPrefixForStatType(statType, false) + TemporalUtils.format(date, Types.DATE, _session);
            }
            else if(colDef.getSqlType() == Types.TIMESTAMP && cellObj instanceof java.util.Date)
            {
               java.util.Date date = (java.util.Date) cellObj;
               Calendar cal = Calendar.getInstance();
               cal.setTime(date);

               //return getPrefixForStatType(statType, false) + "{ts '" + prefixNulls(cal.get(Calendar.YEAR), 4) + "-" +
               //      prefixNulls(cal.get(Calendar.MONTH) + 1, 2) + "-" +
               //      prefixNulls(cal.get(Calendar.DAY_OF_MONTH), 2) + " " +
               //      prefixNulls(cal.get(Calendar.HOUR_OF_DAY), 2) + ":" +
               //      prefixNulls(cal.get(Calendar.MINUTE), 2) + ":" +
               //      prefixNulls(cal.get(Calendar.SECOND), 2) + getNanoString(date);
               return getPrefixForStatType(statType, false) + TemporalUtils.format(date, Types.TIMESTAMP, _session);
            }
            else if(cellObj instanceof Byte[] || cellObj instanceof byte[])
            {
               Byte[] cellObjBytes=null;
               if (cellObj instanceof Byte[])
               {
                  cellObjBytes = (Byte[])cellObj;
               }
               else
               {
                  byte[] cellObjBytesPrimitives = (byte[])cellObj;
                  cellObjBytes = new Byte[cellObjBytesPrimitives.length];
                  int i = 0;
                  for (byte b : cellObjBytesPrimitives) 
                  {	  
                     cellObjBytes[i++] = b;
                  }
               }

               Formatter formatter = new Formatter();
               for (byte b : cellObjBytes)
               {
                  formatter.format("%02x", b);
               }
               String cellObjStr = formatter.toString();
               formatter.close();

               String prefix="X'";
               String suffix="'";
               if(colDef.getDialectType().equals(DialectType.DB2))
               {
                  if(colDef.getSqlType() == Types.BLOB)
                  {
                     prefix="BLOB(X'";
                     suffix="')";
                  }
               }
               else if(colDef.getDialectType().equals(DialectType.ORACLE))
               {
                  prefix="'";
                  suffix="'";
               }
               else if(colDef.getDialectType().equals(DialectType.MSSQL))
               {
                  prefix="0x";
                  suffix="";
               }

               return getPrefixForStatType(statType, false) + prefix + cellObjStr + suffix;
            }
            else
            {
               return getPrefixForStatType(statType, false) + "'" + cellObj.toString().replaceAll("'", "''") + "'";
            }
         }
      }
   }

//   private String getNanoString(java.util.Date date)
//   {
//      if(false == date instanceof Timestamp)
//      {
//         return "'}";
//      }
//
//      Timestamp ts = (Timestamp) date;
//
//      int nanos = ts.getNanos() / 1000;
//
//      if(0 == nanos)
//      {
//         return "'}";
//      }
//
//      return "." + prefixNulls(nanos, 6) + "'}";
//   }

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
