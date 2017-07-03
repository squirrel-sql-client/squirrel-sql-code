package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class FloatingPointBaseDTProperties
{
   public static final String KEY_USE_LOCALE_FORMAT = "useLocaleFormat";
   public static final String KEY_USE_USER_DEFINED_FORMAT = "useUserDefinedFormat";
   public static final String KEY_USE_JAVA_DEFAULT_FORMAT = "useJavaDefaultFormat";
   public static final String KEY_MINIMUM_FRACTION_DIGITS = "minimumFractionDigits";
   public static final String KEY_MAXIMUM_FRACTION_DIGITS = "maximumFractionDigits";
   public static final String KEY_USER_DEFINDED_MAXIMUM_FRACTION_DIGITS = "userDefindedMaximumFractionDigits";
   public static final String KEY_USER_DEFINDED_MINIMUM_FRACTION_DIGITS = "userDefindedMinimumFractionDigits";
   public static final String KEY_USER_DEFINED_DECIMAL_SEPARATOR = "userDefinedDecimalSeparator";
   public static final String KEY_USER_DEFINED_GROUPING_SEPARATOR = "userDefinedGroupingSeparator";


   private static final int DEFAULT_MINIMUM_FRACTION_DIGITS = 0;
   private static final int DEFAULT_MAXIMUM_FRACTION_DIGITS = 5;

   private static int _minimumFractionDigits;
   private static int _maximumFractionDigits;


   private static boolean _useLocaleFormat = false;
   private static boolean _useUserDefinedFormat = false;
   private static boolean _useJavaDefaultFormat = false;


   private static int _userDefinedMinimumFractionDigits;
   private static int _userDefinedMaximumFractionDigits;

   private static String _userDefinedGroupingSeparator;
   private static String _userDefinedDecimalSeparator;


   private static boolean propertiesAlreadyLoaded = false;


   public static boolean isUseLocaleFormat()
   {
      return _useLocaleFormat;
   }

   public static boolean isUseJavaDefaultFormat()
   {
      return _useJavaDefaultFormat;
   }

   public static boolean isUseUserDefinedFormat()
   {
      return _useUserDefinedFormat;
   }

   /**
    * Minimum number of digits allowed in the fraction portion of a number.
    */
   public static int getMinimumFractionDigits()
   {
      return _minimumFractionDigits;
   }

   /**
    * How many Digits after the comma should be shown?
    */
   public static int getMaximumFractionDigits()
   {
      return _maximumFractionDigits;
   }


   public static int getUserDefinedMinimumFractionDigits()
   {
      return _userDefinedMinimumFractionDigits;
   }

   public static int getUserDefinedMaximumFractionDigits()
   {
      return _userDefinedMaximumFractionDigits;
   }



   public static String getUserDefinedGroupingSeparator()
   {
      return _userDefinedGroupingSeparator;
   }

   public static String getUserDefinedDecimalSeparator()
   {
      return _userDefinedDecimalSeparator;
   }


   private static Integer toInteger(String key, int defaultValue)
   {
      String value = DTProperties.get(DataTypeBigDecimal.class.getName(), key);

      if(null == value)
      {
         return defaultValue;
      }

      return Integer.valueOf(value);
   }

   private static boolean toBoolean(String key, boolean defaultValue)
   {

      String value = DTProperties.get(DataTypeBigDecimal.class.getName(), key);

      if (null == value)
      {
         return defaultValue;
      }

      return Boolean.parseBoolean(value);
   }


   private static String _toString(String key, String defaultValue)
   {
      String value = DTProperties.get(DataTypeBigDecimal.class.getName(), key);

      if (StringUtilities.isEmpty(value, true))
      {
         return defaultValue;
      }

      return value;
   }


   public static void setUseJavaDefaultFormat(boolean useJavaDefaultFormat)
   {
      DTProperties.put(DataTypeBigDecimal.class.getName(),
            KEY_USE_JAVA_DEFAULT_FORMAT,
            Boolean.valueOf(useJavaDefaultFormat).toString());

      _useJavaDefaultFormat = useJavaDefaultFormat;
   }

   public static void setUseLocaleFormat(boolean useLocaleFormat)
   {
      DTProperties.put(DataTypeBigDecimal.class.getName(),
            KEY_USE_LOCALE_FORMAT,
            Boolean.valueOf(useLocaleFormat).toString());

      _useLocaleFormat = useLocaleFormat;
   }

   public static void setUseUserDefinedFormat(boolean useUserDefinedFormat)
   {
      DTProperties.put(DataTypeBigDecimal.class.getName(),
            KEY_USE_USER_DEFINED_FORMAT,
            Boolean.valueOf(useUserDefinedFormat).toString());

      _useUserDefinedFormat = useUserDefinedFormat;
   }



   public static void setMinimumFractionDigits(int minimumFractionDigits)
   {
      DTProperties.put(DataTypeBigDecimal.class.getName(),
            KEY_MINIMUM_FRACTION_DIGITS,
            Integer.valueOf(minimumFractionDigits).toString());

      _minimumFractionDigits = minimumFractionDigits;
   }

   public static void setMaximumFractionDigits(int maximumFractionDigits)
   {
      DTProperties.put(DataTypeBigDecimal.class.getName(),
            KEY_MAXIMUM_FRACTION_DIGITS,
            Integer.valueOf(maximumFractionDigits).toString());

      _maximumFractionDigits = maximumFractionDigits;
   }


   public static void setUserDefinedMinimumFractionDigits(int userDefinedminimumFractionDigits)
   {
      DTProperties.put(DataTypeBigDecimal.class.getName(),
            KEY_USER_DEFINDED_MINIMUM_FRACTION_DIGITS,
            Integer.valueOf(userDefinedminimumFractionDigits).toString());

      _userDefinedMinimumFractionDigits = userDefinedminimumFractionDigits;

   }

   public static void setUserDefinedMaximumFractionDigits(int userDefinedMaximumFractionDigits)
   {
      DTProperties.put(DataTypeBigDecimal.class.getName(),
            KEY_USER_DEFINDED_MAXIMUM_FRACTION_DIGITS,
            Integer.valueOf(userDefinedMaximumFractionDigits).toString());

      _userDefinedMaximumFractionDigits = userDefinedMaximumFractionDigits;
   }



   public static void setUserDefinedDecimalSeparator(String userDefinedDecimalSeparator)
   {
      DTProperties.put(DataTypeBigDecimal.class.getName(), KEY_USER_DEFINED_DECIMAL_SEPARATOR, userDefinedDecimalSeparator);
      _userDefinedDecimalSeparator = userDefinedDecimalSeparator;
   }

   public static void setUserDefinedGroupingSeparator(String userDefinedGroupingSeparator)
   {
      DTProperties.put(DataTypeBigDecimal.class.getName(), KEY_USER_DEFINED_GROUPING_SEPARATOR, userDefinedGroupingSeparator);
      _userDefinedGroupingSeparator = userDefinedGroupingSeparator;
   }



   static void loadProperties()
   {

      //set the property values
      // Note: this may have already been done by another instance of
      // this DataType created to handle a different column.
      if (propertiesAlreadyLoaded == false)
      {
         _useLocaleFormat = toBoolean(KEY_USE_LOCALE_FORMAT, true);
         _useUserDefinedFormat = toBoolean(KEY_USE_USER_DEFINED_FORMAT, true);
         _useJavaDefaultFormat = toBoolean(KEY_USE_JAVA_DEFAULT_FORMAT, true);


         _minimumFractionDigits = toInteger(KEY_MINIMUM_FRACTION_DIGITS, DEFAULT_MINIMUM_FRACTION_DIGITS);
         _maximumFractionDigits = toInteger(KEY_MAXIMUM_FRACTION_DIGITS, DEFAULT_MAXIMUM_FRACTION_DIGITS);

         _userDefinedMaximumFractionDigits = toInteger(KEY_USER_DEFINDED_MAXIMUM_FRACTION_DIGITS, DEFAULT_MAXIMUM_FRACTION_DIGITS);
         _userDefinedMinimumFractionDigits = toInteger(KEY_USER_DEFINDED_MINIMUM_FRACTION_DIGITS, DEFAULT_MINIMUM_FRACTION_DIGITS);


         _userDefinedDecimalSeparator = _toString(KEY_USER_DEFINED_DECIMAL_SEPARATOR, ".");
         _userDefinedGroupingSeparator  = _toString(KEY_USER_DEFINED_GROUPING_SEPARATOR, ",");


         propertiesAlreadyLoaded = true;
      }
   }
}
