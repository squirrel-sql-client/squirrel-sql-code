package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects.boiler;

import java.util.Map;

public record ToolSpec(String name, String description, Map<String, Object> inputSchema)
{
}
