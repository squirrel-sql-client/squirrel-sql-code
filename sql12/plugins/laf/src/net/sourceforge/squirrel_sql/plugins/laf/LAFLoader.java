package net.sourceforge.squirrel_sql.plugins.laf;

import net.sourceforge.squirrel_sql.fw.util.SquirrelURLClassLoader;
import net.sourceforge.squirrel_sql.fw.util.SystemInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.apache.commons.lang3.StringUtils;

import javax.swing.LookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;

public class LAFLoader
{
   private static ILogger s_log = LoggerController.createLogger(LAFLoader.class);

   public static LookAndFeel getLafOrDefaultMetalOnError(LAFPreferences prefs, SquirrelURLClassLoader lafClassLoader)
   {
      return _getLookAndFeel(prefs.getLookAndFeelClassName(), lafClassLoader, false);
   }


   public static LookAndFeel getLafOrNullOnError(String lafClassName, SquirrelURLClassLoader lafClassLoader)
   {
      return _getLookAndFeel(lafClassName, lafClassLoader, true);
   }


   private static LookAndFeel _getLookAndFeel(String lookAndFeelClassName, SquirrelURLClassLoader lafClassLoader, boolean readingLookAndFeelList)
   {
      LookAndFeel laf = new MetalLookAndFeel();

      if (false == MetalLookAndFeel.class.getName().equals(lookAndFeelClassName))
      {
         Class<?> lafClass;
         try
         {
            if (lafClassLoader != null)
            {
               lafClass = Class.forName(lookAndFeelClassName, true, lafClassLoader);
            }
            else
            {
               lafClass = Class.forName(lookAndFeelClassName);
            }

            if(   false == readingLookAndFeelList
               && isJGoodies(lookAndFeelClassName)
               && SystemInfo.isLinux())
            {
               String jgoodiesLinuxMsg =
                  "JGoddies L&F display error: JGoddies Look and Feels on Linux leave tab titles empty at several places. If you are using JGoddies on Linux please consider switching to a Metal or FlatLaf Look and Feel.\n" +
                  "Note: JGoodies does not offer open source updates for its Look and Feels anymore, see: http://www.jgoodies.com/downloads/libraries/";
               s_log.error(jgoodiesLinuxMsg);
            }

            laf = (LookAndFeel) lafClass.getDeclaredConstructor().newInstance();
         }
         catch (Throwable t)
         {
            if (readingLookAndFeelList)
            {
               if(false == isJGoodies(lookAndFeelClassName))
               {
                  s_log.warn("Failed to load Look and Feel class " + lookAndFeelClassName, t);
               }
               return null;
            }
            else
            {
               String jgoodiesWindowsLafErr =
                     "superclass access check failed: class com.jgoodies.looks.windows.WindowsLookAndFeel";

               if (StringUtils.containsIgnoreCase(t.toString(), jgoodiesWindowsLafErr))
               {
                  String jgoodiesMsg = "JGoodies WindowsLookAndFeel Java 17 error on MS Windows:\n" +
                        "Failed to load JGoodies WindowsLookAndFeel because it uses an internal com.sun API which from Java 17 on isn't accessible anymore.\n" +
                        "For a workaround add the Java-VM parameter --add-exports=java.desktop/com.sun.java.swing.plaf.windows=ALL-UNNAMED to your <SquirrelInstallationDir>\\squirrel-sql.bat\n" +
                        "For details see bug #1507 at SourceForge: https://sourceforge.net/p/squirrel-sql/bugs/1507\n" +
                        "Note that JGoodies does not offer open source updates for its Look and Feels anymore, see: http://www.jgoodies.com/downloads/libraries/\n" +
                        "Also note: JGoddies Look and Feels on Linux leave tab titles empty at several places. If you are using JGoddies please consider switching to a Metal or FlatLaf Look and Feel.\n" +
                        "Detailed error message:";
                  s_log.error(jgoodiesMsg, t);
               }
               else
               {
                  s_log.error("Failed to load Look and Feel class switching to Metal with Ocean theme.", t);
               }
            }
         }
      }

      return laf;
   }

   private static boolean isJGoodies(String lookAndFeelClassName)
   {
      return StringUtils.startsWithIgnoreCase(lookAndFeelClassName, "com.jgoodies");
   }
}
