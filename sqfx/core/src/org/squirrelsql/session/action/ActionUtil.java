package org.squirrelsql.session.action;

import javafx.scene.control.Menu;
import javafx.scene.control.ToolBar;
import org.squirrelsql.AppState;
import org.squirrelsql.session.SessionTabContext;

import java.util.List;


public class ActionUtil
{
   public static Menu getSessionMenu()
   {
      return AppState.get().getActionManger().getSessionMenu();
   }

   public static ToolBar createToolbar()
   {
      return AppState.get().getActionManger().createToolbar();
   }

   public static void setActionScope(ActionScope actionScope)
   {
      AppState.get().getActionManger().setActionScope(actionScope);
   }

   public static ActionHandle getActionHandleForActiveOrActivatingSessionTabContext(ActionCfg actionCfg)
   {
      return AppState.get().getActionManger().getActionHandleForActiveOrActivatingSessionTabContext(actionCfg);
   }

   public static ActionHandle getActionHandle(StdActionCfg stdActionCfg, SessionTabContext sessionTabContext)
   {
      return AppState.get().getActionManger().getActionHandle(stdActionCfg.getActionCfg(), sessionTabContext);
   }

   public static void updateActionUIs()
   {
      AppState.get().getActionManger().updateActionUIs();
   }

   public static List<ActionCfg> getSQLEditRightMouseActionCfgs()
   {
      return AppState.get().getActionManger().getSQLEditRightMouseActionCfgs();
   }

   public static List<ActionCfg> getAllActionCfgs()
   {
      return AppState.get().getActionManger().getAllActionCfgs();
   }
}
