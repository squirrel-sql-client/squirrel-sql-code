package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.sql.SQLException;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.client.session.mcp.server.McpCall;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public final class McpServerContext
{
   private final ISession session;
   private final AdditionalSQLTab mcpSqlTab;

   private final CallProtocolAndApproveHandler _protocolAndApproveHandler;

   public McpServerContext(ISession session, AdditionalSQLTab mcpSqlTab)
   {
      this.session = session;
      this.mcpSqlTab = mcpSqlTab;
      _protocolAndApproveHandler = new CallProtocolAndApproveHandler(session, mcpSqlTab);
   }

   public ISession getSession()
   {
      return session;
   }

   public AdditionalSQLTab getMcpSqlTab()
   {
      return mcpSqlTab;
   }

   //
   // Just delegates to methods of McpUiProps
   public static boolean isApproveAllAiCalls()
   {
      return McpUiProps.isApproveAllAiCalls();
   }

   public static boolean isApplyAliasesReadOnlyRules()
   {
      return McpUiProps.isApplyAliasesReadOnlyRules();
   }

   public String getDriverName()
   {
      try
      {
         return session.getSQLConnection().getSQLMetaData().getDriverName();
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
         return session.getSQLConnection().getSQLMetaData().getDriverVersion();
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
         return session.getSQLConnection().getSQLMetaData().getDatabaseProductName();
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
         return session.getSQLConnection().getSQLMetaData().getDatabaseProductVersion();
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
