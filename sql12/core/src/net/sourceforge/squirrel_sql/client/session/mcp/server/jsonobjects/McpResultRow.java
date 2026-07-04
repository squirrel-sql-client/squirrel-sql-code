package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects;

import java.util.List;

/**
 * One result-set row: a list of cells, positionally aligned with the
 * {@link McpResultMetaData} columns of the enclosing {@link McpResultSet}.
 *
 * @param cells the row's cells, one per column
 */
public record McpResultRow(List<McpResultCell> cells)
{
}
