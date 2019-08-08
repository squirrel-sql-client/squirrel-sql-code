package net.sourceforge.squirrel_sql.client.preferences;

public enum LocaleWrapperType
{
   LOCALE, DONT_CHANGE;

   public static boolean isNewOrOldDontChangeString(String langCountryPair)
   {
      return "Don't change".equalsIgnoreCase(langCountryPair) || DONT_CHANGE.name().equalsIgnoreCase(langCountryPair);
   }

}
