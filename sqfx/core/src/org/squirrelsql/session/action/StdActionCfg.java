package org.squirrelsql.session.action;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.squirrelsql.Props;
import org.squirrelsql.services.I18n;

public enum StdActionCfg
{
   RUN_SQL("run.png", "sql.run", "runsql", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.ENTER, KeyCodeCombination.CONTROL_DOWN)),
   NEW_SQL_TAB("newsqltab.png", "sql.newsqltab", null, ActionScope.UNSCOPED, new KeyCodeCombination(KeyCode.N, KeyCodeCombination.CONTROL_DOWN)),
   SQL_CODE_COMPLETION(null, "complete.code", "completecode", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.SPACE, KeyCodeCombination.CONTROL_DOWN)),
   EXEC_BOOKMARK("bookmark-exec.png", "bookmark.execute", "bookmarkexecute", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.J, KeyCodeCombination.CONTROL_DOWN)),
   EDIT_BOOKMARK("bookmark-edit.png", "bookmark.edit", "bookmarkedit", ActionScope.UNSCOPED, null),
   ESCAPE_DATE(null, "escape.date", "date", ActionScope.SQL_EDITOR, null),
   SQL_TO_TABLE(null, "sql.to.table", "sql2table", ActionScope.SQL_EDITOR, null),
   SHOW_TOOLS_POPUP(null, "show.tools.popup", null, ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.T, KeyCodeCombination.CONTROL_DOWN)),
   RELOAD_DB_META_DATA("reload.png", "reload.meta.data", "reload", ActionScope.UNSCOPED, new KeyCodeCombination(KeyCode.F5)),
   DUPLICATE_LINE_OR_SELECTION(null, "duplicate.line.or.selection", "duplicate", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.D, KeyCodeCombination.CONTROL_DOWN)),
   VIEW_IN_OBJECT_TREE(null, "view.object.at.caret.in.tree", "viewinobjecttree", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.B, KeyCodeCombination.CONTROL_DOWN)),
   SQL_REFORMAT(null, "format.sql", "formatsql", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN, KeyCodeCombination.ALT_DOWN)),

   TRANSACT_TOGGLE_AUTO_COMMIT("autocommit.png", "transact.toggle.autocommit", null, ActionScope.UNSCOPED, null, ActionType.TOGGLE),
   TRANSACT_COMMIT("commit.png", "transact.commit", null, ActionScope.UNSCOPED, null),
   TRANSACT_ROLLBACK("rollback.png", "transact.rollback", null, ActionScope.UNSCOPED, null);



   public static StdActionCfg[] SESSION_TOOLBAR = new StdActionCfg[]
   {
         RUN_SQL,
         NEW_SQL_TAB,
         EXEC_BOOKMARK,
         EDIT_BOOKMARK,
         RELOAD_DB_META_DATA,
         TRANSACT_TOGGLE_AUTO_COMMIT,
         TRANSACT_COMMIT,
         TRANSACT_ROLLBACK,
   };

   public static StdActionCfg[] SESSION_MENU = new StdActionCfg[]
   {
         RUN_SQL,
         NEW_SQL_TAB,
         EXEC_BOOKMARK,
         EDIT_BOOKMARK,
         RELOAD_DB_META_DATA,
         TRANSACT_TOGGLE_AUTO_COMMIT,
         TRANSACT_COMMIT,
         TRANSACT_ROLLBACK,

   };


   public static StdActionCfg[] SQL_EDITOR_RIGHT_MOUSE_MENU = new StdActionCfg[]
   {
         ESCAPE_DATE,
         SQL_TO_TABLE,
         EXEC_BOOKMARK,
         SHOW_TOOLS_POPUP,
         DUPLICATE_LINE_OR_SELECTION,
         VIEW_IN_OBJECT_TREE,
         SQL_REFORMAT
   };

   private ActionCfg _actionCfg;

   StdActionCfg(String iconName, String i18nKeyOfText, String toolsPopUpSelector, ActionScope actionScope, KeyCodeCombination keyCodeCombination)
   {
      this(iconName, i18nKeyOfText, toolsPopUpSelector, actionScope, keyCodeCombination, ActionType.NON_TOGGLE);
   }

   StdActionCfg(String iconName, String i18nKeyOfText, String toolsPopUpSelector, ActionScope actionScope, KeyCodeCombination keyCodeCombination, ActionType actionType)
   {
      this(new ActionCfg(getImageOrNull(iconName), new I18n(StdActionCfg.class).t(i18nKeyOfText), toolsPopUpSelector, actionScope, keyCodeCombination, actionType));
   }

   private static Image getImageOrNull(String iconName)
   {
      if (null == iconName)
      {
         return null;
      }
      else
      {
         return new Props(StdActionCfg.class).getImage(iconName);
      }
   }

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

   public void setDisable(boolean b)
   {
      _actionCfg.setDisable(b);
   }

   public void setToggleAction(SqFxToggleActionListener toggleActionListener)
   {
      _actionCfg.setToggleAction(toggleActionListener);
   }

   public void setToggleSelected(boolean toggleSelected)
   {
      _actionCfg.setToggleSelected(toggleSelected);
   }
}
