package net.sourceforge.squirrel_sql.client.shortcut;

import javax.swing.KeyStroke;

public class ShortcutUtil
{
   public static String getKeystrokeString(KeyStroke keyStroke)
   {
      if(null == keyStroke)
      {
         return null;
      }

      return keyStroke.toString().replaceAll("pressed ", "");
   }

   static String generateKey(String actionName, KeyStroke defaultKeyStroke)
   {
      if (null != defaultKeyStroke)
      {
      return actionName + "@@@" + defaultKeyStroke.toString();
   }
      else
      {
         return actionName + "@@@" + null;
      }
   }
}
