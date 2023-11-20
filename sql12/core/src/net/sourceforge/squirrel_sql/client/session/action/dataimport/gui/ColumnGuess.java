package net.sourceforge.squirrel_sql.client.session.action.dataimport.gui;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;

public class ColumnGuess
{
   private String _columnName;
   private int _varcharLength;
   private boolean _suggestColumnTypes;

   private String _type;
   private int _numericPrecision;
   private int _numericScale;

   public ColumnGuess(String columnName, int varcharLength, int numericPrecision, int numericScale, boolean suggestColumnTypes, HashSet<String> duplicateColumnNameCheck)
   {
      _varcharLength = varcharLength;
      _numericPrecision = numericPrecision;
      _numericScale = numericScale;

      _suggestColumnTypes = suggestColumnTypes;


      String colNameBuf = columnName;

      for(int i=0; duplicateColumnNameCheck.contains(colNameBuf.toLowerCase()); ++i)
      {
         colNameBuf = columnName + i;
      }

      _columnName = colNameBuf;
      duplicateColumnNameCheck.add(_columnName.toLowerCase());
   }

   private String getDefaultType()
   {
      return "VARCHAR(" + _varcharLength  + ")";
   }

   public void exampleValue(String val)
   {
      if(false == _suggestColumnTypes)
      {
         _type = getDefaultType();
      }

      if(StringUtilities.isEmpty(val))
      {
         return;
      }

      if(null == _type || "INTEGER".equals(_type))
      {
         try
         {
            Integer.parseInt(val);
            _type = "INTEGER";
            return;
         }
         catch (NumberFormatException e)
         {
         }
      }

      if(null == _type || "INTEGER".equals(_type) || getNumericType().equals(_type) )
      {
         try
         {
            Double.parseDouble(val);
            _type = getNumericType();
            return;
         }
         catch (NumberFormatException e)
         {
         }
      }

      if(null == _type || "INTEGER".equals(_type) || getNumericType().equals(_type) || "TIMESTAMP".equals(_type))
      {
         try
         {
            new SimpleDateFormat(ImportPropsDAO.getCSVDateFormat()).parse(val);
            _type = "TIMESTAMP";
            return;
         }
         catch (ParseException pe)
         {
         }
      }

      _type = getDefaultType();
   }

   private String getNumericType()
   {
      return "NUMERIC(" + _numericPrecision + "," + _numericScale + ")";
   }

   public String getColumnSQL()
   {
      return _columnName + " " + ((null != _type) ? _type : getDefaultType());
   }
}
