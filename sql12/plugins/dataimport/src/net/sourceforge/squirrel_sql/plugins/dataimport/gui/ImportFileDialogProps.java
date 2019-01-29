package net.sourceforge.squirrel_sql.plugins.dataimport.gui;

import net.sourceforge.squirrel_sql.fw.props.Props;

public class ImportFileDialogProps
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



   public static void setCSVSeparator(char sep)
   {
      Props.putString(PREF_KEY_CSV_SEPARATOR,"" + sep);
   }

   public static char getCSVSeparator()
   {
      return Props.getString(PREF_KEY_CSV_SEPARATOR,";", true).charAt(0);
   }

   public static void setCSVDateFormat(String format)
   {
      Props.putString(PREF_KEY_CSV_DATE_FORMAT,"" + format);
   }

   public static String getCSVDateFormat()
   {
      return Props.getString(PREF_KEY_CSV_DATE_FORMAT,"yyyy-MM-dd HH:mm:ss");
   }
}
