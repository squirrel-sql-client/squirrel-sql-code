package net.sourceforge.squirrel_sql.client.shortcut;

import java.util.HashMap;
import java.util.Map;

public class ShortcutsJsonBean
{
   private Map<String,String> shortcutByKey = new HashMap();

   public Map<String,String> getShortcutByKey()
   {
      return shortcutByKey;
   }

   public void setShortcutByKey(Map<String,String> shortcutByKey)
   {
      this.shortcutByKey = shortcutByKey;
   }
}
