package net.sourceforge.squirrel_sql.fw.resources;

import net.sourceforge.squirrel_sql.fw.util.Utilities;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceBundleHandler
{
   private LazyResourceBundle _bundle;

   private final String _imagePath;

   public ResourceBundleHandler(String rsrcBundleBaseName, ClassLoader cl)
   {
      _bundle = new LazyResourceBundle(rsrcBundleBaseName, cl);

      _imagePath = _bundle.getString("path.images");
   }

   public String getImagePath()
   {
      return _imagePath;
   }

   public String getString(String key)
   {
      Utilities.checkNull("getString", "key", key);
      return _bundle.getString(key);
   }

   public String getResourceString(String keyName, String propName) throws MissingResourceException
   {
      return _bundle.getString(keyName + "." + propName);
   }
}
