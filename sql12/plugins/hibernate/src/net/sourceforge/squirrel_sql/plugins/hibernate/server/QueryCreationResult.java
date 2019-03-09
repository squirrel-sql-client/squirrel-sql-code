package net.sourceforge.squirrel_sql.plugins.hibernate.server;

public class QueryCreationResult
{
   private String _messagePanelInfoText;
   private ReflectionCaller _rcQuery;

   public void setMessagePanelInfoText(String messagePanelInfoText)
   {
      _messagePanelInfoText = messagePanelInfoText;
   }

   public String getMessagePanelInfoText()
   {
      return _messagePanelInfoText;
   }

   public void setRcQuery(ReflectionCaller rcQuery)
   {
      _rcQuery = rcQuery;
   }

   public ReflectionCaller getRcQuery()
   {
      return _rcQuery;
   }
}
