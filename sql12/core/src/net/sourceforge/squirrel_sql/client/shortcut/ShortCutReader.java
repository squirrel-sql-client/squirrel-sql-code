package net.sourceforge.squirrel_sql.client.shortcut;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.resources.IResources;
import net.sourceforge.squirrel_sql.fw.resources.ResourceBundleHandler;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import javax.swing.Action;
import javax.swing.KeyStroke;
import java.util.MissingResourceException;

public class ShortCutReader
{
   private ResourceBundleHandler _bundleHandler;

   public ShortCutReader(ResourceBundleHandler bundleHandler)
   {
      _bundleHandler = bundleHandler;
   }

   public KeyStroke getDefaultShortcutAsKeyStroke(String fullKey, String actionName)
   {
      return KeyStroke.getKeyStroke(_getShortcutAsString(fullKey, actionName, null, true));
   }

   public KeyStroke getShortcutAsKeyStroke(String fullKey, Action action)
   {
      return KeyStroke.getKeyStroke(_getShortcutAsString(fullKey, (String) action.getValue(Action.NAME), action, false));
   }


   public String getShortcutAsString(String fullKey, Action action)
   {
      return _getShortcutAsString(fullKey, (String) action.getValue(Action.NAME), action, false);
   }


   /**
    * This method registers keyboard shortcuts and maybe replaces them by user defined shortcuts.
    *
    * The preferred way to read shortcuts is from resource files.
    * In case a short cut must be read from the action parameter it must be ensured that at first call the action
    * contains its default shortcut.
    *
    * When this method is called for a shortcut that was not yet registered with the shortcut manager, the shortcut is registered,
    * see {@link ShortcutManager#_registerAccelerator(String, KeyStroke)}
    *
    * @return If defaultShortCut = false maybe a user defined shortcut else the default shortcut.
    */
   private String _getShortcutAsString(String fullResourceKey, String actionName, Action action, boolean defaultShortCut)
   {
      String ret = getShortcutFromResource(fullResourceKey);

      if (StringUtilities.isEmpty(ret, true))
      {
         Object shortcutFromAction = getShortcutFromAction(action);
         if ( null != shortcutFromAction)
         {
            ret = shortcutFromAction.toString();
         }
      }

      if (false == defaultShortCut && false == StringUtilities.isEmpty(actionName, true))
      {
         // Possibly replace the standard shortcut with the user defined one.
         ret = Main.getApplication().getShortcutManager().registerAccelerator(actionName, KeyStroke.getKeyStroke(ret));
      }

      return ret;
   }


   private String getShortcutFromResource(String fullKey) throws MissingResourceException
   {
      try
      {
         return _bundleHandler.getResourceString(fullKey, IResources.MenuItemProperties.ACCELERATOR);
      }
      catch (MissingResourceException e)
      {
         // Some actions don't have accelerators
         return null;
      }
   }

   private Object getShortcutFromAction(Action action)
   {
      if(null == action)
      {
         return null;
      }

      return action.getValue(Action.ACCELERATOR_KEY);
   }
}
