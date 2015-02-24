package org.squirrelsql;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class I18nCache
{
   private Map<String, Properties> _packageName_properties = Collections.synchronizedMap(new HashMap<>());


   public static final String I18N_PROPERTIES_FILE_NAME = "i18n.properties";

   public String getLocalizedString(Class clazz, String key, Object... params)
   {
      return _loadStateless(clazz, key, params, _packageName_properties);
   }

   public static String getLocalizedStringBootstrapSave(Class clazz, String key, Object... params)
   {
      return _loadStateless(clazz, key, params, new HashMap<>());
   }


   private static String _loadStateless(Class clazz, String key, Object[] params, Map<String, Properties> packageName_properties)
   {
      try
      {
         String packageName = clazz.getPackage().getName();
         Properties props = packageName_properties.get(packageName);

         if(null == props)
         {
            synchronized (packageName_properties)
            {
               props = packageName_properties.get(packageName);

               if (null == props)
               {
                  props = new Properties();
                  props.load(clazz.getResourceAsStream(I18N_PROPERTIES_FILE_NAME));
                  packageName_properties.put(packageName, props);
               }
            }
         }

         String ret = props.getProperty(key);

         if(null == ret)
         {
            ret = "Resource " + key + " missing in " + packageName + "." + I18N_PROPERTIES_FILE_NAME;
         }

         return String.format(ret, params);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}
