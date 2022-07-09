package net.sourceforge.squirrel_sql.client.session.mainpanel.changetrack;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.action.ChangeTrackAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;

public class ChangeTrackUtil
{
   public static void gitCommitSqlPanel(SQLPanel sqlPanel, boolean sqlPanelIsActive)
   {
      // Make GIT the current change track type.
      ChangeTrackTypeEnum.GIT.savePreference();

      //See ChangeTrackAction.changeTrackTypeChangedForCurrentSqlPanel(ChangeTrackTypeEnum)
      sqlPanel.getChangeTracker().changeTrackTypeChanged(ChangeTrackTypeEnum.GIT);

      if(sqlPanelIsActive)
      {
         // Needed to update the toolbar change tracking icon in case it wasn't already set to GIT.
         final ChangeTrackAction changeTrackAction = (ChangeTrackAction) Main.getApplication().getActionCollection().get(ChangeTrackAction.class);
         changeTrackAction.setChangeTrackTypeForCurrentSqlPanel(ChangeTrackTypeEnum.GIT);
      }

      sqlPanel.getChangeTracker().rebaseChangeTrackingOnToolbarButtonOrMenuClicked();
   }
}
