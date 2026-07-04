package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects;

import net.sourceforge.squirrel_sql.client.session.mcp.server.annotations.McpProp;

public record McpGetIndexInfoArgs(@McpProp(description = "Catalog name; null = no catalog filter.") String catalog,
                                  @McpProp(description = "Schema name pattern; null = no schema filter.") String schema,
                                  @McpProp(description = "Table name pattern; null = all tables.") String table,
                                  @McpProp(description = "When true, return only indices for unique values; when false, return indices regardless of whether unique or not.") boolean  unique,
                                  @McpProp(description = "When true, result is allowed to reflect approximate or out of data values; when false, results are requested to be accurate") boolean approximate)
{
}
