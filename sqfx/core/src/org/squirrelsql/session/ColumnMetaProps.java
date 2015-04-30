package org.squirrelsql.session;

import org.squirrelsql.table.ColumnNotFoundException;
import org.squirrelsql.table.TableLoader;

import java.util.function.Function;

public enum ColumnMetaProps
{
   // As known from DatabaseMetaData.getColumns()
   COLUMN_NAME("NAME"),
   DATA_TYPE,
   TYPE_NAME,
   COLUMN_SIZE,
   DECIMAL_DIGITS,
   IS_NULLABLE("NULLABLE"),
   REMARKS;

   private String[] _alternativeNames = new String[0];

   ColumnMetaProps(String... alternativeNames)
   {
      _alternativeNames = alternativeNames;
   }

   public String getPropName()
   {
      return toString();
   }

   public static boolean isYes(String boolColPropValue)
   {
      return "YES".equalsIgnoreCase(boolColPropValue);
   }

   /**
    * Some databases use non standard names for the result set returned by DatabaseMetaData.getColumns().
    *
    * This method guesses a name if the standard name could not be found.
    *
    * Names as returned by DB2:
    * TBNAME, NAME, DATA_TYPE ,TYPE_NAME, COLUMN_SIZE, BUFFER_LENGTH, DECIMAL_DIGITS, NUM_PREC_RADIX, NULLABLE, REMARKS, COLUMN_DEF, SQL_DATA_TYPE, SQL_DATETIME_SUB, CHAR_OCTET_LENGTH,
    * ORDINAL_POSITION, IS_NULLABLE, SCOPE_CATALOG, SCOPE_SCHEMA, SCOPE_TABLE, SOURCE_DATA_TYPE, IS_AUTOINCREMENT,IS_GENERATEDCOLUMN
    *
    */
   public String getCellAsString(TableLoader tableColumnMetaDataTableLoader, int rowIx)
   {
      return _getCell((s) -> tableColumnMetaDataTableLoader.getCellAsString(s, rowIx));
   }


   public int getCellAsInt(TableLoader tableColumnMetaDataTableLoader, int rowIx)
   {
      return _getCell((s) -> tableColumnMetaDataTableLoader.getCellAsInt(s, rowIx));
   }

   public Integer getCellAsInteger(TableLoader tableColumnMetaDataTableLoader, int rowIx)
   {
      return _getCell((s) -> tableColumnMetaDataTableLoader.getCellAsInteger(s, rowIx));
   }


   private <R> R _getCell(Function<String,R> f)
   {
      try
      {
         return f.apply(getPropName());
      }
      catch (ColumnNotFoundException e)
      {
         // Go on
      }


      for (String alternativeName : _alternativeNames)
      {
         try
         {
            return f.apply(alternativeName);
         }
         catch (ColumnNotFoundException e)
         {
            // Go on;
         }
      }

      // Now the ColumnNotFoundException is propagated and from the error we may derive more guesses / alternativ names,
      return f.apply(getPropName());
   }



}
