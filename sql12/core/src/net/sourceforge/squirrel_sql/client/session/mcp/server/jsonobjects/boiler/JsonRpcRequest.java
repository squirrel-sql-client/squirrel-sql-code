package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler;

import com.fasterxml.jackson.databind.JsonNode;

public record JsonRpcRequest(String jsonrpc, JsonNode id, String method, JsonNode params)
{
}
