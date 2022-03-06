package net.sourceforge.squirrel_sql.client.session.action.savedsession;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import java.util.ArrayList;
import java.util.List;

public class SavedSessionJsonBean
{
   private IIdentifier _defaultAliasId;
   private String _name;
   private List<SessionSqlJsonBean> _sessionSQLs = new ArrayList<>();

   public void setDefaultAliasId(IIdentifier defaultAliasId)
   {
      _defaultAliasId = defaultAliasId;
   }

   public IIdentifier getDefaultAliasId()
   {
      return _defaultAliasId;
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
}
