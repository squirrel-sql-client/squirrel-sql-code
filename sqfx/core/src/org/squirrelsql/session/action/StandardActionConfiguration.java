package org.squirrelsql.session.action;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.squirrelsql.Props;
import org.squirrelsql.services.I18n;

public enum StandardActionConfiguration
{
   RUN_SQL(new ActionConfiguration(Help.props.getImage("run.png"), Help.i18n.t("sql.run"), ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.SPACE, KeyCodeCombination.CONTROL_DOWN)));

   public static StandardActionConfiguration[] SESSION_TOOLBAR = new StandardActionConfiguration[]
   {
      RUN_SQL
   };

   public static StandardActionConfiguration[] SESSION_MENU = new StandardActionConfiguration[]
   {
         RUN_SQL
   };



   private static class Help
   {
      private static final I18n i18n = new I18n(StandardActionConfiguration.class);
      private static final Props props = new Props(StandardActionConfiguration.class);
   }

   private ActionConfiguration _actionConfiguration;

   StandardActionConfiguration(ActionConfiguration actionConfiguration)
   {
      _actionConfiguration = actionConfiguration;
   }

   public ActionConfiguration getActionConfiguration()
   {
      return _actionConfiguration;
   }
}
