package org.squirrelsql.session.sql;

import org.squirrelsql.session.action.ActionCfg;
import org.squirrelsql.session.sql.filteredpopup.FilteredPopupEntry;

import java.util.List;
import java.util.stream.Collectors;

public class ToolsPopUpActionWrapper implements FilteredPopupEntry
{
   private ActionCfg _actionCfg;

   public ToolsPopUpActionWrapper(ActionCfg actionCfg)
   {
      _actionCfg = actionCfg;
   }


   @Override
   public String getSelShortcut()
   {
      return _actionCfg.getToolsPopUpSelector();
   }

   @Override
   public String getDescription()
   {
      return _actionCfg.getText();
   }

   public ActionCfg getActionCfg()
   {
      return _actionCfg;
   }

   public static List<ToolsPopUpActionWrapper> wrap(List<ActionCfg> toWrap)
   {
      return toWrap.stream().map(ToolsPopUpActionWrapper::new).collect(Collectors.toList());
   }
}
