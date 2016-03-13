package org.squirrelsql.session.action;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
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
   RELOAD_DB_META_DATA("reload.png", "reload.meta.data", "reload", ActionScope.UNSCOPED, new KeyCodeCombination(KeyCode.F5), ActionDependency.SESSION),
   DUPLICATE_LINE_OR_SELECTION(null, "duplicate.line.or.selection", "duplicate", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.D, KeyCodeCombination.CONTROL_DOWN)),
   VIEW_IN_OBJECT_TREE(null, "view.object.at.caret.in.tree", "viewinobjecttree", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.B, KeyCodeCombination.CONTROL_DOWN)),
   SQL_REFORMAT(null, "format.sql", "formatsql", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN, KeyCodeCombination.ALT_DOWN)),
   RERUN_SQL(GlobalIconNames.RERUN, "sql.rerunsql", "rerunsql", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.ENTER, KeyCodeCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN), ActionDependency.SESSION),
   TOGGLE_UPPER_LOWER(null, "toggle.upper.lower.case", "upperlower", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.U, KeyCodeCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN)),
   TOGGLE_COMMENT_LINES(null, "toggle.comment.lines", "comment", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.SUBTRACT, KeyCodeCombination.CONTROL_DOWN)),
   TOGGLE_QUOTE_AS_JAVA_STRING(null, "quote.as.java.string", "quotestring", ActionScope.SQL_EDITOR, null),
   TOGGLE_QUOTE_AS_JAVA_SB(null, "quote.as.java.sb", "quotesb", ActionScope.SQL_EDITOR, null),

   TRANSACT_TOGGLE_AUTO_COMMIT("autocommit.png", "transact.toggle.autocommit", null, ActionScope.UNSCOPED, null, ActionDependency.SESSION, ActionType.TOGGLE),
   TRANSACT_COMMIT("commit.png", "transact.commit", null, ActionScope.UNSCOPED, null, ActionDependency.SESSION),
   TRANSACT_ROLLBACK("rollback.png", "transact.rollback", null, ActionScope.UNSCOPED, null, ActionDependency.SESSION),

   RECONNECT("reconnect.png", "reconnect.to.database", "reconnect", ActionScope.UNSCOPED, new KeyCodeCombination(KeyCode.T, KeyCodeCombination.CONTROL_DOWN, KeyCodeCombination.ALT_DOWN), ActionDependency.SESSION),

   FILE_NEW("filenew.png", "file.new", "filenew", ActionScope.SQL_EDITOR, null),
   FILE_SAVE("filesave.png", "file.save", "filesave", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN)),
   FILE_SAVE_AS("filesaveas.png", "file.save.as", "filesaveas", ActionScope.SQL_EDITOR, null),
   FILE_OPEN("fileopen.png", "file.open", "fileopen", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN)),
   FILE_APPEND("fileappend.png", "file.append", "fileappend", ActionScope.SQL_EDITOR, null),
   FILE_DISCONNECT("filedisconnect.png", "file.disconnect", "filedisconnect", ActionScope.SQL_EDITOR, null),

   SEARCH_IN_TEXT(GlobalIconNames.SEARCH, "text.search", "search", ActionScope.SQL_EDITOR, new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN)),


   /**
    * No real action, just the separator
    */
   SEPARATOR;

   public static StdActionCfg[] SESSION_TOOLBAR = new StdActionCfg[]
   {
         RUN_SQL,
         NEW_SQL_TAB,
         EXEC_BOOKMARK,
         EDIT_BOOKMARK,
         RELOAD_DB_META_DATA,
         SEPARATOR,
         TRANSACT_TOGGLE_AUTO_COMMIT,
         TRANSACT_COMMIT,
         TRANSACT_ROLLBACK,
         SEPARATOR,
         RECONNECT,
         SEPARATOR,
         FILE_SAVE,
         FILE_SAVE_AS,
         FILE_OPEN,
         FILE_NEW,
         FILE_APPEND,
         FILE_DISCONNECT,
         SEPARATOR,
         SEARCH_IN_TEXT
   };

   public static StdActionCfg[] SESSION_MENU = new StdActionCfg[]
   {
         RUN_SQL,
         NEW_SQL_TAB,
         EXEC_BOOKMARK,
         EDIT_BOOKMARK,
         RELOAD_DB_META_DATA,
         SEPARATOR,
         TRANSACT_TOGGLE_AUTO_COMMIT,
         TRANSACT_COMMIT,
         TRANSACT_ROLLBACK,
         SEPARATOR,
         RECONNECT,
         SEPARATOR,
         SEARCH_IN_TEXT

   };


   public static StdActionCfg[] SQL_EDITOR_RIGHT_MOUSE_MENU = new StdActionCfg[]
   {
         ESCAPE_DATE,
         SQL_TO_TABLE,
         EXEC_BOOKMARK,
         SHOW_TOOLS_POPUP,
         DUPLICATE_LINE_OR_SELECTION,
         VIEW_IN_OBJECT_TREE,
         SQL_REFORMAT,
         TOGGLE_UPPER_LOWER,
         TOGGLE_COMMENT_LINES,
         TOGGLE_QUOTE_AS_JAVA_STRING,
         TOGGLE_QUOTE_AS_JAVA_SB,
         SEPARATOR,
         SEARCH_IN_TEXT
   };

   private ActionCfg _actionCfg;

   StdActionCfg(String iconName, String i18nKeyOfText, String toolsPopUpSelector, ActionScope actionScope, KeyCodeCombination keyCodeCombination)
   {
      this(iconName, i18nKeyOfText, toolsPopUpSelector, actionScope, keyCodeCombination, ActionDependency.SESSION_TAB ,ActionType.NON_TOGGLE);
   }

   StdActionCfg(String iconName, String i18nKeyOfText, String toolsPopUpSelector, ActionScope actionScope, KeyCodeCombination keyCodeCombination, ActionDependency actionDependency)
   {
      this(iconName, i18nKeyOfText, toolsPopUpSelector, actionScope, keyCodeCombination, actionDependency ,ActionType.NON_TOGGLE);
   }

   StdActionCfg(String iconName, String i18nKeyOfText, String toolsPopUpSelector, ActionScope actionScope, KeyCodeCombination keyCodeCombination, ActionDependency actionDependency, ActionType actionType)
   {
      this(new ActionCfg(getImageOrNull(iconName), new I18n(StdActionCfg.class).t(i18nKeyOfText), toolsPopUpSelector, actionScope, keyCodeCombination, actionDependency, actionType));
   }

   StdActionCfg()
   {
      // To use only for the separator
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

   public boolean isToggleSelected()
   {
      return _actionCfg.isToggleSelected();
   }
}
