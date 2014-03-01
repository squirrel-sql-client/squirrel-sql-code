package net.sourceforge.squirrel_sql.plugins.hibernate.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class HqlQueryResult implements Serializable
{
   private List<ObjectSubstituteRoot> _queryResultList;
   private Throwable _exceptionOccuredWhenExecutingQuery;

   private HashMap<String, Throwable> _sessionAdminExceptions = new HashMap<String, Throwable>();
   private Integer _updateCount;
   private String _messagePanelInfoText;

   public void putSessionAdminException(String msgKey, Throwable t)
   {
      _sessionAdminExceptions.put(msgKey, t);
   }

   public void setQueryResultList(List<ObjectSubstituteRoot> list)
   {
      _queryResultList = list;
   }

   public List<ObjectSubstituteRoot> getQueryResultList()
   {
      return _queryResultList;
   }

   public void setExceptionOccuredWhenExecutingQuery(Throwable exceptionOccuredWhenExecutingQuery)
   {
      _exceptionOccuredWhenExecutingQuery = exceptionOccuredWhenExecutingQuery;
   }

   public Throwable getExceptionOccuredWhenExecutingQuery()
   {
      return _exceptionOccuredWhenExecutingQuery;
   }

   public HashMap<String, Throwable> getSessionAdminExceptions()
   {
      return _sessionAdminExceptions;
   }

   public void setUpdateCount(int updateCount)
   {
      _updateCount = updateCount;
   }

   public Integer getUpdateCount()
   {
      return _updateCount;
   }

   public void setMessagePanelInfoText(String messagePanelInfoText)
   {
      _messagePanelInfoText = messagePanelInfoText;
   }

   public String getMessagePanelInfoText()
   {
      return _messagePanelInfoText;
   }
}
