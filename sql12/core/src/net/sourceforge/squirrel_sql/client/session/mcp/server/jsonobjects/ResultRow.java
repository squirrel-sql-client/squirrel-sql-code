package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects;

import java.util.List;

/**
 * One result-set row: a list of cells, positionally aligned with the
 * {@link ResultMetaData} columns of the enclosing {@link ResultSet}.
 *
 * @param cells the row's cells, one per column
 */
public record ResultRow(List<ResultCell> cells)
{
}
