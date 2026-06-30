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
public record ResultSet(List<ResultMetaData> resultMetaData, List<ResultRow> rows, Integer rowsLimitedTo, String errorMessage, String updateMessage)
{
   public static ResultSet ofResult(List<ResultMetaData> resultMetaData, List<ResultRow> rows, Integer rowsLimitedTo)
   {
      return new ResultSet(resultMetaData, rows, rowsLimitedTo, null, null);
   }


   public static ResultSet ofResult(List<ResultMetaData> resultMetaData, List<ResultRow> rows)
   {
      return ofResult(resultMetaData, rows, null);
   }

   public static ResultSet ofError(String errorMessage)
   {
      return new ResultSet(null, null, null, errorMessage, null);
   }

   public static ResultSet ofUpdateMessage(String updateMessage)
   {
      return new ResultSet(null, null, null, null, updateMessage);
   }
}
