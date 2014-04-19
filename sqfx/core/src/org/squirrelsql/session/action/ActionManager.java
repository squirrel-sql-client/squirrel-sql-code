package org.squirrelsql.session.action;

import javafx.scene.control.Menu;
import javafx.scene.control.ToolBar;
import org.squirrelsql.AppState;


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

   public ActionHandle getActionHandleForActiveOrActivatingSessionContext(StandardActionConfiguration standardActionConfiguration)
   {
      return AppState.get().getActionMangerImpl().getActionHandleForActiveOrActivatingSessionContext(standardActionConfiguration.getActionConfiguration());
   }
}
