package net.sourceforge.squirrel_sql.client.session.mcp.server;

import net.sourceforge.squirrel_sql.client.session.mcp.server.annotations.McpProp;
import net.sourceforge.squirrel_sql.client.session.mcp.server.annotations.McpTool;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetExportedKeysArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetImportedKeysArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetIndexInfoArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpGetTablesArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpNoArgs;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpResultSet;
import net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.McpSimpleString;

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
   McpSimpleString getSessionName(McpNoArgs none);

   @McpTool(description = "JDBC driver class name")
   McpSimpleString getDriverClassName(McpNoArgs none);

   @McpTool(description = "JDBC driver name")
   McpSimpleString getDriverName(McpNoArgs none);

   @McpTool(description = "Database product version")
   McpSimpleString getDriverVersion(McpNoArgs none);

   @McpTool(description = "Database product name")
   McpSimpleString getDatabaseProductName(McpNoArgs none);

   @McpTool(description = "Database product version")
   McpSimpleString getDatabaseProductVersion(McpNoArgs none);


   @McpTool(description = "Lists database tables (JDBC DatabaseMetaData.getTables).")
   McpResultSet executeQuery(@McpProp(description = "SQL to execute") McpSimpleString sql);


   @McpTool(description = "Lists database tables (JDBC DatabaseMetaData.getTables).")
   McpResultSet getTables(McpGetTablesArgs args);

   @McpTool(description = "Lists imported keys (JDBC DatabaseMetaData.getImportedKeys).")
   McpResultSet getImportedKeys(McpGetImportedKeysArgs args);

   @McpTool(description = "Lists exported keys (JDBC DatabaseMetaData.getExportedKeys).")
   McpResultSet getExportedKeys(McpGetExportedKeysArgs args);

   @McpTool(description = "Lists indexes (JDBC DatabaseMetaData.getIndexInfo).")
   McpResultSet getIndexInfo(McpGetIndexInfoArgs args);

}

