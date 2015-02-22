package org.squirrelsql.session.sql;

import org.squirrelsql.AppState;
import org.squirrelsql.services.Utils;
import org.squirrelsql.session.SessionTabContext;
import org.squirrelsql.session.action.ActionCfg;
import org.squirrelsql.session.action.StdActionCfg;
import org.squirrelsql.session.sql.filteredpopup.FilteredPopup;

import java.util.List;
import java.util.stream.Collectors;

public class ToolsPopupManager
{
   private SQLTextAreaServices _sqlTextAreaServices;

   public ToolsPopupManager(SQLTextAreaServices sqlTextAreaServices, SessionTabContext sessionTabContext)
   {
      _sqlTextAreaServices = sqlTextAreaServices;
      StdActionCfg.SHOW_TOOLS_POPUP.setAction(this::showToolsPopup);
   }

   private void showToolsPopup()
   {
      List<ActionCfg> allActionsWithToolsPopUpSelector = getAllActionsWithToolsPopUpSelector();

      List<ToolsPopUpActionWrapper> wrappers = ToolsPopUpActionWrapper.wrap(allActionsWithToolsPopUpSelector);


      new FilteredPopup<>(_sqlTextAreaServices, "Tools won't be empty ;-)", wrappers, this::executeAction).showPopup();
   }

   private void executeAction(ToolsPopUpActionWrapper wrapper)
   {
      wrapper.getActionCfg().fire();
   }

   private List<ActionCfg> getAllActionsWithToolsPopUpSelector()
   {
      return AppState.get().getActionManager().getAllActionCfgs().stream().filter( ac -> false == Utils.isEmptyString(ac.getToolsPopUpSelector())).collect(Collectors.toList());
   }
}
