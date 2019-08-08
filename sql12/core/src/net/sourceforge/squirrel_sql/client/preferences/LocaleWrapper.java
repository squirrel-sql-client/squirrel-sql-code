package net.sourceforge.squirrel_sql.client.preferences;

import net.sourceforge.squirrel_sql.fw.util.LocaleUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import org.apache.commons.lang.StringUtils;

import javax.swing.JComboBox;
import java.util.ArrayList;
import java.util.Locale;

public class LocaleWrapper
{
   private LocaleWrapperType _localeWrapperType = LocaleWrapperType.DONT_CHANGE;
   private String _localeString;

   private LocaleWrapper(String localeString)
   {
      this(localeString, LocaleWrapperType.LOCALE);
   }

   private LocaleWrapper(LocaleWrapperType localeWrapperType)
   {
      this(null, localeWrapperType);
   }

   private LocaleWrapper(String localeString, LocaleWrapperType localeWrapperType)
   {
      _localeString = localeString;
      _localeWrapperType = localeWrapperType;
   }

   /**
    * Builds a Locale from the user's preferred locale preference.
    *
    * @param prefs
    *           the user's preferences
    * @return a local object. If no preference is found then US English is the default.
    */
   public static Locale constructPreferredLocale(SquirrelPreferences prefs)
   {
      String langCountryPair = prefs.getPreferredLocale();

      if (LocaleWrapperType.isNewOrOldDontChangeString(langCountryPair))
      {
         return null;
      }
      else
      {
         if (shallBeInterpretedAsDefaultEnglishUS(langCountryPair))
         {
            langCountryPair = "en_US";
         }
         String[] parts = langCountryPair.split("_");
         if (parts.length == 2)
         {
            return new Locale(parts[0], parts[1]);
         }
         return new Locale(parts[0]);
      }

   }

   private static boolean shallBeInterpretedAsDefaultEnglishUS(String langCountryPair)
   {
      return StringUtils.isEmpty(langCountryPair);
   }

   @Override
   public String toString()
   {
      switch (_localeWrapperType)
      {
         case DONT_CHANGE:
            return StringManagerFactory.getStringManager(LocaleWrapper.class).getString("preferences.dont.change.locale");
         case LOCALE:
            return getLocaleDisplayString();
         default:
            throw new IllegalStateException("Should not land here. Type is: " + _localeWrapperType.name());
      }
   }

   private String getLocaleDisplayString()
   {
      if(shallBeInterpretedAsDefaultEnglishUS(_localeString))
      {
         return StringManagerFactory.getStringManager(LocaleWrapper.class).getString("preferences.default.locale.en_US");
      }

      return _localeString;
   }

   public static LocaleWrapper[] getAvailableLocaleWrappers()
   {
      ArrayList<LocaleWrapper> ret = new ArrayList<>();

      ret.add(new LocaleWrapper(LocaleWrapperType.DONT_CHANGE));
      for (String localeString : LocaleUtils.getAvailableLocaleStrings())
      {
         ret.add(new LocaleWrapper(localeString));
      }
      return ret.toArray(new LocaleWrapper[0]);
   }

   public static String getSelectedLocalePrefsString(JComboBox localeChooser)
   {
      String ret = LocaleWrapperType.DONT_CHANGE.name();

      LocaleWrapper selectedWrapper = (LocaleWrapper) localeChooser.getSelectedItem();

      if (null != selectedWrapper)
      {
         if (selectedWrapper._localeWrapperType == LocaleWrapperType.LOCALE)
         {
            ret = selectedWrapper._localeString;
         }
      }

      return ret;
   }

   public static void setSelectedLocalePrefsString(JComboBox localeChooser, String localePrefsString)
   {
      LocaleWrapper selectedWrapper = getDontChangeWrapperFromModel(localeChooser);

      for (int i = 0; i < localeChooser.getModel().getSize(); i++)
      {
         LocaleWrapper buf = (LocaleWrapper) localeChooser.getModel().getElementAt(i);

         if (   buf._localeWrapperType == LocaleWrapperType.LOCALE
             && isSameLocale(localePrefsString, buf))
         {
            selectedWrapper = buf;
            break;
         }
      }

      localeChooser.setSelectedItem(selectedWrapper);
   }

   private static boolean isSameLocale(String localePrefsString, LocaleWrapper buf)
   {
      if (StringUtilities.isEmpty(localePrefsString, true) && StringUtilities.isEmpty(buf._localeString, true))
      {
         // Empty string is one of the locales returned by Locale.getAvailableLocales().
         // To XMLBeanReader null and empty is the same.
         // That's why this if is here.
         return true;
      }


      return Utilities.equalsRespectNull(localePrefsString, buf._localeString);
   }

   private static LocaleWrapper getDontChangeWrapperFromModel(JComboBox localeChooser)
   {
      return (LocaleWrapper) localeChooser.getModel().getElementAt(0);
   }
}
