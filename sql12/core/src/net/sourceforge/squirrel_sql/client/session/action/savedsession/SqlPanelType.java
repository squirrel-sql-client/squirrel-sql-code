package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanelPosition;

public enum SqlPanelType
{
   MAIN_SQL_TAB(SQLPanelPosition.MAIN_TAB_IN_SESSION_WINDOW),
   SQL_TAB(SQLPanelPosition.ADDITIONAL_TAB_IN_SESSION_WINDOW),
   SQL_INTERNAL_FRAME(SQLPanelPosition.IN_SQL_WORKSHEET);

   private SQLPanelPosition _associatedPanelPosition;

   SqlPanelType(SQLPanelPosition associatedPanelPosition)
   {
      _associatedPanelPosition = associatedPanelPosition;
   }

   public SQLPanelPosition getAssociatedPanelPosition()
   {
      return _associatedPanelPosition;
   }
}
