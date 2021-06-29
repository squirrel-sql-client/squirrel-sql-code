package net.sourceforge.squirrel_sql.fw.resources;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Locale;

/**
 * Manages an icon scaling factor to adjust icon sizing for different
 * screen resolutions and text sizes used.  Setting the scale to follow
 * text ({@code "Label.font"}) size achieves proper icon sizing on
 * non-dpi-aware platforms, and matches the FlatLaf behavior.
 *
 * @see  <a href="https://github.com/JFormDesigner/FlatLaf/blob/1.2/flatlaf-core/src/main/java/com/formdev/flatlaf/util/UIScale.java"><code>com.formdev.flatlaf.util.UIScale</code></a>
 */
public final class IconScale
{
   private static ILogger log = LoggerController.createLogger(IconScale.class);

   private static double factor = 1.0;

   private static PropertyChangeListener textSizeListener = new PropertyChangeListener()
   {
      @Override public void propertyChange(PropertyChangeEvent evt)
      {
         switch (evt.getPropertyName())
         {
            case "lookAndFeel":
               if (evt.getNewValue() instanceof LookAndFeel)
                  UIManager.getLookAndFeelDefaults().addPropertyChangeListener(this);

               updateScaleFactor();
               break;

            case "Label.font":
               updateScaleFactor();
               break;
         }
      }
   };

   private IconScale()
   {
      // no instances
   }

   static void updateScaleFactor()
   {
      Font font = UIManager.getFont("Label.font");
      double baseFontSize = 12.0;

      if (SystemInfo.isWindows)
      {
         // Windows LaF uses Tahoma font rather than the actual Windows system font (Segoe UI),
         // and its size is always ca. 10% smaller than the actual system font size.
         // Tahoma 11 is used at 100%
         if ("Tahoma".equals(font.getFamily()))
            baseFontSize = 11.0;
      }
      else if (SystemInfo.isMacOS)
      {
         // default font size on macOS is 13
         baseFontSize = 13.0;
      }
      else if (SystemInfo.isLinux)
      {
         // default font size for Unity and Gnome is 15 and for KDE it is 13
         baseFontSize = SystemInfo.isKDE ? 13.0 : 15.0;
      }

      double scale = font.getSize() / baseFontSize;
      setFactor(scale > 1.0 ? Math.round(scale * 4.0) / 4.0 : 1.0);
   }

   public static void setFollowTextSize(boolean follow)
   {
      if (follow)
      {
         UIManager.addPropertyChangeListener(textSizeListener);
         UIManager.getDefaults().addPropertyChangeListener(textSizeListener);
         UIManager.getLookAndFeelDefaults().addPropertyChangeListener(textSizeListener);
         updateScaleFactor();
      }
      else
      {
         UIManager.removePropertyChangeListener(textSizeListener);
         UIManager.getDefaults().removePropertyChangeListener(textSizeListener);
         UIManager.getLookAndFeelDefaults().removePropertyChangeListener(textSizeListener);
         setFactor(1.0);
      }
   }

   public static double getFactor()
   {
      return factor;
   }

   private static void setFactor(double factor)
   {
      IconScale.factor = factor;
      log.info("Icon scale: " + factor);
   }

   public static int ceil(double size)
   {
      return (int) Math.ceil(size * getFactor());
   }

   public static int round(double size)
   {
      return (int) Math.round(size * getFactor());
   }

}


final class SystemInfo
{
   static boolean isWindows;
   static boolean isMacOS;
   static boolean isLinux;
   static boolean isKDE;
   static int javaVersion;

   static
   {
      String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
      isWindows = osName.startsWith("windows");
      isMacOS = osName.startsWith("mac");
      isLinux = osName.startsWith("linux");

      isKDE = (isLinux && System.getenv("KDE_FULL_SESSION") != null);

      javaVersion = Integer.parseInt(System.getProperty("java.specification.version")
                                           .replaceFirst("^(?:1\\.)?(\\d+)", "$1"));
   }

   private SystemInfo()
   {
      // no instances
   }
}
