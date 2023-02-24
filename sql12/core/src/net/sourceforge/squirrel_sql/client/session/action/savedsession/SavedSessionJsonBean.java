package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import java.util.ArrayList;
import java.util.List;

public class SavedSessionJsonBean
{
   private String _defaultAliasIdString;
   private String _name;
   private List<SessionSqlJsonBean> _sessionSQLs = new ArrayList<>();

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
      return SavedSessionUtil.getDisplayString(this);
   }
}
