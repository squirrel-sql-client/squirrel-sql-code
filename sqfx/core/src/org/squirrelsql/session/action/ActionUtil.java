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
      return AppState.get().getActionManager().getSessionMenu();
   }

   public static ToolBar createToolbar()
   {
      return AppState.get().getActionManager().createToolbar();
   }

   public static void setActionScope(ActionScope actionScope)
   {
      AppState.get().getActionManager().setActionScope(actionScope);
   }

   public static ActionHandle getActionHandleForActiveOrActivatingSessionTabContext(ActionCfg actionCfg)
   {
      return AppState.get().getActionManager().getActionHandleForActiveOrActivatingSessionTabContext(actionCfg);
   }
   
   public static ActionHandle addActionToToolbar(ToolBar toolBar, ActionCfg actionCfg)
   {
	   return AppState.get().getActionManager().addActionToToolbar(toolBar, actionCfg);
   }

   public static ActionHandle getActionHandle(StdActionCfg stdActionCfg, SessionTabContext sessionTabContext)
   {
      return AppState.get().getActionManager().getActionHandle(stdActionCfg.getActionCfg(), sessionTabContext);
   }

   public static void updateActionUIs()
   {
      AppState.get().getActionManager().updateActionUIs();
   }

   public static List<ActionCfg> getSQLEditRightMouseActionCfgs()
   {
      return AppState.get().getActionManager().getSQLEditRightMouseActionCfgs();
   }

   public static List<ActionCfg> getAllActionCfgs()
   {
      return AppState.get().getActionManager().getAllActionCfgs();
   }
}
