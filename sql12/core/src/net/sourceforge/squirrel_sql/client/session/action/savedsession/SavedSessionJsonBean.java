package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import java.util.ArrayList;
import java.util.List;

public class SavedSessionJsonBean
{
   private String _defaultAliasIdString;
   private String _name;
   private List<SessionSqlJsonBean> _sessionSQLs = new ArrayList<>();
   private String _groupId;
   private boolean _activeSessionInGroup;
   private String _aliasNameForDebug;

   public void setDefaultAliasIdString(String defaultAliasIdString)
   {
      _defaultAliasIdString = defaultAliasIdString;
   }

   public String getDefaultAliasIdString()
   {
      return _defaultAliasIdString;
   }

   public void setName(String name)
   {
      _name = name;
   }

   public String getName()
   {
      return _name;
   }

   public List<SessionSqlJsonBean> getSessionSQLs()
   {
      return _sessionSQLs;
   }

   public void setSessionSQLs(List<SessionSqlJsonBean> sessionSQLs)
   {
      _sessionSQLs = sessionSQLs;
   }

   @Override
   public String toString()
   {
      //return SavedSessionUtil.getDisplayString(this);
      return "ERROR: Call SavedSessionGrouped.toString() instead.";
   }

   public String getGroupId()
   {
      return _groupId;
   }

   public void setGroupId(String groupId)
   {
      _groupId = groupId;
   }

   public void setActiveSessionInGroup(boolean activeSessionInGroup)
   {
      _activeSessionInGroup = activeSessionInGroup;
   }

   public boolean isActiveSessionInGroup()
   {
      return _activeSessionInGroup;
   }

   public void setAliasNameForDebug(String aliasNameForDebug)
   {
      _aliasNameForDebug = aliasNameForDebug;
   }

   public String getAliasNameForDebug()
   {
      return _aliasNameForDebug;
   }
}
