package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import net.sourceforge.squirrel_sql.client.session.action.savedsession.SavedSessionJsonBean;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class SavedSessionsGroupUtil
{
   public static boolean shouldForceToFocusActiveSqlEditor(SavedSessionsGroupJsonBean group, boolean activeSessionInGroup)
   {
      return null == group || activeSessionInGroup;
   }

   public static boolean shouldForceToFocusActiveSqlEditor(SavedSessionJsonBean savedSessionJsonBean)
   {
      return StringUtilities.isEmpty(savedSessionJsonBean.getGroupId(), true) ||  savedSessionJsonBean.isActiveSessionInGroup();
   }
}
