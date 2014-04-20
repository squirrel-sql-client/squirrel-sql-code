package org.squirrelsql.session.action;

import javafx.scene.control.Menu;
import javafx.scene.control.ToolBar;
import org.squirrelsql.AppState;
import org.squirrelsql.session.SessionTabContext;


public class ActionManager
{
   public Menu getSessionMenu()
   {
      return AppState.get().getActionMangerImpl().getSessionMenu();
   }

   public ToolBar createToolbar()
   {
      return AppState.get().getActionMangerImpl().createToolbar();
   }

   public void setActionScope(ActionScope actionScope)
   {
      AppState.get().getActionMangerImpl().setActionScope(actionScope);
   }

   public ActionHandle getActionHandleForActiveOrActivatingSessionTabContext(StandardActionConfiguration standardActionConfiguration)
   {
      return AppState.get().getActionMangerImpl().getActionHandleForActiveOrActivatingSessionTabContext(standardActionConfiguration.getActionConfiguration());
   }

   public ActionHandle getActionHandle(StandardActionConfiguration standardActionConfiguration, SessionTabContext sessionTabContext)
   {
      return AppState.get().getActionMangerImpl().getActionHandle(standardActionConfiguration.getActionConfiguration(), sessionTabContext);
   }

   public void updateActionUIs()
   {
      AppState.get().getActionMangerImpl().updateActionUIs();
   }
}
