package net.sourceforge.squirrel_sql.client.session.action.savedsession.savedsessionsgroup;

import java.rmi.server.UID;

public class SavedSessionsGroupJsonBean
{
   private String _groupId = new UID().toString();
   private String _groupName;

   public SavedSessionsGroupJsonBean()
   {
   }

   public String getGroupId()
   {
      return _groupId;
   }

   public void setGroupId(String groupId)
   {
      _groupId = groupId;
   }

   public String getGroupName()
   {
      return _groupName;
   }

   public void setGroupName(String groupName)
   {
      _groupName = groupName;
   }

}
