package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.HashMap;


/**
 * This class is an indicator for all the places where the unstable feature
 * of re-reading results from the database are used.
 *
 * The feature is unstable because it tries to re-query the database for
 * data cells that it generally doesn't know the table of nor does generally
 * know unique row identifiers. This is especially true for SQL-Query results.
 */
public class LimitReadLengthFeatureUnstable
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(LimitReadLengthFeatureUnstable.class);

   private static final ILogger s_log = LoggerController.createLogger(LimitReadLengthFeatureUnstable.class);



   /**
    * default length of strings when truncated
    */
   final static int DEFAULT_LIMIT_READ_LENGTH = 100;
   /**
    * If <tt>_limitRead</tt> is <tt>true</tt> then this is how many characters
    * to read during the initial table load.
    */
   static int _limitReadLength = DEFAULT_LIMIT_READ_LENGTH;
   /**
    * If <tt>true</tt> then limit the size of string data that is read
    * during the initial table load.
    */
   static boolean _limitRead = false;
   /**
    * If <tt>_limitRead</tt> is <tt>true</tt> and
    * this is <tt>true</tt>, then only columns whose label is listed in
    * <tt>_limitReadColumnList</tt> are limited.
    */
   static boolean _limitReadOnSpecificColumns = false;
   /**
    * If <tt>_limitRead</tt> is <tt>true</tt> and
    * <tt>_limitReadOnSpecificColumns is <tt>true</tt>, then only columns whose label is listed here.
    * The column names are converted to ALL CAPS before being put on this list
    * so that they will match the label retrieved from _colDef.
    */
   static HashMap<String, String> _limitReadColumnNameMap = new HashMap<String, String>();

   public static void unknownTable()
   {
      String msg = s_stringMgr.getString("LimitReadLengthFeatureUnstable.unknown.table");
      Main.getApplication().getMessageHandler().showErrorMessage(msg);
      s_log.error(msg);
   }

   public static void someErrorReReading(Exception ex)
   {
      String msg = s_stringMgr.getString("LimitReadLengthFeatureUnstable.some.error");
      Main.getApplication().getMessageHandler().showErrorMessage(msg, ex);
      s_log.error(msg, ex);

   }

   public static String getUnstableWarningForGUI()
   {
      return s_stringMgr.getString("LimitReadLengthFeatureUnstable.ui.warning");
   }
}
