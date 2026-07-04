package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects;

/**
 * Description of a single result-set column.
 *
 * @param column      1-based column index
 * @param columnName  column label
 * @param sqlType     {@link java.sql.Types} constant for the column
 * @param sqlTypeName database-specific type name (e.g. {@code "VARCHAR"})
 */
public record McpResultMetaData(int column, String columnName, int sqlType, String sqlTypeName)
{
}
