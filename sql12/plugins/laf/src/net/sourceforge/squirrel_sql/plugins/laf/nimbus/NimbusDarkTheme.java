package net.sourceforge.squirrel_sql.plugins.laf.nimbus;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.net.URL;

/**
 * Encapsulate Nimbus dark theme configuration logic
 *
 * @author Wayne Zhang
 */
public class NimbusDarkTheme
{
   private final static ILogger s_log = LoggerController.createLogger(NimbusLookAndFeelController.class);

   public static void configDarkTheme()
   {
      if (UIManager.getLookAndFeel() instanceof NimbusLookAndFeel)
      {
         configColor("control", Color.gray);
         configColor("info", Color.gray);
         configColor("nimbusBase", new Color(18, 30, 49));
         configColor("nimbusAlertYellow", new Color(248, 187, 0));
         configColor("nimbusDisabledText", new Color(100, 100, 100));
         configColor("nimbusFocus", new Color(115, 164, 209));
         configColor("nimbusGreen", new Color(176, 179, 50));
         configColor("nimbusInfoBlue", new Color(66, 139, 221));
         configColor("nimbusLightBackground", new Color(18, 30, 49));
         configColor("nimbusOrange", new Color(191, 98, 4));
         configColor("nimbusRed", new Color(169, 46, 34));
         configColor("nimbusSelectedText", Color.white);
         configColor("nimbusSelectionBackground", new Color(104, 93, 156));
         configColor("text", new Color(230, 230, 230));
         configColor("Tree.foreground", new Color(255, 255, 255));

         try
         {
            configTreeIcon();
         }
         catch (Exception e)
         {
            s_log.error("Failed to load JTree icons from Java's standard location", e);
         }
      }
   }

   /**
    * Configure color for the given theme key, for example, disabled text. It
    * will load configuration on system properties first, give it a change to
    * override configuration by system properties without change this class.
    *
    * @param key          key of the theme attribute
    * @param defaultValue the default color if it is not defined by system
    *                     property
    */
   private static void configColor(String key, Color defaultValue)
   {
      Color color = defaultValue;
      String systemPropertyColor = System.getProperty(key);
      if (systemPropertyColor != null && !systemPropertyColor.isEmpty())
      {
         try
         {
            color = buildColor(systemPropertyColor);
         }
         catch (Exception e)
         {
            s_log.error("Failed to build colors. Default colors will be used", e);
         }
      }

      UIManager.put(key, color);
   }


   /**
    * Build Color object by parsing color value
    *
    * @param colorValue color value, in format (R,G,B) or R,G,B
    * @return
    */
   private static Color buildColor(String colorValue)
   {
      if (colorValue == null || colorValue.isEmpty())
      {
         throw new RuntimeException("Color value empty");
      }

      String cv = colorValue.trim();
      if (cv.startsWith("("))
      {
         cv = cv.substring(1).trim();
      }
      if (cv.endsWith(")"))
      {
         cv = cv.substring(0, cv.length() - 1).trim();
      }

      String[] rgb = cv.split(",");
      if (rgb.length != 3)
      {
         throw new RuntimeException("Color (" + colorValue + ") format error");
      }

      return new Color(Integer.valueOf(rgb[0].trim()),
            Integer.valueOf(rgb[1].trim()),
            Integer.valueOf(rgb[2].trim())
      );
   }

   /**
    * Rest theme to system default
    *
    * @param window parent window need to refresh when theme changed.
    */
   public static void resetDefaultTheme(Window window)
   {
      if (UIManager.getLookAndFeel() instanceof NimbusLookAndFeel)
      {
         SwingUtilities.invokeLater(() ->
         {
            UIManager.getDefaults().putAll(UIManager.getLookAndFeel().getDefaults());

            // referesh Window to apply the theme change!
            SwingUtilities.updateComponentTreeUI(window);
         });
      }
   }

   /**
    * NetBeans project/files tree expand/close icon color is too dark, it is
    * not visible almost.
    * <p>
    * Set to icons copied from Eclipse dark mode.
    */
   private static void configTreeIcon()
   {
      // clear the default, default value used for unknow reason
      UIManager.getDefaults().remove("Tree.collapsedIcon");
      UIManager.getDefaults().remove("Tree.expandedIcon");

      // open/close icon image files in the Java package are for dark mode only
      UIManager.put("Tree.collapsedIcon", loadImage("close.gif"));
      UIManager.put("Tree.expandedIcon", loadImage("open.gif"));
   }

   private static ImageIcon loadImage(String file)
   {
      // load icon/image files that in the same package of this class,
      // by Java classpath resource.
      URL fileUrl = NimbusDarkTheme.class.getResource(file);
      
      return new ImageIcon(fileUrl);
   }
}
