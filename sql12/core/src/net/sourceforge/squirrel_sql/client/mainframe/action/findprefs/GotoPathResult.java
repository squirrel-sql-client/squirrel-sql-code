package net.sourceforge.squirrel_sql.client.mainframe.action.findprefs;

public enum GotoPathResult
{
   NO_ACTION_BECAUSE_NO_LEAF(false),
   NO_ACTION_BECAUSE_COMPONENT_NOT_FOUND(false),
   NO_ACTION_BECAUSE_DIALOG_NODE_SELECTED_TO_GO_TO(false),
   WENT_TO_COMPONENT_AND_BLINKED(true);

   private boolean _wentToComponent;

   GotoPathResult(boolean wentToComponent)
   {
      _wentToComponent = wentToComponent;
   }

   public boolean isWentToComponent()
   {
      return _wentToComponent;
   }
}
