package net.sourceforge.squirrel_sql.plugins.laf;

import javax.swing.LookAndFeel;
import javax.swing.UIManager.LookAndFeelInfo;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Placeholder that allows SQuirreL to populate the LAF chooser with the LAF name.
 */
public abstract class PlaceholderLookAndFeel extends LookAndFeel
{
   private static Map<Class<? extends LookAndFeel>, LookAndFeelInfo> infos = new ConcurrentHashMap<>();

   protected PlaceholderLookAndFeel(String name)
   {
      infos.computeIfAbsent(getClass(), k -> new LookAndFeelInfo(name, k.getName()));
   }

   public static Collection<LookAndFeelInfo> getInfos()
   {
      return Collections.unmodifiableCollection(infos.values());
   }

   public static boolean isPlaceholder(String className)
   {
      for (Class<?> klass : infos.keySet())
      {
         if (klass.getName().equals(className))
            return true;
      }
      return false;
   }

   @Override
   public String getName()
   {
      return infos.get(getClass()).getName();
   }

   @Override
   public String getID()
   {
      return getName();
   }

   @Override
   public String getDescription()
   {
      return getName();
   }

   @Override
   public boolean isNativeLookAndFeel()
   {
      return false;
   }

   @Override
   public boolean isSupportedLookAndFeel()
   {
      return false;
   }

   public LookAndFeelInfo getLookAndFeelInfo()
   {
      return infos.get(getClass());
   }

}
