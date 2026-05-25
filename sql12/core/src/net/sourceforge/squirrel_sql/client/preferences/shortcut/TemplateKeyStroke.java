package net.sourceforge.squirrel_sql.client.preferences.shortcut;

import javax.swing.KeyStroke;
import org.apache.commons.lang3.StringUtils;

public class TemplateKeyStroke
{
   private final String actionName;
   private KeyStroke keyStroke;

   public TemplateKeyStroke(String actionName, String keyStrokeString)
   {
      this.actionName = actionName;

      if(false == StringUtils.isBlank(keyStrokeString))
      {
         this.keyStroke = KeyStroke.getKeyStroke(keyStrokeString);

         if(null == this.keyStroke)
         {
            throw new IllegalStateException("Failed to create KeyStroke from parameter keyStrokeString %s".formatted(keyStrokeString));
         }
      }
   }

   public String getActionName()
   {
      return actionName;
   }

   public KeyStroke getKeyStroke()
   {
      return keyStroke;
   }
}
