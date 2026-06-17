package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects;

import net.sourceforge.squirrel_sql.client.session.mcp.server.annotations.McpProp;

/**
 * Typed view of the {@code getTables} tool arguments. Mirrors the filter
 * parameters of {@link java.sql.DatabaseMetaData#getTables(String, String, String, String[])};
 * any field may be {@code null} to mean "no filter on this dimension".
 *
 * @param catalog          catalog name; {@code null} = no catalog filter
 * @param schemaPattern    schema name pattern; {@code null} = no schema filter
 * @param tableNamePattern table name pattern; {@code null} = all tables
 * @param types            table types to include (e.g. {@code "TABLE"}, {@code "VIEW"});
 *                         {@code null} = all types
 */
public record GetTablesArgs(
      @McpProp(description = "Catalog name; null = no catalog filter.") String catalog,
      @McpProp(description = "Schema name pattern; null = no schema filter.") String schemaPattern,
      @McpProp(description = "Table name pattern; null = all tables.") String tableNamePattern,
      @McpProp(description = "Table types to include, e.g. [\"TABLE\",\"VIEW\"]; null = all types.") String[] types)
{
}
