package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects;

import net.sourceforge.squirrel_sql.client.session.mcp.server.annotations.McpProp;

public record McpGetPrimaryKeysArgs(@McpProp(description = "Catalog name; null = no catalog filter.") String catalog,
                                    @McpProp(description = "Schema name pattern; null = no schema filter.") String schema,
                                    @McpProp(description = "Table name pattern; null = all tables.") String table)
{
}
