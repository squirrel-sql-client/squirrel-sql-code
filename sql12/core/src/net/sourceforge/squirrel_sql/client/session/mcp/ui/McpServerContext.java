package net.sourceforge.squirrel_sql.client.session.mcp.ui;

import java.sql.SQLException;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.sqltab.AdditionalSQLTab;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public record McpServerContext(ISession session, AdditionalSQLTab mcpSqlTab)
{
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
}
