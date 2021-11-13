package net.sourceforge.squirrel_sql.plugins.laf;

import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

import javax.swing.plaf.metal.MetalTheme;
import java.util.Iterator;

public class MetalThemePreferencesUtil
{
   private static ILogger s_log = LoggerController.createLogger(MetalThemePreferencesUtil.class);

   public static final String DEFAULT_METAL_THEME_CLASS_NAME = "javax.swing.plaf.metal.OceanTheme";

   /**
    * Class has no package, see swingsetthemes.jar.
    */
   public static final String CHARCOAL_THEME_CLASS_NAME = "CharcoalTheme";

   public static MetalLookAndFeelController.MetalThemePreferences getMetalThemePreferences(LAFPlugin plugin, LAFRegister lafRegister)
   {
      try
      {
         MetalLookAndFeelController.MetalThemePreferences ret;

         XMLObjectCache cache = plugin.getSettingsCache();
         Iterator<?> it = cache.getAllForClass(MetalLookAndFeelController.MetalThemePreferences.class);
         if (it.hasNext())
         {
            ret = (MetalLookAndFeelController.MetalThemePreferences) it.next();
         }
         else
         {
            ret = new MetalLookAndFeelController.MetalThemePreferences();

            ClassLoader cl = lafRegister.getLookAndFeelClassLoader();
            Class<?> clazz = Class.forName(DEFAULT_METAL_THEME_CLASS_NAME, false, cl);
            MetalTheme theme = (MetalTheme) clazz.getDeclaredConstructor().newInstance();
            ret.setThemeName(theme.getName());

            try
            {
               cache.add(ret);
            }
            catch (DuplicateObjectException ex)
            {
               s_log.error("MetalThemePreferences object already in XMLObjectCache", ex);
            }
         }

         return ret;
      }
      catch (Exception e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }
}
