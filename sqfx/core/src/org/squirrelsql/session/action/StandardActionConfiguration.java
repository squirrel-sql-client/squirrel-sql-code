package org.squirrelsql.session.action;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.squirrelsql.Props;
import org.squirrelsql.services.I18n;

public enum StandardActionConfiguration
{
   RUN_SQL(new ActionConfiguration(Help.props.getImage("run.png"), Help.i18n.t("sql.run"), ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.ENTER, KeyCodeCombination.CONTROL_DOWN))),
   NEW_SQL_TAB(new ActionConfiguration(Help.props.getImage("newsqltab.png"), Help.i18n.t("sql.newsqltab"), ActionScope.UNSCOPED, new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN))),
   SQL_CODE_CONPLETION(new ActionConfiguration(null, null, ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.SPACE, KeyCodeCombination.CONTROL_DOWN))),
   EXEC_BOOKMARK(new ActionConfiguration(Help.props.getImage("bookmark-exec.png"), Help.i18n.t("bookmark.execute"), ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.J, KeyCodeCombination.CONTROL_DOWN))), // Should be J instead of K but because of KeyMatchWA.checkFunnyCharacterShiftingByCtrl() J is the same as Enter
   EDIT_BOOKMARK(new ActionConfiguration(Help.props.getImage("bookmark-edit.png"), Help.i18n.t("bookmark.edit"), ActionScope.UNSCOPED, null));

   public static StandardActionConfiguration[] SESSION_TOOLBAR = new StandardActionConfiguration[]
   {
      RUN_SQL,
      NEW_SQL_TAB,
      EXEC_BOOKMARK,
      EDIT_BOOKMARK

   };

   public static StandardActionConfiguration[] SESSION_MENU = new StandardActionConfiguration[]
   {
         RUN_SQL,
         NEW_SQL_TAB,
         EXEC_BOOKMARK,
         EDIT_BOOKMARK
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
