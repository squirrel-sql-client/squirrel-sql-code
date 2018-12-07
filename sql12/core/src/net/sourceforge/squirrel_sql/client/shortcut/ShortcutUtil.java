package net.sourceforge.squirrel_sql.client.shortcut;

import javax.swing.KeyStroke;

public class ShortcutUtil
{
   public static String getKeystrokeString(KeyStroke keyStroke)
   {
      return keyStroke.toString().replaceAll("pressed ", "");
   }

   static String generateKey(String actionName, KeyStroke defaultKeyStroke)
   {
      return actionName + "@@@" + defaultKeyStroke.toString();
   }
}
