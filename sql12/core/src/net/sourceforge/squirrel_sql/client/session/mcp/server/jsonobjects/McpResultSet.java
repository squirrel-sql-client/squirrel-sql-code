package net.sourceforge.squirrel_sql.client.session.mcp.server.jsonobjects;

import java.util.List;

/**
 * A JSON-friendly snapshot of a JDBC result set: the column descriptions plus
 * the materialised rows. (Distinct from {@link java.sql.ResultSet}, which is a
 * stateful cursor — this is a plain immutable data carrier for transport.)
 *
 * @param resultMetaData one entry per column, in column order
 * @param rows           the data rows; each row holds one cell per column
 */
public record McpResultSet(List<McpResultMetaData> resultMetaData, List<McpResultRow> rows, Integer rowsLimitedTo, String errorMessage, String updateMessage)
{
   public static McpResultSet ofResult(List<McpResultMetaData> resultMetaData, List<McpResultRow> rows, Integer rowsLimitedTo)
   {
      return new McpResultSet(resultMetaData, rows, rowsLimitedTo, null, null);
   }


   public static McpResultSet ofResult(List<McpResultMetaData> resultMetaData, List<McpResultRow> rows)
   {
      return ofResult(resultMetaData, rows, null);
   }

   public static McpResultSet ofError(String errorMessage)
   {
      return new McpResultSet(null, null, null, errorMessage, null);
   }

   public static McpResultSet ofUpdateMessage(String updateMessage)
   {
      return new McpResultSet(null, null, null, null, updateMessage);
   }
}
