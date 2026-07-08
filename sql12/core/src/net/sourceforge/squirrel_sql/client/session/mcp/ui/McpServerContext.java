package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.sql.SQLException;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.client.session.mcp.server.McpCall;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public final class McpServerContext
{
   private final ISession _session;
   private final AdditionalSQLTab _mcpSqlTab;
   private final McpUiProps _mcpUiProps;

   private final CallProtocolAndApproveHandler _protocolAndApproveHandler;

   public McpServerContext(ISession session, AdditionalSQLTab mcpSqlTab, McpUiProps mcpUiProps)
   {
      this._session = session;
      this._mcpSqlTab = mcpSqlTab;
      this._mcpUiProps = mcpUiProps;
      _protocolAndApproveHandler = new CallProtocolAndApproveHandler(session, mcpSqlTab, mcpUiProps);
   }

   public ISession getSession()
   {
      return _session;
   }

   public AdditionalSQLTab getMcpSqlTab()
   {
      return _mcpSqlTab;
   }

   public McpUiProps getMcpUiProps()
   {
      return _mcpUiProps;
   }

   public String getDriverName()
   {
      try
      {
         return _session.getSQLConnection().getSQLMetaData().getDriverName();
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public String getDriverVersion()
   {
      try
      {
         return _session.getSQLConnection().getSQLMetaData().getDriverVersion();
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public String getDatabaseProductName()
   {
      try
      {
         return _session.getSQLConnection().getSQLMetaData().getDatabaseProductName();
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }

   public String getDatabaseProductVersion()
   {
      try
      {
         return _session.getSQLConnection().getSQLMetaData().getDatabaseProductVersion();
      }
      catch(SQLException e)
      {
         throw Utilities.wrapRuntime(e);
      }
   }


   public boolean callStart(McpCall call, Object callArgs)
   {
      return _protocolAndApproveHandler.callStart(call, callArgs);
   }

   public void callFinished(McpCall call)
   {
      _protocolAndApproveHandler.callFinished(call);
   }

   public void callFailed(McpCall call, Object callArgs, Exception e)
   {
      _protocolAndApproveHandler.callFailed(call, callArgs, e);
   }

}
