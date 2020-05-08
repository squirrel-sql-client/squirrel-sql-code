package net.sourceforge.squirrel_sql.client.gui.desktopcontainer.docktabdesktop;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Toolkit;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @see  https://docs.oracle.com/javase/8/docs/api/java/awt/doc-files/DesktopProperties.html
 */
public final class FontDesktopHints
{
   private static final Map<Key, Object> DEFAULT_HINTS = Collections.singletonMap(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

   private static Map<String, Map<?, ?>> hintsCache = new HashMap<>();
   private static Set<String> cachedProperties = new HashSet<>();
   private static PropertyChangeListener cacheInvalidator = evt -> hintsCache.clear();

   private FontDesktopHints()
   {
      // No instances
   }

   private static Map<Key, Object> get(String deviceID)
   {
      Toolkit toolkit = Toolkit.getDefaultToolkit();

      String fontHintsName;
      if (deviceID == null)
      {
         fontHintsName = "awt.font.desktophints";
      }
      else
      {
         fontHintsName = "awt.font.desktophints." + deviceID;
      }

      if (cachedProperties.add(fontHintsName))
      {
         toolkit.addPropertyChangeListener(fontHintsName, cacheInvalidator);
      }
      return (Map<Key, Object>) toolkit.getDesktopProperty(fontHintsName);
   }

   public static void set(Graphics2D g)
   {
      String key = g.getDeviceConfiguration().getDevice().getIDstring();

      Map<?, ?> hints = hintsCache.computeIfAbsent(key, deviceID -> onComputeHint(deviceID));

      g.addRenderingHints(hints);
   }

   private static Map<?, ?> onComputeHint(String deviceID)
   {
      Map<Key, Object> textHints = Optional.ofNullable(get(deviceID)).orElseGet(() -> get(null));
      return Optional.ofNullable(textHints).orElse(DEFAULT_HINTS);
   }
}
