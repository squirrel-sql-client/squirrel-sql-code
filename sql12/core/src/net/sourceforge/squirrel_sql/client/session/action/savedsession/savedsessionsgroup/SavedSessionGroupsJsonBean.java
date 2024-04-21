package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import java.util.ArrayList;
import java.util.List;

public class SavedSessionGroupsJsonBean
{
   private List<SavedSessionsGroupJsonBean> _groups = new ArrayList<>();

   public List<SavedSessionsGroupJsonBean> getGroups()
   {
      return _groups;
   }

   public void setGroups(List<SavedSessionsGroupJsonBean> groups)
   {
      _groups = groups;
   }
}
