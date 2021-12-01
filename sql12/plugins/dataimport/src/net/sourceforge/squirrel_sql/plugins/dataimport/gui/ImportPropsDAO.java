package net.sourceforge.squirrel_sql.plugins.dataimport.gui;

import java.nio.charset.Charset;

import net.sourceforge.squirrel_sql.fw.props.Props;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.csv.CSVSettingsBean;

public class ImportPropsDAO
{
   private static final String PREF_KEY_IMPORT_DIALOG_WIDTH = "Squirrel.dataimport.dialog.width";
   private static final String PREF_KEY_IMPORT_DIALOG_HEIGHT = "Squirrel.dataimport.dialog.width";
   private static final String PREF_KEY_HEADERS_INCLUDED = "Squirrel.dataimport.dialog.headers_included";
   private static final String PREF_KEY_COMMIT_AFTER_INSERTS_COUNT = "Squirrel.dataimport.dialog.commit_after_inserts_count";
   private static final String PREF_KEY_SINGLE_TRANSACTION = "Squirrel.dataimport.dialog.single_transaction";
   private static final String PREF_KEY_EMPTY_TABLE_ON_IMPORT = "Squirrel.dataimport.dialog.empty_table_on_import";
   private static final String PREF_KEY_SAVE_MODE = "Squirrel.dataimport.dialog.save_mode";
   private static final String PREF_KEY_TRIM_VALUES = "Squirrel.dataimport.trim_values";

   private static final String PREF_KEY_CSV_DATE_FORMAT = "Squirrel.dataimport.CSV.date.format";
   private static final String PREF_KEY_CSV_SEPARATOR = "Squirrel.dataimport.CSV.separator";
   private static final String PREF_KEY_CSV_IMPORT_CHARSET = "Squirrel.dataimport.CSV.import.charset";
   private static final String PREF_KEY_CSV_USE_DOUBLE_QUOTES_AS_TEXT_QUALIFIER = "Squirrel.dataimport.CSV.import.useDoubleQuotesAsTextQualifier";


   public static final String PREF_TABLE_NAME_PATTERN = "dataimport.gui.TableSuggestion.TABLE_NAME_PATTERN";
   public static final String PREF_SUGGEST_TYPES = "dataimport.gui.TableSuggestion.SUGGEST_TYPES";
   public static final String PREF_VARCHAR_LENGTH = "dataimport.gui.TableSuggestion.VARCHAR_LENGTH";
   private static final String PREF_KEY_NUMERIC_PRECISION = "dataimport.gui.TableSuggestion.NUMERIC_PRECISION";
   private static final String PREF_KEY_NUMERIC_SCALE = "dataimport.gui.TableSuggestion.NUMERIC_SCALE";


   public static void setDialogWidth(int width)
   {
      Props.putInt(PREF_KEY_IMPORT_DIALOG_WIDTH, width);
   }

   public static void setDialogHeight(int height)
   {
      Props.putInt(PREF_KEY_IMPORT_DIALOG_HEIGHT, height);
   }

   public static int getDialogWidth()
   {
      return Props.getInt(PREF_KEY_IMPORT_DIALOG_WIDTH, 600);
   }

   public static int getDialogHeight()
   {
      return Props.getInt(PREF_KEY_IMPORT_DIALOG_HEIGHT, 600);
   }


   public static boolean isHeadersIncluded()
   {
      return Props.getBoolean(PREF_KEY_HEADERS_INCLUDED, true);
   }

   public static int getCommitAfterInsertsCount()
   {
      return Props.getInt(PREF_KEY_COMMIT_AFTER_INSERTS_COUNT,100);
   }

   public static boolean isSingleTransaction()
   {
      return Props.getBoolean(PREF_KEY_SINGLE_TRANSACTION, false);
   }

   public static boolean isEmptyTableOnImport()
   {
      return Props.getBoolean(PREF_KEY_EMPTY_TABLE_ON_IMPORT, false);
   }

   public static boolean isSaveMode()
   {
      return Props.getBoolean(PREF_KEY_SAVE_MODE, false);
   }

   public static void setCommitAfterInsertsCount(int commitAfterEveryInsertsCount)
   {
      Props.putInt(PREF_KEY_COMMIT_AFTER_INSERTS_COUNT, commitAfterEveryInsertsCount);
   }

   public static void setEmptyTableOnImport(boolean b)
   {
      Props.putBoolean(PREF_KEY_EMPTY_TABLE_ON_IMPORT, b);
   }

   public static void setHeadersIncluded(boolean b)
   {
      Props.putBoolean(PREF_KEY_HEADERS_INCLUDED, b);
   }

   public static void setSingleTransaction(boolean b)
   {
      Props.putBoolean(PREF_KEY_SINGLE_TRANSACTION, b);
   }

   public static void setSaveMode(boolean b)
   {
      Props.putBoolean(PREF_KEY_SAVE_MODE, b);
   }

   public static boolean isTrimValues()
   {
      return Props.getBoolean(PREF_KEY_TRIM_VALUES, true);
   }

   public static void setTrimValues(boolean b)
   {
      Props.putBoolean(PREF_KEY_TRIM_VALUES, b);
   }



   public static void setCSVSeparator(Character sep)
   {
      Props.putString(PREF_KEY_CSV_SEPARATOR,"" + sep);
   }

   public static Character getCSVSeparator()
   {
      String sep = Props.getString(PREF_KEY_CSV_SEPARATOR, getDefaultCsvSeparator(), true);

      if(("" + null).equals(sep))
      {
         return null;
      }

      return sep.charAt(0);
   }

   public static String getDefaultCsvSeparator()
   {
      return ";";
   }

   public static void setCSVDateFormat(String format)
   {
      Props.putString(PREF_KEY_CSV_DATE_FORMAT,"" + format);
   }

   public static String getCSVDateFormat()
   {
      return Props.getString(PREF_KEY_CSV_DATE_FORMAT, CSVSettingsBean.DEFAULT_DATE_FORMAT);
   }

   public static String getImportCharset()
   {
      return Props.getString(PREF_KEY_CSV_IMPORT_CHARSET, Charset.defaultCharset().name());
   }

   public static void setImportCharset(String importCharset)
   {
      Props.putString(PREF_KEY_CSV_IMPORT_CHARSET, importCharset);
   }

   public static String getTableNamePattern()
   {
      return Props.getString(PREF_TABLE_NAME_PATTERN, "@file");
   }

   public static void setSuggestTypes(boolean suggestColumnTypes)
   {
      Props.putBoolean(PREF_SUGGEST_TYPES, suggestColumnTypes);
   }

   public static void setTableNamePattern(String tableNamePattern)
   {
      Props.putString(PREF_TABLE_NAME_PATTERN, tableNamePattern);
   }

   public static boolean isSuggestColumnTypes()
   {
      return Props.getBoolean(PREF_SUGGEST_TYPES, false);
   }

   public static int getVarCharLength()
   {
      return Props.getInt(PREF_VARCHAR_LENGTH, 200);
   }

   public static void setVarCharLength(int varcharLength)
   {
      Props.putInt(PREF_VARCHAR_LENGTH, varcharLength);
   }

   public static int getNumericPrecision()
   {
      return Props.getInt(PREF_KEY_NUMERIC_PRECISION, 18);
   }

   public static void setNumericPrecision(int numericPrecision)
   {
      Props.putInt(PREF_KEY_NUMERIC_PRECISION, numericPrecision);
   }

   public static int getNumericScale()
   {
      return Props.getInt(PREF_KEY_NUMERIC_SCALE, 5);
   }

   public static void setNumericScale(int numericScale)
   {
      Props.putInt(PREF_KEY_NUMERIC_SCALE, numericScale);
   }

   public static void setUseDoubleQuotesAsTextQualifier(boolean useDoubleQuotesAsTextQualifier)
   {
      Props.putBoolean(PREF_KEY_CSV_USE_DOUBLE_QUOTES_AS_TEXT_QUALIFIER, useDoubleQuotesAsTextQualifier);
   }

   public static boolean isUseDoubleQuotesAsTextQualifier()
   {
      return Props.getBoolean(PREF_KEY_CSV_USE_DOUBLE_QUOTES_AS_TEXT_QUALIFIER, true);
   }
}
