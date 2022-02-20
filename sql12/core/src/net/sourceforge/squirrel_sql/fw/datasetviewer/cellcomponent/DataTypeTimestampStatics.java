package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.util.ThreadSafeDateFormat;

import java.text.DateFormat;

public class DataTypeTimestampStatics
{
   // values for how to use timestamps in WHERE clauses
   public static final int DO_NOT_USE = 0;
   public static final int USE_JDBC_ESCAPE_FORMAT = 1;
   public static final int USE_STRING_FORMAT = 2;

   /** Default date format */
   static final int DEFAULT_LOCALE_FORMAT = DateFormat.SHORT;

   // flag for whether to use the default Java format (true)
   // or the Locale-dependent format (false)
   private boolean _useJavaDefaultFormat = true;

   // Whether to force user to enter dates in exact format or use heuristics to guess it
   private boolean _lenient = true;

   // which locale-dependent format to use; short, medium, long, or full
   private int _localeFormat = DEFAULT_LOCALE_FORMAT;

   // The DateFormat object to use for all locale-dependent formatting.
   // This is reset each time the user changes the previous settings.
   private ThreadSafeDateFormat _dateFormat = new ThreadSafeDateFormat(_localeFormat, _localeFormat);


   // Define whether or not to use Timestamp in internally generated WHERE
   // clauses, and if so what format to use.
   private int internalWhereClauseUsage = USE_JDBC_ESCAPE_FORMAT;
   private boolean _useThreeDigitMillis;

   private TemporalScriptGenerationFormat _timestampScriptFormat;


   public boolean isUseJavaDefaultFormat()
   {
      return _useJavaDefaultFormat;
   }

   public void setUseJavaDefaultFormat(boolean useJavaDefaultFormat)
   {
      this._useJavaDefaultFormat = useJavaDefaultFormat;
   }

   public boolean isLenient()
   {
      return _lenient;
   }

   public void setLenient(boolean lenient)
   {
      this._lenient = lenient;
   }

   public int getLocaleFormat()
   {
      return _localeFormat;
   }

   public void setLocaleFormat(int localeFormat)
   {
      this._localeFormat = localeFormat;
   }

   /**
    * The job of a setter is done by {@link #initDateFormat()}
    *
    * This getter returns null if {@link #_useJavaDefaultFormat} == false and {@link #_useThreeDigitMillis} == false
    */
   public ThreadSafeDateFormat getDateFormat()
   {
      return _dateFormat;
   }

   public void initDateFormat()
   {
      _dateFormat = null;

      if (false == _useJavaDefaultFormat)
      {
         _dateFormat = new ThreadSafeDateFormat(getLocaleFormat(), getLocaleFormat());
         _dateFormat.setLenient(_lenient);
      }
      else if(_useJavaDefaultFormat && _useThreeDigitMillis)
      {
         _dateFormat = new ThreadSafeDateFormat(ThreadSafeDateFormat.DEFAULT_WITH_THREE_MILLI_DIGITS);
      }
   }


   public int getInternalWhereClauseUsage()
   {
      return internalWhereClauseUsage;
   }

   public void setInternalWhereClauseUsage(int internalWhereClauseUsage)
   {
      this.internalWhereClauseUsage = internalWhereClauseUsage;
   }

   public boolean isUseThreeDigitMillis()
   {
      return _useThreeDigitMillis;
   }

   public void setUseThreeDigitMillis(boolean useThreeDigitMillis)
   {
      _useThreeDigitMillis = useThreeDigitMillis;
   }

   public TemporalScriptGenerationFormat getTimestampScriptFormat()
   {
      return _timestampScriptFormat;
   }

   public void setTimestampScriptFormat(TemporalScriptGenerationFormat timestampScriptFormat)
   {
      _timestampScriptFormat = timestampScriptFormat;
   }
}
