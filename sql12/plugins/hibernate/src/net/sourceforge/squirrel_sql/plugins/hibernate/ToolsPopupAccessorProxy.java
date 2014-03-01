package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupAccessor;
import net.sourceforge.squirrel_sql.client.gui.session.ToolsPopupController;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

import javax.swing.*;
import java.util.HashMap;

public class ToolsPopupAccessorProxy implements ToolsPopupAccessor
{
   private HashMap<String, Action> _key_action = new HashMap<String, Action>();

   public void addToToolsPopup(String key, Action action)
   {
      _key_action.put(key, action);
   }

   public void apply(HQLEntryPanelManager hqlEntryPanelManager)
   {
      for (String key : _key_action.keySet())
      {
         Action action = _key_action.get(key);
         hqlEntryPanelManager.addToSQLEntryAreaMenu(action, key);

         if (action instanceof SquirrelAction)
         {
            KeyStroke keyStroke = ((SquirrelAction)action).getKeyStroke();
            if (null != keyStroke)
            {
               hqlEntryPanelManager.registerKeyboardAction(action, keyStroke);
            }
         }
      }
   }
}
