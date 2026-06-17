package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler;

public record InitializeResult(String protocolVersion, ServerCapabilities capabilities, ServerInfo serverInfo)
{
}
