package net.sourceforge.squirrel_sql.fw.gui.stdtextpopup;

import net.sourceforge.squirrel_sql.client.Main;

import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

public class TextActionHelper
{
   private Action _action;
   private String _actionName;
   private KeyStroke _keyStroke;
   private String _defaultEditorKitActionName;

   public TextActionHelper(Action action, String actionName, KeyStroke defaultKeyStroke, String defaultEditorKitActionName)
   {
      _action = action;
      _actionName = actionName;
      _defaultEditorKitActionName = defaultEditorKitActionName;

      _keyStroke = getKeyStroke(defaultEditorKitActionName, defaultKeyStroke);
      action.putValue(Action.NAME, actionName);
      action.putValue(Action.ACCELERATOR_KEY, _keyStroke);
   }

   public void initKeyStroke(JTextComponent comp)
   {
      if(null == comp)
      {
         return;
      }

      comp.getInputMap().put(_keyStroke, _defaultEditorKitActionName);
   }

   public static KeyStroke getKeyStroke(String defaultEditorKitActionName, KeyStroke defaultKeyStroke)
   {
      return KeyStroke.getKeyStroke(Main.getApplication().getShortcutManager().registerAccelerator(defaultEditorKitActionName, defaultKeyStroke));
   }
}
