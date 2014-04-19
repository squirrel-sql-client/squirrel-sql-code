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

   public void setCurrentActionScope(ActionScope actionScope)
   {
      AppState.get().getActionMangerImpl().setCurrentActionScope(actionScope);
   }

   public ActionHandle getActionHandleForActiveOrActivatingSessionTabContext(StandardActionConfiguration standardActionConfiguration)
   {
      return AppState.get().getActionMangerImpl().getActionHandleForActiveOrActivatingSessionTabContext(standardActionConfiguration.getActionConfiguration());
   }

   public ActionHandle getActionHandle(StandardActionConfiguration standardActionConfiguration, SessionTabContext sessionTabContext)
   {
      return AppState.get().getActionMangerImpl().getActionHandle(standardActionConfiguration.getActionConfiguration(), sessionTabContext);
   }
}
