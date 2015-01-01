package org.squirrelsql.session.action;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.squirrelsql.Props;
import org.squirrelsql.services.I18n;

public enum StdActionCfg
{
   RUN_SQL(new ActionCfg(Help.props.getImage("run.png"), Help.i18n.t("sql.run"), ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.ENTER, KeyCodeCombination.CONTROL_DOWN))),
   NEW_SQL_TAB(new ActionCfg(Help.props.getImage("newsqltab.png"), Help.i18n.t("sql.newsqltab"), ActionScope.UNSCOPED, new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN))),
   SQL_CODE_COMPLETION(new ActionCfg(null, null, ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.SPACE, KeyCodeCombination.CONTROL_DOWN))),
   EXEC_BOOKMARK(new ActionCfg(Help.props.getImage("bookmark-exec.png"), Help.i18n.t("bookmark.execute"), ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.J, KeyCodeCombination.CONTROL_DOWN))),
   EDIT_BOOKMARK(new ActionCfg(Help.props.getImage("bookmark-edit.png"), Help.i18n.t("bookmark.edit"), ActionScope.UNSCOPED, null)),
   ESCAPE_DATE(new ActionCfg(null, Help.i18n.t("escape.date"), ActionScope.SQL_EDITOR, null)),
   SQL_TO_TABLE(new ActionCfg(null, Help.i18n.t("sql.to.table"), ActionScope.SQL_EDITOR, null));



   public static StdActionCfg[] SESSION_TOOLBAR = new StdActionCfg[]
   {
      RUN_SQL,
      NEW_SQL_TAB,
      EXEC_BOOKMARK,
      EDIT_BOOKMARK

   };

   public static StdActionCfg[] SESSION_MENU = new StdActionCfg[]
   {
         RUN_SQL,
         NEW_SQL_TAB,
         EXEC_BOOKMARK,
         EDIT_BOOKMARK
   };


   public static StdActionCfg[] SQL_EDITOR_RIGHT_MOUSE_MENU = new StdActionCfg[]
   {
      ESCAPE_DATE,
      SQL_TO_TABLE
   };

   private static class Help
   {
      private static final I18n i18n = new I18n(StdActionCfg.class);
      private static final Props props = new Props(StdActionCfg.class);
   }

   private ActionCfg _actionCfg;

   StdActionCfg(ActionCfg actionCfg)
   {
      _actionCfg = actionCfg;
   }

   public ActionCfg getActionCfg()
   {
      return _actionCfg;
   }


   /**
    * Shortcut method to set an action for the current SessionTabContext
    * @param sqFxActionListener
    */
   public void setAction(SqFxActionListener sqFxActionListener)
   {
      _actionCfg.setAction(sqFxActionListener);
   }

}
