package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

public class SessionMcpState
{
   private final IIdentifier _sessionIdentifier;
   private AdditionalSQLTab _mcpSqlTab;
   private ISQLPanelAdapter _sqlPanelCloseListener;

   public SessionMcpState(IIdentifier sessionIdentifier)
   {
      _sessionIdentifier = sessionIdentifier;

      _sqlPanelCloseListener = new ISQLPanelAdapter()
      {

         @Override
         public void sqlEntryAreaClosed(SQLPanelEvent evt)
         {
            _mcpSqlTab = null;
            evt.getSourceSqlPanel().removeSQLPanelListener(_sqlPanelCloseListener);
         }
      };
   }

   public void setMcpSqlTab(AdditionalSQLTab mcpSqlTab)
   {
      if(null != _mcpSqlTab)
      {
         throw new IllegalStateException("Session already has an _mcpSqlTab");
      }

      _mcpSqlTab = mcpSqlTab;

      _mcpSqlTab.getSQLPanel().addSQLPanelListener(_sqlPanelCloseListener);

   }

   public AdditionalSQLTab getMcpSqlTab()
   {
      return _mcpSqlTab;
   }
}
