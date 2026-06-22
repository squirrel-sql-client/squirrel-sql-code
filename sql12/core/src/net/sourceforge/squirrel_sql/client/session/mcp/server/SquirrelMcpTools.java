package net.sourceforge.squirrel_sql.client.session.mcp.server;

import net.sourceforge.squirrel_sql.client.session.mcp.server.annotations.McpTool;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.GetTablesArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.NoArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.ResultSet;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.SimpleString;

/**
 * The functions this MCP server exposes — one Java method per MCP tool.
 * <p>
 * This interface is the contract a Java developer reads to learn what the
 * server offers; the JSON-RPC / HTTP transport is merely an adapter that
 * routes incoming {@code tools/call} requests to these methods. Keeping the
 * tools here (instead of buried in the protocol plumbing) means adding a tool
 * is: add a method, implement it, register it in the dispatcher.
 */
public interface SquirrelMcpTools
{
   @McpTool(description = "SQuirreL Session name")
   SimpleString getSessionName(NoArgs none);

   @McpTool(description = "JDBC driver class name")
   SimpleString getDriverClassName(NoArgs none);

   @McpTool(description = "JDBC-URL")
   SimpleString getJdbcUrl(NoArgs none);


   /**
    * Tool {@code getTables}: lists database tables, mirroring
    * {@link java.sql.DatabaseMetaData#getTables(String, String, String, String[])}.
    *
    * @param args catalog / schema / table-name patterns and table types to filter by
    * @return the matching tables as a typed result set
    */
   @McpTool(description = "Lists database tables (JDBC DatabaseMetaData.getTables).")
   ResultSet getTables(GetTablesArgs args);

}
