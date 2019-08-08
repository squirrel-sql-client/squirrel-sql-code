package net.sourceforge.squirrel_sql.fw.resources;

import net.sourceforge.squirrel_sql.client.Application;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Needed in StringManager, which is usually used as a static member, to load the bundle late enough
 * to have the locale set by {@link Application#initResourcesAndPrefs()}.
 */
public class LazyResourceBundle
{
   private static boolean _localeInitialized;
   private final String _bundleBaseName;
   private final ClassLoader _loader;
   private ResourceBundle _bundle;

   public LazyResourceBundle(String bundleBaseName, ClassLoader loader)
   {
      _bundleBaseName = bundleBaseName;
      _loader = loader;
   }

   public static void setLocaleInitialized()
   {
      _localeInitialized = true;
   }

   public String getString(String key)
   {
      if(false == _localeInitialized)
      {
         throw new IllegalStateException("Must not create bundle before locale was initalized.");
      }

      if (null == _bundle)
      {
         _bundle = ResourceBundle.getBundle(_bundleBaseName, Locale.getDefault(), _loader);
      }

      return _bundle.getString(key);
   }
}
